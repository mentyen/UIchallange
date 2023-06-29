package com.rebar.utilities.pdf_utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import com.rebar.utilities.Log;
import com.itextpdf.io.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

public class Pdf_Reader {

	private static final String READING_COMPLETED = " -- Reading completed -- ";
	private static final String NOT_ABLE_TO_READ_FILE = "Not able to read file ";
	private static final String WHICH_IS_NOT_MATCHING = " which is not matching ";

	

	private static void CompareTwoTextFiles1(String path1, String path2) throws java.io.IOException {
		try(BufferedReader reader1 = new BufferedReader(new FileReader(path1))){
			try(BufferedReader reader2 = new BufferedReader(new FileReader(path2))){
				String line1 = reader1.readLine();
				String line2 = reader2.readLine();

				boolean areEqual = true;
				int lineNum = 1;
				int count = 0;

				while (line1 != null || line2 != null) {
					if (line1 == null || line2 == null) {
						areEqual = false;
						break;
					} else if (!line1.equalsIgnoreCase(line2)) {
						// areEqual = false;
						Log.info("Two files have different content. They differ at line "
								+ lineNum);
						Log.info("File1 has " + line1 + "\nFile2 has "
								+ line2);
						count = count + 1;
						Log.info("Differnece count : " + count);
					}

					line1 = reader1.readLine();
					line2 = reader2.readLine();
					lineNum++;
				}

				if (areEqual) {
					Log.info("Two files have same content.");
				}
			}
		}

	}

	private static void CompareTwoTextFiles(String file1, String file2)	throws java.io.IOException {

		List<String> list_file1 = null;
		List<String> list_file2 = null;

		list_file1 = new ArrayList<String>();
		list_file2 = new ArrayList<String>();

		String lineText = null;
		try(BufferedReader b1 = new BufferedReader(new FileReader(file1))){
			while ((lineText = b1.readLine()) != null) {
				list_file1.add(lineText);
			}

			try(BufferedReader b2 = new BufferedReader(new FileReader(file2))){
				while ((lineText = b2.readLine()) != null) {
					list_file2.add(lineText);
				}
				Boolean flag = extracted(list_file1, list_file2);

				if (flag) {
					Log.info("Both files are different.");
				} else {
					Log.info("Both files are same.");
				}

			}
		}
	}

	protected static Boolean extracted(List<String> list_file1, List<String> list_file2) {
		Boolean flag = false;

		int i = 0, j = 0;
		while (i < list_file1.size()) {
			while (j < list_file2.size()) {
				if (list_file1.get(i).equals(list_file2.get(j))) {
					Log.info("equal");
				}
			}
		}

		for (String content1 : list_file1) {
			for (String content2 : list_file2) {
				if (content1.equals(content2)) {
					Log.info("Match Found: " + content1);
				}
			}
		}
		return flag;
	}


	@SuppressWarnings("unused")
	private static void CompareTwoTextFiles2(String file1, String file2,String file3) throws java.io.IOException {



		List<String> firstList = new ArrayList<String>();
		List<String> secondList = new ArrayList<String>();
		String lineText = null;


		//Read Second File
		try(BufferedReader b2 = new BufferedReader(new FileReader(file2))){
			while ((lineText = b2.readLine()) != null) {
				secondList.add(lineText);
			}

			//Read First File
			boolean textFalg=false;
			try(BufferedReader b1 = new BufferedReader(new FileReader(file1))){
				while ((lineText = b1.readLine()) != null) {

					if (secondList.contains(lineText)) {
						secondList.remove(lineText);
					}else{
						if(!textFalg){
							firstList.add("FirstSheet:EnteriesRecord in "+file1+WHICH_IS_NOT_MATCHING);
							textFalg=true;
						}

						firstList.add(lineText);
					}	
				}

				firstList.add("SecondSheet:EnteriesRecord in "+file2+WHICH_IS_NOT_MATCHING);
				firstList.addAll(secondList);

				if(firstList.size()!=0){
					create_Write_TextfileWithList(file3,firstList);
				}

			}
		}
	}

	private static void ParsePDFToTextFile(String pdfPath) throws java.io.IOException {

		StampTime("Start Time: ");

		Log.info(pdfPath);

		try(FileWriter writer = new FileWriter("After.txt", false)){
			try(PdfReader reader = new PdfReader(pdfPath)){
				try(PdfDocument pdfDoc = new PdfDocument(reader)){
					int noOfPages = pdfDoc.getNumberOfPages();

					for (int i = 1; i <= noOfPages; i++) {
						String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
						// Log.info(contentOfPage);
						writer.write(contentOfPage);
						writer.write("\r\n");

						// Log.info(" End of page : " + i + " -------- ");
					}
					Log.info(READING_COMPLETED);
					pdfDoc.close();
					writer.close();
				}
			}
		}

	}

	private static void StampTime(String message) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		Log.info(message + dtf.format(now));
	}

	public static String ParsePDF_PageRange(String pdfFile, int startPage,	int endPage) throws java.io.IOException {
		StringBuffer sb;

		try(PdfReader reader = new PdfReader(pdfFile)){
			try(PdfDocument pdfDoc = new PdfDocument(reader)){
				sb = new StringBuffer();
				//int noOfPages = pdfDoc.getNumberOfPages();
				for (int i = startPage; i <= endPage; i++) {
					String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc
							.getPage(i));
					sb.append(contentOfPage);
				}
				Log.info(READING_COMPLETED);
				pdfDoc.close();
				reader.close();
				return sb.toString();

			}
		}
	}

	public static String ParsePDF_SpecificPages(String pdfFile, String pages) throws java.io.IOException {

		StringBuffer sb;

		try(PdfReader reader = new PdfReader(pdfFile)){
			try(PdfDocument pdfDoc = new PdfDocument(reader)){
				sb = new StringBuffer();
				//int noOfPages = pdfDoc.getNumberOfPages();

				String pagesArr[] = pages.split(",");

				for (String singlePage : pagesArr) {
					int index = Integer.parseInt(singlePage);
					String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc
							.getPage(index));
					sb.append(contentOfPage);

				}
				Log.info(READING_COMPLETED);
				pdfDoc.close();
				reader.close();
				return sb.toString();

			}
		}
	}


	public static String ParsePDF_ReadFirstOccurenceOfString(String pdfFile,String startString, String endString) throws java.io.IOException {
		StringBuffer sb;
		try(PdfReader reader = new PdfReader(pdfFile)){
			try(PdfDocument pdfDoc = new PdfDocument(reader)){
				sb = new StringBuffer();
				int noOfPages = pdfDoc.getNumberOfPages();

				for (int i = 1; i <= noOfPages; i++) {
					String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
					sb.append(contentOfPage);	
				}
				String content = startString + StringUtils.substringBetween(sb.toString(), startString, endString) + endString;
				Log.info(READING_COMPLETED);
				pdfDoc.close();
				reader.close();
				return content;

			}
		}
	}



	public static String[] ParsePDF_ReadMulitpleOccurenceOfString(String pdfFile,String startString, String endString) throws java.io.IOException {
		StringBuffer sb;
		try(PdfReader reader = new PdfReader(pdfFile)){
			try(PdfDocument pdfDoc = new PdfDocument(reader)){
				sb = new StringBuffer();
				int noOfPages = pdfDoc.getNumberOfPages();

				for (int i = 1; i <= noOfPages; i++) {
					String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
					sb.append(contentOfPage);	
				}
				String[] content = StringUtils.substringsBetween(sb.toString(), startString, endString);
				Log.info(READING_COMPLETED);
				pdfDoc.close();
				reader.close();
				return content;

			}
		}
	}


	public static String ParsePDF_ReadLastOccurenceOfString(String pdfFile,String startString, String endString) throws java.io.IOException {
		try {
			PdfReader reader=null;
			PdfDocument pdfDoc = null;
			StringBuffer sb;

			reader= new PdfReader(pdfFile);
			pdfDoc = new PdfDocument(reader);
			sb = new StringBuffer();
			int noOfPages = pdfDoc.getNumberOfPages();

			for (int i = 1; i <= noOfPages; i++) {
				String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
				sb.append(contentOfPage);	
			}
			String[] content = StringUtils.substringsBetween(sb.toString(), startString, endString);
			String LastOccurenceOFString = startString + content[content.length-1] + endString;
			Log.info(READING_COMPLETED);
			pdfDoc.close();
			reader.close();
			return LastOccurenceOFString;

		} catch (IOException e) {
			throw new IllegalArgumentException(NOT_ABLE_TO_READ_FILE + pdfFile, e);
		}
	}

	public static String ParsePDF_ReadSpecificOccurenceOfString(String pdfFile,String startString, String endString,int occurenceNumber) throws java.io.IOException {
		try {
			PdfReader reader=null;
			PdfDocument pdfDoc = null;
			StringBuffer sb;

			reader= new PdfReader(pdfFile);
			pdfDoc = new PdfDocument(reader);
			sb = new StringBuffer();
			int noOfPages = pdfDoc.getNumberOfPages();

			for (int i = 1; i <= noOfPages; i++) {
				String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc.getPage(i));
				sb.append(contentOfPage);	
			}
			String[] content = StringUtils.substringsBetween(sb.toString(), startString, endString);
			String LastOccurenceOFString = startString + content[occurenceNumber-1] + endString;
			Log.info(READING_COMPLETED);
			pdfDoc.close();
			reader.close();
			return LastOccurenceOFString;

		} catch (IOException e) {
			throw new IllegalArgumentException(NOT_ABLE_TO_READ_FILE + pdfFile, e);
		}
	}

	public static String ParsePDF_AllPage(String pdfFile)
			throws java.io.IOException {
		try {
			PdfReader reader = null;
			PdfDocument pdfDoc = null;
			StringBuffer sb;

			reader = new PdfReader(pdfFile);
			pdfDoc = new PdfDocument(reader);
			sb = new StringBuffer();
			int noOfPages = pdfDoc.getNumberOfPages();

			for (int i = 1; i <= noOfPages; i++) {
				String contentOfPage = PdfTextExtractor.getTextFromPage(pdfDoc
						.getPage(i));
				sb.append(contentOfPage);
			}
			Log.info(READING_COMPLETED);
			pdfDoc.close();
			reader.close();
			return sb.toString();

		} catch (IOException e) {
			throw new IllegalArgumentException(NOT_ABLE_TO_READ_FILE
					+ pdfFile, e);
		}
	}

	/**
	 * This function is used crate and write in text file
	 * @throws java.io.IOException 
	 */
	public static void create_Write_Textfile(String filePath, String filecontent) throws java.io.IOException {

		// Create new file
		File file = new File(filePath);
		// If file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		try(FileWriter fw = new FileWriter(file, true)){
			try(BufferedWriter bw = new BufferedWriter(fw)){

				String lines[] = filecontent.split("\\r?\\n");
				for (String line : lines) {
					bw.write(line);
					bw.newLine();
				}

				bw.flush();
				// Close connection
				bw.close();
				Log.info("File Write in Desk Sucessfully");
			}
		}
	}

	/**
	 * This function is used crate and write in text file
	 * @throws java.io.IOException 
	 */
	public static void create_Write_TextfileWithList(String filePath, List<String> listContent) throws java.io.IOException {

		// Create new file
		File file = new File(filePath);
		// If file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		try(FileWriter fw = new FileWriter(file, true)){
			try(BufferedWriter bw = new BufferedWriter(fw)){

				for (String line : listContent) {

					bw.write(line);
					bw.newLine();
				}
				bw.flush();
				// Close connection
				bw.close();
				Log.info("File Write on Desk Sucessfully");

			}
		}
	}

	/**
	 * This function is used write in Excel file
	 * @throws java.io.IOException 
	 */
	public static void create_Write_ExcelFile(String filePath, String filecontent) throws java.io.IOException {

		XSSFSheet sheet = null;

		try(XSSFWorkbook workbook = new XSSFWorkbook()){
			// Create a blank sheet
			sheet = workbook.createSheet("Data");
			int rownum = 0;
			String lines[] = filecontent.split("\\r?\\n");

			for (String line : lines) {

				Row row = sheet.createRow(rownum++);
				Cell cell = row.createCell(0);

				cell.setCellValue(line);

			}

			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			out.close();
			Log.info("Excel written successfully on disk.");

		}
	}

	/**
	 * This function is used write in Excel file
	 */
	public static void create_Write_ExcelFileWithList(String filePath,List<String> listContent) {

		XSSFSheet sheet1 = null;
		XSSFSheet sheet2 = null;

		try(XSSFWorkbook workbook = new XSSFWorkbook()){
			// Create a blank sheet
			sheet1 = workbook.createSheet("FirstSheetMisMatches");
			sheet2 = workbook.createSheet("SecondSheetMisMatches");
			int rownum = 0;
			int rownum1 = 0;
			boolean sheetflag=false;


			for (String line : listContent) {

				if(!sheetflag){

					Row row = sheet1.createRow(rownum++);
					Cell cell = row.createCell(0);
					cell.setCellValue(line);
				}
				if(line.contains("SecondSheet")||sheetflag==true){
					sheetflag=true;
					Row row1 = sheet2.createRow(rownum1++);
					Cell cell1 = row1.createCell(0);
					cell1.setCellValue(line);
				}
			}

			// Write the workbook in file system
			FileOutputStream out = new FileOutputStream(filePath);
			workbook.write(out);
			out.close();
			Log.info("Excel written successfully on disk.");

		} catch (java.io.IOException e) {
			Assert.fail("Failed-->" + e.getMessage());
		}
	}

	/*@SuppressWarnings("resource")
	public static void CompareTwoExcelFiles(String path1, String path2,
			String path3) throws IOException, java.io.IOException {

		FileInputStream excelFile1;
		FileInputStream excelFile2;
		Workbook Beforeworkbook;
		Workbook Afterworkbook;
		Sheet BeforeworkbookSheet;
		Sheet AfterworkbookSheet;
		Iterator<Row> Beforeiterator;
		Iterator<Row> Afteriterator;
		Iterator<Cell> firstSheetRowcellIterator;
		Iterator<Cell> secondSheetRowcellIterator;

		excelFile1 = new FileInputStream(new File(path1));
		excelFile2 = new FileInputStream(new File(path2));
		Beforeworkbook = new XSSFWorkbook(excelFile1);
		Afterworkbook = new XSSFWorkbook(excelFile2);
		BeforeworkbookSheet = Beforeworkbook.getSheetAt(0);
		AfterworkbookSheet = Afterworkbook.getSheetAt(0);

		Beforeiterator = BeforeworkbookSheet.iterator();
		Afteriterator = AfterworkbookSheet.iterator();
		Cell firstSheetcurrentCell;
		Cell secondSheetcurrentCell;
		boolean flag = false;
		String dataValue = null;

		try {
			while (Beforeiterator.hasNext()) {

				Row firstSheetcurrentRow = Beforeiterator.next();
				Row secondSheetcurrentRow = Afteriterator.next();
				firstSheetRowcellIterator = firstSheetcurrentRow.iterator();
				secondSheetRowcellIterator = secondSheetcurrentRow.iterator();

				while (firstSheetRowcellIterator.hasNext()) {

					firstSheetcurrentCell = firstSheetRowcellIterator.next();
					secondSheetcurrentCell = secondSheetRowcellIterator.next();

					if (firstSheetcurrentCell
							.getStringCellValue()
							.equalsIgnoreCase(
									secondSheetcurrentCell.getStringCellValue())) {
						Log.info("Row Mached");
					} else {
						Log.info("Row not Mached " + "Row Number"
								+ firstSheetcurrentRow.getRowNum() + " "
								+ firstSheetcurrentCell.getStringCellValue());
						dataValue = dataValue + "Row Number = " + " "
								+ firstSheetcurrentRow.getRowNum() + " "
								+ "FirstExcelValue =  "
								+ firstSheetcurrentCell.getStringCellValue()
								+ " " + "SecondExcelValue = "
								+ secondSheetcurrentCell.getStringCellValue();
						flag = true;

					}
				}

			}
			if (flag) {
				Log.info("Both files are not matched");
				create_Write_ExcelFile(path3, dataValue);

			}
		} catch (Exception er) {
			Assert.fail("Failed due to " + er.getMessage());
		}
	}*/

	@SuppressWarnings("resource")
	public static void CompareTwoExcelFiles1(String path1, String path2,
			String path3) throws IOException, java.io.IOException {

		FileInputStream excelFile1;
		FileInputStream excelFile2;
		Workbook firstWorkbook;
		Workbook secondWorkbook;
		Sheet firstworkbookSheet;
		Sheet secondworkbookSheet;
		try {
			excelFile2 = new FileInputStream(new File(path2));
			secondWorkbook = new XSSFWorkbook(excelFile2);
			secondworkbookSheet = secondWorkbook.getSheetAt(0);
			List<String> secondList = new ArrayList<String>();
			List<String> firstList = new ArrayList<String>();


			// Second WorkBook 
			for (int j = 0; j < secondworkbookSheet.getPhysicalNumberOfRows(); j++) {
				Row secondWorkbooksheetCurrentRow = secondworkbookSheet
						.getRow(j);
				Cell secondWorkBookCurrentCell1 = secondWorkbooksheetCurrentRow
						.getCell(0);
				secondList.add(secondWorkBookCurrentCell1.getStringCellValue());
			}

			excelFile2.close();

			// First WorkBook
			excelFile1 = new FileInputStream(new File(path1));
			firstWorkbook = new XSSFWorkbook(excelFile1);
			firstworkbookSheet = firstWorkbook.getSheetAt(0);
			String firstWorkbookCellValue;
			boolean falg=false;

			for (int i = 0; i < firstworkbookSheet.getPhysicalNumberOfRows(); i++) {
				Row firstWorkbooksheetCurrentRow = firstworkbookSheet.getRow(i);
				Cell firstWorkbookcurrentCell = firstWorkbooksheetCurrentRow
						.getCell(0);
				firstWorkbookCellValue = firstWorkbookcurrentCell
						.getStringCellValue();
				if (secondList.contains(firstWorkbookCellValue)) {
					secondList.remove(firstWorkbookCellValue);
				}else{
					if(!falg){
						firstList.add("FirstSheet:EnteriesRecord in "+path1+WHICH_IS_NOT_MATCHING);
						falg=true;
					}

					firstList.add(firstWorkbookCellValue);
				}
			}
			excelFile1.close();

			firstList.add("SecondSheet:EnteriesRecord in "+path2+WHICH_IS_NOT_MATCHING);
			firstList.addAll(secondList);

			if(firstList.size()!=0){

				create_Write_ExcelFileWithList(path3, firstList);
				create_Write_TextfileWithList("C:\\EQE_AST_MEGA_GF_Eagle\\AfterResult.txt",firstList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
