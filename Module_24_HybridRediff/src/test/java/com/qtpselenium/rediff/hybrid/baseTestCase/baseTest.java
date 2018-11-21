package com.qtpselenium.rediff.hybrid.baseTestCase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.util.Properties;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.qtpselenium.rediff.hybrid.driver.DriverScript;
import com.qtpselenium.rediff.hybrid.reports.ExtentManager;
import com.qtpselenium.rediff.hybrid.util.DataUtil;
import com.qtpselenium.rediff.hybrid.util.Xls_Reader;

public class baseTest {
	
//If all the test cases needs same beforetest annotations which does the same thing like initializing properties file, then we create a baseTest class and all other Test classes extends this base class
//Here we 1st need to read the env.properties file and based on the env mentioned in this file we will then initialize either prod.properties or uat.properties file
	
	public Properties envProp;    //This Properties object refers to either of prod.properties or uat.properties based on value of env variable read from env.properties file
	public Properties prop;      //This properties object refers to env.properties file
	public Xls_Reader xls;
	public String testCaseName;
	public DriverScript ds;
	public ExtentReports rep;            //ExtentManager.java is the file used for Extent reporting. ExtentReports is inbuild class
	public ExtentTest test;              //ExtentTest is also the inbuild class in avnetstact package which is used for extent reporting
	
	@BeforeTest
	public void initializing()
	{
		//Iniitializing test case name
		System.out.println("*********" + this.getClass().getSimpleName());
	//	testCaseName = this.getClass().getSimpleName();
		String arr[] = this.getClass().getPackage().getName().split("\\.");
		String suiteName= arr[arr.length-1];
		System.out.println(suiteName);
		
		//Properties file 
		prop = new Properties();
		envProp = new Properties();
		
		//initiailizing properties file 
		try 
		{
			FileInputStream fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//env.properties");
			prop.load(fs);
			System.out.println(prop.getProperty("env"));    //getProperty() takes the arg as name of key defined in properties file and returns the value of provided key
			
			String env = prop.getProperty("env");  //Based of either of value for key named env respective enviroment file will be initialized in next step
			
			fs = new FileInputStream(System.getProperty("user.dir")+"//src//test//resources//"+env+".properties");
			envProp.load(fs);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		//initializing xls file
		// how do i come to know the suite ?
		System.out.println(envProp.getProperty(suiteName+"_xls"));
		xls = new Xls_Reader(envProp.getProperty(suiteName+"_xls"));
		
		//Initializing DriverScript object
		ds = new DriverScript();
		ds.setProp(prop);   //baseTest.java will call these set functions with there prop & envProp objects and the same object will be set in DriverScript class to make identifiers accessible
		ds.setEnvProp(envProp);
	}
	
	@BeforeMethod
	public void initTest()
	{
	//	rep = ExtentManager.createInstance(prop.getProperty("reportPath"));   //We are initializing extent reporting obj to report path in env.properties file
		rep = ExtentManager.getInstance(prop.getProperty("reportPath"));
		test = rep.createTest(testCaseName);                                 //We are passing the each test case name 
		ds.setExtentTest(test);
	}
	
	@AfterMethod
	public void quit()
	{
		if(ds!= null)
			ds.quit();
		
		if(rep!=null)
			rep.flush();
	}
	
	@DataProvider
	public Object[][] getData(Method method)
	{
		System.out.println("Inside Data provider & Test case name is - " + method.getName());
		testCaseName=method.getName();
		return DataUtil.getTestData(testCaseName, xls);
	}

}
