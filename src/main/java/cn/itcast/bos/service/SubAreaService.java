package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.SubArea;

public interface SubAreaService {

	void save(List<SubArea> list);

	Page<SubArea> findPageDataByCondition(Specification<SubArea> specification, Pageable pageable);

	List<SubArea> findAssociationSubArea(String fixedAreaId);

	void save(SubArea model);
	
	List<SubArea> findAll();

}
