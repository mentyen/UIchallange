package com.cucumber.utilities;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class TextFileReader {
	public static File path = null;
	public static Set<String> temp = null;
	public static String _temp=null;
	public static HashMap<String, Integer> tempMap=null;

	public static void main(String[] args) throws IOException {

	}
	
	private static void log(Object msg) {
		System.out.println(String.valueOf(msg));
	}


	// For larger files

	public static void readLargerTextFile(String fileName) throws IOException {
		File path = new File(fileName);
		try (Scanner scanner = new Scanner(new FileReader(path))) {
			while (scanner.hasNextLine()) {
				// process each line in some way
				log(scanner.nextLine());
			}
		}
	}

	public static List<String> readLargerTextFileAlternate(String fileName) {
		File path = new File(fileName);
		List<String> temp = new LinkedList<String>();
		log("Start reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				// log(line);
				temp.add(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("Finish reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		return temp;
	}
	
	public String readTextFile(String fileName) {
	    String returnValue = "";
	    FileReader file;
	    String line = "";
	    try {
	        file = new FileReader(fileName);
	        BufferedReader reader = new BufferedReader(file);
	                    try {
	            while ((line = reader.readLine()) != null) {
	            returnValue += line + "\n";
	            }
	                    } finally {
	                        reader.close();
	                    }
	    } catch (FileNotFoundException e) {
	        throw new RuntimeException("File not found");
	    } catch (IOException e) {
	        throw new RuntimeException("IO Error occured");
	    }
	    return returnValue;

	}

	public static Set<String> readLargeTextFileAlternate(String fileName) {
		path = new File(fileName);
		temp = new LinkedHashSet<String>();
		log("Start reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				// log(line);
				temp.add(line);				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("Finish reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		return temp;
	}
	
	public static HashMap<String, Integer> readLargeTextFileAlternateWithDuplicates(String fileName) {
		path = new File(fileName);
		tempMap = new HashMap<>();
		long size=0,duplicates=0;
		
		log("Start reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way
				size++;
				if (!tempMap.containsKey(line)){
					tempMap.put(line, 1);
				}else{
					tempMap.put(line, tempMap.get(line) + 1);
					duplicates++;
				}
                			
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log("Finish reading file at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		log("Total number of records including header and footer in "+fileName+" is:--> "+size);
		log("Duplicate records in "+fileName+" is:--> "+duplicates);
		
		return tempMap;
	}

	public static void writeLargerTextFile(String fileName, List<String> lines) throws IOException {
		Path path = Paths.get(fileName);
		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			for (String line : lines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	public static String getHeader(String fileName) {
		path = new File(fileName);
		
		log("Start getting header at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				// process each line in some way				
				_temp=line;	
				break;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		log("Finish getting header at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		return _temp;
		
	}
	
	@SuppressWarnings("deprecation")
	public static String getFooter(String fileName) {
		path = new File(fileName);
		
		log("Start getting footer at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		int n_lines = 1;
		int counter = 0; 
		
		ReversedLinesFileReader object;
		try {
			object = new ReversedLinesFileReader(path);
			while(counter < n_lines) {		  
			    _temp=object.readLine();		   
			    counter++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		log("Finish getting footer at:-->" + DateUtils.getCurrentDate("HH:mm:ss"));
		return _temp;
		
	}
	
	
	

}
