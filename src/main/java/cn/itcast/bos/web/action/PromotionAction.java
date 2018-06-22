package cn.itcast.bos.web.action;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.PromotionService;
import cn.itcast.bos.web.action.common.BaseAction;

@ParentPackage(value="json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {

	@Autowired
	private PromotionService promotionService;
	
	private File titleImgFile;
	private String titleImgFileFileName;
	private String titleImgFileContentType;
	public void setTitleImgFile(File titleImgFile) {
		this.titleImgFile = titleImgFile;
	}
	public void setTitleImgFileFileName(String titleImgFileFileName) {
		this.titleImgFileFileName = titleImgFileFileName;
	}
	public void setTitleImgFileContentType(String titleImgFileContentType) {
		this.titleImgFileContentType = titleImgFileContentType;
	}
	
	@Action(value="promotion_save",results={@Result(name="success",type="redirect",
			location="./pages/take_delivery/promotion.html")})
	public String save() throws IOException{
		//绝对路径（用于上传）
		String savePath = ServletActionContext.getServletContext().getRealPath("/upload/");
		//相对路径（用于保存数据库）
		String saveUrl = ServletActionContext.getRequest().getContextPath()+"/upload/";
		
		//生成随机图片名
		UUID uuid = UUID.randomUUID();
		String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
		String randomFileName = uuid+ext;
		
		//保存图片
		File destFile = new File(savePath+"/"+randomFileName);
		System.out.println(destFile.getAbsolutePath());
		FileUtils.copyFile(titleImgFile, destFile);
		
		model.setTitleImg(saveUrl+randomFileName);
		
		promotionService.save(model);
		return SUCCESS;
	}
	
	@Action(value="promotion_pageQuery",results={@Result(name="success",type="json")})
	public String pageQuery(){
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Promotion> pageData = promotionService.findPageData(pageable);
		pushPageDataToValueStack(pageData);
		return SUCCESS;
	}
}
