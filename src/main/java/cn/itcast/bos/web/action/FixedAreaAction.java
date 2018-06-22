package cn.itcast.bos.web.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.convention.annotation.Action;
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

import cn.itcast.bos.constant.Constants;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.FixedAreaService;
import cn.itcast.bos.web.action.common.BaseAction;
import cn.itcast.crm.domain.Customer;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixedAreaService;
	@Action(value="fixedArea_save",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String save(){
		fixedAreaService.save(model);
		return SUCCESS;
	}
	@Action(value="fixedArea_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows);
		Specification<FixedArea> specification = new Specification<FixedArea>() {

			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = null;
				List<Predicate> list = new ArrayList<>();
				if(StringUtils.isNoneBlank(model.getId())){
					Predicate p = cb.equal(root.get("id").as(String.class), model.getId());
					list.add(p);
				}
				if(StringUtils.isNotBlank(model.getCompany())){
					Predicate p = cb.like(root.get("company").as(String.class), "%"+model.getCompany()+"%");
					list.add(p);
				}
				if(list!=null && list.size()>0){
					Predicate[] predicates = new Predicate[list.size()];
					predicate = cb.and(list.toArray(predicates));
				}
				return predicate;
			}
		};
		Page<FixedArea> pageData = fixedAreaService.findPageDataByCondition(specification,pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}
	
	@Action(value="fixedArea_findNoAssociationCustomers",results={@Result(name="success",type="json")})
	public String findNoAssociationCustomers(){
		Collection<? extends Customer> collection = WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/findNoAssociationCustomers")
				.accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		pushToValueStack(collection);
		return SUCCESS;
	}
	
	@Action(value="fixedArea_findHasAssociationFixedAreaCustomers",results={@Result(name="success",type="json")})
	public String findHasAssociationFixedAreaCustomers(){
		Collection<? extends Customer> collection = WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/findHasAssociationFixedAreaCustomers/"+model.getId())
				.accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		pushToValueStack(collection);
		return SUCCESS;
	}
	
	private String fixedAreaId;
	public void setFixedAreaId(String fixedAreaId) {
		this.fixedAreaId = fixedAreaId;
	}
	@Action(value="fixedArea_findAssociationCustomers",results={@Result(name="success",type="json")})
	public String findAssociationCustomers(){
		Collection<? extends Customer> dblist = WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/findHasAssociationFixedAreaCustomers/"+fixedAreaId)
				.accept(MediaType.APPLICATION_JSON)
				.getCollection(Customer.class);
		pushToValueStack(dblist);
		return SUCCESS;
	}
	
	private String[] customerIds;
	
	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}
	@Action(value="fixedArea_associationCustomerToFixedArea",results={@Result(name="success",type="redirect",location="pages/base/fixed_area.html")})
	public String associationCustomerToFixedArea(){
		String customerIdStr = StringUtils.join(customerIds,",");
		WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService"						
                + "/associationCustomerToFixedArea?customerId="
				+ customerIdStr + "&fixedAreaId=" + model.getId())
				 .put(null);
		return SUCCESS;
	}
	
	private String courierId;
	private String takeTimeId;
	public void setCourierId(String courierId) {
		this.courierId = courierId;
	}
	public void setTakeTimeId(String takeTimeId) {
		this.takeTimeId = takeTimeId;
	}
	@Action(value="fixedArea_associationCourierToFixedArea",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String associationCourierToFixedArea(){
		fixedAreaService.associationCourierToFixedArea(model,courierId,takeTimeId);
		return SUCCESS;
	}
}
