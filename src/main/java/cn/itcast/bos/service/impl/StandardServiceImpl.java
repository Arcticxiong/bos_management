package cn.itcast.bos.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.StandardRepository;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;
@Service
@Transactional
public class StandardServiceImpl implements StandardService {

	@Autowired	
	private StandardRepository standardRepository;
	@Override
	@CacheEvict(value="standard",allEntries=true)
	public void save(Standard standard) {
		standardRepository.save(standard);
	}
	@Override
	public List<Standard> findByName(String name) {
		List<Standard> list = standardRepository.findByName(name);
		return list;
	}
	@Override
//	@Cacheable(value="standard",key="#pageable.pageNumber+'_'+#pageable.pageSize")
	@Cacheable("standard")
	public Page<Standard> findPageData(Pageable pageable) {
		return standardRepository.findAll(pageable);
	}
	@Override
	public void deleteStandard(String[] idArray) {
		for (String id : idArray) {
			standardRepository.delete(Integer.parseInt(id));
		}
	}
	@Override
	@Cacheable("standard")
	public List<Standard> findAll() {
		return standardRepository.findAll();
	}

}
