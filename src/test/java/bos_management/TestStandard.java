package bos_management;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.bos.dao.StandardRepository;
import cn.itcast.bos.domain.base.Standard;

@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class TestStandard {
	@Autowired
	private StandardRepository standardRepository;
	
	@Test
	public void findByName(){
//		System.out.println(standardRepository.findByName("10-15公斤"));
//		System.out.println("-------------------------------");
		List<Standard> list = standardRepository.findByNameLike("%10%");
		for (Standard standard : list) {
			System.out.println(standard);
		}
	}
	
	@Test
	public void testQueryName(){
		System.out.println(standardRepository.queryName("10-15公斤"));
	}
	
	@Test
	public void testFindByName(){
		List<Standard> list = standardRepository.queryName1("%10%");
		for (Standard standard : list) {
			System.out.println(standard);
		}
	}
	
	//这种方法，只修改了name其余字段变为""
	@Test
	public void testSave(){
		Standard standard = new Standard();
		standard.setId(1);
		standard.setName("测试");
		standardRepository.save(standard);
	}
	
	@Test
	@Transactional
	@Rollback(false)//设置事物不回滚
	public void testUpdate(){
		standardRepository.updateName(1, "测试");
	}
}
