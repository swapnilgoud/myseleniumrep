package com.qtpselenium.rediff.hybrid.testcases.stocksuite;

import java.util.Hashtable;

import org.testng.SkipException;
import org.testng.annotations.Test;

import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.baseTestCase.baseTest;
import com.qtpselenium.rediff.hybrid.util.Constants;
import com.qtpselenium.rediff.hybrid.util.DataUtil;

public class AddDelStockTest extends baseTest{

	@Test(dataProvider="getData", priority=1)
	public void addStockTest(Hashtable<String, String> data) throws Exception
	{
		test.log(Status.INFO, "Starting" + testCaseName);  //Logging in extent report
		
		if(DataUtil.isSkip(testCaseName, xls) || data.get(Constants.RUNMODE_COL).equals(Constants.RUNMODE_COL_NO)) //Instead of hardcoding Runmode column name from data sheet we are reading Runmode column name from Constants java file created in util package
		{	
			test.log(Status.SKIP, "Runmode is set to No");
			throw new SkipException("Runmode is set to No");
		}
		System.out.println("Running Add Stock test");
		
		ds.executeKeywords(testCaseName, xls, data);
	}
	
	@Test(dataProvider="getData", dependsOnMethods={"addStockTest"}, priority=2)
	public void deleteStockTest(Hashtable<String, String> data) throws Exception
	{
		test.log(Status.INFO, "Starting" + testCaseName);  //Logging in extent report
		
		if(DataUtil.isSkip(testCaseName, xls) || data.get(Constants.RUNMODE_COL).equals(Constants.RUNMODE_COL_NO)) //Instead of hardcoding Runmode column name from data sheet we are reading Runmode column name from Constants java file created in util package
		{	
			test.log(Status.SKIP, "Runmode is set to No");
			throw new SkipException("Runmode is set to No");
		}
		System.out.println("Running Add Stock test");
		
		ds.executeKeywords(testCaseName, xls, data);
	}
}
