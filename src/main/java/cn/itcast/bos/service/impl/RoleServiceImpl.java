package cn.itcast.bos.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.MenuRepository;
import cn.itcast.bos.dao.PermissionRepository;
import cn.itcast.bos.dao.RoleRepository;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.RoleService;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private MenuRepository menuRepository;
	
	@Autowired
	private PermissionRepository permissionRepository;
	
	@Override
	public List<Role> findByUser(User user) {
		//admin具有所有角色
		if("admin".equals(user.getUsername())){
			return roleRepository.findAll();
		}else{
			return roleRepository.findByUser(user.getId());
		}
	}

	@Override
	public List<Role> findAll() {
		return roleRepository.findAll();
	}

	@Override
	public void save(Role role, String menuIds, String[] permissionIds) {
		//1.保存角色信息
		roleRepository.save(role);
		//2.角色和菜单建立关联关系
		if (StringUtils.isNotBlank(menuIds)) {
			String[] arrayIds = menuIds.split(",");
			for (String menuId : arrayIds) {
				Menu menu = menuRepository.findOne(Integer.parseInt(menuId));
				role.getMenus().add(menu);
			}
		}
		//3.角色和权限建立关联关系
		for(String permissionId:permissionIds){
			Permission permission = permissionRepository.findOne(Integer.parseInt(permissionId));
			role.getPermissions().add(permission);
		}
	}

}
