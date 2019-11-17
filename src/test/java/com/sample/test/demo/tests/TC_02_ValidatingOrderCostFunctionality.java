package com.sample.test.demo.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;


public class TC_02_ValidatingOrderCostFunctionality extends OrderPage{
	
	public static PizzaTypes pizza;
	public static PizzaToppings topping1;
	public static PizzaToppings topping2;
	public static OrderPage orderPage;
	public static String pizzaQuantity;
	public static String paymentType;
	
	@BeforeClass
	public void setUp() {	
	
		orderPage=new OrderPage();	
		
		pizza=PizzaTypes.LARGE_TWOTOPPINGS;
		
		topping1=PizzaToppings.EXTRACHEESE;
		
		topping2=PizzaToppings.ITALIANHAM;
		
		pizzaQuantity="-1";
		
		paymentType="card";
		
	}
	
	
	@Test
	public void TC_02_ValidatingOrderCostFunctionalityWithNegativeQuantity() {
		
		orderPage.selectPizza(pizza.getDisplayName());
		
		orderPage.selectToppings(topping1.getDisplayName());
		
		orderPage.selectToppings(topping2.getDisplayName());
		
		orderPage.selectPizzaQuantity(pizzaQuantity);
		
		orderPage.inputUserName();
		
		orderPage.inputUserEmail();
		
		orderPage.inputUserPfoneNumber();
		
		orderPage.selectPayment(paymentType);
		
		orderPage.plaseOrder();

		Assert.assertNotEquals(orderPage.actualResult(), orderPage.expectedResult(pizza.getCost(), pizza.getDisplayName(), pizzaQuantity));
		
	}	

	

}
