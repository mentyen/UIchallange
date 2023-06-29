package com.cucumber.pages;

import com.cucumber.utilities.DBreader;
import com.cucumber.utilities.GenericMethods;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomePage extends GenericMethods {

	public HomePage(WebDriver driver) {
		super(driver);
		dbReader = new DBreader(driver);
	}
	DBreader dbReader ;

	By searchBox = By.xpath("//form[@name='ybar_mod_searchbox_s']/input[@name='p']");
	By searchBoxF = By.xpath("//form[@name='ybar_mod_searchbox_f']/input[@name='p']");
	By secret = By.xpath("//input[@id='password']");
	By searchBtn = By.xpath("//button[@id='ybar-search']");
	By searchResults=By.xpath("//span[contains(text(),'search results')]");

	public void setValueInToSearchBox(String value) {
		setInputValue(searchBox, value);
	}
	
	public void setPass(String password) {
		setInputValuePW(secret, decrypt(password, getKey()));
	}

	public void tapOnSearchIcon() {
		clickElement(searchBtn, "Search Icon Button");
	}

	public void isCurrentPageHomePage() {
		if(isElementVisible(searchBox, 25)){
			log("User lands on a home page",true);
		}else{
			logFail("Fail to land on a home page",true,true);
		}
	}

    public void actAs(String user) {
		if(user.contains("general")){
			setInputValue(searchBoxF, "fail me");
		}
    }

	public void isCurrentPageResultPage() {
		if(isElementVisible(searchResults,5)){
			log("PASS",true);
		}else{
			logFail("FAIL",true,true);
		}
	}
	private final String contentFilesPath = "Page" + File.separator + "UPA" + File.separator;
	public void validatePageContent(){

		verifyContentFromFile(contentFilesPath + "PayerTab.yml");
		initValidationStatus();
		softAssertPresentValidation(searchResults, "ACH Radio");
		softAssertPresentValidation(searchResults, "VCP Radio");
		verifyContentFromFileSoft(contentFilesPath + "PayerTab.yml");
		//verifyContentFromFileSoft(contentFilesPath + "NPILevelVoidBlock.yml", getContentVariablesForVoidPage());
		validateStatus();
	}
	private String npiPayer=null;
	private Map<String, String> trimAndReturn(Map<String, String> map) {
		Map<String, String> trimedMap = new HashMap<>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (!isEmpty(String.valueOf(entry.getValue()))) {
				trimedMap.put(entry.getKey(), entry.getValue().trim());
			} else {
				trimedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return trimedMap;
	}
//	private Map<String, String> getContentVariablesForVoidPage() {
//		String npi=npiPayer.split("-")[0];
//		setRunTimeProperty("npi",npi);
//		List<Map<String,String>> response= dbReader.fetchAllRecords(QUERY.select_all_from);
//		Map<String, String> expectedDB =new HashMap<>();
//		if (!response.isEmpty()) {
//			expectedDB=trimAndReturn(response.get(0));
//		} else {
//			logWarning("Fail to pull data from the DB",false);
//			Assert.fail("Fail to get information from DB using query : "+replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
//			//hard stop
//		}
//		Map<String, String> contentVariables = new HashMap<String, String>();
//		//active info
//		contentVariables.put("rte_trns_nbr_act",  getRunTimeProperty("rte_trns_nbr_act"));
//		contentVariables.put("bnk_acct_nbr_act",  getRunTimeProperty("bnk_acct_nbr_act"));
//		contentVariables.put("bnk_nm_act",  getRunTimeProperty("bnk_nm_act"));
//		contentVariables.put("adr_txt_act",  getRunTimeProperty("adr_txt_act"));
//		contentVariables.put("cty_st_zip_act",  getRunTimeProperty("cty_nm_act")+", "+getRunTimeProperty("st_nm_act")+" "+getRunTimeProperty("zip_cd_act"));
//		contentVariables.put("payer",  getRunTimeProperty("select_payer_act"));
//		contentVariables.put("npi",  expectedDB.get("npi_nbr") + " " + expectedDB.get("prov_pay_unit_nm").trim());
//		//pending info
//		contentVariables.put("bnk_nm_pending",  getRunTimeProperty("bnk_nm_pend"));
//		contentVariables.put("adr_txt_pending",  getRunTimeProperty("adr_txt_pend"));
//		contentVariables.put("rtn_pending",  getRunTimeProperty("rte_trns_nbr_pend"));
//		contentVariables.put("acc_num_pending",  getRunTimeProperty("bnk_acct_nbr_pend"));
//		contentVariables.put("first_last",  getRunTimeProperty("last")+", "+getRunTimeProperty("first"));
//		contentVariables.put("title",  getRunTimeProperty("title"));
//		contentVariables.put("email",  getRunTimeProperty("email"));
//		contentVariables.put("phone",  getRunTimeProperty("phone").substring(0, 3)+"-"+getRunTimeProperty("phone").substring(3, 6)+"-"+getRunTimeProperty("phone").substring(6, 10));
//		return contentVariables;
//	}
}
