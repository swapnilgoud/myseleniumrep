package com.qtpselenium.rediff.hybrid.keywords;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.Status;

public class AppKeywords extends GenericKeywords{

	public void login()
	{
		String username="";
		String password="";
		
		if(data.get("Username") == null && data.get("Password") == null)    //Checking if Username & Password columns are present in xlsx file for a test case or not
		{
			username = envProp.getProperty("defaultUsername");
			password = envProp.getProperty("defaultPassword");
		}
		else              //Means username and password are present in xlsx file for a test case
		{
			username = data.get("Username");
			password = data.get("Password");
		}
		test.log(Status.INFO, "Validating Login");
		getObject("money_xpath").click();
		getObject("signin_xpath").click();
		getObject("username_id").sendKeys(username);
		getObject("emailsubmit_id").click();
		
		WebDriverWait wait = new WebDriverWait(driver, 20);
		wait.until(ExpectedConditions.visibilityOf(getObject("password_id")));
		
		getObject("password_id").sendKeys(password);
		getObject("continue_id").click();
		
		//Checking if alert comes up while execution in case of mozilla 
		
	/*	if(isAlertPresent()==true)
		{
			wait(2);
			driver.switchTo().alert().accept();
			driver.switchTo().defaultContent();
			test.log(Status.INFO, "Switching to alert & accepted successfully");
		}*/
		
	//	isAlertPresent();
		acceptAlertIfPresent();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("password_id")));
	}
	
	public void validateLogin()
	{
		test.log(Status.INFO, "Validating Login");
		System.out.println("Validating Login");
		
		boolean result = isElementPresent("portfolioselection_xpath");
		String expectedResult = data.get("ExpectedResult");
		String actualResult="";
		
		if(result == true)
			actualResult = "loginsuccess";
		else
			actualResult = "loginfailure";
		if(! expectedResult.equals(actualResult))
			reportFailure("Actual & Expected result from xlsx does not match" + actualResult);
	}
	
	public void defaultLogin()
	{
		test.log(Status.INFO, "Logging in with default id");
		String username = envProp.getProperty("adminusername");
		String password = envProp.getProperty("adminpassword");
		
		System.out.println("Default username is -- " + username);
		System.out.println("Default password is -- " + password);
	}
	
	public void verifyPortfolio()
	{
		test.log(Status.INFO, "Verifying portfolio name in dropdown should be - "+ data.get(dataKey));
		waitTillSelectionisDone("portfolioselection_xpath", data.get(dataKey));
		
	/*	Select s = new Select(getObject("portfolioselection_xpath"));
		String text = s.getFirstSelectedOption().getText();
		
		if(!data.get(dataKey).equals(text))
			reportFailure("Value in drop down did not match. Portfolio not created successfully");
	*/	
	}
	
	public void addStock()
	{
		test.log(Status.INFO, "Adding Stock details");
		waitForPageToLoad();
		click("addStockButton_id");   //For addStock keyword we dont have any object defined in xlsx file. So we are passing the object name and in generickeyword.java we are setting this value to objectKey using setter function and again calling click()
		type("stockName_id", "StockName");
		click("listOfOptions_xpath");
		click("addStockCalendar_id");
		selectDate(data.get("Date"));
		type("stockquantity_id", "Quantity");
		type("stockPurchasePrice_id", "PurchasePrice");
		click("addStockSubmitbutton_id");
		test.log(Status.INFO, "Submitted Stock details");
		test.log(Status.INFO, "Validating Stock details in table after stock addition");
		int rnum = rowNumberWithCellData(data.get("StockName"));
		
		if(rnum==-1)
			reportFailure("Could not find the Stock");
	}
	
	public void deleteStock()
	{
		int rnum = rowNumberWithCellData(data.get("StockName"));
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[1]")).click();
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[3]/div/input[2]")).click();
		driver.switchTo().alert().accept();
		waitForPageToLoad();
		driver.switchTo().defaultContent();
		
		//After deleting than check the same stock name is not displayed in table
		
		rnum = rowNumberWithCellData(data.get("StockName"));
		System.out.println(rnum);
	//	Assert.assertEquals(rnum, -1);
		if(rnum != -1)
			reportFailure("Could not delete the provided Stock name");
		else
			test.log(Status.INFO, "Provided stock name has been deleted successfully");
	}
	
	public void buySellStock()
	{
		int rnum = rowNumberWithCellData(data.get("StockName"));
		
		if(rnum == -1)
			reportFailure("Could not find the Stock");
		
		if(data.get("Action").equals("buy"))
		{
			//Number of Quantity of stocks check before buying
			int quantityChkBeforeBuy = chkQuantity(rnum);
			System.out.println("Quanity of stock before buying is -- " + quantityChkBeforeBuy);
			
			driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[1]")).click();
			driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[3]/div/input[1]")).click();
			
			selectBuySellOption(data.get("Action"));        //Buying an Stock
			wait(2);
			
			click("buySellCalendar_id");
			selectDate(data.get("Date"));                     //If provided date is 1/11/2018 then date format should be d/MM/yyyy or if provided date is 10/11/2018 then it should be dd/MM/yyyy
			type("buysellqty_name", "Quantity");
			type("buysellprice_name","PurchasePrice");
			click("buySellStockButton_id");
			
			waitForPageToLoad();
			//Number of Quantity of stocks check after buying
			int quantityChkAfterBuy = chkQuantity(rnum);
			System.out.println("Quanity of stock after buying is -- " + quantityChkAfterBuy);
			
			buyOrSellStockIsSuccessful(quantityChkBeforeBuy,quantityChkAfterBuy);
			test.log(Status.INFO, "Buy Action SuccessFull");
		}
		
		else if(data.get("Action").equals("sell"))
		{
			rowNumberWithCellData(data.get("StockName"));
			
			//Number of Quantity of stocks check before selling
			int quantityChkBeforeSell = chkQuantity(rnum);
			System.out.println("Quanity of stock before selling is -- " + quantityChkBeforeSell);
			
			driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[1]")).click();
			driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[3]/div/input[1]")).click();
			
			selectBuySellOption(data.get("Action"));        //Selling an Stock
			wait(2);
			click("buySellCalendar_id");
			selectDate(data.get("Date"));                     //If provided date is 1/11/2018 then date format should be d/MM/yyyy or if provided date is 10/11/2018 then it should be dd/MM/yyyy
			type("buysellqty_name", "Quantity");
			type("buysellprice_name","PurchasePrice");
			click("buySellStockButton_id");
			
			waitForPageToLoad();
			
			//Number of Quantity of stocks check after buying
			int quantityChkAfterSell = chkQuantity(rnum);
			System.out.println("Quanity of stock after selling is -- " + quantityChkAfterSell);
			
			buyOrSellStockIsSuccessful(quantityChkBeforeSell,quantityChkAfterSell);
		}
	}
	
	public void checkHistory()
	{
		waitForPageToLoad();
		test.log(Status.INFO, "Checking history");
		int rNum=rowNumberWithCellData(data.get("Stock Name"));
		if(rNum==-1)
			reportFailure("Stock not found in list "+data.get("Stock Name") );
	
		driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[1]")).click();
		driver.findElements(By.xpath("//input[@class='equityTransaction']")).get(rNum-1).click();
		String actual=driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rNum+"]/td[5]")).getText();
		List<WebElement> shares = driver.findElements(By.xpath(Prop.getProperty("shares_xpath")));
		List<WebElement> prices = driver.findElements(By.xpath(Prop.getProperty("prices_xpath")));
		
		int totalShares=0;
		int totalAmount=0;
		
		for(int i=0;i<prices.size();i++){
			int share = Integer.parseInt(shares.get(i).getText());
			int price = Integer.parseInt(prices.get(i).getText());
			totalShares = share + totalShares;
			totalAmount = totalAmount + (share*price);
		}

		test.log(Status.INFO,"Total shares - "+totalShares );
		test.log(Status.INFO,"Total Amount spent "+totalAmount );
		double average = Double.valueOf(totalAmount)/Double.valueOf(totalShares);
		test.log(Status.INFO,"Average - "+average );
		test.log(Status.INFO,"Actual - "+actual );
		
		reportFailure("Actual average is "+ actual+"Expected was "+average);
		// fix the decimals - Math
	}
	
	public void selectDate(String date)
	{
		Date current = new Date();
		SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy");
		try {
			Date dateToSelect = sd.parse(date);
			
			String day = new SimpleDateFormat("d").format(dateToSelect);
			System.out.println(day);
			
			String month = new SimpleDateFormat("MMMM").format(dateToSelect);
			System.out.println(month);
			
			String year = new SimpleDateFormat("yyyy").format(dateToSelect);
			System.out.println(year);
			
			String desiredMonthYear =month+" "+year;
			
			while(true)
			{
				String displayedMonthYear = driver.findElement(By.cssSelector(".dpTitleText")).getText();
				if(displayedMonthYear.equals(desiredMonthYear))
				{
					//select a date
					driver.findElement(By.xpath("//td[text()='"+day+"']")).click();
					break;
				}
				else
				{
					if(dateToSelect.compareTo(current)>0)
						driver.findElement(By.xpath("//*[@id='datepicker']/table/tbody/tr[1]/td[4]/button")).click();
					else if(dateToSelect.compareTo(current)<0)
						driver.findElement(By.xpath("//*[@id='datepicker']/table/tbody/tr[1]/td[2]/button")).click();
				}
				
			}
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int rowNumberWithCellData(String data)
	{
		List<WebElement> rows = driver.findElements(By.xpath("//table[@id='stock']/tbody/tr"));
		
		for(int i = 0; i<rows.size(); i++)
		{
			WebElement rowData = rows.get(i);
			List<WebElement> cells = rowData.findElements(By.tagName("td"));
			for(int cellNum = 0; cellNum < cells.size(); cellNum++)
			{
				String cellData = cells.get(cellNum).getText();
				
				if(!cellData.trim().equals("") && cellData.contains(data))
					return ++i;
			}
		}
		return -1;
	}
	
	public void selectBuySellOption(String option)
	{
		int i = 0;
		while(i!=10)
		{
			//WebElement e = driver.findElement(By.id("equityaction"));
			WebElement e = getObject("actionBuySell_id");
			Select s = new Select(e);
			s.selectByValue(option);
			i++;
		}
	}
	
	public int chkQuantity(int rnum)
	{
		String quantity = driver.findElement(By.xpath("//table[@id='stock']/tbody/tr["+rnum+"]/td[4]")).getText();
		int q = Integer.parseInt(quantity);
		return q;
	}
	
	public void buyOrSellStockIsSuccessful(int quantityChkBeforeBuy , int quantityChkAfterBuy)
	{
		if(quantityChkAfterBuy > quantityChkBeforeBuy)
		{
			System.out.println("Stocks has been buyed out successfully");
			test.log(Status.INFO, "Stocks has been buyed out successfully");
		}
		else 
		{
			System.out.println("Stocks has been selled out successfully");
			test.log(Status.INFO, "Stocks has been selled out successfully");
		}
	}
}
