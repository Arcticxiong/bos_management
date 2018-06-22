package cn.itcast.bos.web.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;
import cn.itcast.bos.web.action.common.BaseAction;
@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class StandardAction extends BaseAction<Standard>{
	
	
	@Autowired
	private StandardService standardService;
	
	//添加(没有id的时候执行save是增加，有id的时候执行save是更新)
	@Action(value="standard_save",results={@Result(name="success",type="redirect",location="./pages/base/standard.html")})
	public String save(){
		standardService.save(model);
		return SUCCESS;
	}
	//表单校验
	@Action(value="standard_validateName",results={@Result(name="success",type="json")})
	public String validateName(){
		String name = model.getName();
		//使用名称查询，获取当前名称对应的集合
		List<Standard> list = standardService.findByName(name);
		if(list!=null && list.size()==0){
			ActionContext.getContext().getValueStack().push(true);
		}else{
			ActionContext.getContext().getValueStack().push(false);
		}
		return SUCCESS;
	}
	
	//分页查询
	@Action(value="standard_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//调用业务层，查询数据结果
		Pageable pageable = new PageRequest(page-1, rows);//pageRequest的页码从0开始，所以需要-1
		Page<Standard> pageData = standardService.findPageData(pageable);
		
		pushPageDataToValueStack(pageData);
		
		return SUCCESS;
	}
	
	private String ids;
	public void setIds(String ids) {
		this.ids = ids;
	}
	//删除
	@Action(value="standard_deleteByids",results={@Result(name="success",type="redirect",location="./pages/base/standard.html")})
	public String deleteByids(){
		String[] idArray=ids.split(",");
		standardService.deleteStandard(idArray);
		return SUCCESS;
	}
	//查询所有标准
	@Action(value="standard_findAll",results={@Result(name="success",type="json")})
	public String findAll(){
		List<Standard> list = standardService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}
}
