package cn.itcast.bos.service;

import java.util.List;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;

public interface RoleService {

	List<Role> findByUser(User user);

	List<Role> findAll();

	void save(Role model, String menuIds, String[] permissionIds);


}
