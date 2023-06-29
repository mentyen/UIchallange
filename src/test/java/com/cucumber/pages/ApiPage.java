package com.cucumber.pages;

import com.cucumber.utilities.*;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.cucumber.utilities.GenericMethods;
import com.rebar.utilities.Log;
import com.rebar.utilities.extentreports.ExtentTestManager;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.math.BigDecimal;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.given;

public class ApiPage extends GenericMethods {

  public ApiPage(WebDriver driver) {
        super(driver);
        dbReader = new DBreader(driver);
    }
   // private String baseUrl=null;
    private final DBreader dbReader;
    Response response = null;

    private void isEmpty(String input, String description) {
            if (empty(input))
                logFail(description + " should not be null, please check your data provider", false,true);
    }
    public void logWarning(Object msg) {
        Log.info(String.valueOf(msg));
        ExtentTestManager.getTest().warning(String.valueOf(msg));
    }
    public void logJson(Object msg) {
        ExtentTestManager.getTest().info(MarkupHelper.createCodeBlock(String.valueOf(msg), CodeLanguage.JSON));
    }

    public void getResponse(String endpoint, String bearerToken) {
        isEmpty(endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");

        setBaseUrl();

        response = given().headers("Authorization", "Bearer " + bearerToken, "Content-type", "application/json")
                .when().get(endpoint);

        log("GET call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint+ "</font></i></b>");
        //String str = String.valueOf(response.body().asString());
        //int status = response.getStatusCode();
        if (response.body().asString().length() < 5000) {
            logJson(response.body().asString());
            Log.info(response.body().asString());
        }
    }

    public void getResponse1(String endpoint, String bearerToken) {
        isEmpty(endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");

        setBaseUrl();
        String endpoint1=replaceArgumentsWithRunTimeProperties(endpoint);

        response = given().headers("Authorization", "Bearer " + bearerToken, "Content-type", "application/json")
                .when().get(endpoint1);

        log("GET call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint1+ "</font></i></b>");

        if (response.body().asString().length() < 5000) {
            if(!empty(response.body().asString())){
                logJson(response.body().asString());
                Log.info(response.body().asString());
            }else{
                consoleLog("Response return body as null");
                logInfo("Response return body as null",false);
            }
        }
    }

    public void getResponsePut(String endpoint, String bearerToken) {
        isEmpty(endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");

        setBaseUrl();
        String endpoint1=replaceArgumentsWithRunTimeProperties(endpoint);
        String payload=getPutPayloadForYieldAdjustment();

        response = given().headers("Authorization", "Bearer " + bearerToken, "Content-type", "application/json")
                .and().body(payload).when().put(endpoint1);

        log("PUT call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint1+ "</font></i></b>");
        log("Payload : <b><i><font color=blue>"  + payload+ "</font></i></b>");

        if (response.body().asString().length() < 5000) {
            if(!empty(response.body().asString())){
                logJson(response.body().asString());
                Log.info(response.body().asString());
            }else{
                consoleLog("Response return body as null");
                logInfo("Response return body as null",false);
            }
        }
    }

    private String getPutPayloadForYieldAdjustment() {
        String payload=null;
        payload="[{\"adjustId\":"+getRunTimeProperty("adjust_id")+"}]";
        return payload;
    }

    public void getResponsePost(String endpoint, String bearerToken) {
        isEmpty(endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");

        setBaseUrl();
        String endpoint1=replaceArgumentsWithRunTimeProperties(endpoint);

        response = given().headers("Authorization", "Bearer " + bearerToken, "Content-type", "application/json")
                .and().body(postPayloadForSingleYieldAdjustment).when().post(endpoint1);

        log("PUT call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint1+ "</font></i></b>");
        log("Payload : <b><i><font color=blue>"  + postPayloadForSingleYieldAdjustment+ "</font></i></b>");

        if (response.body().asString().length() < 5000) {
            if(!empty(response.body().asString())){
                logJson(response.body().asString());
                Log.info(response.body().asString());
            }else{
                consoleLog("Response return body as null");
                logInfo("Response return body as null",false);
            }
        }
    }

    public void getResponsePatch(String _endpoint, String bearerToken) {
        isEmpty(_endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");

        setBaseUrl();
        String endpoint=replaceArgumentsWithRunTimeProperties(_endpoint);

        response = given().headers("Authorization", "Bearer " + bearerToken, "Content-type", "application/json")
                .and().body(calculatedYieldsPayload).when().patch(endpoint);

        log("PATCH call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint+ "</font></i></b>");
        log("Payload : <b><i><font color=blue>"  + calculatedYieldsPayload+ "</font></i></b>");

        if (response.body().asString().length() < 5000) {
            if(!empty(response.body().asString())){
                logJson(response.body().asString());
                Log.info(response.body().asString());
            }else{
                consoleLog("Response return body as null");
                logInfo("Response return body as null",false);
            }
        }
    }

    public void setPostPayloadForSingleYieldAdjustment(String ticker, String _date, String adjustAmount, String numberOfDays, String modifiedBy,String adjustComment) {
        initPostPayloadForSingleYieldAdjustment();

        if (adjustComment.contains("null") || adjustComment.isEmpty()) {
          consoleLog("adjust_comment : "+adjustComment);
        }else{
            adjustComment = adjustComment.concat(getRandomString(10)).toUpperCase();
            setRunTimeProperty("adjust_comment", adjustComment);
        }

        String date=null;
        if (_date.startsWith("T")) {
            if(_date.contains("TU")){
                //unexpected format
                //date = getDate(_date.replace("U",""), "yyyy-dd-MMM");
            }else{
                //date = getDate(_date, "dd-MMM-yyyy");
            }
        } else {
            date = _date;
        }
        setPostPayload(ticker, date, adjustAmount, numberOfDays, adjustComment, modifiedBy);

        setRunTimeProperty("ticker", ticker);
        setRunTimeProperty("begin_Date", date);
        setRunTimeProperty("adjustAmount", adjustAmount);
        setRunTimeProperty("numberOfDays", numberOfDays);
        setRunTimeProperty("modifiedBy", modifiedBy);
    }

    private String postPayloadForSingleYieldAdjustment=null;
    private void initPostPayloadForSingleYieldAdjustment() {
        postPayloadForSingleYieldAdjustment=new String();
    }

    private String calculatedYieldsPayload=null;
    private void initCalculatedYieldsPayload() {
        calculatedYieldsPayload=new String();
    }

    private void setPostPayload(String ticker,String date,String adjustAmount,String numberOfDays,String adjustComment,String modifiedBy) {
        postPayloadForSingleYieldAdjustment="{\n" +
                "    \"ticker\": \""+ticker+"\",\n" +
                "    \"beginDate\": \""+date+"\",\n" +
                "    \"adjustAmount\": \""+adjustAmount+"\",\n" +
                "    \"numberOfDays\": \""+numberOfDays+"\",\n" +
                "    \"adjustComment\": \""+adjustComment+"\",\n" +
                "    \"modifiedBy\": \""+modifiedBy+"\"\n" +
                "}";
    }

    public void getResponseForceDownload(String init_endpoint, String bearerToken) {
        isEmpty(init_endpoint, "Endpoint");
        isEmpty(bearerToken, "bearerToken");
        String endpoint=replaceArgumentsWithRunTimeProperties(init_endpoint);

        setBaseUrl();
        response = given()
                .header("Authorization", "Bearer " + bearerToken)
                .header( "Content-type", "application/force-download")
                .header("Accept-ranges","bytes")
                .header("Cache-control","no-cache, no-store, must-revalidate")
                .header("Content-security-policy","frame-ancestors 'self'")
                .header("Content-Disposition","attachment; filename=TEST.xlsx")
                .when().get(endpoint);

        log("GET call : <b><i><font color=blue>" + getRunTimeProperty("baseURI") + endpoint+ "</font></i></b>");

    }

    private void setBaseUrl() {
        isEmpty(getRunTimeProperty("appUrl"), "appUrlSfdm");
        String baseUrl=getRunTimeProperty("appUrl")+"v1/";
        setRunTimeProperty("baseURI",baseUrl);
        RestAssured.baseURI = baseUrl;
    }

    public void isHeaders(String ct) {
        isEmpty(ct,"Content Type ");

        String contentType = response.header("Content-Type");
        Log.info("Content Type is:-->" + contentType);

        if (contentType.trim().equalsIgnoreCase(ct.trim())) {
            log("Expected Content Type is:--><b><i><font color=blue>" + ct
                    + "</font></i></b> >>> Actual Content Type is:--><b><i><font color=blue>" + contentType
                    + "</font></i></b>");
        } else {
            logFail("Expected Content Type is:--><b><i><font color=blue>" + ct
                    + "</font></i></b> >>> Actual Content Type is:--><b><i><font color=blue>" + contentType
                    + "</font></i></b>",false, true);
        }
    }

    public void isResponseTime(String expectedResponseTime) {
        isEmpty(expectedResponseTime,"Expected Response Time");

        long rt = StringUtils.getLong(expectedResponseTime);
        long responseTime = response.timeIn(TimeUnit.MILLISECONDS);
        Log.info("Response Time is:-->" + responseTime);

        if (responseTime > rt) {
            logWarning("Expected Response Time is:--><b><i><font color=blue>" + rt
                    + "</font></i></b> >>> Actual Response Time is:--><b><i><font color=blue>" + responseTime
                    + "</font></i></b>");
        } else {
            log("Expected Response Time is:--><b><i><font color=blue>" + rt
                    + "</font></i></b> >>> Actual Response Time is:--><b><i><font color=blue>" + responseTime
                    + "</font></i></b>");
        }

    }

    public void isStatus(String expectedStatus) {
        isEmpty(expectedStatus,"Expected Status");
        int stExpected = StringUtils.getInteger(expectedStatus);
        int stActual = response.getStatusCode();
        Log.info("Response Status is:-->" + stActual);
        Log.info("Expected Status is:-->" + stExpected);
        setRunTimeProperty("status",String.valueOf(stActual));

        if (stActual == 204) {
            //were is no data in DB
            logInfo("Expected Status is:--><b><i><font color=blue>" + stExpected
                    + "</font></i></b> >>> Actual Status is:--><b><i><font color=green>" + stActual
                    + "</font></i></b>", false);
        }else{

            if (stExpected != 200) {
                if (stExpected != stActual) {
                    getErrorMessage();
                    logFail("Expected Status is:--><b><i><font color=blue>" + stExpected
                                    + "</font></i></b> >>> Actual Status is:--><b><i><font color=red>" + stActual + "</font></i></b>",false,
                            true);
                } else {
                    log("Expected Status is:--><b><i><font color=blue>" + stExpected
                            + "</font></i></b> >>> Actual Status is:--><b><i><font color=green>" + stActual
                            + "</font></i></b>", false);
                }

            } else {

                if (stExpected != stActual) {
                    getErrorMessage();
                    logFail("Expected Status is:--><b><i><font color=blue>" + stExpected
                                    + "</font></i></b> >>> Actual Status is:--><b><i><font color=red>" + stActual + "</font></i></b>",false,
                            true);
                } else {
                    log("Expected Status is:--><b><i><font color=blue>" + stExpected
                            + "</font></i></b> >>> Actual Status is:--><b><i><font color=blue>" + stActual
                            + "</font></i></b>");
                }
            }
        }

    }

    private void getErrorMessage() {
        logJson(response.body().asString());
        String str = response.body().asString();
        logInfo(str,false);
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject;
            jsonObject = (JSONObject) parser.parse(str);
            String error = (String) jsonObject.get("error");
            String message = (String) jsonObject.get("message");

            logInfo("ERROR:--> <b><i><font color=" + ConfigProvider.getAsString("failColor") + ">" + error.toUpperCase()
                    + "</font></i></b>",false);
            logInfo("MESSAGE:--> <b><i><font color=" + ConfigProvider.getAsString("failColor") + ">"
                    + message.toUpperCase() + "</font></i></b>",false);

        } catch (Exception e) {
            logInfo("Fail to parse response in JsonObject:-->" + e.getMessage(),false);
        }
    }

    public String getEndpoint(String endpoint,String userID) {
        isEmpty(endpoint, "Endpoint");
        setRunTimeProperty("user",userID);
        if(empty(userID)){
            userID="";
        }else{
            userID=userID.trim();
        }
        return endpoint.concat(userID);
    }

    public void compareEntitlementsUserIdApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("entitlementName");
        initDBHashMap();
        getResultFromDB("entitlementName",replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareEntitlementsApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("entitlementName");
        initDBHashMap();
        getResultFromDB("entitlementName",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareUsersApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("userId");
        initDBHashMap();
        getResultFromDB("userId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareProvidedUsersApiVsDataBase() {
        initApiHashMap();
        parceJsonObjToMap("userId");
        initDBHashMap();
        getResultFromDB("userId",replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareTickersApiVsDataBase() {
        initTickersApiObj();
        initTickersDBObj();

        parceJsonToList();
        getResultFromDBAsList(QUERY.select_all_from);

        initValidationStatus();
        //listOfStringMismatch(tickersAPI,tickersDB);
        //validateStaus();
    }

    public void compareYieldAdjustmentsApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareYieldAdjustmentsWithBeginDateApiVsDataBase() {
        initApiHashMap();
        if(!getRunTimeProperty("status").equals("204")){
            parceJsonArrayToMap("adjustId");
        }
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareYieldAdjustmentsWithEndDateApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareYAstartEndDateApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareYAtickerStartEndDateApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareYAAdjustIdApiVsDataBase() {
        initApiHashMap();
       // parceJsonArrayToMap("adjustId");
        parceJsonObjToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareDetailYAAdjustIdApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("adjustId");
        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjustId",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareCalcYieldOnstartEndDateApiVsDataBase() {
        //if where is no records returned by API
        if(response.getStatusCode()!=200){
            initDBHashMap();
            fetchCalculatedYieldsFromDB("closeBusDate_ticker",QUERY.select_all_from);
            if(dbHashMapObj.isEmpty()){
                logInfo("API records size:--> 0",false);
                logInfo("DB records size:-->" + dbHashMapObj.size(),false);
            }else{
                logInfo("API records size:--> 0",false);
                logInfo("DB records size:-->" + dbHashMapObj.size(),false);
                logFail("STATUS : FAIL",true,false);
            }
        }else{
            initApiHashMap();
            parceJsonArrayToMap("closeBusDate_ticker");
            initDBHashMap();
            fetchCalculatedYieldsFromDB("closeBusDate_ticker",QUERY.select_all_from);
            //compareMapContent(apiHashMapObj, dbHashMapObj);
        }
    }

    public void compareCalcYieldOnstartEndDateTickerApiVsDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("closeBusDate_ticker");
        initDBHashMap();
        fetchCalculatedYieldsFromDB("closeBusDate_ticker",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    List<String>tickersDB=null;
    private void initTickersDBObj() {
        tickersDB=new ArrayList<>();
    }

    private void parceJsonToList() {
        JSONArray jsonarray = getRunTimeArray(response);
        Log.info("jsonarray size  : " + jsonarray.size());
        if (!jsonarray.isEmpty()) {
            for (Object ticker : jsonarray) {
                tickersAPI.add(String.valueOf(ticker));
            }
        } else {
            logWarning("API call return Empty JSONArray :--> " + response.getBody().asString());
            //Assert.fail();
        }
    }
    List<String>tickersAPI=null;
    private void initTickersApiObj() {
        tickersAPI=new ArrayList<>();
    }

    private void getResultFromDBAsList(String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        tickersDB = dbReader.getTableDataFromDB(query);
        dbReader.closeConnection();
    }

    private void getResultFromDB(String key,String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbHashMapObj = dbReader.getAllAsMap(query, key);
        dbReader.closeConnection();
    }

    private void getResultFromDBForReport(String key,String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbHashMapObj = dbReader.getAllAsMapForReport(query, key);
        dbReader.closeConnection();
    }
    private void setDBConnection(String env) {
        try {
            switch (env) {
                case "dev":
                    dbReader.getDBConnection(ConfigProvider.getAsString("DBURLGSLT09"), System.getProperty("DBUserNameT09"), System.getProperty("DBSecretT09"));
                    log("<b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">OPEN DB CONNECTION : </font></i></b>"+ ConfigProvider.getAsString("DBURLGSLT09"));
                    break;
                case "qa":
                    dbReader.getDBConnection(ConfigProvider.getAsString("DBURLGSLQA4"), System.getProperty("DBUserNameQA4"), System.getProperty("DBSecretQA4"));
                    log("<b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">OPEN DB CONNECTION : </font></i></b>"+ ConfigProvider.getAsString("DBURLGSLQA4"));
                    break;
                default:
                    AssertionLibrary.assertTrue(false, "Fail to select execution env, please check your NextGen.properties in SfdmEnv : ");
            }

        } catch (Exception e) {
            logFail("<b><i><font color=" + ConfigProvider.getAsString("failColor") + ">FAIL TO CREATE " + ConfigProvider.getAsString("DBURLGSLT09") + " DB CONNECTION :--> " + e.getMessage() + "</font></i></b>",false, true);
        }
    }

    private JSONArray getRunTimeArray(Response response) {
        String str = response.body().asString();
        JSONParser parser = new JSONParser();
        JSONArray jsonarray = null;
        if(empty(str)){
            jsonarray=new JSONArray();
        }else{
            try {
                jsonarray = (JSONArray) parser.parse(str);
            } catch (ParseException e) {
                logFail("Fail to parse json array, exception accrued:-->" + e.getMessage(),false, true);
            }
        }
        return jsonarray;
    }

    private JSONObject getRunTimeObject(Response response) {
        String str = response.body().asString();
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            logFail("Fail to parse json obj, exception :-->" + e.getMessage(),false, true);
        }
        return obj;
    }

    private String getString(String str){
        if(str==null){
            str="null";
        }
        return str;
    }
    List<HashMap<String, String>> apiListOfHashMapObj =null;
    private void initApiListOfHashMap(){
        apiListOfHashMapObj = new ArrayList<>();
    }
    List<HashMap<String, String>> dbListOfHashMapObj =null;
    private void initListOfDBHashMap(){
        dbListOfHashMapObj = new ArrayList<>();
    }
    HashMap<String, HashMap<String, String>> apiHashMapObj =null;
    private void initApiHashMap(){
        apiHashMapObj = new HashMap<>();
    }
    HashMap<String, HashMap<String, String>> dbHashMapObj =null;
    private void initDBHashMap(){
        dbHashMapObj = new HashMap<>();
    }
    private void parceJsonArrayToMap(String _key) {
        String key =null;
        JSONArray jsonarray = getRunTimeArray(response);
        Log.info("JSONArray size  : " + jsonarray.size());
        if (!jsonarray.isEmpty()) {
            for (Object arr : jsonarray) {
                JSONObject jsonObject = (JSONObject) arr;

                HashMap<String, String> temp = getKeyValueFromJsonObj(jsonObject);

                if(_key.equalsIgnoreCase("closeBusDate_ticker")){
                    key = String.valueOf(jsonObject.get("closeBusDate"))+String.valueOf(jsonObject.get("ticker"));
                }else{
                    key = String.valueOf(jsonObject.get(_key));
                }

                if (!empty(key)) {
                    apiHashMapObj.put(key, temp);
                } else {
                    Log.debug(_key+" as key:-->" + key);
                }
            }

        } else {
            logInfo("API call return Empty JSONArray:--> [" + response.getBody().asString()+"]",false);

        }

    }

    private void parceJsonObjToMap(String _key) {
        JSONObject jsonObject = getRunTimeObject(response);
        Log.info("jsonObject size  : " + jsonObject.size());
        if (!jsonObject.isEmpty()) {
            Log.info("jsonObject size  : " + jsonObject.size());
            HashMap<String, String> temp = getKeyValueFromJsonObj(jsonObject);
            String key = String.valueOf(jsonObject.get(_key));
            if (!empty(key)) {
                apiHashMapObj.put(key, temp);
            } else {
                Log.debug(_key+" as key:-->" + key);
            }

        } else {
            logWarning("API call return Empty JSONObject:--> " + response.getBody().asString());
            //Assert.fail();
        }
    }

    public void compareLastDateProcessedApiResponseAgainstDataBase() {
        initApiHashMap();
        parceJsonObjToMap("closeBusDate");
        initDBHashMap();
        getResultFromDB("closeBusDate",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void compareReportsApiResponseAgainstDataBase() {
        initApiHashMap();
        parceJsonArrayToMap("reportId");
        initDBHashMap();
        getResultFromDB("reportId",replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    private HashMap<String, String> getKeyValueFromJsonObj(JSONObject jsonObj) {
        HashMap<String, String> temp = new HashMap<String, String>();
        jsonObj.keySet().forEach(keyStr ->
        {
            Object keyvalue = jsonObj.get(keyStr);
            String value=null;
            try {
            if(keyvalue!=null){
                if(keyvalue instanceof Number){
                    if (keyvalue instanceof Long) {
                        value= getString(keyvalue.toString()).trim();
                    }else if(keyvalue instanceof Double){
                        value=BigDecimalToString(new BigDecimal(keyvalue.toString()));
                        value = formatToStringWithOneFloatingPoint(value);
                    }else if (keyvalue instanceof BigDecimal){
                        value=BigDecimalToString(new BigDecimal(keyvalue.toString()));
                    }
                }else{
                    value= getString(keyvalue.toString()).trim();
                }
            }else{
                value="null";
            }
            } catch (Exception e) {

            }
            temp.put(keyStr.toString().toUpperCase(), value);
            //for nested objects iteration if required
            //if (keyvalue instanceof JSONObject)
            //    printJsonObject((JSONObject)keyvalue);
        });
        return temp;
    }
    private String formatToStringWithOneFloatingPoint(String str) {
        if (!str.contains(".")) {
            return str.concat(".0");
        }
        return str;
    }

    private String BigDecimalToString(Object object) {
        if (object != null) {
            if (object instanceof BigDecimal) {
                double d = ((BigDecimal) object).doubleValue();
                DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                df.setMaximumFractionDigits(340);
                return String.valueOf(df.format(d));
            } else {
                Log.info("object is not instance off BigDecimal:-->" + object);
                return String.valueOf(object);
            }
        } else {
            Log.info("object is null :-->" + object);
            return String.valueOf(object);
        }
    }

    public void compareParametersBasedOnTheReportIdApiResponseAgainstDataBase(String reportID) {
        setRunTimeProperty("reportID",reportID);
        initApiListOfHashMap();
        parceJsonArrayToListOfMaps();
        initListOfDBHashMap();
        getResultFromDBAsListOfHashMap(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        compareListOfMapContent(apiListOfHashMapObj, dbListOfHashMapObj);
    }
    private void compareListOfMapContent(List<HashMap<String, String>> apiListOfMap, List<HashMap<String, String>> dbListOfMap) {
        Log.info("obj Api size:-->" + apiListOfMap.size());
        Log.info("obj dB size:-->" + dbListOfMap.size());

        //will only compare list size based on a report name(some has 1 map, and some has 2 maps)
        if(!apiListOfMap.equals(dbListOfMap)){
            if (apiListOfMap.size() != dbListOfMap.size()) {
                compareListOfMapByKeyValues(apiListOfMap,dbListOfMap);
                logWarning("MISMATCH CONTENT" ,false);
                logFail("MISMATCH SIZE : obj A size:-->" + apiListOfMap.size()+" obj B size:-->" + dbListOfMap.size() ,false,true);
            }else{
                compareListOfMapByKeyValues(apiListOfMap,dbListOfMap);
                logFail("MISMATCH CONTENT" ,false,true);
            }
        }else{
            log("obj A size/content equals to obj B size/content STAUS : PASS");
        }

    }

    private void compareListOfMapByKeyValues(List<HashMap<String, String>> apiListOfMap, List<HashMap<String, String>> dbListOfMap) {
        Map<String,List<Map.Entry<String, String>>> mismatches=new HashMap<>();
        String keyObjA=null;
        boolean flag=false;
        for(HashMap <String,String> objAmap:apiListOfMap){
            for (HashMap<String, String> objBmap : dbListOfMap) {
               List<Map.Entry<String, String>> _mismatches = compareMapsByValues(objAmap, objBmap);
                mismatches.put(keyObjA,_mismatches);
            }
        }

        if(!mismatches.isEmpty()){
            for(Map.Entry<String, List<Map.Entry<String, String>>> entry1 : mismatches.entrySet()){
                String key1 = entry1.getKey();
                for(Map.Entry<String,String> mapEntrys: entry1.getValue()) {
                    logWarning("KEY:["+key1+"] --> CONTENT MISMATCH: ["+mapEntrys.getKey()+"]["+mapEntrys.getValue()+"]");
                };
            }
        }else{
            log("Content comparison status : PASS");
        }

    }

    private void compareListOfMapContent(List<HashMap<String, String>> apiListOfMap, List<HashMap<String, String>> dbListOfMap,String key) {
        Log.info("obj Api size:-->" + apiListOfMap.size());
        Log.info("obj dB size:-->" + dbListOfMap.size());

        //will only compare list size based on a report name(some has 1 map, and some has 2 maps)
        if(!apiListOfMap.equals(dbListOfMap)){
            if (apiListOfMap.size() != dbListOfMap.size()) {
                compareListOfMapByKeyValues(apiListOfMap,dbListOfMap,key);
                logWarning("MISMATCH CONTENT" ,false);
                logFail("MISMATCH SIZE : obj A size:-->" + apiListOfMap.size()+" obj B size:-->" + dbListOfMap.size() ,false,true);
            }else{
                compareListOfMapByKeyValues(apiListOfMap,dbListOfMap,key);
                logFail("MISMATCH CONTENT" ,false,true);
            }
        }else{
            log("obj A size/content equals to obj B size/content STAUS : PASS");
        }

    }

    private void compareListOfMapByKeyValues(List<HashMap<String, String>> apiListOfMap, List<HashMap<String, String>> dbListOfMap,String key) {
        Map<String,List<Map.Entry<String, String>>> mismatches=new HashMap<>();
        String keyObjA=null;
        boolean flag=false;
        for(HashMap <String,String> objAmap:apiListOfMap){
             keyObjA=objAmap.get(key).trim();
            for (HashMap<String, String> objBmap : dbListOfMap) {
                if (objBmap.get(key).trim().equalsIgnoreCase(keyObjA)) {
                    flag=true;
                    List<Map.Entry<String, String>> _mismatches = compareMapsByValues(objAmap, objBmap);
                    mismatches.put(keyObjA,_mismatches);
                }
            }
            if(!flag){
                consoleLog("List of MapB does not have key from List Of mapA : "+keyObjA);
            }
            flag=false;
        }

        if(!mismatches.isEmpty()){
            for(Map.Entry<String, List<Map.Entry<String, String>>> entry1 : mismatches.entrySet()){
                String key1 = entry1.getKey();
                for(Map.Entry<String,String> mapEntrys: entry1.getValue()) {
                    logWarning("KEY:["+key1+"] --> CONTENT MISMATCH: ["+mapEntrys.getKey()+"]["+mapEntrys.getValue()+"]");
                };
            }
        }else{
            log("Content comparison status : PASS");
        }

    }

    public static <K, V extends Comparable<V>> List<Map.Entry<K, V>> compareMapsByValues(Map<K, V> map1, Map<K, V> map2) {
        List<Map.Entry<K, V>> mismatches = new ArrayList<>();

        for (Map.Entry<K, V> entry1 : map1.entrySet()) {
            K key1 = entry1.getKey();
            V value1 = entry1.getValue();

            if (map2.containsKey(key1)) {
                V value2 = map2.get(key1);
                if (value1.compareTo(value2) != 0) {
                    mismatches.add(entry1);
                }
            } else {
                mismatches.add(entry1);
            }
        }

        for (Map.Entry<K, V> entry2 : map2.entrySet()) {
            K key2 = entry2.getKey();
            V value2 = entry2.getValue();

            if (!map1.containsKey(key2)) {
                mismatches.add(entry2);
            }

            if (map1.containsKey(key2)) {
                 V value1 = map1.get(key2);
                if (value2.compareTo(value1) != 0) {
                    mismatches.add(entry2);
                }
            } else {
                mismatches.add(entry2);
            }
        }

        return mismatches;
    }



    private void getResultFromDBAsListOfHashMap(String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbListOfHashMapObj = dbReader.getListOfMaps(query);
        dbReader.closeConnection();
    }

    private void parceJsonArrayToListOfMaps() {
        JSONArray jsonarray = getRunTimeArray(response);
        Log.info("jsonarray size  : " + jsonarray.size());
        if (!jsonarray.isEmpty()) {
            for (Object arr : jsonarray) {
                JSONObject jsonObject = (JSONObject) arr;
                Log.info("jsonObject size  : " + jsonObject.size());
                HashMap<String, String> temp = getKeyValueFromJsonObj(jsonObject);
                apiListOfHashMapObj.add(temp);
            }

        } else {
            logWarning("API call return Empty JSONArray:--> " + response.getBody().asString());
            //Assert.fail();
        }
    }

    private void fetchAllYieldAdjustmentsFromDB(String key,String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbHashMapObj = dbReader.fetchYieldAdjustmentsRecords(replaceArgumentsWithRunTimeProperties(query), key);
        dbReader.closeConnection();
    }

    private void fetchCalculatedYieldsFromDB(String key,String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbHashMapObj = dbReader.fetchCalculatedYieldRecords(replaceArgumentsWithRunTimeProperties(query), key);
        dbReader.closeConnection();
    }

    public void setReportEndpointParamBasedOn(String reportId,String date,String ticker) {
//        switch (reportId) {
//            case "yld_mo_dly_acct_var":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_dly_cusip_var":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_dly_unit_check":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_earn_summ":
//                setRunTimeProperty("ticker",ticker);
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_earn_summ_detail":
//                setRunTimeProperty("ticker",ticker);
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_hist_units":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_mtd_income":
//                setRunTimeProperty("ticker",ticker);
//                break;
//            case "yld_mo_undistributed_gl":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//            case "yld_mo_zero_units":
//                setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
//                break;
//        }
    }

    public void setPrerequisite(String string,String param) {
        String date=null;
        switch(param){
            case"begin_Date":
                date=null;
                if (string.startsWith("T")) {
                    if(string.contains("TU")){
                        //unexpected format
                        //date = getDate(string.replace("U",""), "yyyy-dd-MMM");
                    }else{
                        //date = getDate(string, "dd-MMM-yyyy");
                    }
                } else {
                    date = string;
                }
                setRunTimeProperty("beginDate",date);
                break;
            case"end_Date":
                 date=null;
                if (string.startsWith("T")) {
                    if(string.contains("TU")){
                        //unexpected format
                        //date = getDate(string.replace("U",""), "yyyy-dd-MMM");
                    }else{
                        //date = getDate(string, "dd-MMM-yyyy");
                    }
                } else {
                    date = string;
                }
                setRunTimeProperty("endDate",date);
                break;
            case"ticker":
                setRunTimeProperty("ticker",string);
                break;
            case"adjustId":
                switch(string){
                    case"exists":
                        String str=getAdjustmentIDs().get(0);
                        consoleLog("ADJUSTMENT ID : "+str);
                        logInfo("EXISTING ADJUSTMENT ID : "+str+" pulled from the database and set as prerequisite",false);
                        setRunTimeProperty("adjust_id",str);
                        break;
                    case"active":
                        str=getActiveAdjustmentIDs(QUERY.select_all_from).get(0);
                        consoleLog("ACTIVE ADJUSTMENT ID : "+str);
                        logInfo("ACTIVE ADJUSTMENT ID : "+str+" pulled from the database and set as prerequisite",false);
                        setRunTimeProperty("adjust_id",str);
                        break;
                    case"active_pastDate":
                        str=getActiveAdjustmentIDs(QUERY.select_all_from).get(0);
                        consoleLog("ACTIVE ADJUSTMENT ID with a PAST DATE: "+str);
                        logInfo("ACTIVE ADJUSTMENT ID with a PAST DATE: "+str+" pulled from the database and set as prerequisite",false);
                        setRunTimeProperty("adjust_id",str);
                        break;
                    case"notExists":
                        setRunTimeProperty("adjust_id","0000");
                        break;
                    case"string":
                        setRunTimeProperty("adjust_id","abcd");
                        break;
                    case"specialChar":
                        setRunTimeProperty("adjust_id","#@12");
                        break;
                    case"empty":
                        setRunTimeProperty("adjust_id","");
                        break;
                    case"long":
                        setRunTimeProperty("adjust_id","9223372036854775807");
                        break;
                }
                break;
        }
    }

    private List<String> getAdjustmentIDs() {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        List<String> adjustmentIds = dbReader.getTableDataFromDB(QUERY.select_all_from);
        dbReader.closeConnection();

        if (adjustmentIds.isEmpty()) {
            logFail("Fail to get adjustmentIds from the yield_adjust_master",false, true);
        }
        return adjustmentIds;
    }

    private List<String> getActiveAdjustmentIDs(String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        //will get desc Y ids for ADCB2TW
        List<String> adjustmentIds = dbReader.getTableDataFromDB(query);
        dbReader.closeConnection();

        if (adjustmentIds.isEmpty()) {
            logFail("Fail to get active adjustmentIds from the yield_adjust_master",false, true);
        }
        return adjustmentIds;
    }


    public void verifyActiveFlagAs(String expect) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        String activeFlagFromMaster = dbReader.getCellValue(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        String activeFlagFromDetails = dbReader.getCellValue(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
        dbReader.closeConnection();

        initValidationStatus();
        if(!empty(activeFlagFromMaster)){
            if(!activeFlagFromMaster.trim().equalsIgnoreCase(expect.trim())){
                //validationMissmatchs.add("GSL.MASTER EXPECTED : "+expect+" ACTUAL : "+activeFlagFromMaster);
            }
        }else{
            //validationMissmatchs.add("active_flag_from_yield_adjust_master return : "+activeFlagFromMaster);
        }

        if(!empty(activeFlagFromDetails)){
            if(!activeFlagFromDetails.trim().equalsIgnoreCase(expect.trim())){
               // validationMissmatchs.add("GSL.DETAILS EXPECTED : "+expect+" ACTUAL : "+activeFlagFromDetails);
            }
        }else{
            //validationMissmatchs.add("active_flag_from_yield_adjust_details return : "+activeFlagFromDetails);
        }
        //validateStaus();
    }

//    private ThreadLocal<List<HashMap<String, String>>> excelData = new ThreadLocal<>() {
//        @Override
//        protected List<HashMap<String, String>> initialValue() {
//            return new ArrayList<>();
//        }
//    };

    public void compareXlsxReportContentAgainstDataBase(String reportId) {

        switch (reportId) {
            case "yld_mo_dly_acct_var":
                initApiListOfHashMap();
                getReportDataFromApiResponse(reportId);
                initListOfDBHashMap();
                getReportAsListOfMapsFromDB(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
                compareListOfMapContent(apiListOfHashMapObj, dbListOfHashMapObj,"CLIENTNUMBER");
                break;
            case "yld_mo_dly_cusip_var":
                //Map<'client+cusip',Map<Key,Value>>
                initApiHashMap();
                getReportDataFromApiResponse(reportId);
                initDBHashMap();
                getResultFromDBForReport("clientcusip",replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
                //compareMapContent(apiHashMapObj, dbHashMapObj);
                break;
            case "yld_mo_dly_unit_check":
                initApiListOfHashMap();
                getReportDataFromApiResponse(reportId);
                initListOfDBHashMap();
                getReportAsListOfMapsFromDB(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
                compareListOfMapContent(apiListOfHashMapObj, dbListOfHashMapObj,"CLIENT");
                break;
            case "yld_mo_earn_summ":

                break;
            case "yld_mo_earn_summ_detail":

                break;
            case "yld_mo_hist_units":

                break;
            case "yld_mo_mtd_income":

                break;
            case "yld_mo_undistributed_gl":

                break;
            case "yld_mo_zero_units":

                break;
        }
    }

    private void getReportDataFromApiResponse(String reportID) {

        int headerIndex = 0;int bodyIndex=0;
        switch (reportID) {
            case "yld_mo_dly_acct_var":
                headerIndex= 3;bodyIndex= 4;
                try {
                    InputStream is = new ByteArrayInputStream(response.getBody().asByteArray());
                    apiListOfHashMapObj = ReadExcel.readData(is, reportID, headerIndex, bodyIndex);
                } catch (Exception e) {
                    logFail("Fail to get xlsx data from API stream", false, true);
                }
                break;
            case "yld_mo_dly_cusip_var":
                headerIndex= 3;bodyIndex= 4;
                try {
                    InputStream is = new ByteArrayInputStream(response.getBody().asByteArray());
                    apiHashMapObj = ReadExcel.readDataCusipVar(is, reportID, headerIndex, bodyIndex);
                } catch (Exception e) {
                    logFail("Fail to get xlsx data from API stream", false, true);
                }
                break;
            case "yld_mo_dly_unit_check":
                headerIndex= 3;bodyIndex= 4;
                try {
                    InputStream is = new ByteArrayInputStream(response.getBody().asByteArray());
                    apiListOfHashMapObj = ReadExcel.readData(is, reportID, headerIndex, bodyIndex);
                } catch (Exception e) {
                    logFail("Fail to get xlsx data from API stream", false, true);
                }
                break;
            case "yld_mo_earn_summ":
                //setRunTimeProperty("ticker",ticker);
                //setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
                headerIndex= 3;bodyIndex= 4;
                break;
            case "yld_mo_earn_summ_detail":
                // setRunTimeProperty("ticker",ticker);
                //setRunTimeProperty("busDate",getDate(date, "dd-MMM-yyyy"));
                headerIndex= 3;bodyIndex= 4;
                break;
            case "yld_mo_hist_units":
                headerIndex= 3;bodyIndex= 4;
                break;
            case "yld_mo_mtd_income":
                headerIndex= 3;bodyIndex= 4;
                break;
            case "yld_mo_undistributed_gl":
                headerIndex= 3;bodyIndex= 4;
                break;
            case "yld_mo_zero_units":
                headerIndex= 3;bodyIndex= 4;
                break;
        }

    }

    private void getReportAsListOfMapsFromDB(String query) {
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        dbListOfHashMapObj = dbReader.getReportsListOfMaps(query);
        dbReader.closeConnection();
    }

    public void setTickerWitchHasRecordsBasedOnStartAndEndDates() {
        List<String> result =null;
        Random rand = new Random();
        setDBConnection(ConfigProvider.getAsString("SfdmEnv"));
        result = dbReader.getTableDataFromDB(replaceArgumentsWithRunTimeProperties(QUERY.select_all_from));
            if(!result.isEmpty()){
                setRunTimeProperty("ticker",result.get(rand.nextInt(result.size()-1)));
            }
        consoleLog("TICKER : "+getRunTimeProperty("ticker"));

        dbReader.closeConnection();
    }

    public void validateResponse() {
        int stExpected = 200;
        int stActual = response.getStatusCode();
        Log.info("Status is:-->" + stActual);

        if (stActual != 200) {
            if(stActual==204){
                logInfo("Expected Status is:--><b><i><font color=blue>" + stExpected
                        + "</font></i></b> >>> Actual Status is:--><b><i><font color=blue>" + stActual + "</font></i></b>",false);
            }else{
                getErrorMessage();
                logWarning("Expected Status is:--><b><i><font color=blue>" + stExpected
                        + "</font></i></b> >>> Actual Status is:--><b><i><font color=red>" + stActual + "</font></i></b>",false);
            }
        }

    }

    public void verifyResponseMessage(String expected) {
        String str = response.body().asString();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObject;
            jsonObject = (JSONObject) parser.parse(str);
            String message = (String) jsonObject.get("message");

          if(message.contains(expected)){
              log("MESSAGE:--> <b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">"
                      + message.toUpperCase() + "</font></i></b>",false);
          }else{
              logWarning("Expected MESSAGE:--> <b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">"
                      + expected + "</font></i></b>  Actual MESSAGE : <b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">"+ message.toUpperCase() + "</font></i></b>",false);
          }

        } catch (Exception e) {
            logInfo("Fail to parse response in JsonObject:-->" + e.getMessage(),false);
        }
    }

    public void verifyThatInsertedYieldPopulateDataBase() {
        initApiHashMap();

        HashMap<String,String> payload=new HashMap<>();
        payload.put("TICKER",getRunTimeProperty("ticker"));
        payload.put("BEGIN_DATE",getRunTimeProperty("begin_Date"));
        payload.put("MODIFIED_DATE",getRunTimeProperty("begin_Date"));
        payload.put("ACTIVE_FLAG","Y");
        payload.put("TOTAL_ADJUST_AMOUNT",getRunTimeProperty("adjustAmount"));
        payload.put("NUMBER_DAYS",formatToStringWithOneFloatingPoint(getRunTimeProperty("numberOfDays")));
        payload.put("ADJUST_COMMENT",getRunTimeProperty("adjust_comment"));
        payload.put("MODIFIED_BY",getRunTimeProperty("modifiedBy"));
        apiHashMapObj.put(getRunTimeProperty("adjust_comment"), payload);

        initDBHashMap();
        fetchAllYieldAdjustmentsFromDB("adjust_comment",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    public void setParametrizedPayloadAs(String _closeBusDate, String ticker, String acctNum, String unitsOutstanding, String accruedInterest, String amortizedCost, String otherAdjustments) {
        initCalculatedYieldsPayload();

        String closeBusDate = getDateBasedOnRequest(_closeBusDate,"dd-MMM-yyyy");
        closeBusDate ="12-Mar-2023";//modify when get a logic from dev
        calculatedYieldsPayload = "[\n" +
                "    {\n" +
                "        \"closeBusDate\": \""+closeBusDate+"\",\n" +
                "        \"ticker\": \""+ticker+"\",\n" +
                "        \"acctNum\": "+acctNum+",\n" +
                "        \"unitsOutstanding\": "+unitsOutstanding+",\n" +
                "        \"accruedInterest\": "+accruedInterest+",\n" +
                "        \"amortizedCost\": "+amortizedCost+",\n" +
                "        \"otherAdjustments\": "+otherAdjustments+"}\n" +
                "]";

        setRunTimeProperty("closeBusDate", closeBusDate);
        setRunTimeProperty("ticker", ticker);
        setRunTimeProperty("acctNum", acctNum);
        setRunTimeProperty("unitsOutstanding", unitsOutstanding);
        setRunTimeProperty("accruedInterest", accruedInterest);
        setRunTimeProperty("amortizedCost", amortizedCost);
        setRunTimeProperty("otherAdjustments", otherAdjustments);

    }

    private String getDateBasedOnRequest(String _date,String format) {
        String d=null;
        if (_date.startsWith("T")) {
            if(_date.contains("TU")){
                //unexpected format
               // d = getDate(_date.replace("U",""), "yyyy-dd-MMM");
            }else{
              //  d = getDate(_date, format);
            }
        } else {
            d = _date;
        }
        return d;
    }

    public void verifyThatCalculatedYieldPatchPopulateDataBase() {
        initApiHashMap();
        parsePayloadToMap("closeBusDate_ticker");
        initDBHashMap();
        fetchCalculatedYieldsFromDB("closeBusDate_ticker",QUERY.select_all_from);
        //compareMapContent(apiHashMapObj, dbHashMapObj);
    }

    private void parsePayloadToMap(String closeBusDate_ticker) {
        HashMap<String,String> payload=new HashMap<>();
        payload.put("closeBusDate".toUpperCase(),getRunTimeProperty("closeBusDate"));
        payload.put("ticker".toUpperCase(),getRunTimeProperty("ticker"));
        payload.put("acctNum".toUpperCase(),getRunTimeProperty("acctNum"));
        payload.put("unitsOutstanding".toUpperCase(),getRunTimeProperty("unitsOutstanding"));
        payload.put("accruedInterest".toUpperCase(),getRunTimeProperty("accruedInterest"));
        payload.put("amortizedCost".toUpperCase(),getRunTimeProperty("amortizedCost"));
        payload.put("otherAdjustments".toUpperCase(),getRunTimeProperty("otherAdjustments"));
        apiHashMapObj.put(getRunTimeProperty("closeBusDate")+getRunTimeProperty("ticker"), payload);
    }
    By userName = By.xpath("//input[@id='username']");
    By sidePanel=By.xpath("//li[@aria-label='YIELDS']");

}

