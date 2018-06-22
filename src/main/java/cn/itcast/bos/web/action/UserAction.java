package cn.itcast.bos.web.action;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.UserService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class UserAction extends BaseAction<User> {

	@Autowired
	private UserService userService;
	
	@Action(value="user_login",results={
			@Result(name="success",type="redirect",location="/index.html"),
			@Result(name="error",type="redirect",location="/login.html")})
	public String login(){
		// 用户名和密码 都保存在model中
		// 基于shiro实现登录
		Subject subject = SecurityUtils.getSubject();
		//用户名和密码信息
		AuthenticationToken token = new UsernamePasswordToken(model.getUsername(),model.getPassword());
		try {
			subject.login(token);
			return SUCCESS;
		} catch (AuthenticationException e) {
//			e.printStackTrace();
			if(e instanceof UnknownAccountException){
				System.out.println("用户名输入有误！");
			}
			if(e instanceof IncorrectCredentialsException){
				System.out.println("密码输入有误！");
			}
			return ERROR;
		}
	}
	
	@Action(value="user_logout",results={
			@Result(name="success",type="redirect",location="login.html")})
	public String logout(){
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		return SUCCESS;
	}
	
	@Action(value="user_list",results={@Result(name="success",type="json")})
	public String list(){
		List<User> list = userService.findAll();
		pushToValueStack(list);
		return SUCCESS;
	}
	
	private String[] roleIds;
	public void setRoleIds(String[] roleIds) {
		this.roleIds = roleIds;
	}
	
	@Action(value="user_save",results={@Result(name="success",type="redirect",location="./pages/system/userlist.html")})
	public String save(){
		userService.save(model,roleIds);
		return SUCCESS;
	}
}
