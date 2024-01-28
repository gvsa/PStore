package org.automatics.qa.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class makes available Test Data from Excel file in JSON format
 * 
 * @author gvs
 *
 */
public class TestDataFactory {

	/**
	 * internal logger object
	 */
	private static final Logger log = LoggerFactory.getLogger(TestDataFactory.class);

	/**
	 * allData will hold Excel data in a single JSON object each row referenced by
	 * TDx (x is the row number)
	 */
	private static JsonObject allData;

	/**
	 * list of the row jsons
	 */
	private static ArrayList<JsonObject> rowJsonList = new ArrayList<JsonObject>();

	/**
	 * Initialize method
	 */
	public static void init() {

		log.info("file being used=" + RuntimeConfig.getPropertyByName(TestProperties.TEST_DATA_FILEPATH));

//		ExcelWorkbook testDataExcelFile = null;
		XSSFWorkbook testDataExcelFile = null;
		try {
			testDataExcelFile = new XSSFWorkbook(new File(RuntimeConfig.getPropertyByName(TestProperties.TEST_DATA_FILEPATH)));
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Worksheet testDataSheet = testDataExcelFile.worksheets("TestData");
		XSSFSheet testDataSheet = testDataExcelFile.getSheet(RuntimeConfig.getPropertyByName(TestProperties.TEST_SHEET));
//		log.info("found data = " + testDataSheet.getSheetColumnCount(0));


		/**
		 * This code used iterators to convert each row into a JsonObject with TDx as 
		 * reference (x being the row number)
		 */
		
		Iterator<Row> rowParser = testDataSheet.rowIterator();
		allData = new JsonObject();
		int ctr = 1;
		rowParser.next();
		while (rowParser.hasNext()) {
			XSSFRow currentRow = (XSSFRow) rowParser.next();
			Iterator<Cell> cellParser = currentRow.cellIterator();
			JsonObject rowjson = new JsonObject();
			while (cellParser.hasNext()) {
				/**
				 * cellParse.next() has to be cast to XSSFCell before processing
				 */
				XSSFCell currentCell = (XSSFCell) cellParser.next();
				String cvalue = "--" + currentCell.getStringCellValue();
				String data = currentCell.getStringCellValue();
				log.info("data found = {}", data);
				if (!data.trim().equals("")) {
					if (data.contains("=")) {
						String key = data.split("=")[0];
						log.info("Key name=" + key);

						String value = data.substring(data.indexOf('=') + 1);
						log.info("Keyvalue=" + value);

						rowjson.add(key, (new JsonParser()).parse("\"" + value + "\""));
					} // end of adding key value pair as json
					else if (data.endsWith("Test")) {// it's a classname
						log.info("Adding class name: {}", data);
						rowjson.add("TestName", (new JsonParser().parse(data)));
//					rowjson=rowjson.getAsJsonObject(data);
					}
				}
			} // end of cellparser
			log.info("row json = " + rowjson);
			rowJsonList.add(rowjson);
			
			// Each row of test data will be referenced by TD(ctr)
			allData.add("" + ctr, rowjson);
			ctr++;

		} // end of rowparser

		log.info("Total data parsed=" + allData);
		
		if(testDataExcelFile!=null) {
			try {
				testDataExcelFile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Return the test data read from the input file
	 * 
	 * @return JSON implementation of all rows from Excel test data input
	 */
	public static JsonObject getAllData() {
		return allData;
	}
	
	/**
	 * Return the ArrayLists form of the Row Jsons. Tests can be verified by
	 * accessing TestName key of each json
	 * 
	 * @return an ArrayList of JsonObject representation of each td
	 */
	public ArrayList<JsonObject> getRowJsonList() {
		return rowJsonList;
	}
	
}
