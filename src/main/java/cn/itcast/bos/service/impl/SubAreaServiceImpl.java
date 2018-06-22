package cn.itcast.bos.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.SubAreaRepository;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.SubAreaService;

@Service
@Transactional
public class SubAreaServiceImpl implements SubAreaService {

	@Autowired
	private SubAreaRepository subAreaRepository;

	@Override
	public void save(List<SubArea> list) {
		subAreaRepository.save(list);
	}

	@Override
	public Page<SubArea> findPageDataByCondition(Specification<SubArea> specification, Pageable pageable) {
		return subAreaRepository.findAll(specification, pageable);
	}

	@Override
	public List<SubArea> findAssociationSubArea(String fixedAreaId) {
		return subAreaRepository.findAssociationSubArea(fixedAreaId);
	}

	@Override
	public void save(SubArea model) {
		subAreaRepository.save(model);
	}

	@Override
	public List<SubArea> findAll() {
		return subAreaRepository.findAll();
	}
	
}
