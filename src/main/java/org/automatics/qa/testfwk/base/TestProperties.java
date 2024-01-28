package org.automatics.qa.testfwk.base;

/**
 * This contains all values that must be set via default.properties file during
 * execution
 * 
 * @author gvs
 *
 */
public abstract class TestProperties {

	/**
	 * variable to point which Environment test is running on
	 */
	public static final String ENV = "env";

	/**
	 * variable to hold the escaped path to the test-data Excel file
	 */
	public static final String TEST_DATA_FILEPATH = "test.datafile";

	/**
	 * Test Sheet name from the Excel file
	 */
	public static final String TEST_SHEET = "test.sheet";

	/**
	 * Report File Name
	 */
	public static final String REPORT_FILE_NAME = "report.filename";
	
	/**
	 * Boolean Local Execution flag
	 */
	public static final String EXEC_LOCAL = "execlocal";
	
	/**
	 * DataKey to be used during local executions
	 */
	public static final String DATA_KEY = "row.number";
	
	/**
	 * Base Url
	 */
	public static final String BASE_URL = "baseurl";

}
