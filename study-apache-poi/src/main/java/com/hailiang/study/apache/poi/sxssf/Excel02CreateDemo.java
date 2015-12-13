package com.hailiang.study.apache.poi.sxssf;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class Excel02CreateDemo {

	public static void main(String[] args) throws Throwable {
		// -1表示：关闭自动flush至磁盘，在内存中存放所有的行
		SXSSFWorkbook wb = new SXSSFWorkbook(-1);
		/*
		  SXSSF在flush sheet的数据时，每个sheet都会产生一个临时文件，这个临时文件可以非常大，如，一个20MB的csv文件，
		  产生的临时文件大小会比20MB大的多，因此可以有必要使用gzip压缩技术来压缩临时文件
		 */
		//告知SXSSF在使用flush时，会产生临时文件，可以使用gzip压缩临时文件，如下：
		wb.setCompressTempFiles(true); // 
		
		Sheet sh = wb.createSheet();
		for (int rownum = 0; rownum < 1000; rownum++) {
			Row row = sh.createRow(rownum);
			for (int cellnum = 0; cellnum < 10; cellnum++) {
				Cell cell = row.createCell(cellnum);
				String address = new CellReference(cell).formatAsString();
				cell.setCellValue(address);
			}
			
			// 手动控制row，flush至磁盘
			if (rownum % 100 == 0) {
				((SXSSFSheet) sh).flushRows(100); //内存中保留100行，其他的行将flush至磁盘
				/*((SXSSFSheet) sh).flushRows(); //等同于((SXSSFSheet) sh).flushRows(0)，该方法将所有的行flush至磁盘*/
			}
		}
		
		
		FileOutputStream out = new FileOutputStream("C:/poi/sxssf2.xlsx");
		wb.write(out);
		out.close();

		// 必须使用disponse方法清理临时文件
		wb.dispose();
		wb.close();
	}

}
