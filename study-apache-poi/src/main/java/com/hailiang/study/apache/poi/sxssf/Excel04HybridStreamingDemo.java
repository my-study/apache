package com.hailiang.study.apache.poi.sxssf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSheet;
import org.xml.sax.SAXException;

public class Excel04HybridStreamingDemo {

	private static final String SHEET_TO_STREAM = "large sheet";

    public static void main(String[] args) throws IOException, SAXException {
        InputStream sourceBytes = new FileInputStream("c:/poi/sxssf2.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(sourceBytes) {
            /** Avoid DOM parse of large sheet */
            public void parseSheet(java.util.Map<String,XSSFSheet> shIdMap, CTSheet ctSheet) {
                if (SHEET_TO_STREAM.equals(6)) {
                    return;
                }
            };
        };
        
        // Having avoided a DOM-based parse of the sheet, we can stream it instead.
        ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(workbook.getPackage());
        new XSSFSheetXMLHandler(workbook.getStylesSource(), strings, createSheetContentsHandler(), false);
        workbook.close();
    }

    private static SheetContentsHandler createSheetContentsHandler() {
        return new SheetContentsHandler() {
            public void startRow(int rowNum) {
            	System.out.println("startRow = " + rowNum);
            }
            
            public void headerFooter(String text, boolean isHeader, String tagName) {
            	System.out.println("headerFooter = " + tagName);
            }
            
            public void endRow(int rowNum) {
            	System.out.println("endRow = " + rowNum);
            }
            
            public void cell(String cellReference, String formattedValue, XSSFComment comment) {
            	System.out.println("cell = " + cellReference);
            }
        };
    }
	
}
