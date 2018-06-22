package cn.itcast.bos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.bos.domain.take_delivery.WorkBill;

public interface WorkBillRepository extends JpaRepository<WorkBill,Integer>,JpaSpecificationExecutor<WorkBill> {


}
