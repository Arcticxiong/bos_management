package cn.itcast.bos.service;

import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import cn.itcast.bos.domain.page.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;

public interface PromotionService {

	void save(Promotion model);
	
	Page<Promotion> findPageData(Pageable pageable);

	@Path("/findPageData")
	@GET
	@Produces({"application/xml","application/json"})
	PageBean<Promotion> findPageData(@QueryParam("page")Integer page,@QueryParam("rows")Integer rows);
	
	@Path("/findById/{id}")
	@GET
	@Consumes({"application/xml","application/json"})
	@Produces({"application/xml","application/json"})
	Promotion findById(@PathParam("id")Integer id);

	void updateStatus(Date date);
	
	
}
