package org.automatics.qa.testfwk.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.google.common.annotations.Beta;
import com.google.gson.JsonElement;

/**
 * This class holds together all test-related objects statically for ready
 * reference
 * 
 * @author gvs
 *
 */

@Beta
public class StaticFactory {

	/**
	 * {@link ExtentHtmlReporter} object
	 */
	public static ExtentHtmlReporter extentHtmlReporterObject = null;

	/**
	 * {@link ExtentReports} object
	 */
	public static ExtentReports extentReportsObject = null;

	/**
	 * {@link ExtentTest} object
	 */
	public static ExtentTest extentTestObject = null;

	/**
	 * private logger
	 */
	private static final Logger log = LoggerFactory.getLogger(StaticFactory.class);
	
	/**
	 * Default constructor
	 */
	public StaticFactory() {
		log.info("StaticFactory initialized.");
	}
	
	/**
	 * Statically hold current pet's json data to be re-used by a future test
	 */
	public static JsonElement petInfo = null;

}
