package cn.itcast.bos.service;

import java.util.List;

import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.domain.system.User;

public interface MenuService {

	List<Menu> findAll();

	void save(Menu model);

	List<Menu> findByUser(User user);


}
