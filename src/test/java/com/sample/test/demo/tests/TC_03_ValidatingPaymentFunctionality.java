package com.sample.test.demo.tests;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;
import com.sample.test.pages.OrderPage;

public class TC_03_ValidatingPaymentFunctionality extends TestBase {

	// this test case in real life will be done manually and log as a bug
	// dialog should pop up with information that one of the payment method has to
	// be selected for future validation
	
	public static PizzaTypes pizza;
	public static PizzaToppings toppings;
	public static OrderPage orderPage;
	public static TC_03_ValidatingPaymentFunctionality tc_03_ValidatingPaymentFunctionality;
	public static String pizzaQuantity;

	@BeforeClass
	public void start() {
		
		pizza = PizzaTypes.SMALL_NOTOPPINGS;
		
		orderPage = new OrderPage();
		
		tc_03_ValidatingPaymentFunctionality = new TC_03_ValidatingPaymentFunctionality();
		
		pizzaQuantity = "4";
	}

	@Test

	public void TC_03_ValidatingPaymentFunctionalityBySelectingBothPayments() {

		tc_03_ValidatingPaymentFunctionality.selectPizza();
		
		tc_03_ValidatingPaymentFunctionality.selectPizzaQuantity();
		
		tc_03_ValidatingPaymentFunctionality.inputUserName();
		
		tc_03_ValidatingPaymentFunctionality.inputUserEmail();
		
		tc_03_ValidatingPaymentFunctionality.inputUserPfoneNumber();
		
		tc_03_ValidatingPaymentFunctionality.selectPayment("card");
		
		tc_03_ValidatingPaymentFunctionality.selectPayment("cash");
		
		tc_03_ValidatingPaymentFunctionality.plaseOrder();

		Assert.assertNotEquals(tc_03_ValidatingPaymentFunctionality.expectedResult(), tc_03_ValidatingPaymentFunctionality.expectedResult(),
				"Both payments can not be selected");

	}

	@Test

	public void TC_03_ValidatingPaymentFunctionalityByNotSelectingAnyOfThePayments() throws InterruptedException {

		tc_03_ValidatingPaymentFunctionality.selectPizza();
		
		tc_03_ValidatingPaymentFunctionality.selectPizzaQuantity();
		
		tc_03_ValidatingPaymentFunctionality.inputUserName();
		
		tc_03_ValidatingPaymentFunctionality.inputUserEmail();
		
		tc_03_ValidatingPaymentFunctionality.inputUserPfoneNumber();
		
		tc_03_ValidatingPaymentFunctionality.plaseOrder();

		Assert.assertNotEquals(tc_03_ValidatingPaymentFunctionality.expectedResult(), tc_03_ValidatingPaymentFunctionality.expectedResult(),
				"Please choose one of the payment from the option list");

	}

	public void selectPizza() {

		selectValueFromDD(driver.findElement(orderPage.getPizza1()), pizza.getDisplayName());
	}

	public void selectToppings() {

		selectValueFromDD(driver.findElement(orderPage.getPizza1Toppings1()), toppings.getDisplayName());
	}

	public void selectPizzaQuantity() {

		pizzaQuantity = "2";

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

	public void selectPayment(String paymentType) {

		if (paymentType.equalsIgnoreCase("card")) {
			radioButtonClick(driver.findElement(orderPage.getRadioCheckCard()));

		}
		if (paymentType.equalsIgnoreCase("cash")) {
			radioButtonClick(driver.findElement(orderPage.getRadioCash()));
		}
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

		String actualOrderResult = orderPage.getDialogText().toString();

		return actualOrderResult;
	}

}
