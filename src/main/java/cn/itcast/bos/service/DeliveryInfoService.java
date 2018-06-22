package cn.itcast.bos.service;

import cn.itcast.bos.domain.transit.DeliveryInfo;

public interface DeliveryInfoService {

	void save(String transitInfoId, DeliveryInfo model);


}
