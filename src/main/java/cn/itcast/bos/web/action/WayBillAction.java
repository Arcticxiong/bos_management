package cn.itcast.bos.web.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.WayBillService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class WayBillAction extends BaseAction<WayBill> {
	
	//日志对象
	private static final Logger LOGGER= Logger.getLogger(WayBillAction.class);
	
	@Autowired
	private WayBillService wayBillService;
	
	@Action(value="waybill_save",results={@Result(name="success",type="json")})
	public String save(){
		Map<String, Object> map = new HashMap<>();
	
		try {
			// 防止瞬时态异常
			if(model.getOrder()!=null && model.getOrder().getId()==null){
				model.setOrder(null);
			}
			wayBillService.save(model);
			map.put("success", true);
			map.put("msg", "保存运单成功。运单号："+model.getWayBillNum());
			LOGGER.info("保存运单成功。运单号："+model.getWayBillNum());
		} catch (Exception e) {
			e.printStackTrace();
			//保存失败
			map.put("success", false);
			map.put("msg", "保存运单失败。运单号："+model.getWayBillNum());
			LOGGER.error("保存运单失败。运单号："+model.getWayBillNum());
		}
		pushToValueStack(map);
		return SUCCESS;
	}
	
	@Action(value="waybill_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows, new Sort(Direction.DESC,"id"));
		//调用业务层进行查询
		Page<WayBill> pageData = wayBillService.findPageData(model,pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}
	
	@Action(value="waybill_findByWayBillNum",results={@Result(name="success",type="json")})
	public String findByWayBillNum(){
		WayBill wayBill = wayBillService.findByWayBillNum(model.getWayBillNum());
		Map<String, Object> result = new HashMap<>();
		if(wayBill == null){
			result.put("success", false);
		}else {
			result.put("success", true);
			result.put("wayBillData", wayBill);
		}
		pushToValueStack(result);
		return SUCCESS;
	}
}
