package com.cucumber.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ExcelReader {

	private static XSSFSheet xssfSheet;
	private static XSSFWorkbook xssfWorkbook;
	private static FileInputStream fis;
	private static FileOutputStream fos;
	private static DataFormatter dataFormatter = new DataFormatter();
	private static Logger logger = Logger.getLogger(ReadExcel.class.getName());
	
	public static void main(String[] args) {
		String excelName = "SLZ_TestData";
		//String excelName1 = "SLZ_TestData1";
		String sheetName = "GetBargain";
		String header = "DTC_DOM";
		String value = "1124217";
		String header2 = "FED";
		String value2 = "1124245";
		ExcelReader.setData(excelName, sheetName, header2, value2);
		ExcelReader.setCellData(excelName, sheetName, header, value);
		//System.out.println(ExcelReader.getCellData(excelName, sheetName, header));
		System.out.println(ExcelReader.getCellData(excelName, sheetName, header2));
	}

	private static void setup(String fileName, String sheetName) throws IOException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		File folderPath = new File(loader.getResource("testdata").getFile());
		File xlsFile = new File(folderPath + File.separator + fileName + ".xls");
		File xlsxFile = new File(folderPath + File.separator + fileName + ".xlsx");
		if (xlsFile.exists()) {
			fis = new FileInputStream(xlsFile);
		} else if (xlsxFile.exists()) {
			fis = new FileInputStream(xlsxFile);
		} else {
			throw new IOException("ExcelDetails annotation may be missing or excel file/sheet doesn't exists.");
		}
		xssfWorkbook = new XSSFWorkbook(fis);
		xssfSheet = xssfWorkbook.getSheet(sheetName);
		// xssfWorkbook.close();
	}

	public static void close(String fileName) {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			File folderPath = new File(loader.getResource("testdata").getFile());
			File xlsFile = new File(folderPath + File.separator + fileName + ".xls");
			File xlsxFile = new File(folderPath + File.separator + fileName + ".xlsx");
			if (xlsFile.exists()) {
				fos = new FileOutputStream(xlsFile);
			} else if (xlsxFile.exists()) {
				fos = new FileOutputStream(xlsxFile);
			} else {
				throw new IOException("Excel Details annotation may be missing or excel file/sheet doesn't exists.");
			}
			xssfWorkbook.write(fos);
			fos.close();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fos);
		}
	}

	public static void setCellData(String excelName, String sheetName, String header, String value) {
		try {
			setup(excelName, sheetName);
			override(header, value);
			close(excelName);
			WaitUtils.sleep(1000);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
	
	public static void setData(String fileName, String sheetName, String header, String value){
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			File folderPath = new File(loader.getResource("testdata").getFile());
			File xlsxFile = new File(folderPath + File.separator + fileName + ".xlsx");			
            FileInputStream file = new FileInputStream(xlsxFile);

            @SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            Cell cell = null;int cellNum=0;

            String[] columnHeaders = getDataFromRow(sheet, 0);
    		for (int i = 0; i < columnHeaders.length; i++) {
    			if (columnHeaders[i].equalsIgnoreCase(header)) {
    				cellNum = i;
    				break;
    			}
    		}
    		
    		 // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.RED.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

          //Retrieve the row and check for null
            XSSFRow sheetrow = sheet.getRow(1);
            if(sheetrow == null){
                sheetrow = sheet.createRow(1);
            }
            //Update the value of cell
            cell = sheetrow.getCell(cellNum);
            if(cell == null){
                cell = sheetrow.createCell(cellNum);
            }            
            cell.setCellValue(value); 
            cell.setCellStyle(headerCellStyle);

            file.close();

            FileOutputStream outputStream  =new FileOutputStream(xlsxFile);
            workbook.write(outputStream );
            outputStream .close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	private static void override(String header, String value) {
		int cellNum = 0;
		String[] columnHeaders = getDataFromRow(xssfSheet, 0);
		for (int i = 0; i < columnHeaders.length; i++) {
			if (columnHeaders[i].equalsIgnoreCase(header)) {
				cellNum = i;
				break;
			}
		}
		Row r = xssfSheet.getRow(1);
		if (r == null) {
			r = xssfSheet.createRow(1);
		}
		Cell c = r.getCell(cellNum);		
		c.setCellValue(value);
		try {
			fis.close();
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}finally{
			IOUtils.closeQuietly(fis);
		}

	}

	@SuppressWarnings("finally")
	public static String getCellData(String excelName, String sheetName, String header) {
		String value = null;
		try {
			setup(excelName, sheetName);
			String[] columnHeaders = getDataFromRow(xssfSheet, 0);
			String[] valuesFromRow = getDataFromRow(xssfSheet, 1);
			for (int i = 0; i < columnHeaders.length; i++) {
				if (columnHeaders[i].equalsIgnoreCase(header)) {
					value = valuesFromRow[i];
				}
			}

		} catch (IOException e) {
			logger.warning(e.getMessage());
		} finally {
			IOUtils.closeQuietly(fis);
			return value;
		}

	}

	public static Object[][] readData(String[] excelInfo) {
		String excelName = excelInfo[0];
		String sheetName = excelInfo[1];
		List<Object[]> results = new ArrayList<>();
		try {
			setup(excelName, sheetName);
			int numRows = xssfSheet.getLastRowNum();
			for (int i = 1; i <= numRows; i++) {
				Map<String, String> inputValues = getHashMapDataFromRow(xssfSheet, i);
				results.add(new Object[] { inputValues });
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}

		finally {
			IOUtils.closeQuietly(fis);
		}
		return results.toArray(new Object[0][]);
	}

	public static List<HashMap<String, String>> readData(String excelName, String sheetName) {
		List<HashMap<String, String>> excelData = new ArrayList<>();
		try {
			setup(excelName, sheetName);
			int numRows = xssfSheet.getLastRowNum();
			for (int i = 1; i <= numRows; i++) {
				HashMap<String, String> inputValues = getHashMapDataFromRow(xssfSheet, i);
				excelData.add(inputValues);
			}
		} catch (IOException e) {
			logger.warning(e.getMessage());
		} finally {
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

	public static String[] getDataFromRow(Sheet sheet, int rowIndex) {
		FormulaEvaluator formulaEvaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		Row row = sheet.getRow(rowIndex);
		short numCells = row.getLastCellNum();
		String[] result = new String[numCells];
		for (int i = 0; i < numCells; i++) {
			result[i] = getValueAsString(row.getCell(i), formulaEvaluator);
		}
		return result;
	}

	public static List<String> getDataFromColumn(Sheet sheet, int colIndex) {
		List<String> colValues = new ArrayList<>();
		for (Row row : sheet) { // For each Row.
			Cell cell = row.getCell(colIndex); // Get the Cell at the Index /
												// Column you wa
			colValues.add(cell.toString());
		}
		return colValues;
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

}
