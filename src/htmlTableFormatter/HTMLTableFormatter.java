package htmlTableFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HTMLTableFormatter {
	private static final boolean verbose = false;
	private static int tableWidth = -1;

	public static void main(String[] args) {
		// args filename TODO
		ArrayList<String> arrTable;
		String finalHTML = "";
		String fileName = "input.txt";
		String outputFileName = "output.html";
		
		//read input into arraylist
		arrTable = readInput(fileName);
		
		//convert arraylist into string using parameters
		finalHTML = convert2String(arrTable);
		
		//output to file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
			bw.write(finalHTML);
			bw.close();
			System.out.println("Output to " + outputFileName + " Complete!\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static String convert2String(ArrayList<String> arrTable) {
		String out = "<table>";
		int width = tableWidth;
		int cnt = 0;
		for(String s : arrTable){
			if(cnt % width == 0) out += "<tr>";
			if(cnt < width){
				out += "<th class=\"col" + ((cnt % width) + 1) + "\">" + s + "</th>";
			}else{
				out += "<td class=\"col" + ((cnt % width) + 1) + "\">" + s + "</td>";
			}
			if((cnt+1) % width == 0) out += "</tr>";
			cnt++;
		}
		out += "</table>";
		return out;
	}

	public static ArrayList<String> readInput(String fn){
		ArrayList<String> out = new ArrayList<String>();
		boolean widthSet = false;
		int width = -1;
		//parse in file
		try {
			BufferedReader br = new BufferedReader(new FileReader(fn));
			String strLine = "";
			while((strLine = br.readLine()) != null){
				if(strLine.contains("\"")){
					//santize ","'s!
					strLine = sanitizeCommas(strLine);
					if(verbose) System.out.println("strLine sanitized: " + strLine);
					
				}
				
				String[] arrStr = strLine.split(",");
				if(widthSet == false){
					width = arrStr.length;
					widthSet = true;
				}
				for(String s : arrStr){
					out.add(s);
				}
			}
			br.close();
			
			System.out.println("Input read...");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//set the width
		if(widthSet) setWidth(width);
		
		return out;
	}
	
	private static String sanitizeCommas(String str){
		String out = str;
		boolean replace = false;
		int offset = 0; //used to offset when changes have been made.
		char[] charArr = str.toCharArray();
		for(int i = 0; i < charArr.length; i++){
			char c = charArr[i];
			if(c == '\"'){
				if(replace){
					replace = false;
				}else{
					replace = true;
				}
			}
			if(replace & c == ','){
				//replace commas
				String before = out.substring(0, i+offset);
				String after = out.substring(i+offset+1);
				out = before + "&#44;" + after;
				offset += 4;
			}
		}
		//removing quotes
		out = out.replace("\"", "");
		return out;
	}
	
	public static void setWidth(int width){
		tableWidth = width;
	}

}
