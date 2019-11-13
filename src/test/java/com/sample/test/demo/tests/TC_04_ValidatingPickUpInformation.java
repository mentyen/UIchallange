package com.sample.test.demo.tests;

import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

import junit.framework.Assert;

public class TC_04_ValidatingPickUpInformation extends TestBase {

	@Test
	public void  missingPhoneNumber() {

		OrderPage orderPage = new OrderPage();

		PizzaTypes pizza = PizzaTypes.SMALL_NOTOPPINGS;

		//PizzaToppings toppings = PizzaToppings.ITALIANHAM;
		

		driver.findElement(orderPage.getResetButton()).click();

		selectValueFromDD(driver.findElement(orderPage.getPizza1()), pizza.getDisplayName());

		//selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()), toppings.getDisplayName());
		

		String num = getRandomNumber();

		Assert.assertTrue("Quantity shoud be greater then 0", Integer.parseInt(num) > 0);

		sendKey(driver.findElement(orderPage.getPizzaQantity()), num);
		

		String name = getRandomName();

		Assert.assertTrue("UserName should not be null", !name.isEmpty());

		sendKey(driver.findElement(orderPage.getName()), name);
		
		//phone number does not get entered in to expected field
		
		sendKey(driver.findElement(orderPage.getEmail()), getRandomEmail());		

		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));		

		driver.findElement(orderPage.getPlaceOrderButton()).click();
		

		String actual = driver.findElement(orderPage.getDialogText()).getText();

		String expected = "Missing phone number";

		Assert.assertEquals(expected, actual);

	}
	
	@Test
	public void missingUserName() {

		OrderPage orderPage = new OrderPage();

		PizzaTypes pizza = PizzaTypes.SMALL_ONETOPPINGS;

		PizzaToppings toppings = PizzaToppings.EXTRACHEESE;
		

		driver.findElement(orderPage.getResetButton()).click();

		selectValueFromDD(driver.findElement(orderPage.getPizza1()), pizza.getDisplayName());

		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()), toppings.getDisplayName());
		

		String num = getRandomNumber();

		Assert.assertTrue("Quantity shoud be greater then 0", Integer.parseInt(num) > 0);

		sendKey(driver.findElement(orderPage.getPizzaQantity()), num);
		
	    //userName does not get entered in to expected field
					
		sendKey(driver.findElement(orderPage.getEmail()), getRandomEmail());	
		
		
        String phoneNumber=getRandomPhoneNumber();
		
		Assert.assertTrue("PfoneNumber should not be null",!phoneNumber.isEmpty());
				
		sendKey(driver.findElement(orderPage.getPhone()),getRandomPhoneNumber());	
		

		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));		

		driver.findElement(orderPage.getPlaceOrderButton()).click();
		

		String actual = driver.findElement(orderPage.getDialogText()).getText();

		String expected = "Missing name";

		Assert.assertEquals(expected, actual);

	}

}
