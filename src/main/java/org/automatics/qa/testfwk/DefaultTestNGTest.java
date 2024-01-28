package org.automatics.qa.testfwk;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.automatics.qa.core.TestDataFactory;
import org.automatics.qa.testfwk.base.BaseClass;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.automatics.qa.testfwk.core.StaticFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.asserts.SoftAssert;
import org.testng.xml.XmlTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.gson.JsonObject;

/**
 * This serves as a default class to be inherited by all sub-classes/Test cases
 * @author gvs
 *
 */
public class DefaultTestNGTest extends BaseClass {
	
	/**
	 * Internal logger object that can be accessed from within the tests for logging messages to the automtaion.log file
	 */
//	protected Logger log = LogManager.getLogger(this.getClass());
	
	/**
	 * <i>softassert</i> provides various methods to compile assertions into a
	 * single instance. <i>softassert.asserEquals(actual, expected)</i> is the most
	 * commonly used method. Always remember to do a <i>softassert.assertAll();</i>
	 * at the end of your test method!
	 * 
	 */
	protected SoftAssert softassert = new SoftAssert();
	
	/**
	 * Internal Reporter object
	 */
	protected ExtentHtmlReporter htmlreporter;
	
	/**
	 * Internal Reports object
	 */
	protected ExtentReports extent;
	
	/**
	 * Internal test object
	 */
	protected ExtentTest test;
	
	/**
	 * Internal Test Data object in Json form
	 */
	protected JsonObject testDataJson;
	
	/**
	 * Internal Test Data object in Map form
	 */
	protected Map<String, String> testDataMap;

	/**
	 * defaultBeforeSuite runs just before every TestNG suite
	 * @param context ITestContext object that can be used to pre-read TestNG fields
	 */
	@BeforeSuite(alwaysRun = true)
	public void defaultBeforeSuite(ITestContext context) {
		log.info("**********Starting Test suite**********");
		RuntimeConfig.init();
		RuntimeConfig.setConfigFromTestNGContext(context);
		
		if(!new File(".\\reports").exists()) {new File(".\\reports").mkdir();}
		
		htmlreporter = new ExtentHtmlReporter(System.getProperty("user.dir") + "\\reports\\" + context.getCurrentXmlTest().getSuite().getName() + ".html");
		extent = new ExtentReports();
		extent.attachReporter(htmlreporter);
		StaticFactory.extentHtmlReporterObject = htmlreporter;
		StaticFactory.extentReportsObject = extent;
	}

	/**
	 * defaultBeforeTest runs just before every TestNG test
	 * @param context ITestContext object that can be used to pre-read TestNG fields
	 */
	@BeforeTest(alwaysRun = true)
	public void defaultBeforeTest(ITestContext context) {
//		RuntimeConfig.init();
		test = StaticFactory.extentReportsObject.createTest(context.getCurrentXmlTest().getName());
		StaticFactory.extentTestObject = test;
	}

	/**
	 * defaultBeforeClass performs the following functions before passing control to the Test method:
	 * 1. Initialize RuntimeConfig from property file
	 * 2. Log the parameters for the current test
	 * 
	 * @param context ITestContext object that can be used to pre-read TestNG fields
	 */
	@BeforeClass(alwaysRun = true)
	public void defaultBeforeClass(ITestContext context) {

		if (RuntimeConfig.getConfig().containsKey(TestProperties.EXEC_LOCAL)) {
			log.info("execlocal property found. reading vaalue..");
			if (Boolean.parseBoolean(RuntimeConfig.getPropertyByName(TestProperties.EXEC_LOCAL))) {
				log.info("execlocal=true found. reading test data based on row.number value..");
				TestDataFactory.init();
				testDataJson = (JsonObject) TestDataFactory.getAllData().get(RuntimeConfig.getPropertyByName(TestProperties.DATA_KEY));
			} else {
				log.info("execlocal=false found. reading test data from context..");
				testDataJson = TestDataFactory.getAllData().get(context.getCurrentXmlTest().getTestParameters().get("dataKey")).getAsJsonObject();
				}
		} else {
			log.info("did not find execlocal property. Assuming remote exec.. reading test data..");
			testDataJson = TestDataFactory.getAllData().get(context.getCurrentXmlTest().getTestParameters().get("dataKey")).getAsJsonObject();
		}

		log.info("test data json= '{}'", testDataJson);
		testDataMap = new HashMap<String, String>();
		for (String tstring : testDataJson.keySet()) {
			log.info("tstring = '{}'; and data is '{}'", tstring, testDataJson.get(tstring).getAsString());
			testDataMap.put(tstring, testDataJson.get(tstring).getAsString());
		}

		log.info("setting test data for curreent test... data='{}'", testDataJson);
		Map<String, String> paramList = context.getCurrentXmlTest().getAllParameters();

		for (Entry<String, String> e : paramList.entrySet()) {
			log.info("Parameter Key={}", e.getKey());
			log.info("Parameter Value={}", e.getValue());
		}

	}

	/**
	 * defaultBeforeMethod runs just before the Test method
	 * @param method Method objecct that can be manipulated before it executes
	 * @param context ITestContext object that can be used to pre-read TestNG fields
	 */
	@BeforeMethod(alwaysRun = true)
	public void defaultBeforeMethod(Method method, ITestContext context) {
		log.trace("Starting method->{}:{}", (Object) method.getDeclaringClass().getName(), (Object) method.getName());
	}

	/**
	 * defaultAfterMethod runs just before the Test method
	 * @param method Method objecct that can be manipulated before it executes
	 * @param context ITestContext object that can be used to pre-read TestNG fields
	 */
	@AfterMethod(alwaysRun = true)
	public void defaultAfterMethod(Method method, ITestContext context, ITestResult result) {
		log.trace("Ending method->{}:{}", (Object) method.getDeclaringClass().getName(), (Object) method.getName());
		
		if(result.getStatus() == ITestResult.FAILURE)
        {
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName()+" Test case FAILED due to below issues:", ExtentColor.RED));
            test.fail(result.getThrowable());
        }
        else if(result.getStatus() == ITestResult.SUCCESS)
        {
            test.log(Status.PASS, MarkupHelper.createLabel(result.getName()+" Test Case PASSED", ExtentColor.GREEN));
        }
        else
        {
            test.log(Status.SKIP, MarkupHelper.createLabel(result.getName()+" Test Case SKIPPED", ExtentColor.ORANGE));
            test.skip(result.getThrowable());
        }
	}


	/**
	 * defaultAfterClass closes the browser and ensures cleanup
	 * @param xmlTest Current Test as an {@link XmlTest} object
	 * @param context current Test's {@link ITestContext} object
	 */
	@AfterClass(alwaysRun = true)
	public void defaultAfterClass(XmlTest xmlTest, ITestContext context) {
		log.info("After class ended '{}'", context.getCurrentXmlTest().getName());
	}

	/**
	 * defaultAfterTest cleans up and logs the test state
	 * 
	 * @param xmlTest Current Test as an {@link XmlTest} object
	 * @param context current Test's {@link ITestContext} object
	 */
	@AfterTest(alwaysRun = true)
	public void defaultAfterTest(XmlTest xmlTest, ITestContext context) {
		log.info("After test ended '{}'", context.getCurrentXmlTest().getName());
	}

	/**
	 * defaultAfterSuite cleans up after each TestNG Suite
	 * @param context ITestContext object that can be used to read TestNG fields
	 */
	@AfterSuite(alwaysRun = true)
	public void defaultAfterSuite(ITestContext context) {
		log.info("**********Ending Test suite**********");
		log.info("*************************************");
		StaticFactory.extentReportsObject.flush();
	}

	/**
	 * Method to read test data values directly from Excel-input
	 * @param key The key whose value you wish to obtain. eg. <i>getFromTestData</i>("username"); to obtain username 
	 * @return The value corresponding to the key provided, null in all other cases 
	 */
	protected String getFromTestData(String key) {
		String result = null;
		try {
			result = testDataMap.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
