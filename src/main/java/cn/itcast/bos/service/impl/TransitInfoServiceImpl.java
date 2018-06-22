package cn.itcast.bos.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.TransitInfoRepository;
import cn.itcast.bos.dao.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.TransitInfoService;

@Service
@Transactional
public class TransitInfoServiceImpl implements TransitInfoService {

	@Autowired
	private TransitInfoRepository transitInfoRepository;

	@Autowired
	private WayBillRepository wayBillRepository;
	
	@Autowired
	private WayBillIndexRepository wayBillIndexRepository;
	@Override
	public void createTransits(String wayBillIds) {
		if(StringUtils.isNotBlank(wayBillIds)){
			String[] arrayWayBillIds = wayBillIds.split(",");
			for (String wayBillId : arrayWayBillIds) {
				//根据运单id查询运单
				WayBill wayBill = wayBillRepository.findOne(Integer.parseInt(wayBillId));
				//判断运单状态是否为待发货
				if(wayBill.getSignStatus()==1){
					//生成运单配送信息（工作流对象）
					TransitInfo transitInfo = new TransitInfo();
					//建立运单与工作流的关联关系
					transitInfo.setWayBill(wayBill);
					//更改工作流状态
					transitInfo.setStatus("出入库中转");
					//保存工作流对象
					transitInfoRepository.save(transitInfo);
					//更改运单状态
					wayBill.setSignStatus(2);//派送中
					//同步更新索引库
					wayBillIndexRepository.save(wayBill);
				}
			}
		}
	}
	@Override
	public Page<TransitInfo> findPageData(Pageable pageable) {
		return transitInfoRepository.findAll(pageable);
	}

}
