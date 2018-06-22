package cn.itcast.bos.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.WayBillRepository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.WayBillService;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {

	@Autowired
	private WayBillRepository wayBillRepository;
	
	@Autowired
	private WayBillIndexRepository wayBillIndexRepository;

	@Override
	public void save(WayBill wayBill) {
		if(wayBill!=null && wayBill.getId()!=null){
			WayBill persistWayBill = wayBillRepository.findOne(wayBill.getId());
			if(persistWayBill.getSignStatus()!=1){
				System.out.println("运单已经开始配送，无法保存修改");
				throw new RuntimeException("运单已经开始配送，无法保存修改");
			}
		}
		//运单状态：1.待发货 2.派送中 3.已签收 4.异常
		wayBill.setSignStatus(1);
		wayBillRepository.save(wayBill);
		//保存同时，在索引库创建数据
		wayBillIndexRepository.save(wayBill);
	}

	@Override
	public Page<WayBill> findPageData(WayBill wayBill, Pageable pageable) {
		//无条件查询，直接查询数据库
		if(StringUtils.isBlank(wayBill.getWayBillNum())
				&& StringUtils.isBlank(wayBill.getSendAddress())
				&& StringUtils.isBlank(wayBill.getRecAddress())
				&& StringUtils.isBlank(wayBill.getSendProNum())
				&& (wayBill.getSignStatus()==null || wayBill.getSignStatus()==0)){
			return wayBillRepository.findAll(pageable);
		}else{
			//组织查询条件，查询索引库
			BoolQueryBuilder query = new BoolQueryBuilder();
			if(StringUtils.isNotBlank(wayBill.getWayBillNum())){
				query.must(new TermQueryBuilder("wayBillNum", wayBill.getWayBillNum()));
			}
			if(StringUtils.isNotBlank(wayBill.getSendAddress())){
				query.must(new BoolQueryBuilder()
						.should(new WildcardQueryBuilder("sendAddress", "*"+wayBill.getSendAddress()+"*"))
						.should(new QueryStringQueryBuilder(wayBill.getSendAddress()).field("sendAddress").defaultOperator(Operator.AND)));
			}
			if(StringUtils.isNotBlank(wayBill.getRecAddress())){
				query.must(new BoolQueryBuilder()
						.should(new WildcardQueryBuilder("recAddress", "*"+wayBill.getRecAddress()+"*"))
						.should(new QueryStringQueryBuilder(wayBill.getRecAddress()).field("recAddress").defaultOperator(Operator.AND)));
			}
			if(StringUtils.isNotBlank(wayBill.getSendProNum())){
				query.must(new TermQueryBuilder("sendProNum", wayBill.getSendProNum()));
			}
			if(wayBill.getSignStatus()!=null && wayBill.getSignStatus()!=0){
				query.must(new TermQueryBuilder("signStatus", wayBill.getSignStatus()));
			}
			SearchQuery searchQuery = new NativeSearchQuery(query);
			searchQuery.setPageable(pageable);//设置分页
			return wayBillIndexRepository.search(searchQuery);
		}
	}

	@Override
	public WayBill findByWayBillNum(String wayBillNum) {
		return wayBillRepository.findByWayBillNum(wayBillNum);
	}

	@Override
	public void syncIndex() {
		//查询数据库
		List<WayBill> wayBills = wayBillRepository.findAll();
		//同步索引库
		wayBillIndexRepository.save(wayBills);
	}

	@Override
	public List<WayBill> findWayBills(WayBill wayBill) {
		//无条件查询，直接查询数据库
				if(StringUtils.isBlank(wayBill.getWayBillNum())
						&& StringUtils.isBlank(wayBill.getSendAddress())
						&& StringUtils.isBlank(wayBill.getRecAddress())
						&& StringUtils.isBlank(wayBill.getSendProNum())
						&& (wayBill.getSignStatus()==null || wayBill.getSignStatus()==0)){
					return wayBillRepository.findAll();
				}else{
					//组织查询条件，查询索引库
					BoolQueryBuilder query = new BoolQueryBuilder();
					if(StringUtils.isNotBlank(wayBill.getWayBillNum())){
						query.must(new TermQueryBuilder("wayBillNum", wayBill.getWayBillNum()));
					}
					if(StringUtils.isNotBlank(wayBill.getSendAddress())){
						query.must(new BoolQueryBuilder()
								.should(new WildcardQueryBuilder("sendAddress", "*"+wayBill.getSendAddress()+"*"))
								.should(new QueryStringQueryBuilder(wayBill.getSendAddress()).field("sendAddress").defaultOperator(Operator.AND)));
					}
					if(StringUtils.isNotBlank(wayBill.getRecAddress())){
						query.must(new BoolQueryBuilder()
								.should(new WildcardQueryBuilder("recAddress", "*"+wayBill.getRecAddress()+"*"))
								.should(new QueryStringQueryBuilder(wayBill.getRecAddress()).field("recAddress").defaultOperator(Operator.AND)));
					}
					if(StringUtils.isNotBlank(wayBill.getSendProNum())){
						query.must(new TermQueryBuilder("sendProNum", wayBill.getSendProNum()));
					}
					if(wayBill.getSignStatus()!=null && wayBill.getSignStatus()!=0){
						query.must(new TermQueryBuilder("signStatus", wayBill.getSignStatus()));
					}
					SearchQuery searchQuery = new NativeSearchQuery(query);
					return wayBillIndexRepository.search(searchQuery).getContent();
				}
	}

	@Override
	public List<Object[]> chartWayBill() {
		return wayBillRepository.chartWayBill();
	}

}
