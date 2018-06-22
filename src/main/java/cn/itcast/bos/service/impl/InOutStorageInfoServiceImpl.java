package cn.itcast.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.InOutStorageInfoRepository;
import cn.itcast.bos.dao.TransitInfoRepository;
import cn.itcast.bos.domain.transit.InOutStorageInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.InOutStorageInfoService;

@Service
@Transactional
public class InOutStorageInfoServiceImpl implements InOutStorageInfoService {

	@Autowired
	private InOutStorageInfoRepository inOutStorageInfoRepository;

	@Autowired
	private TransitInfoRepository transitInfoRepository;
	
	@Override
	public void save(String transitInfoId, InOutStorageInfo inOutStorageInfo) {
		//保存出入库信息
		inOutStorageInfoRepository.save(inOutStorageInfo);
		//建立出入库对象和运输配送对象的关联关系
		TransitInfo transitInfo = transitInfoRepository.findOne(Integer.parseInt(transitInfoId));
		transitInfo.getInOutStorageInfos().add(inOutStorageInfo);
		//修改状态
		if("到达网点".equals(inOutStorageInfo.getOperation())){
			//设置运输状态为到达网点
			transitInfo.setStatus("到达网点");
			//更新网点地址，显示配送路径
			transitInfo.setOutletAddress(inOutStorageInfo.getAddress());
		}
	}

}
