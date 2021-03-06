package cn.itcast.bos.service;

import java.util.List;

import cn.itcast.bos.domain.system.User;

public interface UserService {
	
	User findByUsername(String username);

	List<User> findAll();

	void save(User model, String[] roleIds);

}
