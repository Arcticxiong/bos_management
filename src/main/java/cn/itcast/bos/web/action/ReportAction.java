package cn.itcast.bos.web.action;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.sql.DataSource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.highchart.HighChart;
import cn.itcast.bos.service.WayBillService;
import cn.itcast.bos.utils.FileUtils;
import cn.itcast.bos.web.action.common.BaseAction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;

@ParentPackage("json-default")
@Namespace("/")
@Controller
@Scope("prototype")
public class ReportAction extends BaseAction<WayBill>{

	@Autowired
	private WayBillService wayBillService;
	
	//导出excel
	@Action("report_exportXls")
	public String exportXls() throws Exception{
		//查询出满足当前条件的数据
		List<WayBill> wayBills = wayBillService.findWayBills(model);
		//生成excel文件
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		//创建sheet
		HSSFSheet sheet = hssfWorkbook.createSheet();
		//创建表头
		HSSFRow headRow = sheet.createRow(0);
		
		/**创建一个样式对象*/
		HSSFCellStyle cellStyle= hssfWorkbook.createCellStyle();
		/**标题设置成为粗体*/
		HSSFFont font = hssfWorkbook.createFont(); 
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); 
		/**标题设置字体颜色为红色*/
		short color = HSSFColor.RED.index;
		font.setColor(color);/**设置字体颜色为红色*/
		cellStyle.setFont(font);/**将font对象赋给样式*/
		/**设置列的宽度*/
		for(int i=0;i<7;i++){
			sheet.setColumnWidth(i,6000);
		}
		HSSFCell createCell0 = headRow.createCell(0);
		HSSFCell createCell1 = headRow.createCell(1);
		HSSFCell createCell2 = headRow.createCell(2);
		HSSFCell createCell3 = headRow.createCell(3);
		HSSFCell createCell4 = headRow.createCell(4);
		HSSFCell createCell5 = headRow.createCell(5);
		HSSFCell createCell6 = headRow.createCell(6);
		
		createCell0.setCellStyle(cellStyle);
		createCell1.setCellStyle(cellStyle);
		createCell2.setCellStyle(cellStyle);
		createCell3.setCellStyle(cellStyle);
		createCell4.setCellStyle(cellStyle);
		createCell5.setCellStyle(cellStyle);
		createCell6.setCellStyle(cellStyle);
		
		createCell0.setCellValue("运单号");
		createCell1.setCellValue("寄件人");
		createCell2.setCellValue("寄件人电话");
		createCell3.setCellValue("寄件人地址");
		createCell4.setCellValue("收件人");
		createCell5.setCellValue("收件人电话");
		createCell6.setCellValue("收件人地址");
		//表格数据
		for(WayBill wayBill : wayBills){
			// 设置Sheet中最后一行的行号+1，或者for循环的时候用索引for(int i=0;i<wayBills.size();i++)，用i的形式创建行号
			HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum()+1);
			dataRow.createCell(0).setCellValue(wayBill.getWayBillNum());
			dataRow.createCell(1).setCellValue(wayBill.getSendName());
			dataRow.createCell(2).setCellValue(wayBill.getSendMobile());
			dataRow.createCell(3).setCellValue(wayBill.getSendAddress());
			dataRow.createCell(4).setCellValue(wayBill.getRecName());
			dataRow.createCell(5).setCellValue(wayBill.getRecMobile());
			dataRow.createCell(6).setCellValue(wayBill.getRecAddress());
		}
		//下载导出(两头一流)
		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");//一头
		String filename = "运单数据.xls";
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment;filename="+filename);//二头
		
		//将excel文件写到输入流（一流）
		ServletOutputStream outputStream = ServletActionContext.getResponse().getOutputStream();
		hssfWorkbook.write(outputStream);
		//关闭
		hssfWorkbook.close();
		
		return NONE;
	}
	
	//导出pdf
	@Action("report_exportPdf")
	public String exportPdf() throws Exception{
		// 查询出 满足当前条件 结果数据
		List<WayBill> wayBills = wayBillService.findWayBills(model);
		
		//下载导出
		// 设置头信息
		ServletActionContext.getResponse().setContentType("application/pdf");
		String filename = "运单数据.pdf";
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		// 设置以附件的形式导出
		ServletActionContext.getResponse().setHeader("Content-Disposition",
				"attachment;filename=" + filename);
		//生成pdf文件
		Document document = new Document();
		PdfWriter.getInstance(document, ServletActionContext.getResponse().getOutputStream());
		document.open();
		//写pdf数据
		// 向document 生成pdf表格
		Table table = new Table(7);//创建7列的表格
		table.setWidth(80); // 宽度
		table.setBorder(1); // 边框
		table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); // 水平对齐方式
		table.getDefaultCell().setVerticalAlignment(Element.ALIGN_TOP); // 垂直对齐方式
		/**设置表格属性*/
		table.setBorderColor(new Color(0, 0, 255)); //将边框的颜色设置为蓝色
	    table.setPadding(5);//设置表格与字体间的间距
	    //table.setSpacing(5);//设置表格上下的间距
		table.setAlignment(Element.ALIGN_CENTER);//设置字体显示居中样式

		// 设置表格字体
		BaseFont cn = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H",false);
		Font font = new Font(cn, 10, Font.NORMAL, Color.BLUE);

		// 写表头
		table.addCell(buildCell("运单号", font));
		table.addCell(buildCell("寄件人", font));
		table.addCell(buildCell("寄件人电话", font));
		table.addCell(buildCell("寄件人地址", font));
		table.addCell(buildCell("收件人", font));
		table.addCell(buildCell("收件人电话", font));
		table.addCell(buildCell("收件人地址", font));
		// 写数据
		for (WayBill wayBill : wayBills) {
			table.addCell(buildCell(wayBill.getWayBillNum(), font));
			table.addCell(buildCell(wayBill.getSendName(), font));
			table.addCell(buildCell(wayBill.getSendMobile(), font));
			table.addCell(buildCell(wayBill.getSendAddress(), font));
			table.addCell(buildCell(wayBill.getRecName(), font));
			table.addCell(buildCell(wayBill.getRecMobile(), font));
			table.addCell(buildCell(wayBill.getRecAddress(), font));
		}
		// 将表格加入文档
		document.add(table);
		document.close();
		return NONE;
	}
	private Cell buildCell(String content, Font font)
			throws BadElementException {
		Phrase phrase = new Phrase(content, font);
		return new Cell(phrase);
	}
//	@Autowired
//	private DataSource dataSource;
	
	@Action("report_exportJasperPdf")
	public String exportJasperPdf() throws IOException, DocumentException,
			JRException, SQLException {
		// 查询出 满足当前条件 结果数据
		List<WayBill> wayBills = wayBillService.findWayBills(model);

		// 下载导出
		// 设置头信息（两头）
		ServletActionContext.getResponse().setContentType("application/pdf");
		String filename = "运单数据jasper.pdf";
		String agent = ServletActionContext.getRequest().getHeader("user-agent");
		filename = FileUtils.encodeDownloadFilename(filename, agent);
		ServletActionContext.getResponse().setHeader("Content-Disposition","attachment;filename=" + filename);

		// 根据 jasperReport模板 生成pdf
		// 读取jasper模板文件
		String jrxml = ServletActionContext.getServletContext().getRealPath("/WEB-INF/jasper/waybillcondition.jrxml");
		JasperReport report = JasperCompileManager.compileReport(jrxml);

		// 设置模板数据
		// Parameter变量
		Map<String, Object> paramerters = new HashMap<String, Object>();
		paramerters.put("company", "传智播客-黑马程序员");
		// Field变量
		JasperPrint jasperPrint = JasperFillManager.fillReport(report,paramerters,new JRBeanCollectionDataSource(wayBills));
		// 使用JRPdfExproter导出器导出pdf
		JRPdfExporter exporter = new JRPdfExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM,
				ServletActionContext.getResponse().getOutputStream());//一流
		exporter.exportReport();// 导出
		ServletActionContext.getResponse().getOutputStream().close();//关闭流
		return NONE;
	}

	@Action(value="report_highcharts",results={@Result(name="success",type="json")})
	public String highcharts(){
		//查询数据库，返回List<Object[]>
		List<Object[]> list = wayBillService.chartWayBill();
		List<HighChart> charList = new ArrayList<>();
		for(int i=0;i<list.size();i++){
			HighChart highChart = new HighChart();
			highChart.setData((Long)list.get(i)[0]);
			highChart.setName(list.get(i)[1].toString());
			charList.add(highChart);
		}
		pushToValueStack(charList);
		return SUCCESS;
	}
}
