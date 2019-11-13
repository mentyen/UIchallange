package com.sample.test.demo.tests;

import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

import junit.framework.Assert;

public class TC_02_ValidatingOrderCostFunctionality extends TestBase{
	
	//this test case in real life will be done manually and log as a bug
		//quantity window should not be able to accept negative value
	
	@Test
	public void validatingOrderCostFunctionality() {
		
		OrderPage orderPage=new OrderPage();
		
		PizzaTypes pizza=PizzaTypes.SMALL_ONETOPPINGS;
		
		PizzaToppings toppings=PizzaToppings.EXTRACHEESE;
		
		
		driver.findElement(orderPage.getResetButton()).click();
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1()),pizza.getDisplayName());		
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()),toppings.getDisplayName());		
		
		//passing negative order quantity
		
		String num=getNegativeRandomNumber();
		
		//Assert.assertTrue("Quantity shoud be greater then 0", Integer.parseInt(num)>0);
		
		sendKey(driver.findElement(orderPage.getPizzaQantity()),num);
		
		
		String name=getRandomName();
		
		Assert.assertTrue("UserName should not be null", !name.isEmpty());
		
		sendKey(driver.findElement(orderPage.getName()),name);
		
				
		sendKey(driver.findElement(orderPage.getEmail()),getRandomEmail());
		
		
		String phoneNumber=getRandomPhoneNumber();
		
		Assert.assertTrue("PfoneNumber should not be null",!phoneNumber.isEmpty());
				
		sendKey(driver.findElement(orderPage.getPhone()),phoneNumber);	
		
		
		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));
						
		boolean payment=getPaymentOptionInformation(driver.findElement(orderPage.getRadioCheckCard()),driver.findElement(orderPage.getRadioCash()));
				
		Assert.assertTrue("One of the payment method needs to be selected", payment);
				
		
		driver.findElement(orderPage.getPlaceOrderButton()).click();
							
		double actualResult=Double.parseDouble(driver.findElement(orderPage.getDialogText()).getText().substring(33));
		
		boolean orderCost=true;
				
		if(actualResult<=0) {
			orderCost=false;
		}
			
		Assert.assertTrue("Order cost should not be less then zero",orderCost);
		
		
		String actual = driver.findElement(orderPage.getDialogText()).getText();

		String expected = "Thank you for your order! TOTAL: " + getOrderPrice(Integer.parseInt(num) * pizza.getCost())
				+ " " + pizza.getDisplayName();

		Assert.assertEquals(expected, actual);
		
	}

}
