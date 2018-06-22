package bos_management;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.bos.domain.take_delivery.WayBill;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TestElasticSearch {
	@Autowired
	ElasticsearchTemplate elasticsearchTemplate;
	
	@Test
	public void createIndexAndMapping(){
		elasticsearchTemplate.createIndex(WayBill.class);
		elasticsearchTemplate.putMapping(WayBill.class);
	}
	
}
