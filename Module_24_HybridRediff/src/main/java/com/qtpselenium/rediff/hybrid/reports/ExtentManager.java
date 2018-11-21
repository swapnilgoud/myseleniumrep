package com.qtpselenium.rediff.hybrid.reports;


//http://relevantcodes.com/Tools/ExtentReports2/javadoc/index.html?com/relevantcodes/extentreports/ExtentReports.html


import java.io.File;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.ChartLocation;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {
	private static ExtentReports extent;
	
	public static String screenShotReportPath;     //Creating this screenShotReportPath string variable only to pass dynamically generated folder name in which screen shot will go

	public static ExtentReports getInstance(String reportPath) {
		if (extent == null) 
		{
			Date d = new Date();
			String fileName = d.toString().replace(":", "_").replace(" ", "_")+".html";
			String folderName = d.toString().replace(":", "_").replace(" ", "_");
			
			//Directory of reports
			new File(reportPath+folderName+"//screenshots").mkdirs();
			
			reportPath = reportPath+folderName+"//";
			
			screenShotReportPath = reportPath+"screenshots//";                //Creating this screenShotReportPath string variable only to pass dynamically generated folder name in which screen shot will go
			
			System.out.println(reportPath+fileName);
			createInstance(reportPath+fileName);
		}
		return extent;
	}
	
	public static ExtentReports createInstance(String fileName)
	{
		ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(fileName);
		htmlReporter.config().setTestViewChartLocation(ChartLocation.BOTTOM);
		htmlReporter.config().setChartVisibilityOnOpen(true);
		htmlReporter.config().setTheme(Theme.DARK);
		htmlReporter.config().setDocumentTitle("Reports");
		htmlReporter.config().setEncoding("utf-8");
		htmlReporter.config().setReportName("Reports - Automation Testing");
		
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		
		return extent;
	}
}
