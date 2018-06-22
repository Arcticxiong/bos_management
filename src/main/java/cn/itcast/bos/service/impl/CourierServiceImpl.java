package cn.itcast.bos.service.impl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.CourierRespository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.service.CourierService;
@Service
@Transactional
public class CourierServiceImpl implements CourierService {
	@Autowired
	private CourierRespository courierRepository;
	@Override
	@RequiresPermissions("courier:add")
	public void save(Courier courier) {
		courierRepository.save(courier);
	}
	@Override
	public Page<Courier> findPageData(Pageable pageable) {
		return courierRepository.findAll(pageable);
	}
	@Override
	public Page<Courier> findPageQueryCourierByCondition(Specification<Courier> specification, Pageable pageable) {
		return courierRepository.findAll(specification,pageable);
	}
	@Override
	public void delBatch(String[] idArray) {
		for (String id : idArray) {
			courierRepository.batchDelTag(Integer.parseInt(id));
		}
	}
	@Override
	public void restore(String[] idArray) {
		for (String id : idArray) {
			courierRepository.restoreDelTag(Integer.parseInt(id));
		}
	}
	@Override
	public List<Courier> findnoassociation() {
		//方案一
//		 List<Courier> list = courierRepository.findByFixedAreasIsNull();
		
		//方案二
		Specification<Courier> specification = new Specification<Courier>() {

			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = cb.isEmpty(root.get("fixedAreas").as(Set.class));
				return predicate;
			}
		};
		List<Courier> list = courierRepository.findAll(specification);
		 
		return list;
	}
	@Override
	public List<Courier> findAssociationCourier(String fixedAreaId) {
		return courierRepository.findAssociationCourier(fixedAreaId);
	}

}
