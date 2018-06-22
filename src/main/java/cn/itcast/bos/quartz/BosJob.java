package cn.itcast.bos.quartz;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.service.PromotionService;
import cn.itcast.bos.service.WayBillService;

public class BosJob implements Job{
	
	@Autowired
	private PromotionService promotionService;
	
	@Autowired
	private WayBillService wayBillService;
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("活动过期处理程序执行...：当前时间："+new Date());
		// 任务一：每分钟执行一次，当前时间大于Promotion表的endDate字段的时间，说明该活动已经过去，需要更新status = '2'
		promotionService.updateStatus(new Date());
		// 任务二：同步索引库
		wayBillService.syncIndex();
	}

}
