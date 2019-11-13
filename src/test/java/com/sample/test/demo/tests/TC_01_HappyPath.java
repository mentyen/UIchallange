package com.sample.test.demo.tests;

import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

import junit.framework.Assert;

public class TC_01_HappyPath extends TestBase{
	
	//If Business requirements needs to automate all of the pizza variations it could be done in different classes and group in smoke
	
	@Test
	public void happyPath() {
		
		OrderPage orderPage=new OrderPage();
		
		PizzaTypes pizza=PizzaTypes.SMALL_ONETOPPINGS;
		
		PizzaToppings toppings=PizzaToppings.EXTRACHEESE;
		
		
		driver.findElement(orderPage.getResetButton()).click();
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1()),pizza.getDisplayName());		
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()),toppings.getDisplayName());		
		
			
		String num=getRandomNumber();
		
		Assert.assertTrue("Quantity shoud be greater then 0", Integer.parseInt(num)>0);
		
		sendKey(driver.findElement(orderPage.getPizzaQantity()),num);
		
		
		String name=getRandomName();
		
		Assert.assertTrue("UserName should not be null", !name.isEmpty());
		
		sendKey(driver.findElement(orderPage.getName()),name);
		
				
		sendKey(driver.findElement(orderPage.getEmail()),getRandomEmail());
		
		
		String phoneNumber=getRandomPhoneNumber();
		
		Assert.assertTrue("PfoneNumber should not be null",!phoneNumber.isEmpty());
				
		sendKey(driver.findElement(orderPage.getPhone()),getRandomPhoneNumber());	
		
		
		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));
				
		boolean flag=getPaymentOptionInformation(driver.findElement(orderPage.getRadioCheckCard()),driver.findElement(orderPage.getRadioCash()));
				
		Assert.assertTrue("One of the payment method needs to be selected", flag);
		
		
		driver.findElement(orderPage.getPlaceOrderButton()).click();
					
		String actual=driver.findElement(orderPage.getDialogText()).getText();
						
		String expected="Thank you for your order! TOTAL: "+getOrderPrice(Integer.parseInt(num)*pizza.getCost())+" "+pizza.getDisplayName();
		
		Assert.assertEquals(expected, actual);
			
		
	}

}
