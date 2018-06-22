package cn.itcast.bos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.bos.domain.transit.TransitInfo;

public interface TransitInfoRepository extends JpaRepository<TransitInfo,Integer>,JpaSpecificationExecutor<TransitInfo> {


}
