package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Courier;

public interface CourierService {

	void save(Courier courier);

	Page<Courier> findPageData(Pageable pageable);

	Page<Courier> findPageQueryCourierByCondition(Specification<Courier> specification, Pageable pageable);

	void delBatch(String[] idArray);

	void restore(String[] idArray);

	List<Courier> findnoassociation();

	List<Courier> findAssociationCourier(String fixedAreaId);

}
