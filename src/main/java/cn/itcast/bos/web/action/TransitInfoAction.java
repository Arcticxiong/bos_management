package cn.itcast.bos.web.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.TransitInfoService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class TransitInfoAction extends BaseAction<TransitInfo> {

	@Autowired
	private TransitInfoService transitInfoService;
	
	private String wayBillIds;
	public void setWayBillIds(String wayBillIds) {
		this.wayBillIds = wayBillIds;
	}
	@Action(value="transit_create",results={@Result(name="success",type="json")})
	public String create(){
		Map<String, Object> map = new HashMap<>();
		try {
			transitInfoService.createTransits(wayBillIds);
			map.put("success", true);
			map.put("msg", "开启中转配送成功");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "开启中转配送失败");
		}
		pushToValueStack(map);
		return SUCCESS;
	}
	
	@Action(value = "transit_pageQuery", results = { @Result(name = "success", type = "json") })
	public String pageQuery() {
		// 分页查询
		Pageable pageable = new PageRequest(page - 1, rows);
		// 调用业务层 查询分页数据
		Page<TransitInfo> pageData = transitInfoService.findPageData(pageable);

		// 压入值栈返回
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}
}
