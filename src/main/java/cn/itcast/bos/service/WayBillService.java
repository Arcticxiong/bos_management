package cn.itcast.bos.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.take_delivery.WayBill;

public interface WayBillService {

	void save(WayBill model);

	Page<WayBill> findPageData(WayBill model, Pageable pageable);

	WayBill findByWayBillNum(String wayBillNum);

	void syncIndex();

	List<WayBill> findWayBills(WayBill model);

	List<Object[]> chartWayBill();
}
