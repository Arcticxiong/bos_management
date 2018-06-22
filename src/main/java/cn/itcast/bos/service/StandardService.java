package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.base.Standard;

public interface StandardService {

	void save(Standard standard);

	List<Standard> findByName(String name);

	Page<Standard> findPageData(Pageable pageable);

	void deleteStandard(String[] idArray);

	List<Standard> findAll();

}
