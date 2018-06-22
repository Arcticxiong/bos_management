package cn.itcast.bos.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.AreaRepository;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.AreaService;

@Service
@Transactional
public class AreaServiceImpl implements AreaService {

	@Autowired
	private AreaRepository areaRepository;

	@Override
	public void saveAreas(List<Area> areas) {
		areaRepository.save(areas);
	}

	@Override
	public Page<Area> findPageDataByCondition(Specification<Area> specification, Pageable pageable) {
		return areaRepository.findAll(specification, pageable);
	}

	@Override
	public void save(Area model) {
		areaRepository.save(model);
	}

	@Override
	public void delete(String ids) {
		String[] idArray = ids.split(",");
		for (String id : idArray) {
			areaRepository.delete(id);
		}
	}

}
