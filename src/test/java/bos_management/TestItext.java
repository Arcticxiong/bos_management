package bos_management;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class TestItext {

	public static void main(String[] args) throws Exception{
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
		document.open();
		BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		document.add(new Paragraph("helloworld!你好，世界！",new Font(bfChinese)));
		document.close();
	}
}
