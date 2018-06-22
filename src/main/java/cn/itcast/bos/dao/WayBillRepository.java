package cn.itcast.bos.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.bos.domain.take_delivery.WayBill;

public interface WayBillRepository extends JpaRepository<WayBill,Integer>,JpaSpecificationExecutor<WayBill> {

	WayBill findByWayBillNum(String wayBillNum);

	@Query("select count(w.signStatus),case when w.signStatus=1 then '待发货' "
			+ "when w.signStatus=2 then '派送中' "
			+ "when w.signStatus=3 then '已签收' "
			+ "else '异常' end "
			+ "from WayBill w group by w.signStatus")
	List<Object[]> chartWayBill();


}
