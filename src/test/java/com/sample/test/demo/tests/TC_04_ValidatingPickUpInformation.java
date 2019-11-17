package com.sample.test.demo.tests;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

import org.testng.Assert;

public class TC_04_ValidatingPickUpInformation extends OrderPage {

	public static PizzaTypes pizza;
	public static PizzaToppings topping1;
	public static PizzaToppings topping2;
	public static OrderPage orderPage;
	public static TC_04_ValidatingPickUpInformation tc_04_objvalidatingPickUpFunctionality;
	public static String pizzaQuantity;

	@BeforeClass
	public void setUp() {

		tc_04_objvalidatingPickUpFunctionality = new TC_04_ValidatingPickUpInformation();

		orderPage = new OrderPage();

		pizza = PizzaTypes.LARGE_TWOTOPPINGS;

		topping1 = PizzaToppings.ONIONS;

		topping2 = PizzaToppings.PROVOLNE;

		pizzaQuantity = "5";

	}

	@Test
	public void TC_04_ValidatingPickUpInformationByNotProvidingPhoneNumber() {

		tc_04_objvalidatingPickUpFunctionality.selectPizza();

		tc_04_objvalidatingPickUpFunctionality.selectToppings1(topping1.getDisplayName());

		tc_04_objvalidatingPickUpFunctionality.selectToppings2(topping2.getDisplayName());

		tc_04_objvalidatingPickUpFunctionality.selectPizzaQuantity();

		tc_04_objvalidatingPickUpFunctionality.inputUserName();

		tc_04_objvalidatingPickUpFunctionality.inputUserEmail();

		tc_04_objvalidatingPickUpFunctionality.plaseOrder();

		Assert.assertEquals(tc_04_objvalidatingPickUpFunctionality.actualResult(), "Missing phone number");

	}

	@Test
	public void TC_04_ValidatingPickUpInformationByNotProvidingUserName() {

		tc_04_objvalidatingPickUpFunctionality.selectPizza();

		tc_04_objvalidatingPickUpFunctionality.selectToppings1(topping1.getDisplayName());

		tc_04_objvalidatingPickUpFunctionality.selectToppings2(topping2.getDisplayName());

		tc_04_objvalidatingPickUpFunctionality.selectPizzaQuantity();

		tc_04_objvalidatingPickUpFunctionality.inputUserPfoneNumber();

		tc_04_objvalidatingPickUpFunctionality.inputUserEmail();

		tc_04_objvalidatingPickUpFunctionality.plaseOrder();

		Assert.assertEquals(tc_04_objvalidatingPickUpFunctionality.actualResult(), "Missing name");

	}

	public void selectPizza() {

		driver.findElement(orderPage.getResetButton()).click();

		selectValueFromDD(driver.findElement(orderPage.getPizza1()), pizza.getDisplayName());
	}

	public void selectToppings1(String toppingsName) {

		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()), toppingsName);
	}

	public void selectToppings2(String toppingsName) {

		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings2()), toppingsName);
	}

	public void selectPizzaQuantity() {

		sendKey(driver.findElement(orderPage.getPizzaQantity()), pizzaQuantity);
	}

	public void inputUserName() {

		sendKey(driver.findElement(orderPage.getName()), getRandomName());
	}

	public void inputUserEmail() {

		sendKey(driver.findElement(orderPage.getEmail()), getRandomEmail());

	}

	public void inputUserPfoneNumber() {

		sendKey(driver.findElement(orderPage.getPhone()), getRandomPhoneNumber());
	}

	public void selectPayment() {

		radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));

	}

	public void plaseOrder() {

		driver.findElement(orderPage.getPlaceOrderButton()).click();

	}

	public String expectedResult() {

		String expectedOrderResult = "Thank you for your order! TOTAL: "
				+ getOrderPrice(Integer.parseInt(pizzaQuantity) * pizza.getCost()) + " " + pizza.getDisplayName();

		return expectedOrderResult;
	}

	public String actualResult() {

		return driver.findElement(orderPage.getDialogText()).getText();
	}

}
