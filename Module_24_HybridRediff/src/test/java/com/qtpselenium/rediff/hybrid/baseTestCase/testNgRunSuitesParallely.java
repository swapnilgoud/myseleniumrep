package com.qtpselenium.rediff.hybrid.baseTestCase;

import java.util.Arrays;

import org.testng.TestNG;

public class testNgRunSuitesParallely {

	public static void main(String[] args) {

		TestNG testng = new TestNG();
		testng.setTestSuites(Arrays.asList(new String[] {System.getProperty("user.dir")+"//src/test/resources/testng.xml"}));
		
		testng.setSuiteThreadPoolSize(2);
		
		testng.run();
	}

}

//Test cases in 1 suite can be executed parallely by writing parallel= tests in suiteA.xml
//But if we have to execute multiple suites parallely then we can't write parallel=tests in testng.xml along with all suite names'
//We have to use testNg object to execute all the suites mentioned in testng.xml parallely -- this example given in 'TestNg_ParallelExecution_OfSuites' package