package cn.itcast.bos.web.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.OrderService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class OrderAction extends BaseAction<Order> {

	@Autowired
	private OrderService orderService;
	
	@Action(value="order_findByOrderNum",results={@Result(name="success",type="json")})
	public String findByOrderNum(){
		Order order = orderService.findByOrderNum(model.getOrderNum());
		Map<String, Object> result = new HashMap<>();
		if(order == null){
			result.put("success", false);
		}else{
			//生成运单号
			WayBill wayBill = new WayBill();
			wayBill.setWayBillNum("waybill"+RandomStringUtils.randomNumeric(32));
			order.setWayBill(wayBill);
			result.put("success", true);
			result.put("orderData", order);
		}
		pushToValueStack(result);
		return SUCCESS;
	}
}
