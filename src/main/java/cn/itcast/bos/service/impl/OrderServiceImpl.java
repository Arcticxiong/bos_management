package cn.itcast.bos.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.constant.Constants;
import cn.itcast.bos.dao.AreaRepository;
import cn.itcast.bos.dao.FixedAreaRepository;
import cn.itcast.bos.dao.OrderRepository;
import cn.itcast.bos.dao.WorkBillRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WorkBill;
import cn.itcast.bos.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	@Autowired
	FixedAreaRepository fixedAreaRepository;
	
	@Autowired
	AreaRepository areaRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	@Qualifier("jmsQueueTemplate")
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private WorkBillRepository workBillRepository;
	
	@Override
	public void saveOrder(Order order) {
//		System.out.println(order);
		order.setOrderNum(UUID.randomUUID().toString());//订单号
		order.setOrderTime(new Date());//下单时间
		order.setStatus("1");//待取件      订单状态 1 待取件 2 运输中 3 已签收 4 异常
		//由于寄件人和收件人区域的id为空，所以需要先根据省市区查找对应数据库中的区域获取id，然后关联订单
		Area sendArea=order.getSendArea();
		Area persistSendArea = areaRepository.findByProvinceAndCityAndDistrict(sendArea.getProvince(),
				sendArea.getCity(),sendArea.getDistrict());
		Area recArea=order.getSendArea();
		Area persistRecArea = areaRepository.findByProvinceAndCityAndDistrict(recArea.getProvince(),
				recArea.getCity(),recArea.getDistrict());
		order.setSendArea(persistSendArea);
		order.setRecArea(persistRecArea);
		
		// 自动分单逻辑，基于CRM地址库完全匹配，获取定区，匹配快递员
		String fixedAreaId = WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/findFixedAreaIdByAddressAndId?"
				+ "address="+order.getSendAddress()+"&customerId="+order.getCustomer_id())
				.get(String.class);
		if(fixedAreaId!=null){
			//定区ID不为空，则根据该id找到对应的定区
			FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
			//获取该定区关联的快递员
			Iterator<Courier> iterator = fixedArea.getCouriers().iterator();
			while(iterator.hasNext()){
				Courier courier = iterator.next();
				if(courier!=null){
					//自动分单成功
					System.out.println("自动分单成功");
					saveOrder(order,courier);
					generateWorkBill(order);
					return;//默认分单给第一个快递员，待优化
				}
			}
		}
		// 自动分单 逻辑， 通过省市区 ，查询分区关键字，匹配地址，基于分区实现自动分单
		for(SubArea subArea:persistSendArea.getSubareas()){
			// 当前客户的下单地址 是否包含分区 关键字
			if(order.getSendAddress().contains(subArea.getKeyWords())){
				//找到该分区对应的定区以及快递员
				Iterator<Courier> iterator = subArea.getFixedArea().getCouriers().iterator();
				while(iterator.hasNext()){
					Courier courier = iterator.next();
					if(courier!=null){
						//自动分担成功
						System.out.println("自动分单成功");
						saveOrder(order,courier);
						generateWorkBill(order);
						return;//默认分单给第一个快递员，待优化
					}
				}
			}
		}
		for(SubArea subArea:persistSendArea.getSubareas()){
			// 当前客户的下单地址 是否包含分区 关键字
			if(order.getSendAddress().contains(subArea.getAssistKeyWords())){
				//找到该分区对应的定区以及快递员
				Iterator<Courier> iterator = subArea.getFixedArea().getCouriers().iterator();
				while(iterator.hasNext()){
					Courier courier = iterator.next();
					if(courier!=null){
						//自动分担成功
						System.out.println("自动分单成功");
						saveOrder(order,courier);
						generateWorkBill(order);
						return;//默认分单给第一个快递员，待优化
					}
				}
			}
		}
		//自动分单失败，进入人工分单
		order.setOrderType("2");
		orderRepository.save(order);
	}

	private void saveOrder(Order order, Courier courier) {
		//将快递员与订单关联
		order.setCourier(courier);
		//设置订单类型为自动分担
		order.setOrderType("1");//1.自动分担，2.手工分单
		//保存订单
		orderRepository.save(order);
	}
	
	//生成工单，发送短信
	private void generateWorkBill(final Order order){
		//生成工单
		WorkBill workBill = new WorkBill();
		workBill.setType("新");//工单类型    新，追，销
		workBill.setPickstate("新单");//取件状态
		workBill.setBuildtime(new Date());//工单生成时间
		workBill.setAttachbilltimes(0);
		workBill.setRemark(order.getRemark());//备注
		final String smsNumber = RandomStringUtils.randomNumeric(4);
		workBill.setSmsNumber(smsNumber);
		workBill.setOrder(order);
		workBill.setCourier(order.getCourier());
		//发送短信
		jmsTemplate.send("bos_couriersms", new MessageCreator() {
			//模版内容:速运快递 短信序号：${num}，取件地址：${sendAddress}，联系人：${sendName}，手机：${sendMobile}，快递员捎话:${sendMobileMsg}
			@Override
			public Message createMessage(Session session) throws JMSException {
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("telephone", order.getCourier().getTelephone());
				mapMessage.setString("num", smsNumber);
				mapMessage.setString("sendAddress", order.getSendAddress());
				mapMessage.setString("sendName", order.getSendName());
				mapMessage.setString("sendMobile", order.getSendMobile());
				mapMessage.setString("sendMobileMsg", order.getSendMobileMsg());
				return mapMessage;
			}
		});
		workBill.setPickstate("已通知");
		workBillRepository.save(workBill);
	}

	@Override
	public Order findByOrderNum(String orderNum) {
		return orderRepository.findByOrderNum(orderNum);
	}

}
