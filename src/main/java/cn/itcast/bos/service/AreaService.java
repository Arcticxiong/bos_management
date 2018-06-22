package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import cn.itcast.bos.domain.base.Area;

public interface AreaService {

	void saveAreas(List<Area> list);

	Page<Area> findPageDataByCondition(Specification<Area> specification, Pageable pageable);

	void save(Area model);

	void delete(String idArray);


}
