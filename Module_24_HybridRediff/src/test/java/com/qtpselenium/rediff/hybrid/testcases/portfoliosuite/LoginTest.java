package com.qtpselenium.rediff.hybrid.testcases.portfoliosuite;

import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.util.Constants;
import com.qtpselenium.rediff.hybrid.util.DataUtil;
import com.qtpselenium.rediff.hybrid.baseTestCase.baseTest;

public class LoginTest extends baseTest{

	//If all the test cases needs same beforetest annotations which does the same thing like initializing properties file, then we create a baseTest class and all other Test classes extends this base class
	
	@Test(dataProvider="getData")
	public void loginTest(Hashtable<String, String> data) throws Exception
	{
		System.setProperty("webdriver.gecko.driver", "C://AllDrivers//geckodriver.exe");
		System.setProperty("webdriver.chrome.driver", "C://AllDrivers//chromedriver.exe");
		System.setProperty("webdriver.ie.driver", "C://AllDrivers//IEDriverServer.exe");
		
		
		test.log(Status.INFO, "Starting" + testCaseName);  //Logging in extent report
		
		if(DataUtil.isSkip(testCaseName, xls) || data.get(Constants.RUNMODE_COL).equals(Constants.RUNMODE_COL_NO)) //Instead of hardcoding Runmode column name from data sheet we are reading Runmode column name from Constants java file created in util package
		{	
			test.log(Status.SKIP, "Runmode is set to No");
			throw new SkipException("Runmode is set to No");
		}
		System.out.println("Running Login test");
		
		ds.executeKeywords(testCaseName, xls, data);
	}
}
