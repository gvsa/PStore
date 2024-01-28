package org.automatics.qa.testfwk.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import com.google.common.annotations.Beta;

/**
 * Class is responsible for maintaining and providing runtime-related data to framework/tests on demand
 * @author gvs
 *
 */
public class RuntimeConfig {

	/**
	 * Properties object that stores the configuration defined
	 */
	private static final ThreadLocal<Properties> config = new InheritableThreadLocal<Properties>();

	/**
	 * internal logger object
	 */
	private static final Logger log = LoggerFactory.getLogger(RuntimeConfig.class);

	/**
	 * Initialize the parameters used for runtime from default.properties file
	 */
	public static void init() {
		Properties temporaryPropertyHolder = new Properties();
		try {
			temporaryPropertyHolder.load(
					new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/default.properties"));
			log.info("Environment = " + temporaryPropertyHolder.getProperty("env"));
		} catch (FileNotFoundException e) {
			log.warn("default.properties file not found in resources folder. Trying to locate in parent folder..");
			try {
				temporaryPropertyHolder
						.load(new FileInputStream(System.getProperty("user.dir") + "/default.properties"));
				log.info("Environment = " + temporaryPropertyHolder.getProperty("env"));
			} catch (IOException e1) {
				log.error("Error - cannot find default.properties file in parent folder.");
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			log.error("error reading default.properties file! File should be in the same folder as jar file.");
			e.printStackTrace();
		}

		config.set(temporaryPropertyHolder);
	}

	/**
	 * Returns the configuration object
	 * 
	 * @return Properties object containing configuration
	 */
	public static Properties getConfig() {
		return config.get();
	}

	/**
	 * Sets current configuration to the Properties parmeter passes
	 * 
	 * @param newConfig Properties object containing new configuration to use
	 */
	public static void setConfig(Properties newConfig) {
		config.set(newConfig);
	}

	/**
	 * returns the property value given it's name
	 * 
	 * @param propName Property name
	 * @return String representation of the value
	 */
	public static String getPropertyByName(String propName) {
		String returnVal = RuntimeConfig.getConfig().getProperty(propName);
		if (returnVal.isEmpty()) {
			log.error("Pref value requested not found: {}", (Object) propName);
		}
		return returnVal;
	}

	/**
	 * Set TestNG context for future use
	 * 
	 * @param context TestNG context
	 */
	@Beta
	public static void setConfigFromTestNGContext(ITestContext context) {
		// TODO Auto-generated method stub
		log.trace("Setting config for thread: {}", (Object) Thread.currentThread().getId());

	}

}
