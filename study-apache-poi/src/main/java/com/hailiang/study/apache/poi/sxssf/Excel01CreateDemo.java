package com.hailiang.study.apache.poi.sxssf;

import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;

public class Excel01CreateDemo {

	public static void main(String[] args) throws Throwable {
		// SXSSFWorkbook.DEFAULT_WINDOW_SIZE 的值为 100，
		// 此处表示，保持100行数据在内存中，超过的行，将写入到磁盘中
		SXSSFWorkbook wb = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
		
		/*
		  SXSSF在flush sheet的数据时，每个sheet都会产生一个临时文件，这个临时文件可以非常大，如，一个20MB的csv文件，
		  产生的临时文件大小会比20MB大的多，因此可以有必要使用gzip压缩技术来压缩临时文件
		 */
		//告知SXSSF在使用flush时，会产生临时文件，可以使用gzip压缩临时文件，如下：
		wb.setCompressTempFiles(true);
		
		Sheet sh = wb.createSheet();
		for (int rownum = 0; rownum < 1000; rownum++) {
			Row row = sh.createRow(rownum);
			for (int cellnum = 0; cellnum < 10; cellnum++) {
				Cell cell = row.createCell(cellnum);
				String address = new CellReference(cell).formatAsString();
				cell.setCellValue(address);
			}
		}

		// 因为SXSSF采用stream的方式读写数据，设置的window size为100（即SXSSFWorkbook.DEFAULT_WINDOW_SIZE），
		// 故内存中值保存100条行数据
		for (int rownum = 0; rownum < 900; rownum++) {
			Assert.assertNull(sh.getRow(rownum));
		}

		// 最后100行，然后在内存中
		for (int rownum = 900; rownum < 1000; rownum++) {
			Assert.assertNotNull(sh.getRow(rownum));
		}

		FileOutputStream out = new FileOutputStream("C:/poi/sxssf.xlsx");
		wb.write(out);
		out.close();

		// 必须使用disponse方法清理临时文件
		wb.dispose();
		wb.close();
	}

}
