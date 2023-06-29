package com.cucumber.stepdefinition;
import com.rebar.utilities.configprovider.ConfigProvider;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class StepDefinition extends AbstractSteps {

    public StepDefinition() {
        super();
    }


/////////////////////////////////////////

    @Then("Compare last date processed api response against data base")
    public void compareLastDateProcessedApiResponseAgainstDataBase() {
        getPageObjectManager().getApiPage().compareLastDateProcessedApiResponseAgainstDataBase();
    }
////////////////////////////////////////////

    @Then("Validate home page attributes for {string}")
    public void validateHomePageAttributes(String user) {
        getPageObjectManager().getHomePage().actAs(user);
        getPageObjectManager().getHomePage().setValueInToSearchBox("Elon Musk");
        getPageObjectManager().getHomePage().tapOnSearchIcon();
        getPageObjectManager().getHomePage().isCurrentPageResultPage();
    }

    @Given("User launch the app url")
    public void userLaunchTheAppUrl() {
        if (getPageObjectManager() == null) {
            startDriver();
            getDriver().get(ConfigProvider.getAsString("ApplicationUrl"));
        }
    }

    @And("Verify user lands on a home page")
    public void verifyUserLandsOnAHomePage() {
        getPageObjectManager().getHomePage().isCurrentPageHomePage();
    }
}
