package cn.itcast.bos.web.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.service.RoleService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {

	@Autowired
	private RoleService roleService;
	
	@Action(value="role_list",results={@Result(name="success",type="json")})
	public String list(){
		List<Role> list = roleService.findAll();
		pushToValueStack(list);
		return SUCCESS;
	}
	
	private String menuIds;
	private String[] permissionIds;

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}
	public void setPermissionIds(String[] permissionIds) {
		this.permissionIds = permissionIds;
	}
	
	
	@Action(value="role_save",results={@Result(name="success",type="redirect",location="./pages/system/role.html")})
	public String save(){
		roleService.save(model,menuIds,permissionIds);
		return SUCCESS;
	}
}
