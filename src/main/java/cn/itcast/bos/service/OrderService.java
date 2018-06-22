package cn.itcast.bos.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import cn.itcast.bos.domain.take_delivery.Order;

public interface OrderService {

	@POST
	@Path("/order/save")
	@Consumes({"application/xml","application/json"})
	void saveOrder(Order order);

	Order findByOrderNum(String orderNum);
}
