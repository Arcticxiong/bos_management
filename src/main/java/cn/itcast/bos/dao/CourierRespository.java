package cn.itcast.bos.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.base.Courier;

public interface CourierRespository extends JpaRepository<Courier, Integer>,JpaSpecificationExecutor<Courier> {

	Page<Courier> findAll(Specification<Courier> specification, Pageable pageable);
	
	@Query(value="update Courier set deltag='1' where id=?1 ")
	@Modifying
	void batchDelTag(Integer id);

	@Query(value="update Courier set deltag='' where id=?1 ")
	@Modifying
	void restoreDelTag(int parseInt);
	
	List<Courier> findByFixedAreasIsNull();
	
	//迫切外链接查询 HQL语句的从表不使用实体类的类名而是使用主表外键的属性名，
	//如下语句从表不是用FixedArea 而是使用快递员表的fixedAreas外键属性
	@Query(value="from Courier c inner join fetch c.fixedAreas f where f.id=?")
	List<Courier> findAssociationCourier(String fixedAreaId);


}
