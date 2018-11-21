package com.qtpselenium.rediff.hybrid.driver;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.keywords.AppKeywords;
import com.qtpselenium.rediff.hybrid.util.Constants;
import com.qtpselenium.rediff.hybrid.util.Xls_Reader;

public class DriverScript {

	// Read the xls file and get the keyword
	
	public Properties envProp;  //As DriverScript class should be able to access the actual identifiers which are stored in properties file. So declearing Properties file in this class as well
	public Properties Prop;     //And we will write Get & Set method to set the prop & envProp objects from baseTest class to DriverScript class
	AppKeywords app;
	public ExtentTest test;
	
	public void executeKeywords(String testCaseName, Xls_Reader xls, Hashtable<String, String> mydata) throws Exception 
	{
		
		int totalRowsinSheet = xls.getRowCount(Constants.KEYWORDS_SHEET);  //Instead of hardcoding sheet name = Keywords we are reading sheet name from Constants java file created in util package
		System.out.println("Total rows are in Keywords sheet are -- " + totalRowsinSheet);
		
		test.log(Status.INFO, "Total rows are in Keywords sheet are -- " + totalRowsinSheet); //Logging in extent report
		
		app = new AppKeywords();
		
	//Send properties file and Hashtable to Generic Keyword class
		
		app.setProp(Prop);
		app.setEnvProp(envProp);
		app.setData(mydata);
		app.setExtentTest(test);
		
		for(int rNum = 2; rNum <= totalRowsinSheet; rNum++)     //initializing rNum=2 because 1st row is the column headers and actual data starts from row no 2
		{
			String tcId = xls.getCellData(Constants.KEYWORDS_SHEET, Constants.TCID_COL, rNum);
			if(tcId.equalsIgnoreCase(testCaseName))
			{
				String keyword = xls.getCellData(Constants.KEYWORDS_SHEET, Constants.KEYWORD_COL, rNum);
				String objectKey = xls.getCellData(Constants.KEYWORDS_SHEET, Constants.OBJECT_COL, rNum);
				String dataKey = xls.getCellData(Constants.KEYWORDS_SHEET, Constants.DATA_COL, rNum);
				String proceedOnFail = xls.getCellData(Constants.KEYWORDS_SHEET, Constants.PROCEED_COL, rNum);
				String data = mydata.get(dataKey);
				
				app.setDataKey(dataKey);
				app.setObjectKey(objectKey);
				app.setProceedOnFail(proceedOnFail);
				
			//	System.out.println(tcId +"  ---  "+keyword+"  ---  "+Prop.getProperty(objectKey)+"  ---  "+data);
				
	//If any new keyword is introduced in xls file then instead of adding each time else if stmt we can make use of reflections api			
				
		/*		if(keyword.equals("openBrowser"))
					app.openBrowser();
				else if(keyword.equals("navigate"))
					app.navigate();
				else if(keyword.equals("click"))
					app.click();
				else if(keyword.equals("type"))
					app.type();
				else if(keyword.equals("validateLogin"))
					app.validateLogin();              
				*/
				
				//Reflections api
				
				Method method;  //in our case keyword name is same as function name in Generickeyword.java file so we can make use of reflections api here when there is same name of variable and function
					method = app.getClass().getMethod(keyword);
					method.invoke(app);

			}
		}
		app.assertAll();
	}

	public Properties getEnvProp() {
		return envProp;
	}

	public void setEnvProp(Properties envProp) {  //baseTest.java will call these set functions with there prop & envProp objects and the same object will be set in DriverScript class to make identifiers accessible
		this.envProp = envProp;
	}

	public Properties getProp() {
		return Prop;
	}
	
	public void setExtentTest(ExtentTest test) {
		this.test = test;
	}

	public void setProp(Properties prop) {
		Prop = prop;
	}
	
	public void quit()
	{
		if(app!=null)
			app.quit();
	}

}
