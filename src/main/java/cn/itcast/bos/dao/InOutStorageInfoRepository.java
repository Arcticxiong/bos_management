package cn.itcast.bos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.bos.domain.transit.InOutStorageInfo;

public interface InOutStorageInfoRepository extends JpaRepository<InOutStorageInfo,Integer>,JpaSpecificationExecutor<InOutStorageInfo> {


}
