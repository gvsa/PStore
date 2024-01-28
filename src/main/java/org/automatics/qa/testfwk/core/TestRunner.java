package org.automatics.qa.testfwk.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.automatics.qa.core.TestDataFactory;
import org.automatics.qa.testfwk.base.TestProperties;
import org.automatics.qa.testfwk.config.RuntimeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.gson.JsonObject;

/**
 * Custom TestRunner class implementation
 * @author gvs
 *
 */
public class TestRunner {
	/**
	 * internal logger object
	 */
	private static final Logger log = LoggerFactory.getLogger(TestRunner.class);

	/**
	 * This main method parses the properties and Excel files and executes listed tests via TestNG
	 * @param args no arguments expected
	 */
	public static void main(String args[]) {
		
		int index = 1;
		
//		log.info("Running = " + args[0].trim());
		
		System.setProperty("org.uncommons.reportng.escape-output", "false");
        
		// read default.properties and load the properties in a usable class
		RuntimeConfig.init();

		TestDataFactory.init();
		//tdf = new TestDataFactory();
		// read the test data from the Excel location specified in default.properties
//		tdf.init();
		
		// store all test-data-in-json-format to parse for execution
		JsonObject tdata = TestDataFactory.getAllData();
		log.info("All Test Data = "  + tdata);
		
		log.info("first = " + tdata.get("1"));
		
		Set<String> keys = tdata.keySet();
		
		// Create object of TestNG Class
		TestNG runner = new TestNG();
		List<XmlSuite> suites = new ArrayList<XmlSuite>();
		XmlSuite testSuite = new XmlSuite();
		testSuite.setName(RuntimeConfig.getPropertyByName(TestProperties.REPORT_FILE_NAME));
		List<XmlTest> testsList = new ArrayList<XmlTest>();
		
		// Iterate over all TD (TestData) keys and create a suite for each entry
		for(String singleStr: keys) {
			
			// the Test has to be created with a reference to the suite it will be added to
			XmlTest oneTest = new XmlTest(testSuite);
			log.info("current key = {}",singleStr);
			
			String testName= tdata.get(singleStr).getAsJsonObject().get("TestName").getAsString().replaceAll("\"", "");
			
			// set unique name for each test suite by adding millisecond count to the name
			oneTest.setName(testName + index++);
			
			log.info("Test Name found = {}", testName);
//#gvs			oneTest.setName(testName);
			
			//V1.1.0 - add a single test parameter called testData
//			oneTest.addParameter("testData", tdata.getAsJsonObject(singleStr).toString());
			oneTest.addParameter("dataKey", singleStr);
			
			// V1.0.0 - parse the remainder of the row json and add the test data as parameters
/*			for(String testKey:tdata.get(singleStr).getAsJsonObject().keySet()) {
				oneTest.addParameter(testKey, tdata.get(singleStr).getAsJsonObject().get(testKey).getAsString());
				log.info(testKey + " value=" + oneTest.getParameter(testKey));
			}
*/			
			List<XmlClass> testclasses = new ArrayList<XmlClass>();
			
			// we are assuming the package naming is constant
			testclasses.add(new XmlClass("org.automatics.qa.tests." +  testName));
			oneTest.setXmlClasses(testclasses);
			
			log.info("Test XML = " + oneTest.toXml(" "));
			
			testsList.add(oneTest);
			
//			oneTest=null;
		}

		testSuite.setTests(testsList);
		
		// parallel execution is not supported as of V1
		testSuite.setParallel(XmlSuite.ParallelMode.NONE);
//		testSuite.setParallel(XmlSuite.ParallelMode.FALSE);

		/*
		List<String> listeners = new ArrayList<String>();
		
		listeners.add("org.uncommons.reportng.HTMLReporter");
		listeners.add("org.uncommons.reportng.JUnitXMLReporter");
		
		testSuite.setListeners(listeners);
		*/
		
		log.info("Suite XML = " + testSuite.toXml());
		suites.add(testSuite);
		runner.setXmlSuites(suites);
		
		// run the test suites
		runner.run();
		
		
	}
}
