package com.rebar.utilities.Text_utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.testng.Assert;

import com.rebar.utilities.pdf_utilities.Pdf_Reader;

public class TextFileReader {
	
	public static void main(String[] args) throws java.io.IOException {

		
		ParseTextFileFromDelimter("C:\\Users\\ADCGNWD\\Desktop\\DatFile\\CustomExtract_ZF2Reports_5-EDS_71_IEDS_ZF2_TRAN_DEOD_TRADED_CASH - Copy.DAT",
				"|","C:\\Users\\ADCGNWD\\Desktop\\DatFile\\CustomExtract_ZF2Reports_5-EDS_71_IEDS_ZF2_TRAN_DEOD_TRADED_CASH - Copy1.DAT");
		
		
	}

	@SuppressWarnings("resource")
	public static void ParseTextFileFromDelimter(String filePath,String delimter,String outPutFilePath){
		try{
			
			BufferedReader br =null;
			String line;
			// Create new file
			File file = new File(filePath);
			// If file doesn't exists, then create it
			br= new BufferedReader(new FileReader(file));
			
			while((line = br.readLine())!=null) 
			{
				String alterContent=line.replaceAll("\\"+delimter , " ");
				Pdf_Reader.create_Write_Textfile(outPutFilePath,alterContent);
			}
		}
		catch(Exception e){
			Assert.fail("Failed-->"+e.getMessage());
		}
	}
	
	

}
