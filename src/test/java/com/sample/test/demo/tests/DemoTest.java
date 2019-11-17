package com.sample.test.demo.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;



public class DemoTest extends OrderPage {
	
	public static PizzaTypes pizza;
	public static PizzaToppings toppings;
	public static OrderPage orderPage;
	public static TC_01_HappyPath happyPath;
	public String pizzaQuantity;
	
	@BeforeMethod
	public void start() {
		
		happyPath=new TC_01_HappyPath();
		
		orderPage=new OrderPage();	
		
		pizza=PizzaTypes.SMALL_ONETOPPINGS;
		
		toppings=PizzaToppings.EXTRACHEESE;
		
	}
	

	@Test
	public void demoTest(){
	
		DemoTest obj=new DemoTest();
		
		//obj.setUpX();
		//orderPage.setUp();
		
	}
	
	public void setUpX() {
		
		System.out.println(driver.getTitle());
		
		System.out.println(driver.findElement(orderPage.getPizza1()).isDisplayed());
		
	}
	
}
