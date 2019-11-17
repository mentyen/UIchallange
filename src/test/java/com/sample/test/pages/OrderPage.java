package com.sample.test.pages;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.sample.test.demo.TestBase;
import com.sample.test.demo.constants.PizzaToppings;
import com.sample.test.demo.constants.PizzaTypes;

public class OrderPage extends TestBase {

	private By pizza1 = By.id("pizza1Pizza");
	private By pizza1Toppings1 = By.xpath("//div[@id='pizza1']//select[@class='toppings1']");
	private By pizza1Toppings2 = By.xpath("//div[@id='pizza1']//select[@class='toppings2']");
	private By pizzaQantity = By.id("pizza1Qty");
	private By pizzaCost = By.id("pizza1Cost");
	private By name = By.id("name");
	private By email = By.id("email");
	private By phone = By.id("phone");
	private By radioCheckCard = By.id("ccpayment");
	private By radioCash = By.id("cashpayment");
	private By placeOrderButton = By.id("placeOrder");
	private By resetButton = By.id("reset");
	private By dialog = By.id("dialog");
	private By dialogText = By.xpath("//div[@id='dialog']/p");

	public By getPizza1() {
		return pizza1;
	}

	public By getPizza1Toppings1() {
		return pizza1Toppings1;
	}

	public By getPizza1Toppings2() {
		return pizza1Toppings2;
	}

	public By getPizzaQantity() {
		return pizzaQantity;
	}

	public By getPizzaCost() {
		return pizzaCost;
	}

	public By getName() {
		return name;
	}

	public By getEmail() {
		return email;
	}

	public By getPhone() {
		return phone;
	}

	public By getRadioCheckCard() {
		return radioCheckCard;
	}

	public By getRadioCash() {
		return radioCash;
	}

	public By getPlaceOrderButton() {
		return placeOrderButton;
	}

	public By getResetButton() {
		return resetButton;
	}

	public By getDialog() {
		return dialog;
	}

	public By getDialogText() {
		return dialogText;
	}
	
	public void selectPizza(String pizzaName) {

		if (!pizzaName.isEmpty()) {
			selectValueFromDD(driver.findElement(getPizza1()), pizzaName);
		} else {

		}

	}

	public void selectToppings(String pizzaToppingName) {

		if (!pizzaToppingName.isEmpty()) {
			selectValueFromDD(driver.findElement(getPizza1Toppings1()), pizzaToppingName);
		} else {

		}
	}

	public void selectPizzaQuantity(String pizzaQantity) {

		if (!pizzaQantity.isEmpty()) {
			sendKey(driver.findElement(getPizzaQantity()), pizzaQantity);
		} else {

		}

	}

	public void inputUserName() {

		sendKey(driver.findElement(getName()), getRandomName());
	}

	public void inputUserEmail() {

		sendKey(driver.findElement(getEmail()), getRandomEmail());

	}

	public void inputUserPfoneNumber() {

		sendKey(driver.findElement(getPhone()), getRandomPhoneNumber());
	}

	public void selectPayment(String paymentType) {

		if (!paymentType.isEmpty()) {
			if (paymentType.equalsIgnoreCase("card")) {
				radioButtonClick(driver.findElement(getRadioCheckCard()));
			} else {
				radioButtonClick(driver.findElement(getRadioCash()));
			}
		}

	}

	public void plaseOrder() {

		driver.findElement(getPlaceOrderButton()).click();

	}

	public String expectedResult(double pizzaCost, String pizzaName, String pizzaQuantity) {

		String expectedResult="Thank you for your order! TOTAL: " + getOrderPrice(Integer.parseInt(pizzaQuantity) * pizzaCost)+" "+pizzaName;
		
		return expectedResult;

	}

	public String actualResult() {

		return driver.findElement(getDialogText()).getText();
	}

}
