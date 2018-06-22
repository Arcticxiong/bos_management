package cn.itcast.bos.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
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

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.SubAreaService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class SubAreaAction extends BaseAction<SubArea> {

	@Autowired
	private SubAreaService subAreaService;
	
	private File file;
	private String fileFileName;
	private String fileContentType;
	public void setFile(File file) {
		this.file = file;
	}
	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}
	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	
	@Action(value = "subArea_save",results = {@Result(name = "success",type = "redirect",location = "./pages/base/sub_area.html")})
    public String save(){
        subAreaService.save(model);
        return SUCCESS;
    }
	
	@Action(value="subArea_batchImport",results={@Result(name="success",type="redirect",location="pages/base/sub_area.html")})
	public String batchImport() throws Exception{
		List<SubArea> list = new ArrayList<>();
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		for (Row row : sheet) {
			if(row.getRowNum()==0){
				continue;
			}
			if(row.getCell(0)!=null && StringUtils.isBlank(row.getCell(0).getStringCellValue())){
				continue;
			}
			SubArea subArea = new SubArea();
			subArea.setId(row.getCell(0).getStringCellValue());
			FixedArea fixedArea = new FixedArea();
			fixedArea.setId(row.getCell(1).getStringCellValue());
			subArea.setFixedArea(fixedArea);
			Area area = new Area();
			area.setId(row.getCell(2).getStringCellValue());
			subArea.setArea(area);
			subArea.setKeyWords(row.getCell(3).getStringCellValue());
			subArea.setStartNum(row.getCell(4).getStringCellValue());
			subArea.setEndNum(row.getCell(5).getStringCellValue());
			subArea.setSingle(row.getCell(6).getStringCellValue().charAt(0));
			subArea.setAssistKeyWords(row.getCell(7).getStringCellValue());
			list.add(subArea);
		}
		subAreaService.save(list);
		return SUCCESS;
	}
	
	@Action(value="subArea_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows);
		Specification<SubArea> specification = new Specification<SubArea>() {

			@Override
			public Predicate toPredicate(Root<SubArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicates = new ArrayList<>();
				Join<SubArea, Area> join = root.join("area",JoinType.INNER);
				if(model.getArea()!=null && StringUtils.isNotBlank(model.getArea().getProvince())){
					Predicate p = cb.like(join.get("province").as(String.class), "%"+model.getArea().getProvince()+"%");
					predicates.add(p);
				}
				if(model.getArea()!=null && StringUtils.isNotBlank(model.getArea().getCity())){
					Predicate p = cb.like(join.get("city").as(String.class), "%"+model.getArea().getCity()+"%");
					predicates.add(p);
				}
				if(model.getArea()!=null && StringUtils.isNotBlank(model.getArea().getDistrict())){
					Predicate p = cb.like(join.get("district").as(String.class), "%"+model.getArea().getDistrict()+"%");
					predicates.add(p);
				}
				Join<SubArea, FixedArea> join2 = root.join("fixedArea",JoinType.LEFT);
				if(model.getFixedArea()!=null && StringUtils.isNotBlank(model.getFixedArea().getId())){
                    Predicate p = cb.equal(join2.get("id").as(String.class),model.getFixedArea().getId());
                    predicates.add(p);
                }
                if(StringUtils.isNotBlank(model.getKeyWords())){
                    Predicate p = cb.like(root.get("keyWords").as(String.class),"%"+model.getKeyWords()+"%");
                    predicates.add(p);
                }
				if(predicates!=null && predicates.size()>0){
					Predicate[] p = new Predicate[predicates.size()];
					query.where(predicates.toArray(p));
				}
				return query.getRestriction();
			}
		};
		Page<SubArea> pageData = subAreaService.findPageDataByCondition(specification,pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}

	private String fixedAreaId;
	public void setFixedAreaId(String fixedAreaId) {
		this.fixedAreaId = fixedAreaId;
	}
	@Action(value="subArea_findAssociationSubArea",results={@Result(name="success",type="json")})
	public String findAssociationSubArea(){
		List<SubArea> list = subAreaService.findAssociationSubArea(fixedAreaId);
		pushToValueStack(list);
		return SUCCESS;
	}
}
