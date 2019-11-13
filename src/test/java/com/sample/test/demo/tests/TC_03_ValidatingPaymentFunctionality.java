package com.sample.test.demo.tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

public class TC_03_ValidatingPaymentFunctionality extends TestBase {
	
	//this test case in real life will be done manually and log as a bug
	//dialog should pop up with information that one of the payment method has to be selected for future validation

	@Test

	public void validatingPaymentFunctionality() {

        OrderPage orderPage=new OrderPage();
		
		PizzaTypes pizza=PizzaTypes.SMALL_ONETOPPINGS;
		
		PizzaToppings toppings=PizzaToppings.EXTRACHEESE;
		
		
		driver.findElement(orderPage.getResetButton()).click();
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1()),pizza.getDisplayName());		
		
		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()),toppings.getDisplayName());		
		
			
		String num=getRandomNumber();
		
		Assert.assertTrue(Integer.parseInt(num)>0,"Quantity shoud be greater then 0");
		
		sendKey(driver.findElement(orderPage.getPizzaQantity()),num);
		
		
		String name=getRandomName();
		
		Assert.assertTrue(!name.isEmpty(),"UserName should not be null");
		
		sendKey(driver.findElement(orderPage.getName()),name);
		
				
		sendKey(driver.findElement(orderPage.getEmail()),getRandomEmail());
		
		
		String phoneNumber=getRandomPhoneNumber();
		
		Assert.assertTrue(!phoneNumber.isEmpty(),"PfoneNumber should not be null");
		
		
		sendKey(driver.findElement(orderPage.getPhone()),getRandomPhoneNumber());	
		
		//none of the payment methods selected	
				
		boolean flag=getPaymentOptionInformation(driver.findElement(orderPage.getRadioCheckCard()),driver.findElement(orderPage.getRadioCash()));
				
		Assert.assertTrue(flag,"One of the payment method needs to be selected");
		

		driver.findElement(orderPage.getPlaceOrderButton()).click();

		String actual = driver.findElement(orderPage.getDialogText()).getText();

		String expected = "Thank you for your order! TOTAL: " + getOrderPrice(Integer.parseInt(num) * pizza.getCost())
				+ " " + pizza.getDisplayName();

		Assert.assertEquals(expected, actual);

	}

}
