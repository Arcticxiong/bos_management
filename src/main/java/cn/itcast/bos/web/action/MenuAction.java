package cn.itcast.bos.web.action;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.MenuService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class MenuAction extends BaseAction<Menu> {

	@Autowired
	private MenuService menuService;
	
	@Action(value="menu_list",results={@Result(name="success",type="json")})
	public String list(){
		List<Menu> list = menuService.findAll();
		pushToValueStack(list);
		return SUCCESS;
	}
	
	@Action(value="menu_save",results={@Result(name="success",type="redirect",location="./pages/system/menu.html")})
	public String save(){
		menuService.save(model);
		return SUCCESS;
	}
	
	@Action(value="menu_showMenu",results={@Result(name="success",type="json")})
	public String showMenu(){
		//获取当前用户，根据当前用户查询菜单
		Subject subject = SecurityUtils.getSubject();
		User user = (User) subject.getPrincipal();
		List<Menu> list = menuService.findByUser(user);
		pushToValueStack(list);
		return SUCCESS;
	}
}
