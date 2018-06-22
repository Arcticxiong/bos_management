package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.FixedArea;

public interface FixedAreaService {

	void save(FixedArea model);

	Page<FixedArea> findPageDataByCondition(Specification<FixedArea> specification, Pageable pageable);

	void associationCourierToFixedArea(FixedArea model, String courierId, String takeTimeId);

	List<FixedArea> findAll();
}
