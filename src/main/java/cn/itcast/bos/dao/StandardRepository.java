package cn.itcast.bos.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Standard;

public interface StandardRepository extends JpaRepository<Standard, Integer>{

	List<Standard> findByName(String name);

	List<Standard> findByNameLike(String name);
	// 默认执行的是HQL语句
	// nativeQuery=false 配置JPQL（类似于HQL）也是默认值， nativeQuery=true 配置SQL 
	@Query(value="from Standard where name = ?")
	List<Standard> queryName(String name);
	
	//不按命名规则写的查询方法 配置@Query 没写语句 ， 实体类 @NamedQuery定义，叫做命名查询
	@Query
	List<Standard> queryName1(String name);
	
	//使用@Query注解完成 ， 搭配使用@Modifying 标记修改、删除操作
	@Query(value="update Standard set name=?2 where id=?1")
	@Modifying
	void updateName(Integer id , String name);
}
