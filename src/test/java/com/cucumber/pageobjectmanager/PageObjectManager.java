package com.cucumber.pageobjectmanager;

import com.cucumber.pages.ApiPage;
import com.cucumber.pages.HomePage;
import org.openqa.selenium.WebDriver;

public class PageObjectManager {
    private WebDriver driver;
    private HomePage homePage;
    private ApiPage apiPage;

    public PageObjectManager(WebDriver driver) {
        this.driver = driver;
    }
    public ApiPage getApiPage() {
        return (apiPage == null) ? apiPage = new ApiPage(driver) : apiPage;
    }
    public HomePage getHomePage() {
        return (homePage == null) ? homePage = new HomePage(driver) : homePage;
    }


}