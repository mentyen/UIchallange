package com.cucumber.utilities;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;


public class ReadExcel {
	// private static XSSFSheet xssfSheet;
	// private static HSSFSheet hssfSheet;

	private static FileInputStream fis;
	private static ThreadLocal<XSSFWorkbook> xssfWorkbook = new ThreadLocal<>();
	private static DataFormatter dataFormatter = new DataFormatter();
	private static Logger logger = Logger.getLogger(ReadExcel.class.getName());

	private static ThreadLocal<XSSFSheet> xssfSheet = new ThreadLocal<>();
	private static ThreadLocal<XSSFSheet> hssfSheet = new ThreadLocal<>();
	// private static ThreadLocal<FileInputStream> fis = new ThreadLocal<>();

	private ReadExcel() {
	}

	//adding method to read stream from the api
	private static ThreadLocal<InputStream> fis1 = new ThreadLocal();

	private static void setup(InputStream inputStreamXlsx, String sheetName) throws IOException {

		if (inputStreamXlsx == null) {
			throw new IOException("ExcelDetails annotation may be missing or excel file/sheet doesn't exists.");
		}
		fis1.set(inputStreamXlsx);
		ZipSecureFile.setMinInflateRatio(0);
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis1.get());
		xssfSheet.set(xssfWorkbook.getSheet(sheetName));
		xssfWorkbook.close();
	}

	public static HashMap<String, HashMap<String, String>> readDataCusipVar(InputStream is, String sheetName, int headerIndex, int bodyIndex) {
		//System.out.println("Read data: " + sheetName);
		HashMap<String, HashMap<String, String>> excelData = new HashMap<String, HashMap<String, String>>();

		try {
			setup(is, sheetName);
			int numRows = ((XSSFSheet)xssfSheet.get()).getLastRowNum();
			//System.out.println("Last row : " + numRows);
			for(int i = bodyIndex; i <= numRows; ++i) {
				String key=getKeyFor((Sheet)xssfSheet.get(), i,headerIndex,"client_cusip");
				HashMap<String, String> inputValues = getHashMapDataFromRow((Sheet)xssfSheet.get(), i,headerIndex);
				excelData.put(key,inputValues);
			}
		} catch (IOException var9) {
			logger.warning(var9.getMessage());
		} finally {
			IOUtils.closeQuietly((Closeable)fis1.get());
		}
		return excelData;
	}

	private static String getKeyFor(Sheet sheet, int rowIndex, int headerIndex,String _key) {
		HashMap<String, String> results = new HashMap<>();
		String key=null;
		String[] columnHeaders = getHeadersReports(sheet, headerIndex);
		String[] valuesFromRow = getDataFromRowReports(sheet, rowIndex);
		switch (_key) {
			case "client_cusip":
				for (int i = 0; i < columnHeaders.length; i++) {
					if(columnHeaders[i].equalsIgnoreCase("client")){
						key=valuesFromRow[i];
						break;
					}
				}
				for (int i = 0; i < columnHeaders.length; i++) {
					if(columnHeaders[i].equalsIgnoreCase("cusip")){
						key=key+valuesFromRow[i];
						break;
					}
				}
				break;
		}
		return key;
	}

	public static List<HashMap<String, String>> readData(InputStream is, String sheetName,int headerIndex,int bodyIndex) {
		//System.out.println("Read data: " + sheetName);
		List<HashMap<String, String>> excelData = new ArrayList();

		try {
			setup(is, sheetName);
			int numRows = ((XSSFSheet)xssfSheet.get()).getLastRowNum();
			//System.out.println("Last row : " + numRows);
			for(int i = bodyIndex; i <= numRows; ++i) {
				HashMap<String, String> inputValues = getHashMapDataFromRow((Sheet)xssfSheet.get(), i,headerIndex);
				excelData.add(inputValues);
			}
		} catch (IOException var9) {
			logger.warning(var9.getMessage());
		} finally {
			IOUtils.closeQuietly((Closeable)fis1.get());
		}

		return excelData;
	}

	private static HashMap<String, String> getHashMapDataFromRow(Sheet sheet, int rowIndex,int headerIndex) {
		HashMap<String, String> results = new HashMap<>();
		String[] columnHeaders = getHeadersReports(sheet, headerIndex);
		String[] valuesFromRow = getDataFromRowReports(sheet, rowIndex);

		for (int i = 0; i < columnHeaders.length; i++) {
			if (i >= valuesFromRow.length) {
				results.put(columnHeaders[i], "");
			} else {
				results.put(columnHeaders[i], valuesFromRow[i]);
			}
		}
		return results;
	}

	private static String[] getHeadersReports(Sheet sheet, int rowIndex) {
		FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		Row row = sheet.getRow(rowIndex);
		short numCells = row.getLastCellNum();
		String[] result = new String[numCells];
		for (int i = 0; i < numCells; i++) {
			String header=null;
			header = getValueAsString(row.getCell(i), formulaEvaluator).replace(" ", "").replace("/", "").toUpperCase();
			if (header.equalsIgnoreCase("DATE")) {
				result[i] ="CLOSEBUSDATE";
			} else {
				result[i] =header;
			}
		}
		return result;
	}

	private static String[] getDataFromRowReports(Sheet sheet, int rowIndex) {
		FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		Row row = sheet.getRow(rowIndex);
		short numCells = row.getLastCellNum();
		String[] result = new String[numCells];
		for (int i = 0; i < numCells; i++) {
			result[i] = getValueAsStringReports(row.getCell(i), formulaEvaluator);
		}
		return result;
	}

	private static String getValueAsStringReports(Cell cell, FormulaEvaluator formulaEvaluator) {
		String value=null;
		if (cell != null) {
			CellType cellType = cell.getCellType();
			if (cellType.equals(CellType.BOOLEAN)) {
				return String.valueOf(cell.getBooleanCellValue());
			} else if (cellType.equals(CellType.NUMERIC)) {
				//System.out.println("Value : numeric :-->"+dataFormatter.formatCellValue(cell));
				value=dataFormatter.formatCellValue(cell).replace(",","");
				return value;
			} else if (cellType.equals(CellType.STRING)) {
				//System.out.println("Value : string :-->"+cell.getRichStringCellValue().getString());
				return cell.getRichStringCellValue().getString();
			} else if (cellType.equals(CellType.FORMULA)) {
				//System.out.println("Value : formula :-->"+formulaEvaluator.evaluate(cell).getStringValue());
				return formulaEvaluator.evaluate(cell).getStringValue();
			}
		}
		return "";
	}
	///////////////////end////////////

	private static void setup(String fileName, String sheetName) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		// System.out.println("In Read excel setup: " +
		// Thread.currentThread().getId());

		String userDir = System.getProperty("user.dir");
		userDir = userDir + "\\src\\test\\resources\\data\\";
		// System.out.println(userDir);
		// File folderPath = new File(loader.getResource("./data").getFile());

		String xlsFilePath = userDir + fileName + ".xls";
		String xlsxFilePath = userDir + fileName + ".xlsx";
		// System.out.println(xlsxFilePath);

		File xlsFile = new File(xlsFilePath);
		File xlsxFile = new File(xlsxFilePath);

		if (xlsFile.exists()) {
			fis = new FileInputStream(xlsFilePath);
			// fis.set(new FileInputStream(xlsFile));
		} else if (xlsxFile.exists()) {
			fis = new FileInputStream(xlsxFilePath);
			// fis.set(new FileInputStream(xlsxFile));
		} else {
			throw new IOException("ExcelDetails annotation may be missing or excel file/sheet doesn't exists.");
		}

		XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis);
		// XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fis.get());
		// xssfSheet = xssfWorkbook.getSheet(sheetName);
		xssfSheet.set(xssfWorkbook.getSheet(sheetName));
		xssfWorkbook.close();
	}

	public static Object[][] readData(String[] excelInfo) {
		String excelName = excelInfo[0];
		String sheetName = excelInfo[1];
		List<Object[]> results = new ArrayList<>();
		try {
			setup(excelName, sheetName);
			// int numRows = xssfSheet.getLastRowNum();
			int numRows = xssfSheet.get().getLastRowNum();
			for (int i = 1; i <= numRows; i++) {
				Map<String, String> inputValues = getHashMapDataFromRow(xssfSheet.get(), i);
				results.add(new Object[] { inputValues });
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

		finally {
			// IOUtils.closeQuietly(fis.get());
			IOUtils.closeQuietly(fis);
		}
		return results.toArray(new Object[0][]);
	}

	public static List<HashMap<String, String>> readData(String excelName, String sheetName) {

		// System.out.println("Before Read data:" +
		// Thread.currentThread().getId());
		List<HashMap<String, String>> excelData = new ArrayList<>();

		try {
			// System.out.println(excelName + " excelName is read -->" +
			// sheetName);
			setup(excelName, sheetName);
			// int numRows = xssfSheet.getLastRowNum();
			int numRows = xssfSheet.get().getLastRowNum();
			// System.out.println(numRows);

			for (int i = 1; i <= numRows; i++) {
				HashMap<String, String> inputValues = getHashMapDataFromRow(xssfSheet.get(), i);
				excelData.add(inputValues);
			}

		} catch (IOException e) {
			logger.warning(e.getMessage());
		} finally {
			// IOUtils.closeQuietly(fis.get());
			IOUtils.closeQuietly(fis);
		}
		return excelData;
	}

	private static HashMap<String, String> getHashMapDataFromRow(Sheet sheet, int rowIndex) {
		HashMap<String, String> results = new HashMap<>();
		String[] columnHeaders = getDataFromRow(sheet, 0);
		String[] valuesFromRow = getDataFromRow(sheet, rowIndex);

		for (int i = 0; i < columnHeaders.length; i++) {
			if (i >= valuesFromRow.length) {
				results.put(columnHeaders[i], "");
			} else {
				results.put(columnHeaders[i], valuesFromRow[i]);
			}
		}
		return results;
	}

	private static String[] getDataFromRow(Sheet sheet, int rowIndex) {
		FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		Row row = sheet.getRow(rowIndex);
		short numCells = row.getLastCellNum();
		String[] result = new String[numCells];
		for (int i = 0; i < numCells; i++) {
			result[i] = getValueAsString(row.getCell(i), formulaEvaluator);
		}
		return result;
	}

	private static String getValueAsString(Cell cell, FormulaEvaluator formulaEvaluator) {
		if (cell != null) {
			CellType cellType = cell.getCellType();
			if (cellType.equals(CellType.BOOLEAN)) {
				return String.valueOf(cell.getBooleanCellValue());
			} else if (cellType.equals(CellType.NUMERIC)) {
				return dataFormatter.formatCellValue(cell);
			} else if (cellType.equals(CellType.STRING)) {
				return cell.getRichStringCellValue().getString();
			} else if (cellType.equals(CellType.FORMULA)) {
				return formulaEvaluator.evaluate(cell).getStringValue();
			}
		}
		return "";
	}

	public static Object[][] getData(String excelName, String sheetName) {
		List<Object[]> results = new ArrayList<>();
		try {
			setup(excelName, sheetName);
			int numRows = xssfSheet.get().getLastRowNum();
			for (int i = 1; i <= numRows; i++) {
				Map<String, String> inputValues = getMapDataFromRow(xssfSheet.get(), i);
				results.add(new Object[] { inputValues });
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

		finally {
			// IOUtils.closeQuietly(fis.get());
			IOUtils.closeQuietly(fis);
		}
		return results.toArray(new Object[0][]);
	}

	public static int getRowCount(File file, String sheetName) {

		int numRows = 0;

		try {
			if (file.exists()) {
				// fis.set( new FileInputStream(file));
				// xssfWorkbook.set(new XSSFWorkbook(fis.get()));

				xssfWorkbook.set(new XSSFWorkbook(fis));
				xssfSheet.set(xssfWorkbook.get().getSheet(sheetName));
				numRows = xssfSheet.get().getLastRowNum();

				System.out.println("Number of Rows in the excel sheet" + file.getName() + "is" + numRows);

				xssfWorkbook.get().close();
			}
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
		return numRows;
	}

	private static Map<String, String> getMapDataFromRow(Sheet sheet, int rowIndex) {
		Map<String, String> results = new LinkedHashMap<>();
		String[] columnHeaders = getDataFromRow(sheet, 0);
		String[] valuesFromRow = getDataFromRow(sheet, rowIndex);
		for (int i = 0; i < columnHeaders.length; i++) {
			if (i >= valuesFromRow.length) {
				results.put(columnHeaders[i], "");
			} else {
				results.put(columnHeaders[i], valuesFromRow[i]);
			}
		}
		return results;
	}

}
