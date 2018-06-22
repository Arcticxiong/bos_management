package cn.itcast.bos.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.CourierRespository;
import cn.itcast.bos.dao.FixedAreaRepository;
import cn.itcast.bos.dao.TakeTimeRepository;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.TakeTime;
import cn.itcast.bos.service.FixedAreaService;

@Service
@Transactional
public class FixedAreaServiceImpl implements FixedAreaService {

	@Autowired
	private FixedAreaRepository fixedAreaRepository;
	@Autowired
	private CourierRespository courierRespository;
	@Autowired
	private TakeTimeRepository takeTimeRepository;

	@Override
	public void save(FixedArea model) {
		fixedAreaRepository.save(model);
	}

	@Override
	public Page<FixedArea> findPageDataByCondition(Specification<FixedArea> specification, Pageable pageable) {
		return fixedAreaRepository.findAll(specification, pageable);
	}

	@Override
	public void associationCourierToFixedArea(FixedArea model, String courierId, String takeTimeId) {
		FixedArea fixedArea = fixedAreaRepository.findOne(model.getId());
		Courier courier = courierRespository.findOne(Integer.parseInt(courierId));
		TakeTime takeTime = takeTimeRepository.findOne(Integer.parseInt(takeTimeId));
		// 快递员 关联到 定区上（多对多的数据库结构，由定区一端维护关联关系）
		fixedArea.getCouriers().add(courier);
		// 将收派时间 关联到 快递员上（一对多的数据库结构，由快递员一端维护关联关系）
		courier.setTakeTime(takeTime);
	}

	@Override
	public List<FixedArea> findAll() {
		return fixedAreaRepository.findAll();
	}

}
