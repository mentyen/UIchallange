//package com.cucumber.utilities;
//
//import com.cucumber.pages.QUERY;
//import com.rebar.utilities.Log;
//import org.openqa.selenium.WebDriver;
//import java.sql.*;
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.List;
//
//public class ClouderaImpalaJdbc extends GenericMethods {
//
//	Connection connection = null;
//	private String url;
//	private String userName;
//	private String secret;
//	private DataSource dataSource;
//
//	public ClouderaImpalaJdbc(WebDriver driver) {
//		super(driver);
//		try {
//			Class.forName("com.cloudera.impala.jdbc41.DataSource");
//		} catch (ClassNotFoundException e) {
//			Log.info("Fail to initialize Class.forName :-->" + e.getMessage());
//		}
//	}
//
//	public void loadProperties(String ImpalaDBURL, String ImpalaUID, String ImpalaSecret) {
//		this.url = ImpalaDBURL;
//		this.userName = ImpalaUID;
//		this.secret = ImpalaSecret;
//		dataSource = setUp();
//	}
//
//	/**
//	 * Returns sql connection.
//	 */
//	public void getConnection() throws SQLException {
//		connection = dataSource.getConnection();
//	}
//
//	private DataSource setUp() {
//		DataSource dataSource = new DataSource();
//		dataSource.setURL(url);
//		dataSource.setUserID(userName);
//		dataSource.setPassword(secret);
//		return dataSource;
//	}
//
//	public String getCellValue(String command) throws SQLException {
//		logInfo("Execute query :--> <b><i><font color=blue>" + command + "</font></i></b>", false);
//		String value = null;
//		Statement stmt = null;
//		try {
//			stmt = connection.createStatement();
//			ResultSet rs = stmt.executeQuery(command);
//			while (rs.next()) {
//				value = rs.getString(1);
//			}
//		} catch (Exception e) {
//			Log.info("Fail to read cell value :-->" + e.getMessage());
//		} finally {
//			if (stmt != null) {
//				stmt.close();
//			}
//		}
//		return value;
//	}
//
//	public void closeConnection() {
//		try {
//			connection.close();
//			Log.info("KUDU DB connection closed successfully");
//		} catch (SQLException e) {
//			Log.warn("Fail to close KUDU DB connection :-->" + e.getMessage());
//		}
//	}
//
//	HashMap<String, HashMap<String, String>> allRecordForTransactionID = null;
//
//	public HashMap<String, HashMap<String, String>> fetchAllRecords(String recordForTransactionID) {
//		allRecordForTransactionID = new HashMap<String, HashMap<String, String>>();
//		Statement stmt = null;
//		Log.info("KUDU QUERY:==> " + recordForTransactionID);
//		try {
//			stmt = connection.createStatement();
//			ResultSet resultSet = stmt.executeQuery(recordForTransactionID);
//			ResultSetMetaData rsmd = resultSet.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//
//			while (resultSet.next()) {
//				HashMap<String, String> all_k_v = new HashMap<String, String>();
//				String transaction_id_as_key = null;
//				// you get a single result row in here, not the entire ResultSet
//				for (int i = 1; i <= columnCount; i++) {
//					int type = rsmd.getColumnType(i);
//					String typeName = rsmd.getColumnTypeName(i);
//					String name = rsmd.getColumnName(i).toUpperCase();
//					String value;
//					switch (type) {
//					case Types.VARCHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						if (name.contains("TRANSACTION_ID")) {
//							transaction_id_as_key = value.trim();
//						}
//						break;
//					case Types.CHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DOUBLE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						value = trimDouble(value);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.INTEGER:
//						if (name.equals("TRADE_DATE") || name.equals("SECURITY_SETTLEMENT_DATE")
//								|| name.equals("EFFECTIVE_DATE") || name.equals("TERM_DATE")) {
//							value = resultSet.getString(i) == null ? "null"
//									: DateUtils.convertofEpochMilli(resultSet.getInt(i));
//						} else {
//							value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
//						}
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DATE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.TIMESTAMP:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.BOOLEAN:
//						value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DECIMAL:
//						try {
//							if (name.equals("LOAN_RATE")) {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							} else {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							}
//							// System.out.println(name + " [" + typeName + "]: " + value);
//							all_k_v.put(name, value.trim());
//						} catch (Exception e) {
//							System.err.println(e.getMessage());
//							all_k_v.put(name, null);
//						}
//						break;
//					case Types.NUMERIC:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					default:
//						Log.warn("The column type (" + rsmd.getColumnTypeName(i) + " for column "
//								+ rsmd.getColumnName(i) + ", Label: " + rsmd.getColumnLabel(i)
//								+ ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
//
//					}
//
//				}
//				allRecordForTransactionID.put(transaction_id_as_key, all_k_v);
//				break;
//			}
//		} catch (Exception e) {
//			Log.warn("Exception fetching data for multiple rows from KUDU:-->" + e.getMessage());
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return allRecordForTransactionID;
//	}
//
//	private String trimDouble(String d) {
//		return d.substring(0, d.length() - 2);
//	}
//
//	public HashMap<String, HashMap<String, String>> fetchRecordsForMultipleTransactions(List<String> trn_ids) {
//		allRecordForTransactionID = new HashMap<String, HashMap<String, String>>();
//		Statement stmt = null;
//		// for multiple transaction ids will be creating a single str
//		String transaction_id = concatQUERY(trn_ids);
//
//		String query = replaceArgument(QUERY.RECORDS_FOR_SPECIFIC_TRANSACTION_ID, transaction_id);
//		Log.info("KUDU QUERY:==> " + query);
//		try {
//			stmt = connection.createStatement();
//			ResultSet resultSet = stmt.executeQuery(query);
//			ResultSetMetaData rsmd = resultSet.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//
//			while (resultSet.next()) {
//				HashMap<String, String> all_k_v = new HashMap<String, String>();
//				String transaction_id_as_key = null;
//				for (int i = 1; i <= columnCount; i++) {
//					int type = rsmd.getColumnType(i);
//					// String typeName = rsmd.getColumnTypeName(i);
//					String name = rsmd.getColumnName(i).toUpperCase();
//					String value;
//
//					switch (type) {
//					case Types.VARCHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						if (name.contains("TRANSACTION_ID")) {
//							transaction_id_as_key = value.trim();
//						}
//						break;
//					case Types.CHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DOUBLE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDouble(i));
//						value = trimDouble(value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.INTEGER:
//						if (name.equals("TRADE_DATE") || name.equals("SECURITY_SETTLEMENT_DATE")
//								|| name.equals("EFFECTIVE_DATE") || name.equals("TERM_DATE")) {
//							value = resultSet.getString(i) == null ? "null"
//									: DateUtils.convertofEpochMilli(resultSet.getInt(i));
//						} else {
//							value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
//						}
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DATE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.TIMESTAMP:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.BOOLEAN:
//						value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DECIMAL:
//						try {
//							if (name.equals("LOAN_RATE")) {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							} else {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							}
//							//// System.out.println(name + ": " + value);
//							all_k_v.put(name, value.trim());
//						} catch (Exception e) {
//							//System.err.println(e.getMessage());
//							all_k_v.put(name, null);
//						}
//						break;
//					case Types.NUMERIC:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
//						//// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					default:
//						Log.warn("The column type (" + rsmd.getColumnTypeName(i) + " for column "
//								+ rsmd.getColumnName(i) + ", Label: " + rsmd.getColumnLabel(i)
//								+ ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
//
//					}
//
//				}
//				allRecordForTransactionID.put(transaction_id_as_key, all_k_v);
//			}
//		} catch (Exception e) {
//			Log.warn("Exception fetching data for multiple rows from KUDU:-->" + e.getMessage());
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//
//		return allRecordForTransactionID;
//	}
//
//	////////////////////////////////////////////////////////////////
//
//	public HashMap<String, HashMap<String, String>> fetchAllRecordsAgencyloanPositions(String queryString,
//			String loan_id) throws SQLException {
//		logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>", false);
//		allRecordForTransactionID = new HashMap<String, HashMap<String, String>>();
//		Statement stmt = null;
//		try {
//			stmt = connection.createStatement();
//			ResultSet resultSet = stmt.executeQuery(queryString);
//			ResultSetMetaData rsmd = resultSet.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//
//			while (resultSet.next()) {
//				HashMap<String, String> all_k_v = new HashMap<String, String>();
//
//				// you get a single result row in here, not the entire ResultSet
//				for (int i = 1; i <= columnCount; i++) {
//					int type = rsmd.getColumnType(i);
//					String typeName = rsmd.getColumnTypeName(i);
//					String name = rsmd.getColumnName(i).toUpperCase();
//					String value;
//					switch (type) {
//					case Types.VARCHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						System.out.println(name + " [" + typeName + "]: " + value);
//						if (name.contains("LOAN_ID")) {
//							all_k_v.put(name, loan_id);
//						} else {
//							all_k_v.put(name, value.trim());
//						}
//						break;
//					case Types.CHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DOUBLE:
//						DecimalFormat decimalFormatter = new DecimalFormat("############");
//						// value = resultSet.getString(i) == null ? "null" :
//						// String.valueOf(resultSet.getDouble(i));
//						value = resultSet.getString(i) == null ? "null"
//								: String.valueOf(decimalFormatter.format(Double.parseDouble(resultSet.getString(i))));
//						System.out.println(name + " [" + typeName + "]: " + value);
//						// value=trimDouble(value);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.INTEGER:
//						if (name.equals("SETTLE_DATE") || name.equals("LOAN_MATURITY_DATE")
//								|| name.equals("COLLATERAL_DUE_DATE") || name.equals("CREATE_DATE")
//								|| name.equals("TRADE_DATE")) {
//							value = resultSet.getString(i) == null ? "null"
//									: DateUtils.convertofEpochMilli(resultSet.getInt(i));
//						} else {
//							value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
//						}
//						//System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DATE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
//						//System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.TIMESTAMP:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
//						//System.out.println(name + " [" + typeName + "]: " + value);
//						if (!name.equalsIgnoreCase("CREATE_TIMESTAMP")) {
//							all_k_v.put(name, value.trim());
//						}
//						break;
//					case Types.BOOLEAN:
//						value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
//						//System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DECIMAL:
//						try {
//							if (name.equals("NONCASH_COLLATERAL_REQ_MARGIN")) {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							} else {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							}
//							//System.out.println(name + " [" + typeName + "]: " + value);
//							all_k_v.put(name, value.trim());
//						} catch (Exception e) {
//							System.err.println(e.getMessage());
//							all_k_v.put(name, null);
//						}
//						break;
//					case Types.NUMERIC:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
//						//System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					default:
//						Log.warn("The column type (" + rsmd.getColumnTypeName(i) + " for column "
//								+ rsmd.getColumnName(i) + ", Label: " + rsmd.getColumnLabel(i)
//								+ ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
//
//					}
//
//				}
//				allRecordForTransactionID.put(loan_id, all_k_v);
//				break;
//			}
//		} catch (Exception e) {
//			Log.warn("Exception fetching data for multiple rows from KUDU:-->" + e.getMessage());
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return allRecordForTransactionID;
//	}
//
//	public HashMap<String, HashMap<String, String>> fetchAllRecordsAgencyloanPositions(String queryString)
//			throws SQLException {
//		logInfo("Execute query :--> <b><i><font color=blue>" + queryString + "</font></i></b>", false);
//		allRecordForTransactionID = new HashMap<String, HashMap<String, String>>();
//		String loan_id = null;
//		Statement stmt = null;
//		try {
//			stmt = connection.createStatement();
//			ResultSet resultSet = stmt.executeQuery(queryString);
//			ResultSetMetaData rsmd = resultSet.getMetaData();
//			int columnCount = rsmd.getColumnCount();
//
//			while (resultSet.next()) {
//				HashMap<String, String> all_k_v = new HashMap<String, String>();
//
//				// you get a single result row in here, not the entire ResultSet
//				for (int i = 1; i <= columnCount; i++) {
//					int type = rsmd.getColumnType(i);
//					String typeName = rsmd.getColumnTypeName(i);
//					String name = rsmd.getColumnName(i).toUpperCase();
//					String value;
//					switch (type) {
//					case Types.VARCHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//
//						if (name.contains("G1_LOAN_NUM")) {
//							loan_id = value.trim();
//							// System.out.println("LOAN_ID: " + loan_id);
//						}
//
//						if (name.contains("CUSTODY_ACCOUNT")) {
//							loan_id = loan_id + value.trim();
//							// System.out.println("LOAN_ID: " + loan_id);
//						}
//
//						if (name.contains("LOAN_ID")) {
//							// Would have to ignore LOAN_ID
//						} else {
//							// System.out.println(name + " [" + typeName + "]: " + value);
//							all_k_v.put(name, value.trim());
//						}
//
//						break;
//					case Types.CHAR:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getString(i);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DOUBLE:
//						// value = resultSet.getString(i) == null ? "null" :
//						// String.valueOf(resultSet.getDouble(i));
//
//						DecimalFormat decimalFormatter = new DecimalFormat("############");
//						value = resultSet.getString(i) == null ? "null"
//								: String.valueOf(decimalFormatter.format(Double.parseDouble(resultSet.getString(i))));
//						// value=trimDouble(value);
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.INTEGER:
//						if (name.equals("SETTLE_DATE") || name.equals("LOAN_MATURITY_DATE")
//								|| name.equals("COLLATERAL_DUE_DATE") || name.equals("CREATE_DATE")
//								|| name.equals("TRADE_DATE")) {
//							value = resultSet.getString(i) == null ? "null"
//									: DateUtils.convertofEpochMilli(resultSet.getInt(i));
//						} else {
//							value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getInt(i));
//						}
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DATE:
//						value = resultSet.getString(i) == null ? "null" : String.valueOf(resultSet.getDate(i));
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.TIMESTAMP:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getTimestamp(i).toString();
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						if (!name.equalsIgnoreCase("CREATE_TIMESTAMP")) {
//							all_k_v.put(name, value.trim());
//						}
//						break;
//					case Types.BOOLEAN:
//						value = resultSet.getString(i) == null ? "null" : (resultSet.getBoolean(i) ? "true" : "false");
//						// System.out.println(name + ": " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					case Types.DECIMAL:
//						try {
//							if (name.equals("NONCASH_COLLATERAL_REQ_MARGIN")) {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							} else {
//								value = resultSet.getString(i) == null ? "null"
//										: doubleToString(resultSet.getBigDecimal(i));
//							}
//							// Log.info(name + " [" + typeName + "]: " + value);
//							all_k_v.put(name, value.trim());
//						} catch (Exception e) {
//							// System.err.println(e.getMessage());
//							all_k_v.put(name, null);
//						}
//						break;
//					case Types.NUMERIC:
//						value = resultSet.getString(i) == null ? "null" : resultSet.getBigDecimal(i).toString();
//						// System.out.println(name + " [" + typeName + "]: " + value);
//						all_k_v.put(name, value.trim());
//						break;
//					default:
//						Log.warn("The column type (" + rsmd.getColumnTypeName(i) + " for column "
//								+ rsmd.getColumnName(i) + ", Label: " + rsmd.getColumnLabel(i)
//								+ ") is currently not supported in method \"printResultColumns\".\nAdd it as case there.");
//
//					}
//
//				}
//
//				if (!empty(loan_id)) {
//					all_k_v.put("LOAN_ID", loan_id);
//				}
//
//				allRecordForTransactionID.put(loan_id, all_k_v);
//			}
//
//		} catch (Exception e) {
//			Log.warn("Exception fetching data for multiple rows from KUDU:-->" + e.getMessage());
//		} finally {
//			if (stmt != null) {
//				try {
//					stmt.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return allRecordForTransactionID;
//	}
//
//}
