package com.qtpselenium.rediff.hybrid.keywords;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;












import org.apache.commons.io.FileUtils;
//import java.io.FileInputStream;
//import java.io.IOException;
//import junit.framework.Assert;
//import org.apache.tools.ant.util.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
//import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
//import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.qtpselenium.rediff.hybrid.reports.ExtentManager;

public class GenericKeywords {
	
	public Properties envProp;
	public Properties Prop;
	public String objectKey;
	public String dataKey;
	public Hashtable<String, String> data;
	public WebDriver driver;
	public ExtentTest test;
	public String proceedOnFail;
	public SoftAssert softAssert = new SoftAssert();
	
	/********************************************SETTER FUNCTIONS********************************************/
	
	public void setEnvProp(Properties envProp) {
		this.envProp = envProp;
	}
	
	public void setProceedOnFail(String proceedOnFail) {
		this.proceedOnFail = proceedOnFail;
	}

	public void setProp(Properties prop) {
		Prop = prop;
	}

	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}

	public void setDataKey(String dataKey) {
		this.dataKey = dataKey;
	}

	public void setData(Hashtable<String, String> data) {
		this.data = data;
	}
	
	public void setExtentTest(ExtentTest test) {
		this.test = test;
	}

	/*******************************************************************************************************/
	
	public void openBrowser()
	{
		String br = data.get(dataKey);
		System.out.println("Opening browser " + br);
		
		System.setProperty("webdriver.gecko.driver", "C://AllDrivers//geckodriver.exe");
		System.setProperty("webdriver.chrome.driver", "C://AllDrivers//chromedriver.exe");
		System.setProperty("webdriver.ie.driver", "C://AllDrivers//IEDriverServer.exe");
		
		if(Prop.getProperty("gridRun").equals("Y"))
		{
			//Run on Grid
			
			DesiredCapabilities cap = null;
			if(br.equals("Mozilla"))
			{
				cap = DesiredCapabilities.firefox();
				cap.setJavascriptEnabled(true);
				cap.setPlatform(Platform.WINDOWS);
			}
			else if(br.equals("Chrome"))
			{
				cap = DesiredCapabilities.chrome();
				cap.setJavascriptEnabled(true);
				cap.setPlatform(Platform.WINDOWS);
			}
			else if(br.equals("IE"))
			{
				cap = DesiredCapabilities.internetExplorer();
				cap.setJavascriptEnabled(true);
				cap.setPlatform(Platform.WINDOWS);
			}
			
		//	driver = new RemoteWebDriver(remoteAddress, capabilities);
			
			try {
				driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), cap);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		else   //If gridRun flag is set to false in env prop file then run as normal pgm
		{
			if(br.equals("Mozilla"))
			{
			//	System.setProperty("webdriver.gecko.driver", "C://AllDrivers//geckodriver.exe");
			//	driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE, "C://firefox.log");
				FirefoxOptions options = new FirefoxOptions();
				options.setPageLoadStrategy(PageLoadStrategy.NORMAL);
				driver = new FirefoxDriver();
			}
			else if(br.equals("Chrome"))
			{
				ChromeOptions ops = new ChromeOptions();
				ops.addArguments("--disable-notifications");
		        ops.addArguments("disable-infobars");
		        ops.addArguments("--start-maximized");
		        
		        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "C://chrome.log");
				System.setProperty(ChromeDriverService.CHROME_DRIVER_SILENT_OUTPUT_PROPERTY, "true");
				
				driver = new ChromeDriver(ops);
			}
			else if(br.equals("IE"))
			{
				System.setProperty(InternetExplorerDriverService.IE_DRIVER_LOGLEVEL_PROPERTY,"INFO");
				System.setProperty(InternetExplorerDriverService.IE_DRIVER_LOGFILE_PROPERTY, "C:\\IE.log");
				
				driver = new InternetExplorerDriver();
			}
			
			driver.manage().timeouts().pageLoadTimeout(40,TimeUnit.SECONDS);   //waiting for application to load completely
			driver.manage().timeouts().setScriptTimeout(40, TimeUnit.SECONDS);    //Wait for Ajax calls to be completed
			driver.manage().timeouts().implicitlyWait(40, TimeUnit.SECONDS);
			driver.manage().window().maximize();
		}
	}
	
	public void navigate()
	{
		System.out.println("Navigating to a website" + envProp.getProperty(objectKey));
		driver.get(envProp.getProperty(objectKey));
		test.log(Status.INFO, "Navigating to a website" + envProp.getProperty(objectKey)); //Logging to extent report
	}
	
	public void click()
	{
		System.out.println("Clicking" + Prop.getProperty(objectKey));
		getObject(objectKey).click();
		test.log(Status.INFO, "Navigating to a website" + "Clicking" + Prop.getProperty(objectKey)); //Logging to extent report
	}
	
	public void click(String objectKey)  //For addStock keyword we dont have any object defined in xlsx file. So we are passing the object name and in generickeyword.java we are setting this value to objectKey using setter function and again calling click()
	{
		setObjectKey(objectKey);
		click();
	}
	
	public void type()
	{
		System.out.println("Typing" +Prop.getProperty(objectKey)+" . Data - "+ data.get(dataKey));
		getObject(objectKey).sendKeys(data.get(dataKey));
		test.log(Status.INFO, Prop.getProperty(objectKey)+" . Data - "+ data.get(dataKey));
	}
	
	public void type(String objectKey, String dataKey)
	{
		setObjectKey(objectKey);
		setDataKey(dataKey);
		type();
	}
	
	public void clear()
	{
		test.log(Status.INFO,"Clearing data - "+objectKey);
		getObject(objectKey).clear();
	}
	
	public void quit()
	{
		if(driver!=null)
			driver.quit();
	}
	
	public void validateTitle()
	{
		System.out.println("Validating title -- " + Prop.getProperty(objectKey));
		String expectedTitle = Prop.getProperty(objectKey);
		String actualTitle = driver.getTitle();
		System.out.println("Actual title is -- " + actualTitle);
		
	//	Assert.assertEquals(expectedTitle, actualTitle);
		if(! expectedTitle.equals(actualTitle))
		{
			//Report an error
			reportFailure("Title did not match. Got title as - " + actualTitle);
		}
	}
	
	public void validateElementPresent()
	{
		if(! isElementPresent(objectKey))
		{
			//report an error
			reportFailure("Element is not present - " + objectKey);
		}
	}
	
	public WebElement getObject(String objectKey)
	{
		WebElement e = null;
		
		try
		{
			if(objectKey.endsWith("_xpath"))
				e = driver.findElement(By.xpath(Prop.getProperty(objectKey)));
			else if(objectKey.endsWith("_id"))
				e = driver.findElement(By.id(Prop.getProperty(objectKey)));
			else if(objectKey.endsWith("_css"))
				e = driver.findElement(By.cssSelector(Prop.getProperty(objectKey)));
			else if(objectKey.endsWith("name"))
				e = driver.findElement(By.name(Prop.getProperty(objectKey)));
			
			WebDriverWait wait = new WebDriverWait(driver, 20);
			//Check the visibility of object
			wait.until(ExpectedConditions.visibilityOf(e));
			//Check if webelement is interactable
			wait.until(ExpectedConditions.elementToBeClickable(e));
		}
		catch(Exception ex)
		{
			//report the failure if element not present or not interactable
			reportFailure("WebElement not found - " + objectKey);
		}
		
		return e;
	}
	
	public boolean isElementPresent(String objectKey)
	{
		List<WebElement> list = null;
		
		if(objectKey.endsWith("_xpath"))
			list = driver.findElements(By.xpath(Prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_id"))
			list = driver.findElements(By.id(Prop.getProperty(objectKey)));
		else if(objectKey.endsWith("_css"))
			list = driver.findElements(By.cssSelector(Prop.getProperty(objectKey)));
		else if(objectKey.endsWith("name"))
			list = driver.findElements(By.name(Prop.getProperty(objectKey)));
		
		if(list.size()==0)
			return false;
		
		else
			return true;
	}
	
	/***************************Reporting Function************************************/
	
	public void reportFailure(String failureMsg)
	{
		//fail the test case
		//take the screenshot, embed the screenshot in report
		
		test.log(Status.FAIL, failureMsg);
		System.out.println("Inside reportFailure function in Generic Keyword class");
	//	Assert.fail(failureMsg);   //If test case fails then Assert.fail() will stop the execution there itself. If we want to run with minor errors then we use softAssert
		
		takeScreenshot(); //calling this function when the test case fails to take screen shot
		
		if(proceedOnFail.equals("Y"))  //In SuiteA.xlsx we have a col name ProccedOnFail. If for a keyword proceedOnFail is set to Y then all the test cases are executed & test is completely executed
		{
			softAssert.fail(failureMsg);   //Soft Assertions
		}
		else
		{
			softAssert.fail(failureMsg); //In SuiteA.xlsx we have a col name ProccedOnFail. If for a keyword proceedOnFail is not set to Y then if that test cases is failed & test is fails to execute
			softAssert.assertAll();
		}
	}
	
	public void takeScreenshot()
	{
		Date d = new Date();
		String  screenshotFile = d.toString().replace(" ", "_").replace(":", "_")+".jpeg";
		System.out.println("screenshotFile in takeScreenshot " + screenshotFile);
		 
		//this command takes screenshot
		File srcFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		
		try {
	//		FileUtils.copyFile(srcFile, new File(Prop.getProperty("screenShotPath")+screenshotFile));  //For now screenShotPath in env.Properties is commented to get dynamically generated folder name
			FileUtils.copyFile(srcFile, new File(ExtentManager.screenShotReportPath+screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Put the screenshot file in reports
		try {
	//		test.log(Status.INFO, "Screenshots => " + test.addScreenCaptureFromPath(Prop.getProperty("screenShotPath")+screenshotFile)); //For now screenShotPath in env.Properties is commented to get dynamically generated folder name
			test.log(Status.FAIL, "Screenshots => " + test.addScreenCaptureFromPath(ExtentManager.screenShotReportPath+screenshotFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void assertAll()
	{
		softAssert.assertAll();
	}
	
	public void isAlertPresent()
	{
		boolean foundAlert = false;
		WebDriverWait wait = new WebDriverWait(driver, 40);
		
		if(wait.until(ExpectedConditions.alertIsPresent())==null)
		{
			System.out.println("No alert found");
		//	foundAlert = false;
			test.log(Status.INFO, "No Alert found");
		}
		
		else
		{
			System.out.println("Alert found !!");
	//		foundAlert=true;
			test.log(Status.INFO, "Alert found !!");
			wait(2);
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Switching to alert & accepted successfully");
		}
		
	//	return foundAlert;
	}
	
	public void acceptAlertIfPresent()
	{
		test.log(Status.INFO, "Switching to alert");
		wait(3);
		
		try{
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Alert accepted successfully");
		}catch(Exception e){
			if(objectKey.equals("Y"))
				reportFailure("Alert not found when mandatory");
		}
	}
	
	public void select()
	{
		test.log(Status.INFO, "Selecting value from " + Prop.getProperty(objectKey)+" Data "+ data.get(dataKey));
		
		//Validate if the value is present in drop down
		if(!isElementInList())
			reportFailure("Provided element is not in the list -" + data.get(dataKey));
		
		new Select(getObject(objectKey)).selectByVisibleText(data.get(dataKey));
				
	}
	
	public boolean isElementInList()
	{
		List<WebElement> option = new Select(getObject(objectKey)).getOptions();   //This gives all the values of drop down to list
		for(int i = 0; i <option.size(); i++)
		{	
			if(option.get(i).getText().equals(data.get(dataKey)))   //Checking each drop down value text matches expected text or not 
				return true;
		}
		
		return false;
	}
	
	public void validateElementNotInList()
	{
		if(isElementInList())
			reportFailure("Could not delete the option - " );
	}
	
	public void waitForPageToLoad()                       //This function is created as jquery icons are displayed after creating portfolio
	{
		JavascriptExecutor js = (JavascriptExecutor)driver;
		
		int i = 0;
		while(i!=10)
		{
			String state = (String)js.executeScript("return document.readyState;");
			System.out.println(state);
			
			if(state.equals("Complete"))                          //If page state is ready it means page is loaded completely 
				break;
			else
				wait(2);
			i++;
		}
		
		i=0;
		while(i!=10)
		{
			Boolean result= (Boolean) js.executeScript("return window.jQuery != undefined && jQuery.active == 0;");
			System.out.println(result);
			
			if(result )
			 	break;
			else
				wait(2);
			i++;
		}
	}
	
	public void wait(int time)
	{
		try {
			Thread.sleep(time * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void waitTillSelectionisDone(String objectkey, String expectedOption)
	{
		int i = 0;
		String currentDropDownValue ="";
		while(i!=10)
		{
			WebElement e =getObject(objectkey);
			Select s = new Select(e);
			currentDropDownValue = s.getFirstSelectedOption().getText();
			System.out.println(currentDropDownValue);
			
			if(currentDropDownValue.equals(expectedOption))
				return;
			else
				wait(2);
			i++;
		}
		
		reportFailure("Actual Value in drop down and expected value in drop down does not match. Got actual value as " + currentDropDownValue);
	}
}
