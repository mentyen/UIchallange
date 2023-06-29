package com.cucumber.stepdefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.rebar.utilities.Log;
import com.cucumber.utilities.ReadExcel;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;

public class DefaultStepDefinition {

	private static ThreadLocal<String> testDataId = new ThreadLocal<>();
	private static ThreadLocal<String> scenarioName = new ThreadLocal<>();
	private static ThreadLocal<String> excel = new ThreadLocal<>();
	private static ThreadLocal<String> sheet = new ThreadLocal<>();
	private static ThreadLocal<ArrayList<Map<String, String>>> excelData = new ThreadLocal<ArrayList<Map<String, String>>>() {
		@Override
		protected ArrayList<Map<String, String>> initialValue() {
			return new ArrayList<>();
		}
	};

	public static ThreadLocal<Map<String, String>> currentIterationMap = new ThreadLocal<Map<String, String>>();

	@Before
	public void readScenarioName(Scenario scenario) {
		testDataId.set(scenario.getName());
	}	

	@Given("^A workbook named \"([^\"]*)\" and sheet named \"([^\"]*)\" is read$")
	public synchronized void a_workbook_named_and_sheet_named_is_read(String excelName, String sheetName) {
		//currentIterationMap = null;
		if (scenarioName.get() == null || !sheetName.equals(sheet.get()) || !excelName.equals(excel.get()) || !testDataId.get().equals(scenarioName.get())) {
			excelData.get().addAll(ReadExcel.readData(excelName, sheetName));
		}

		List<Map<String, String>> removeData = new ArrayList<>();
		for (Map<String, String> map : excelData.get()) {
			if (map.get("TestDataID").equals(testDataId.get())) {
				currentIterationMap.set(map);
				removeData.add(map);
				break;
			}
		}
		if(!removeData.isEmpty()){
		excelData.get().remove(removeData.get(0));
		}
		scenarioName.set(testDataId.get());
		sheet.set(sheetName);
		excel.set(excelName);
	}
}