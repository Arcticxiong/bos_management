package cn.itcast.bos.web.action;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.CourierService;
import cn.itcast.bos.web.action.common.BaseAction;
@ParentPackage("json-default")
@Namespace("/")
@Actions
@Controller
@Scope("prototype")
public class CourierAction extends BaseAction<Courier>{

	@Autowired
	private CourierService courierService;


	@Action(value="courier_save",results={
			@Result(name="success",type="redirect",location="./pages/base/courier.html"),
			@Result(name="error",type="redirect",location="./unauthorized.html")})
	public String save(){
		try {
			courierService.save(model);
		} catch (Exception e) {
//			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	//分页查询
	@Action(value="courier_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		//调用业务层，查询数据结果
		Pageable pageable = new PageRequest(page-1, rows);//pageRequest的页码从0开始，所以需要-1
		//无查询条件的分页查询
		//			Page<Courier> pageData = courierService.findPageData(pageable);

		//带条件的分页查询
		//定义Specification
		Specification<Courier> specification = new Specification<Courier>() {
			/**
			 * 传递：
			 * 		Root<Courier> root：（连接语句的时候需要字段，获取字段的名称）代表Criteria查询的根对象，Criteria查询的查询根定义了实体类型，能为将来导航获得想要的结果，它与SQL查询中的FROM子句类似
			 * 		CriteriaQuery<?> query： （简单的查询可以使用CriteriaQuery）代表一个specific的顶层查询对象，它包含着查询的各个部分，比如：select 、from、where、group by、order by等
			 * 		CriteriaBuilder cb：（复杂的查询可以使用CriteriaBuilder构建）用来构建CritiaQuery的构建器对象
			 * 返回：Predicate：封装查询条件
			 * 
			 */
			@Override
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = null;
				//两张表的联合查询（快递员表和收派标准表）
				//list用来封装多个查询条件
				List<Predicate> list = new ArrayList<>();
				//快递员工号
				if(StringUtils.isNotBlank(model.getCourierNum())){
					//相当于courierNum = s001
					Predicate p = cb.equal(root.get("courierNum").as(String.class), model.getCourierNum());
					list.add(p);
				}
				//所属单位
				if(StringUtils.isNotBlank(model.getCompany())){
					//相当于company like %中粮%
					Predicate p = cb.like(root.get("company").as(String.class), "%"+model.getCompany()+"%");
					list.add(p);
				}
				//快递员类型
				if(StringUtils.isNotBlank(model.getType())){
					//相当于type like %小件员%
					Predicate p = cb.like(root.get("type").as(String.class), "%"+model.getType()+"%");
					list.add(p);
				}
				//收派标准（夺表）
				//指定连接， 相当于inner join t_courier c on t_standard s
				Join<Courier, Standard> join = root.join("standard", JoinType.INNER);
				if(model.getStandard()!=null && model.getStandard().getId()!=null){
					Predicate p = cb.equal(join.get("id").as(Integer.class), model.getStandard().getId());
					list.add(p);
				}
				//构建条件组合
				if(list!=null && list.size()>0){
					//构建一个数组 ，长度为条件的长度
					Predicate p[] = new Predicate[list.size()];
					//将查询条件从集合中的值转换成数组中的值
					predicate = cb.and(list.toArray(p));
				}
				return predicate;
			}
		};

		Page<Courier> pageData = courierService.findPageQueryCourierByCondition(specification,pageable);

		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

	private String ids;
	public void setIds(String ids) {
		this.ids = ids;
	}
	@Action(value="courier_deleteCourier",results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String deleteCourier(){
		String[] idArray = ids.split(",");
		courierService.delBatch(idArray);
		return SUCCESS;
	}
	@Action(value="courier_restoreCourier",results = { @Result(name = "success", location = "./pages/base/courier.html", type = "redirect") })
	public String restoreCourier(){
		String[] idArray = ids.split(",");
		courierService.restore(idArray);
		return SUCCESS;
	}
	
	@Action(value="courier_findnoassociation",results={@Result(name="success",type="json")})
	public String findnoassociation(){
		List<Courier> list = courierService.findnoassociation();
		pushToValueStack(list);
		return SUCCESS;
	}
	
	private String fixedAreaId;
	public void setFixedAreaId(String fixedAreaId) {
		this.fixedAreaId = fixedAreaId;
	}
	
	@Action(value="courier_findAssociationCourier",results={@Result(name="success",type="json")})
	public String findAssociationCourier(){
		List<Courier> list = courierService.findAssociationCourier(fixedAreaId);
		pushToValueStack(list);
		return SUCCESS;
	}
}
