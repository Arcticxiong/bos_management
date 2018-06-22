package cn.itcast.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.WorkBillRepository;
import cn.itcast.bos.service.WorkBillService;

@Service
@Transactional
public class WorkBillServiceImpl implements WorkBillService {

	@Autowired
	private WorkBillRepository workBillRepository;

}
