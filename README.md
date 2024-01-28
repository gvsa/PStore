# Pet Store Automation Framework

Pre-requisites:
- IDE with JDK8, Maven 3+ installed (Eclipse Photon IDe recommended)

Project Structure:
- The framework consists of the following packages under the parent package - <b>org.automatics.qa</b>
  - testfwk.base - contains the BaseClass and TestProperties class for proper functioning of framework at runtime
  - testfwk.config - contains RunTimeConfig class that helps read parameters passed via the <b>default.properties</b> file
  - testfwk.core - contains the TestRunner and additional classes to handle TestData during execution
  - testfwk - contains the DefaultTestNGTest class which abstracts the pre- and post- test steps from the test code
  - tests - this package contains all tests, naming convention - class name should end with ..Test.java

Resources:
- the resouces folder contains the log4j configuration file (log4j2.xml) and default.properties file (runtime configuration) to be used while executing via IDE

How to execute:
Method 1 (via command line):
- build the project using <b>mvn clean install</b> command
- copy the target/PStore-24.1.26.jar to a folderof your choice, along with the <b>src/main/resources/default.properties</b> and <b>src/main/resources/automation-test-data.xlsx</b> (file name and path can be configured in the properties file)
- execute via command line using <b> java -jar PStore-24.1.26.jar </b>

Method 2 (via IDE):
- Right click the testfwk/TestRunner.java file and Run as a Java Application

Test Results and logs:
- Results will be available in the following 2 locations:
  - test-output directory
  - reports/TEST_AUTOMATION_EXEC.html (file name can be configured via properties file)
  - detailed logs are available in <b>automation.log</b>
