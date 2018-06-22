package cn.itcast.bos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.bos.domain.transit.SignInfo;

public interface SignInfoRepository extends JpaRepository<SignInfo,Integer>,JpaSpecificationExecutor<SignInfo> {


}
