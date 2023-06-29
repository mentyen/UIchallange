package com.cucumber.utilities;


import com.cucumber.pages.QUERY;
import com.rebar.utilities.Log;
import org.openqa.selenium.WebDriver;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class DBreader extends GenericMethods {
    private Connection con;

    public DBreader(WebDriver driver) {
        super(driver);
        try {
            // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            Log.info("Fail to initialize Class.forName :-->" + e.getMessage());
        }
    }

    public void getDBConnection(String dbUrl, String dbUserName, String dbUserSecret) throws Exception {
        con = DriverManager.getConnection(dbUrl, dbUserName, decrypt(dbUserSecret, getKey()));
    }

    public void executeQuery(String command) {
        logInfo("Execute query :--> <b><i><font color=blue>" + command + "</font></i></b>", false);

        try {
            con.createStatement().executeQuery(command);
        } catch (Exception e) {
            Log.warn("Fail to execute query ,following exception occured:-->" + e.getMessage());
        }

    }

    public String getCellValue(String command) {
        logInfo("Execute query :--> <b><i><font color=blue>" + command + "</font></i></b>", false);
        String value = null;
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(command);
            while (rs.next()) {
                value = rs.getString(1);
            }
        } catch (Exception e) {
            Log.info("Fail reading db ,following exception occured:-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    Log.warn("Fail to execute query ,following exception occured:-->" + e.getMessage());
                }
            }
        }
        return value;
    }

    public List<String> getTableDataFromDB(String queryString) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>", false);
        Statement stmt = null;
        List<String> list_items = new ArrayList<String>();
        try {
            stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(queryString);
            ResultSetMetaData result_meta = result.getMetaData();
            int col_count = result_meta.getColumnCount();
            while (result.next()) {
                for (int i = 1; i <= col_count; i++) {
                    list_items.add(result.getString(i));
                }
            }
        } catch (SQLException e) {
            Log.info("Fail to read row value :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.info("Fail to close statement :-->" + e.getMessage());
                }
            }
        }
        return list_items;

    }

    public String[][] getTableDataFromDBForMulitpleRows(String queryString, int row_count) {
        try {
            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(queryString);
            ResultSetMetaData result_meta = result.getMetaData();
            int col_count = result_meta.getColumnCount();

            String[][] arrResult = new String[row_count][col_count];

            int k = 0;

            while (result.next() && k < row_count) {
                for (int i = 0; i < col_count; i++) {
                    arrResult[k][i] = result.getString(i + 1);
                }
                ++k;
            }

            return arrResult;
        } catch (SQLException e) {
            Log.info(e);
        }
        return null;

    }

    /* will return firs row for comparison with kudu */
    HashMap<String, HashMap<String, String>> fetchAllRecords = null;
    HashMap<String, String> exchangeRates = null;

    public HashMap<String, HashMap<String, String>> fetchAllRecords(String queryString) {
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                // will add first row info to hash for comparison
                fetchAllRecords.put(getTransactionId(rsmd, resultSet), getKeyValuePairs(rsmd, resultSet));
                // will stop here, if need more rows for comparison remove brake,
                break;
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    public HashMap<String, String> getExchageRates(String queryString) {
        logInfo("Executed query:==>" + queryString, false);
        exchangeRates = new HashMap<String, String>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                String value = null;
                String key = null;
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    int type = rsmd.getColumnType(i);
                    String typeName = rsmd.getColumnTypeName(i);
                    String name = rsmd.getColumnName(i);
                    switch (type) {
                        case Types.CHAR:
                            key = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                            // Log.info(key);
                            break;
                        case Types.DOUBLE:
                            value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                            // Log.info(name + " [" + typeName + "]: " + value);
                            break;
                        case Types.INTEGER:
                            value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                            // Log.info(name + " [" + typeName + "]: " + value);
                            break;
                        case Types.DECIMAL:
                            value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                            // Log.info(name + " [" + typeName + "]: " + value);
                            break;
                        case Types.NUMERIC:
                            value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                            // Log.info(name + " [" + typeName + "]: " + value);

                            break;
                        default:
                            Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column "
                                    + rsmd.getColumnName(i) + ", Label: " + rsmd.getColumnLabel(i)
                                    + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
                    }
                }
                exchangeRates.put(key.trim(), value.trim());

            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return exchangeRates;
    }

    private HashMap<String, String> getKeyValuePairs(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    // Log.info(name + ": " + value);
                    if (name.contains("ACTIVITY_ID")) {
                        all_k_v.put("TRANSACTION_ID", value.trim());
                    } else {
                        all_k_v.put(name, value.trim());
                    }
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    if (name.equals("TRADE_DATE") || name.equals("SECURITY_SETTLEMENT_DATE")
                            || name.equals("EFFECTIVE_DATE") || name.equals("TERM_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i).toString(), "yyyy-MM-dd");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR")) {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR")) {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    // will be removing OFFSET_ID from validation
                    if (!name.contains("YFH_OFFSET_ID")) {
                        all_k_v.put(name, value.trim());
                    }
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    private String getTransactionId(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    if (name.contains("TRANSACTION_ID") || name.contains("ACTIVITY_ID")) {
                        return value.trim();
                    }
                    break;
            }
        }
        return null;
    }

    private String getLoanId(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        // will have to trim loan_id for loan_num+custody_account
        // G1_LOAN_NUM,CUSTODY_ACCOUNT
        String loan_id = null;
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    if (name.contains("LOAN_ID")) {
                        return value.trim();
                    }
                    break;
            }
        }
        return null;
    }

    public HashMap<String, HashMap<String, String>> fetchAllRecords(String queryString, int rowCount) {
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        int iteration = 0;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
                fetchAllRecords.put(getTransactionId(rsmd, resultSet), getKeyValuePairs(rsmd, resultSet));
                // fetchAllRecordsStreamedToKudu.put(transaction_id, all_k_v);
                iteration++;
                if (iteration == rowCount) {
                    break;
                }
            }

        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    HashMap<String, HashMap<String, String>> allRecordForTransactionID = null;

    public HashMap<String, HashMap<String, String>> fetchRecordsForMultipleTransactions(List<String> trn_ids) {
        allRecordForTransactionID = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        // for multiple transaction ids will be creating a single str
        String transaction_id = concatQUERY(trn_ids);
        try {
            stmt = con.createStatement();
        } catch (SQLException e1) {
            Log.warn(e1.getMessage());
        }
        String query = replaceArgument(QUERY.select_all_from, transaction_id);
        // Log.warn(query);
        try {
            ResultSet resultSet = stmt.executeQuery(query);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            // int columnCount = rsmd.getColumnCount();

            while (resultSet.next()) {
                allRecordForTransactionID.put(getTransactionId(rsmd, resultSet), getKeyValuePairs(rsmd, resultSet));
                // allRecordForTransactionID.put(transaction_id_as_key, all_k_v);
            }
        } catch (Exception e) {
            Log.info("Exception fetching data for multiple rows from KUDU:-->" + e.getMessage());
        }

        return allRecordForTransactionID;
    }

    public void closeConnection() {
        try {
            con.close();
            Log.info("DB connection closed successfully");
        } catch (Exception e) {
            Log.warn("Fail to close db connection :-->" + e.getMessage());
        }
    }

    ///////////////////////////////////// POSITIONS ///////////////////////////////

    public HashMap<String, HashMap<String, String>> fetchAllRecordsPositions(String queryString) {
        Log.info("EXECUTING QUERY:==> " + queryString);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                fetchAllRecords.put(getLoanId(rsmd, resultSet), getKeyValuePairsPositions(rsmd, resultSet));
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    private HashMap<String, String> getKeyValuePairsPositions(ResultSetMetaData rsmd, ResultSet resultSet)
            throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    // Log.info(name + "[VARCHAR]: " + value);
                    if (name.contains("ACTIVITY_ID")) {
                        all_k_v.put("TRANSACTION_ID", value.trim());
                    } else {
                        all_k_v.put(name, value.trim());
                    }
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    // Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    if (name.equals("TRADE_DATE") || name.equals("SETTLE_DATE") || name.equals("COLLATERAL_DUE_DATE")
                            || name.equals("LOAN_MATURITY_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i).toString(), "yyyy-MM-dd");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    if (name.equals("TRADE_DATE") || name.equals("SETTLE_DATE") || name.equals("COLLATERAL_DUE_DATE")
                            || name.equals("LOAN_MATURITY_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i).toString(), "yyyy-MM-dd");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR")) {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    /*
                     * if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR") ||
                     * name.equals("NONCASH_COLLATERAL_REQ_MARGIN")||name.equals("MARKET_VALUE") ||
                     * name.equals("US_EQUIV_MARKET_VALUE")) { value = resultSet.getString(i) ==
                     * null ? "null" : doubleToString(resultSet.getBigDecimal(i)); } else { //value
                     * = resultSet.getString(i) == null ? "null" :
                     * doubleToString(resultSet.getBigDecimal(i)); value = resultSet.getString(i) ==
                     * null ? "null" : resultSet.getBigDecimal(i).toString(); }
                     */

                    if (name.equals("LOAN_QUANTITY")) {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    } else {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    }

                    // Log.info(name + " [" + typeName + "]: " + value);
                    // will be removing OFFSET_ID from validation
                    if (!name.contains("YFH_OFFSET_ID")) {
                        all_k_v.put(name, value.trim());
                    }
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    /////////////////////////////SFDM///////////////////////////////////////////////////////////////////////////////////

    private String getKey(ResultSetMetaData rsmd, ResultSet resultSet, String module) throws SQLException {
        String key = "";
        String closebusdate = null;
        String ticker = null;
        String client = null;
        String cusip = null;
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String name = rsmd.getColumnName(i);
            String value;
            value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
            switch (module) {
                case "closeBusDate_ticker":
                    if (name.toLowerCase().contains("closebusdate") || name.toLowerCase().contains("ticker")) {
                        key = key.concat(value.trim());
                    }
                    break;
                case "ticker_closeBusDate":
                    if (name.toLowerCase().contains("closebusdate")) {
                        closebusdate = value.trim();
                    }
                    if (name.toLowerCase().contains("ticker")) {
                        ticker = value.trim();
                    }
                    if (empty(closebusdate) & empty(ticker)) {
                        key = ticker + closebusdate;
                    }
                    break;
                case "clientcusip":
                    if (name.equalsIgnoreCase("client")) {
                        client = value.trim();
                    }
                    if (name.equalsIgnoreCase("cusip")) {
                        cusip = value.trim();
                    }
                    if (!empty(client) & !empty(cusip)) {
                        key = client + cusip;
                    }
                    break;
                case "adjust_comment":
                    if (name.toLowerCase().contains("adjust_comment")) {
                        key = key.concat(value.trim());
                    }
                    break;
                case "ticker_adjust_comment":
                    if (name.toLowerCase().contains("ticker")||name.toLowerCase().contains("adjust_comment")) {
                        key = key.concat(value.trim());
                    }
                    break;
                case "entitlementName":
                    if (name.toLowerCase().contains("entitlementname")) {
                        key = value.trim();
                    }
                    break;
                case "userId":
                    if (name.toLowerCase().contains("userid")) {
                        key = value.trim();
                    }
                    break;
                case "closeBusDate":
                    if (name.toLowerCase().contains("closebusdate")) {
                        key = value.trim();
                    }
                    break;

                case "reportId":
                    if (name.toLowerCase().contains("reportid")) {
                        key = value.trim();
                    }
                    break;
                case "adjustId":
                    if (name.toLowerCase().contains("adjustid")) {
                        key = value.trim();
                    }
                    break;
            }
        }
        return key;
    }

    private HashMap<String, String> getKeyValues(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "" : resultSet.getString(i);
                    Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "" : resultSet.getString(i);
                    Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    if (name.equalsIgnoreCase("NUMBER_DAYS")||name.equalsIgnoreCase("NUMBERDAYS")) {
                        value = resultSet.getString(i) == null ? "0.00" : resultSet.getBigDecimal(i).toString();
                    } else {
                        value = resultSet.getString(i) == null ? "0.00" : BigDecimalToString(resultSet.getBigDecimal(i));
                    }
                     Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    private String BigDecimalToString(BigDecimal object) {
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

    public HashMap<String, HashMap<String, String>> fetchAllRecords(String queryString, String key) {
        logInfo("<b><i><font color=" + ConfigProvider.getAsString("infoColor") + ">EXECUTE QUERY : </font></i></b>" + queryString,false);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        String sKey = null;
        HashMap<String, String> keyValue = null;

        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                sKey = getKey(rsmd, resultSet, key);
                keyValue = getKeyValues(rsmd, resultSet);
                fetchAllRecords.put(sKey, keyValue);
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    public HashMap<String, HashMap<String, String>> getAllAsMap(String queryString, String key) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>",false);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        String sKey = null;
        HashMap<String, String> keyValue = null;

        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                sKey = getKey(rsmd, resultSet, key);
                keyValue = getKeyValuesEntitlements(rsmd, resultSet);
                fetchAllRecords.put(sKey, keyValue);
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    public HashMap<String, HashMap<String, String>> getAllAsMapForReport(String queryString, String key) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>",false);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        String sKey = null;
        HashMap<String, String> keyValue = null;

        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                sKey = getKey(rsmd, resultSet, key);
                keyValue =getKeyValuePairsForReportData(rsmd, resultSet);
                fetchAllRecords.put(sKey, keyValue);
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    private HashMap<String, String> getKeyValuesEntitlements(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getString(i));
           // Log.info(name + ": " + value);
            all_k_v.put(name, value.trim());
        }
        return all_k_v;
    }

    public List<HashMap<String, String>> getListOfMaps(String queryString) {
        Log.info("EXECUTING QUERY:==> " + queryString);
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                list.add(getKeyValuePairsForReportParams(rsmd, resultSet));
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return list;
    }

    private HashMap<String, String> getKeyValuePairsForReportParams(ResultSetMetaData rsmd, ResultSet resultSet)
            throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    Log.info(name + "[VARCHAR]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    if (name.equals("CLOSE_BUS_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i), "mm/dd/yyyy");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    if (name.equals("CLOSE_BUS_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i), "mm/dd/yyyy");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR")) {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    if (name.equals("LOAN_QUANTITY")) {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    } else {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    }
                     Log.info(name + " [" + typeName + "]: " + value);
                     all_k_v.put(name, value.trim());
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    public HashMap<String, HashMap<String, String>> fetchYieldAdjustmentsRecords(String queryString, String key) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>",false);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        String sKey = null;
        HashMap<String, String> keyValue = null;

        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                sKey = getKey(rsmd, resultSet, key);
                keyValue = getYieldAdjustmentsKeyValues(rsmd, resultSet);
                fetchAllRecords.put(sKey, keyValue);
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }

    private HashMap<String, String> getYieldAdjustmentsKeyValues(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    //Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    //Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    if (name.equalsIgnoreCase("ADJUSTID") || name.equalsIgnoreCase("NUMBERDAYS")) {
                        value = resultSet.getString(i) == null ? "0.00" : resultSet.getBigDecimal(i).toString();
                    } else {
                        value = resultSet.getString(i) == null ? "0.00" : BigDecimalToString(resultSet.getBigDecimal(i));
                        value = formatToStringWithOneFloatingPoint(value);
                    }

                   // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    private String formatToStringWithOneFloatingPoint(String str) {
        if (!str.contains(".")) {
            return str.concat(".0");
        }
        return str;
    }

    public List<HashMap<String, String>> getReportsListOfMaps(String queryString) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>",false);
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                list.add(getKeyValuePairsForReportData(rsmd, resultSet));
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return list;
    }

    private HashMap<String, String> getKeyValuePairsForReportData(ResultSetMetaData rsmd, ResultSet resultSet)
            throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    if(name.equalsIgnoreCase("date1")||name.equalsIgnoreCase("date2")||name.equalsIgnoreCase("maturity")){
                    value = resultSet.getString(i) == null ? "": resultSet.getString(i);
                    }else{
                        value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                    }
                   // Log.info(name + "[VARCHAR]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
                   // Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    if (name.equals("CLOSE_BUS_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i), "mm/dd/yyyy");
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    if (name.equals("CLOSE_BUS_DATE")) {
                        value = resultSet.getString(i) == null ? "null"
                                : DateUtils.formatDate(resultSet.getTimestamp(i), "mm/dd/yyyy");
                    }else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    }
                    Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    if (name.equals("LOAN_RATE") || name.equals("MARKET_VAL_IN_LOAN_CURR")) {
                        value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    if (name.equals("DAYDAYVARIANCE")) {
                        //100.00
                        value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    }else if (name.equals("PRIORDAILYYIELDUNIT")||name.equals("DAILYYIELDUNIT")){
                        //round(13)
                        //if value=0 --> 0.0000000000000
                        //value = resultSet.getString(i) == null ? "0" : formatToStringWithThirteenFloatingPoint(resultSet.getBigDecimal(i).toString());
                        value = resultSet.getString(i) == null ? "0" : formatToStringWithThirteenFloatingPoint(doubleToString1(resultSet.getBigDecimal(i)));
                    } else if (name.equalsIgnoreCase("par1")
                            || name.equalsIgnoreCase("par2")
                            || name.equalsIgnoreCase("pardiff")
                            || name.equalsIgnoreCase("Accr1")
                            || name.equalsIgnoreCase("Accr2")
                            || name.equalsIgnoreCase("AccrDiff")
                            || name.equalsIgnoreCase("Amrt1")
                            || name.equalsIgnoreCase("Amrt2")
                            || name.equalsIgnoreCase("AmrtDiff")
                            || name.equalsIgnoreCase("EXCEP")
                            || name.equalsIgnoreCase("CASHFLOWS")
                            || name.equalsIgnoreCase("CURRBAL")
                            || name.equalsIgnoreCase("PREVBAL")
                            || name.equalsIgnoreCase("DIFF")
                    ) {
                        //100.00
                        value = resultSet.getString(i) == null ? "0.00" : doubleToString(resultSet.getBigDecimal(i));
                    } else {
                        value = resultSet.getString(i) == null ? "0" : doubleToString(resultSet.getBigDecimal(i));
                    }
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

    private String doubleToString1(Object object) {

            if (object instanceof BigDecimal) {
                Log.info("object is BigDecimal:-->" + object);
                double d = ((BigDecimal) object).doubleValue();
                if (String.valueOf(d).equals("0.0") || String.valueOf(d).equals("-0.0")) {
                    return formatToStringWithThirteenFloatingPoint(String.valueOf(d));
                } else {
                    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    df.setMaximumFractionDigits(340);
                    return String.valueOf(df.format(d));
                }
            } else if (object instanceof Double) {
                Log.info("object is Double:-->" + object);
                if (object.toString().equals("0.0") || object.toString().equals("-0.0")) {
                    return formatToStringWithThirteenFloatingPoint(object.toString());
                } else {
                    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                    df.setMaximumFractionDigits(340);
                    return String.valueOf(df.format(object));
                }
            } else {
                Log.info("object is not instance off double or bigDecimal:-->" + object);
                return String.valueOf(object);
            }

    }

    private String roundByThirteenDecimal(String input) {
        double number=Double.parseDouble(input);
        DecimalFormat df=new DecimalFormat("#.#############");
        return df.format(number);
    }

    private String formatToStringWithThirteenFloatingPoint(String str) {
        if(str.equals("0")){
            return "0";
        }else{
            while (str.length()-str.indexOf('.')<=13) {
                str+="0";
            }
            return str;
        }

    }

    public HashMap<String, HashMap<String, String>> fetchCalculatedYieldRecords(String queryString, String key) {
        logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>",false);
        fetchAllRecords = new HashMap<String, HashMap<String, String>>();
        Statement stmt = null;
        String sKey = null;
        HashMap<String, String> keyValue = null;

        try {
            stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery(queryString);
            ResultSetMetaData rsmd = resultSet.getMetaData();
            while (resultSet.next()) {
                sKey = getKey(rsmd, resultSet, key);
                keyValue = getCalculatedYieldKeyValues(rsmd, resultSet);
                fetchAllRecords.put(sKey, keyValue);
            }
        } catch (Exception e) {
            Log.warn("Fail to read data from data base :-->" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    Log.warn("Fail to close statment :-->" + e.getMessage());
                }
            }
        }
        return fetchAllRecords;
    }
    private HashMap<String, String> getCalculatedYieldKeyValues(ResultSetMetaData rsmd, ResultSet resultSet) throws SQLException {
        HashMap<String, String> all_k_v = new HashMap<String, String>();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int type = rsmd.getColumnType(i);
            String typeName = rsmd.getColumnTypeName(i);
            String name = rsmd.getColumnName(i);
            String value;
            switch (type) {
                case Types.VARCHAR:
                    value = resultSet.getString(i) == null ? "" : resultSet.getString(i);
                    //Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.CHAR:
                    value = resultSet.getString(i) == null ? "" : resultSet.getString(i);
                   // Log.info(name + ": " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DOUBLE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.INTEGER:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DATE:
                    value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.TIMESTAMP:
                    value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
                    // Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.BOOLEAN:
                    value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
                     //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.DECIMAL:
                    value = resultSet.getString(i) == null ? "null" : doubleToString(resultSet.getBigDecimal(i));
                     //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                case Types.NUMERIC:
                    // value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
                    value = resultSet.getString(i) == null ? "null" : BigDecimalToString(resultSet.getBigDecimal(i));
                    //value = formatToStringWithOneFloatingPoint(value);
                    //Log.info(name + " [" + typeName + "]: " + value);
                    all_k_v.put(name, value.trim());
                    break;
                default:
                    Log.info("The column type (" + rsmd.getColumnTypeName(i) + " for column " + rsmd.getColumnName(i)
                            + ", Label: " + rsmd.getColumnLabel(i)
                            + ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
            }
        }
        return all_k_v;
    }

}
