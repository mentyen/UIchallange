package com.rebar.utilities.Text_utilities;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.rebar.utilities.Log;

public class DAT_Compare_Utility {

	public static void main(String[] args) {
		String file1_path = "C:\\Users\\adcibtj\\Downloads\\2-DRAS_DE_31_DTH47ZF2EGLOT-Master.TXT";
		String file2_path = "C:\\Users\\adcibtj\\Downloads\\2-DRAS_DE_31_DTH47ZF2EGLOT-Master1.TXT";

		boolean flag;
		try {
			flag = compareFiles(file1_path, file2_path);
			Log.info(flag);
		} catch (IOException e) {
			Log.error(e);
		}


	}

	private static Boolean compareFiles(String file1, String file2) throws IOException {

		List<String> list_file1 = null;
		List<String> list_file2 = null;

		list_file1 = new ArrayList<String>();
		list_file2 = new ArrayList<String>();

		String lineText = null;
		int diffCount = 0;
		Boolean flag = false;

		try(FileWriter writer = new FileWriter("Difference.txt", false)){
			try(BufferedReader b1 = new BufferedReader(new FileReader(file1))){
				while ((lineText = b1.readLine()) != null) {
					list_file1.add(lineText.trim());
				}
				lineText = null;
				try(BufferedReader b2 = new BufferedReader(new FileReader(file2))){
					while ((lineText = b2.readLine()) != null) {
						list_file2.add(lineText.trim());
					}

					int i = 0, j = 0;
					Log.info("Comparison started: ---- ");
					while (i < list_file1.size()) {
						while (j < list_file2.size()) {
							boolean f = list_file1.get(i).equals(list_file2.get(j));
							if (f) {
								list_file2.remove(j);
								j = 0; break;
							} else {
								j++;
							}
						} // End of Inner while

						if((j == list_file2.size()) && (list_file2.size() != 0)) {
							Log.info("Expected: " + list_file1.get(i));
							writer.write("Expected: " + list_file1.get(i) + "\n");
							Log.info("Actual  : Not found in file");
							writer.write("Actual  : Not found in file\n");
							Log.info("---------------------------");
							writer.write("---------------------------\n");
							j = 0;
							flag = true;
							diffCount++;
						}
						i++;
					} // End of Outer while

					if (flag) {
						Log.info("Both files are different.");
						writer.write("Both files are different.\n");
						Log.info("Total number of difference found are: "+ diffCount);
						writer.write("Total number of difference found are: "+ diffCount + "\n");
					} else {
						Log.info("Both files are same.");
						writer.write("Both files are same.\n");
					}

				}
			}

		}

		return flag;
	}

}
