package htmlTableFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class HTMLTableFormatter {
	private static boolean verbose = false;
	private static int tableWidth = -1;
	private static boolean customid = false;
	private static String id;

	public static void main(String[] args) {
		ArrayList<String> arrTable;
		String finalHTML = "";
		String fileName = "input.txt";
		String outputFileName = "output.html";
		boolean error = false;
		//set verbose
		String argsStr = argStringBuilder(args);
		if(argsStr.contains("-v")){
			verbose = true;
		}
		debugln("args: " + argsStr);
		
		//set id
		if(argsStr.contains("-id")){
			//there is a custom id set
			int arrPos = -1;
			for(int i=0;i<args.length;i++){
				if(args[i].equals("-id")){
					arrPos = i;
					break;
				}
			}
			if(arrPos == -1){
				error = true;
				errorln("Somehow there was an error with the -id command. Exiting.");
			}
			if(arrPos + 1 > args.length){
				error = true;
				errorln("No input filename specified after -id command. Exiting.");
			}
			if(!error){
				customid = true;
				id = args[arrPos+1];
			}
		}
		
		// args filename
		if(argsStr.contains("-i")){
			//there is an input filename
			int arrPos = -1;
			for(int i=0;i<args.length;i++){
				if(args[i].equals("-i")){
					arrPos = i;
					break;
				}
			}
			if(arrPos == -1){
				error = true;
				errorln("Somehow there was an error with the -i command. Exiting.");
			}
			if(arrPos+1 > args.length){
				error = true;
				errorln("No input filename specified after -i command. Exiting.");
			}
			if(!error){
				fileName = args[arrPos+1];
				
				if(fileName.contains(".")){
					outputFileName = fileName.substring(0, fileName.lastIndexOf('.')) + ".html";
				}else{
					outputFileName = fileName + ".html";
				}
			}
		}else{
			outln("No filename specified in parameters. Using default input.txt and output.html.");
		}
		
		
		//read input into arraylist
		arrTable = readInput(fileName);
		
		//convert arraylist into string using parameters
		finalHTML = convert2String(arrTable);
		
		//output to file
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFileName));
			bw.write(finalHTML);
			bw.close();
			outln("Output to " + outputFileName + " Complete!\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static String convert2String(ArrayList<String> arrTable) {
		String out = "<table";
		if(customid) out+= " id=\"" + id + "\" ";
		out += ">";
		int width = tableWidth;
		int cnt = 0;
		int rowcnt = 0;
		for(String s : arrTable){
			if(cnt % width == 0){
				if(rowcnt == 0) out += "<tr>";
				if(rowcnt > 0 && rowcnt % 2 == 0) out += "<tr class=\"even\">";
				if(rowcnt > 0 && rowcnt % 2 == 1) out += "<tr class=\"odd\">";
				rowcnt++;
			}
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
					debugln("strLine sanitized: " + strLine);
					
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
			
			outln("Input read from " + fn + "...");
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
	
	private static String argStringBuilder(String[] arr){
		StringBuilder builder = new StringBuilder();
		for(String s : arr) {
		    builder.append(s + " ");
		}
		return builder.toString();
	}
	
	public static void setWidth(int width){
		tableWidth = width;
	}
	
	public static void debugln(String str){
		if(verbose){
			System.out.println(str);
		}
	}
	
	public static void errorln(String str){
		System.err.println(str);
	}
	
	public static void outln(String str){
		System.out.println(str);
	}
	
	public static void out(String str){
		System.out.print(str);
	}

}
