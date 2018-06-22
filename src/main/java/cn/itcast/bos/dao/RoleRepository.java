package cn.itcast.bos.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;

public interface RoleRepository extends JpaRepository<Role,Integer>,JpaSpecificationExecutor<Role> {

	@Query("from Role r inner join fetch r.users u where u.id=?")
	List<Role> findByUser(int id);


}
