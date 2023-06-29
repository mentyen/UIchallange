//package com.cucumber.utilities;
//
//import org.junit.Assert;
//import java.sql.*;
//import java.util.Date;
//import java.util.*;
//import java.util.stream.IntStream;
//
//public class DataBase {
//
//    public enum DatabaseType {
//        CloudStage(1), CloudStage2(2), CloudIMPL(3), CloudIMPL2(4), CloudTest(5),
//        CloudTest2(6), PROD(7), CloudDev(8), CloudHubTest1(9);
//
//        public final int values;
//        DatabaseType(final int value) {
//            this.values = value;
//        }
//    }
//
//    public static DatabaseType getDatabaseType() {
//        try {
//            switch (System.getProperty("Environment").toUpperCase()) {
//
//                case "CLOUDSTAGE":
//                    return DatabaseType.CloudStage;
//                case "CLOUDSTAGE2":
//                    return DatabaseType.CloudStage2;
//                case "CLOUDTEST":
//                    return DatabaseType.CloudTest;
//                case "CLOUDTEST2":
//                    return DatabaseType.CloudTest2;
//                case "CLOUDIMPL":
//                    return DatabaseType.CloudIMPL;
//                case "CLOUDIMPL2":
//                    return DatabaseType.CloudIMPL2;
//                case "CLOUDDEV":
//                    return DatabaseType.CloudDev;
//                case "CLOUDHUBTEST1":
//                    return DatabaseType.CloudHubTest1;
//                default:
//                    Assert.fail("Given environment is not listed");
//                    return null;
//
//            }
//        } catch (Exception e) {
//            Log.info("Fail to get database type on DataBase page, exception :-->" + e.getMessage());
//            return null;
//        }
//    }
//
//    /**
//     * Executes the select db query and return the complete Result Set
//     *
//     * @param selectQuery query to be executed
//     * @return Resultset
//     */
//
//    public static ResultSet executeSelectQueryToBeRemoved(String selectQuery, DatabaseType dbType) {
//        Date startDate = new Date();
//
//        selectQuery = replaceArgumentsWithRunTimeProperties(selectQuery);
//        Statement stmt = null;
//        ResultSet resultSet = null;
//        try {
//            stmt = getConnection(dbType).createStatement();
//            resultSet = stmt.executeQuery(selectQuery);
//        } catch (SQLException e) {
//            Log.info("SQLException: " + e);
//        }
//        if (null == resultSet)
//            Log.info("No data was returned for this query");
//
//        Date endDate = new Date();
//        double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;
//
//        if (timeDifference > 60)
//            Log.info("<B>Time taken to run this query in minutes : " + timeDifference / 60 + "</B>");
//        else
//            Log.info("Time taken to run this query in seconds : " + timeDifference);
//
//        return resultSet;
//    }
//
//
//    /**
//     * Creates database connection using the Config parameters -
//     * 'DBConnectionString', 'DBConnectionUsername' and 'DBConnectionPassword'
//     *
//     * @return Db Connection
//     */
//    private static Connection getConnection(DatabaseType dbType) {
//        Connection con = null;
//        String connectString = null;
//        String userName = null;
//        String password = null;
//        try {
//            switch (dbType) {
//
//                case CloudStage:
//                    connectString = TestData.StageDBConnectionString;
//                    Log.info("Connecting to Stage DB:-" + connectString);
//                    userName = TestData.StageDBUsername;
//                    password = TestData.StageDBCountersign;
//                    break;
//
//                case CloudStage2:
//                    connectString = TestData.Stage2DBConnectionString;
//                    Log.info("Connecting to Stage 2 DB:-" + connectString);
//                    userName = TestData.Stage2DBUsername;
//                    password = TestData.Stage2DBCountersign;
//                    break;
//
//                case CloudIMPL:
//                    connectString = getRunTimeProperty("IMPLConnectionString");
//                    Log.info("Connecting to IMPL DB:-" + connectString);
//                    userName = getRunTimeProperty("IMPLDBUsername");
//                    password = getRunTimeProperty("IMPLDBPassword");
//                    break;
//                case CloudIMPL2:
//                    connectString = getRunTimeProperty("IMPL2ConnectionString");
//                    Log.info("Connecting to IMPL2 DB:-" + connectString);
//                    userName = getRunTimeProperty("IMPL2DBUsername");
//                    password = getRunTimeProperty("IMPL2DBPassword");
//                    break;
//                case PROD:
//                    connectString = getRunTimeProperty("ProdDBConnectionString");
//                    Log.info("Connecting to Stage DB:-" + connectString);
//                    userName = getRunTimeProperty("ProdDBUsername");
//                    password = getRunTimeProperty("ProdDBPassword");
//                    break;
//
//                case CloudTest:
//                    connectString = TestData.Test1DBConnectionString;
//                    Log.info("Connecting to Test1 DB:-" + connectString);
//                    userName = TestData.Test1DBUsername;
//                    password = TestData.Test1DBCountersign;
//                    break;
//
//                case CloudTest2:
//                    connectString = TestData.Test2DBConnectionString;
//                    Log.info("Connecting to Test2 DB:-" + connectString);
//                    userName = TestData.Test2DBUsername;
//                    password = TestData.Test2DBCountersign;
//                    break;
//                case CloudDev:
//                    connectString = TestData.DevDBConnectionString;
//                    Log.info("Connecting to CloudDev DB:-" + connectString);
//                    userName = TestData.devDBUsername;
//                    password = TestData.devDBCountersign;
//                    break;
//                case CloudHubTest1:
//                    connectString = TestData.HubTest1DBConnectionString;
//                    Log.info("Connecting to CloudHubTest1 DB:-" + connectString);
//                    userName = TestData.HubTest1DBUsername;
//                    password = TestData.HubTest1DBCountersign;
//                    print(userName+" "+password);
//                    break;
//                default:
//                    break;
//
//            }
//
//            con = DriverManager.getConnection(connectString, userName, password);
//            if (con != null)
//                Log.info("Connection succeeded");
//            else
//                Log.info("Unable to establish connection");
//
//        } catch (SQLException e) {
//            Log.info("Exception occurred : " + e);
//        }
//
//        DBConnection = con;
//        return DBConnection;
//    }
//
//    public static List<Map<String, String>> select(String selectQuery) {
//        selectQuery = replaceArgumentsWithRunTimeProperties(selectQuery);
//        Log.info("selectQuery : " + selectQuery);
//        Statement stmt = null;
//        ResultSet resultSet = null;
//        String exception = null;
//        List<Map<String, String>> response = null;
//        try {
//            stmt = getConnection(getDatabaseType()).createStatement();
//            resultSet = stmt.executeQuery(selectQuery);
//            response = convertResultSetToListOfMaps(resultSet);
//        } catch (Exception e) {
//            exception = e.getMessage();
//            printWarning("select() method throws Exception: " + exception);
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                    Log.info("STMT close");
//                } catch (SQLException e) {
//                    printWarning(e.getMessage());
//                }
//            }
//        }
//        closeConnection();
//        if (response == null) {
//            Assert.fail("select() method throws Exception: " + exception);
//        }
//        return response;
//    }
//
//
//    /*
//     * This method will fetch a select query grabbing only first record
//     * it will add "fetch first row only" if no fetch first statement is present on the query
//     * it will also add all of the columns into runtime properties
//     * TODO: remove runtime properties behaviour and understand where it is needed and why
//     */
//    public static Map<String, String> selectFirst(String query){
//        if(!query.toLowerCase().contains("fetch first"))
//            query += "  fetch first row only";
//        Map<String, String> first = select(query).get(0);
//        Set<String> keys = first.keySet();
//        for (String key : keys) {
//            putRunTimeProperty(key, first.get(key));
//        }
//        return first;
//    }
//
//
//    /** Executes update query in DB
//     * @return number of rows affected
//     */
//    public static int update(String updateQuery) {
//        DatabaseType dbType = getDatabaseType();
//        Date startDate = new Date();
//        Statement stmt = null;
//        int rows = 0;
//        try {
//            stmt = getConnection(dbType).createStatement();
//            updateQuery = replaceArgumentsWithRunTimeProperties(updateQuery);
//
//            if (getRunTimeProperty("replaceNULLInQuery") != null
//                    && getRunTimeProperty("replaceNULLInQuery").equalsIgnoreCase("true")) {
//                if (updateQuery.contains("'(null)'") || updateQuery.contains("'(NULL)'")
//                        || updateQuery.contains("'null'") || updateQuery.contains("'NULL'")) {
//                    updateQuery = updateQuery.replace("'(null)'", "NULL").replace("'(NULL)'", "NULL")
//                            .replace("'null'", "NULL").replace("'NULL'", "NULL");
//                }
//            }
//
//            Log.info("\nExecuting the update query - '" + updateQuery + "'");
//            rows = stmt.executeUpdate(updateQuery);
//        } catch (Exception e) {
//            printWarning("DatBase class ,update() method Exception : " + e);
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException e) {
//                    printWarning("DatBase class ,update() method Exception : " + e);
//                }
//            }
//        }
//        if (0 == rows)
//            Log.info("No rows were updated by this query");
//        else
//            Log.info("No. of rows  updated by this query :" + rows);
//        closeConnection();
//        Date endDate = new Date();
//        double timeDifference = (endDate.getTime() - startDate.getTime()) / 1000.00;
//
//        if (timeDifference > 60)
//            Log.info("<B>Time taken to run this query in minutes : " + timeDifference / 60 + "</B>");
//        else
//            Log.info("Time taken to run this query in seconds : " + timeDifference);
//
//        return rows;
//    }
//
//    public static int delete(String deleteQuery) {
//        return update(deleteQuery);
//    }
//
//    /**
//     * Executes insert query in DB
//     *
//     * @return
//     */
//    public static int insert(String insertQuery) {
//        return update(insertQuery);
//    }
//
//    /**
//     * Executes delete Batch
//     *used for create enrollment clean up
//     */
//    public static void delete(List<String> queries) {
//        Statement stmt = null;
//        boolean status = true;
//        try {
//            stmt = getConnection(getDatabaseType()).createStatement();
//            for (String query : queries) {
//                stmt.addBatch(query);
//            }
//            int[] results = stmt.executeBatch();
//            Log.info("Updated Rows :==> " + String.valueOf(IntStream.of(results).sum()));
//        } catch (Exception e) {
//            printWarning("Exception occurred as: " + e.getMessage());
//            status = false;
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                    Log.info("STMT close");
//                } catch (SQLException e) {
//                    printWarning(e.getMessage());
//                }
//            }
//        }
//        closeConnection();
//        if (!status)
//            Assert.fail("<-----Unable to Create Connection With Database!! Exception occurred----->");
//    }
//    public static void insert(List<String> queries) {
//        Statement stmt = null;
//        boolean status = true;
//        String exc=null;
//        try {
//            stmt = getConnection(getDatabaseType()).createStatement();
//            for (String query : queries) {
//                Log.info("Executing the insert query - '" + query + "'");
//                stmt.addBatch(query);
//            }
//            int[] results = stmt.executeBatch();
//            Log.info("Updated Rows :==> " + String.valueOf(IntStream.of(results).sum()));
//        } catch (Exception e) {
//            exc=e.getMessage();
//            printWarning("Exception occurred as: " + exc);
//            status = false;
//        } finally {
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                    Log.info("STMT close");
//                } catch (SQLException e) {
//                    printWarning(e.getMessage());
//                }
//            }
//        }
//        closeConnection();
//        if (!status)
//            Assert.fail("Exception occurred as: " + exc);
//    }
//
//    private static List<Map<String, String>> convertResultSetToListOfMaps(ResultSet resultSet) throws Exception {
//        // Convert that ResultSet into a HashMap
//        List<Map<String, String>> maps = new ArrayList<>();
//        while (resultSet.next()) {
//            ResultSetMetaData meta = resultSet.getMetaData();
//            Map<String, String> colMapData = new HashMap<String, String>();
//            for (int col = 1; col <= meta.getColumnCount(); col++) {
//                try {
//                    colMapData.put(meta.getColumnLabel(col), resultSet.getObject(col).toString());
//                } catch (NullPointerException e) {
//                    colMapData.put(meta.getColumnLabel(col), "NULL");
//                }
//            }
//            maps.add(colMapData);
//        }
//        return maps;
//    }
//
//
//    public static boolean testDBConnection() {
//        DatabaseType dbType = null;
//        dbType = getDatabaseType();
//        boolean status = false;
//        try {
//            if (getConnection(dbType).isValid(3))
//                status = true;
//        } catch (Exception e) {
//            printWarning("openConnection() throws Exception : " + e.getMessage());
//            closeConnection();
//        }
//        return status;
//    }
//
//    /* Closes DB connection
//     * TODO: use static connection through a test scenario and close it only once, avoid opening in each query
//     */
//    public static void closeConnection() {
//        if (DBConnection != null) {
//            try {
//                DBConnection.close();
//                DBConnection = null;
//                Log.info("Database connection closed successfully.");
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//}
//
