package cn.itcast.bos.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
import cn.itcast.bos.service.AreaService;
import cn.itcast.bos.utils.PinYin4jUtils;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class AreaAction extends BaseAction<Area> {

	@Autowired
	private AreaService areaService;
	
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
	
	//添加区域功能
	@Action(value="area_save",results={@Result(name="success",type="redirect",location="pages/base/area.html")})
	public String save(){
		areaService.save(model);
		return SUCCESS;
	}
	
	//删除
	private String ids;
	public void setIds(String ids) {
		this.ids = ids;
	}
	@Action(value="area_deleteArea",results={@Result(name="success",type="redirect",location="pages/base/area.html")})
	public String deleteArea(){
//		String[] idArray = ids.split(",");
		areaService.delete(ids);
		return SUCCESS;
	}
	
	//分页查询
	@Action(value="area_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows);
		Specification<Area> specification = new Specification<Area>() {

			@Override
			public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//				Predicate predicate = null;
				List<Predicate> list = new ArrayList<>();
				if(StringUtils.isNotBlank(model.getProvince())){
					Predicate p = cb.like(root.get("province").as(String.class),"%"+model.getProvince()+"%");
					list.add(p);
				}
				if(StringUtils.isNotBlank(model.getCity())){
					Predicate p = cb.like(root.get("city").as(String.class),"%"+model.getCity()+"%");
					list.add(p);
				}
				if(StringUtils.isNotBlank(model.getDistrict())){
					Predicate p = cb.like(root.get("district").as(String.class),"%"+model.getDistrict()+"%");
					list.add(p);
				}
				if(list!=null && list.size()>0){
					Predicate[] p = new Predicate[list.size()];
//					predicate=cb.and(list.toArray(p));
					query.where(list.toArray(p));
				}
//				return predicate;
				query.orderBy(cb.desc(root.get("id").as(String.class)));
				return query.getRestriction();
			}
		};
		Page<Area> pageData = areaService.findPageDataByCondition(specification,pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}
	
	
	//导入
	@Action(value="area_batchImport",results={@Result(name="success",type="json")})
	public String batchImport() throws Exception{
		//解析workbook（工作薄）---> sheet（工作表）---> row（行）---> cell（列/单元格） 
		List<Area> list = new ArrayList<>();
		// 编写解析代码逻辑
		// 基于.xls 格式解析 HSSF
		// 1、 加载Excel文件对象
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
		//2.读取一个sheet
		HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
		//3.读取sheet中每一行，一行数据对应一个区域对象
		for (Row row : sheet) {
			//跳过头行
			if(row.getRowNum()==0){
				continue;
			}
			//跳过空值的行，第一个单元格为主键，主键为空的话，要求此行作废
			if(row.getCell(0)==null || StringUtils.isBlank(row.getCell(0).getStringCellValue())){
				continue;
			}
			
			Area area = new Area();
			area.setId(row.getCell(0).getStringCellValue());//区域编号
			area.setProvince(row.getCell(1).getStringCellValue());//省
			area.setCity(row.getCell(2).getStringCellValue());//市
			area.setDistrict(row.getCell(3).getStringCellValue());//区
			area.setPostcode(row.getCell(4).getStringCellValue());//邮编
			//基于pinyin4j生成城市编码和简码
			String province = area.getProvince();
			String city = area.getCity();
			String district = area.getDistrict();
			
			province = province.substring(0, province.length()-1);//去掉省
			city = city.substring(0, city.length()-1);//去掉市
			district = district.substring(0, district.length()-1);//去掉区
			
			//简码
			//获得省市区 首字母数组
			String[] headArray = PinYin4jUtils.getHeadByString(province+city+district);
			StringBuffer sb = new StringBuffer();
			for (String head : headArray) {
				sb.append(head);
			}
			String shortcode=sb.toString();
			area.setShortcode(shortcode);
			//城市编码
			String citycode = PinYin4jUtils.hanziToPinyin(city, "");
			area.setCitycode(citycode);
			list.add(area);
		}
		areaService.saveAreas(list);
		return SUCCESS;
	}
}
