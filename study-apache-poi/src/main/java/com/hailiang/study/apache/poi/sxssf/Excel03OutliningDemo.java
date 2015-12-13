package com.hailiang.study.apache.poi.sxssf;

import java.io.FileOutputStream;

import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class Excel03OutliningDemo {

	public static void main(String[] args) throws Exception {
		new Excel03OutliningDemo().collapseRow();
	}

	private void collapseRow() throws Exception {
		SXSSFWorkbook wb2 = new SXSSFWorkbook(100);
		SXSSFSheet sheet2 = (SXSSFSheet) wb2.createSheet("new sheet");

		int rowCount = 20;
		for (int i = 0; i < rowCount; i++) {
			sheet2.createRow(i);
		}

		sheet2.groupRow(4, 9); //4~9行折叠
		sheet2.groupRow(11, 19);//11~19行折叠

		sheet2.setRowGroupCollapsed(4, true); //第4行的，默认折叠
		

		FileOutputStream fileOut = new FileOutputStream("c:/poi/outlining_collapsed.xlsx");
		wb2.write(fileOut);
		fileOut.close();
		wb2.dispose();
		wb2.close();
	}
	
}
