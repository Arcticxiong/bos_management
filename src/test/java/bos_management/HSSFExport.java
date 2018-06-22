package bos_management;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.SubAreaService;
@RunWith(value=SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class HSSFExport {
	@Autowired
	SubAreaService subAreaService;

	@Test
	public void test1() throws FileNotFoundException, IOException{
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("fixedArea");
		 HSSFFont font = workbook.createFont();  
        font.setColor(HSSFColor.VIOLET.index);  
        font.setFontHeightInPoints((short) 12);  
//        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		sheet.setDefaultColumnWidth(12);
		List<SubArea> subAreas = subAreaService.findAll();
		
		HSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("分拣编号");
		row.createCell(1).setCellValue("省");
		row.createCell(2).setCellValue("市");
		row.createCell(3).setCellValue("区");
		row.createCell(4).setCellValue("关键字");
		row.createCell(5).setCellValue("起始号");
		row.createCell(6).setCellValue("终止号");
		row.createCell(7).setCellValue("单双号");
		row.createCell(8).setCellValue("辅助关键字");
		row.createCell(9).setCellValue("定区");
		
		for(int i=0;i<subAreas.size();i++){
			HSSFRow rowData = sheet.createRow(i+1);
			rowData.createCell(0).setCellValue(subAreas.get(i).getId());
			rowData.createCell(1).setCellValue(subAreas.get(i).getArea().getProvince());
			rowData.createCell(2).setCellValue(subAreas.get(i).getArea().getCity());
			rowData.createCell(3).setCellValue(subAreas.get(i).getArea().getDistrict());
			rowData.createCell(4).setCellValue(subAreas.get(i).getKeyWords());
			rowData.createCell(5).setCellValue(subAreas.get(i).getStartNum());
			rowData.createCell(6).setCellValue(subAreas.get(i).getEndNum());
			rowData.createCell(7).setCellValue(subAreas.get(i).getSingle());
			rowData.createCell(8).setCellValue(subAreas.get(i).getAssistKeyWords());
			rowData.createCell(9).setCellValue(subAreas.get(i).getFixedArea().getFixedAreaName());
			
		}
		
		workbook.write(new FileOutputStream("d:\\测试.xls"));
		workbook.close();
	}
}
