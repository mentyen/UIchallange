PROJECT OVERVIEW
----------------
	 project contains test automation suites for testing all secfin applications.

Project Structure
-----------------
1. Feature File: 

	Folder \src\test\resources\features contains feature file. Each feature file contains test scenarios and steps to execute in scenarios.

2. Page Class:
   
	Package com.rebar.cucumber.pages contains the java classes specific to each page on web site which will be used for automating test script steps.
 	
3. PageObjectManager Class:

	Package com.rebar.pageobjectmanager contains PageObjectManager class, it is used to manage and handle instances of different page object models. User needs to create each page instance in PageObjectManager class.
   
4. Step Definition Class:

	Package com.rebar.stepdefinition contains project specific step definition classes for feature files which is related with each feature step in the scenario.
  
Also step definition package contains below classes used by Nexgen Framework:

	- AbstractSteps
	- DefaultStepDefinition 

5. Test Data:

	Test Data files are located at below location:  
	\src\test\resources\data
   
6. Properties File:

	All properties files are kept and maintained at below location:
	\src\test\resources\properties 
	
   
	NextGen.properties
	- To keep and maintain NextGen framework details.

7. TestRunner Class:

	Package com.rebar.testrunners contains TestNG TestRunner to run specific suite to trigger test execution.

8. AutomationReports:
	
	Post test execution 	
	- Extent automation reports are store in /AutomationReports folder.
	
      
How To Run Automation Suite In IDE 
--------------------------------------

	1. Import the project in eclipse or any other java/maven supported IDE.
	2. Go to class testrunners/TestRunner.java
	3. Add the tag value from the below Cucumber Tags based on the suite and environment you want to run.

How To Run Automation Suite In GitLab Pipeline 
----------------------------------------------

	1. Open project in GitLab
	2. Go to CI/CD => Pipelines
	3. Click on Run Pipelines button
	4. Select the branch in which Automation Suite need to be executed
	5. Enter variable name as "cucumberTag" and enter the value for this variable from below listed Cucumber Tags based on the suite and environment you want to run. 
    6. Enter variable name as "testRunner" and enter the value for this variable from below listed TestRunners based on the suite and environment you want to run.	

cucumberTag/testRunner
-------------

 @MKT_YFH_REGRESSION_API_PositionsOnloans-CLIENTAPI
 TestRunnerClient
 @MKT_YFH_REGRESSION_DATABASE_AGENCY-KUDU
 TestRunnerEDP
 @MKT_YFH_REGRESSION_API
 TestRunnerCombined
 @MKT_YFH_REGRESSION_API_VOUCHER-IMMS
 TestRunnerIMMS