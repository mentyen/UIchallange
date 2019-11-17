package com.sample.test.demo.tests;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;



public class TC_01_HappyPath extends OrderPage{
	
	public static PizzaTypes pizza;
	public static PizzaToppings toppings;
	public static OrderPage orderPage;
	public static TC_01_HappyPath happyPath;
	public static String pizzaQuantity;
	
	@BeforeMethod
	public void setUp() {
		
		happyPath=new TC_01_HappyPath();
		
		orderPage=new OrderPage();
		
		pizza=PizzaTypes.SMALL_ONETOPPINGS;
		
		toppings=PizzaToppings.EXTRACHEESE;
		
		pizzaQuantity="4";
		
	}

	
	@Test
	public void happyPath() {
    
		happyPath.selectPizza();
		
		happyPath.selectToppings();
		
		happyPath.selectPizzaQuantity();
		
		happyPath.inputUserName();
		
		happyPath.inputUserEmail();
		
		happyPath.inputUserPfoneNumber();
		
		happyPath.selectPayment();
		
		happyPath.plaseOrder();
						
		Assert.assertEquals(happyPath.expectedResult(), happyPath.actualResult());
		
	}
	
	
	public void selectPizza() {
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1()),pizza.getDisplayName());	
	}
	
	public void selectToppings() {
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()),toppings.getDisplayName());
	}
	
	public void selectPizzaQuantity() {
				
		sendKey(driver.findElement(orderPage.getPizzaQantity()),pizzaQuantity);
	}
	
	public void inputUserName() {
		
		sendKey(driver.findElement(orderPage.getName()),getRandomName());
	}
	
	public void inputUserEmail() {
		
		sendKey(driver.findElement(orderPage.getEmail()),getRandomEmail());
		
	}
	
	public void inputUserPfoneNumber() {
		
		sendKey(driver.findElement(orderPage.getPhone()),getRandomPhoneNumber());
	}
	
	public void selectPayment() {

		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));
		
	}
	
	public void plaseOrder() {
		
		driver.findElement(orderPage.getPlaceOrderButton()).click();
		
	}
	
	public String expectedResult() {
		
		String expectedOrderResult="Thank you for your order! TOTAL: "+getOrderPrice(Integer.parseInt(pizzaQuantity)*pizza.getCost())+" "+pizza.getDisplayName();
		
		return expectedOrderResult;
	}
	
	public String actualResult() {
		
		return driver.findElement(orderPage.getDialogText()).getText();
	}

}
