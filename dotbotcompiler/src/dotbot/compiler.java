package dotbot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class compiler {
	
	static String CHECK = "";
	
	static ArrayList <String> userFunctions = new ArrayList <>(); 
	
	
	public static void main (String args[]) throws IOException {
		
		File file = new File("test.txt");
		String content = null;
		try {
		       FileReader reader = new FileReader(file);
		       char[] chars = new char[(int) file.length()];
		       reader.read(chars);
		       content = new String(chars);
		       reader.close();
		   } catch (IOException e) {
		       e.printStackTrace();
		}
		compiler.compile (content);
		
	}
	
	private static void compile (String x) {

		JSONObject superJSON = new JSONObject(x);
		
		JSONObject definitions = superJSON.getJSONObject("definitions");		
		
		JSONArray main = superJSON.getJSONArray("main");
		
		JSONObject variables = new JSONObject();
		
		for (int i = 0; i < main.length(); i++) {
			
			compiler.evaluateObject(main.getJSONObject(i), variables, definitions);
			
		}
		
	}

	private static String evaluateObject(JSONObject jsonObject, JSONObject v, JSONObject d) {
		String name = (String) jsonObject.keys().next();
		
		JSONObject function = jsonObject.getJSONObject(name);
		//System.out.print(name);
		//System.out.println(function.toString());
		
		JSONArray keys = function.names();
		
		for (int i = 0; i < keys.length(); i++) { 
			String temp_str = keys.getString(i);
			
			if (function.get(temp_str).getClass() != compiler.CHECK.getClass()) {
				
				function.put(temp_str, compiler.evaluateObject(function.getJSONObject(temp_str), v, d));
				
			}
			
		}
		//System.out.println(jsonObject.toString());
		switch (name) {
		
		case "add": return compiler.add((String) function.get("arg0"),(String) function.get("arg1"), v);
		
		case "sub": return compiler.sub((String) function.get("arg0"),(String) function.get("arg1"), v);
		
		case "assign": compiler.assign((String) function.get("arg0"),(String) function.get("arg1"), v); 
		break;
		
		case "print": compiler.print((String) function.get("arg0"), v);
		break;
		
		default: 
			if (d.has(name)) {
				//put all arguments into an array
				JSONObject definedfunction = d.getJSONObject(name);
				System.out.println("User-defined function: " + definedfunction.toString());
				JSONObject localvariables = new JSONObject();
				JSONObject argumentsdefinitions = definedfunction.getJSONObject("args"); //definition of arguments
				System.out.println("User-defined function arguments: " + argumentsdefinitions.toString());
				System.out.println("Our function:" + function.toString());
				if (function.length() == argumentsdefinitions.length()) {
					for (int i = 0; i < function.length(); i++) {
						String argumentnum = "arg" + i;
						localvariables.put(argumentsdefinitions.getString(argumentnum), lookup(function.getString(argumentnum), v));
					}
					System.out.println("Local variables: " + localvariables.toString());
					JSONArray ourcode = definedfunction.getJSONArray("code");
					for (int i = 0; i < ourcode.length(); i++) {
						
						compiler.evaluateObject(ourcode.getJSONObject(i), localvariables, d);
					
					}
				}
				else {
					
					//"different numeber of arguments" error
					
				}
			}
			else {
				
				//put an error statement "not a function" here
				
			}
		}
		
		return "";		
		
	}

	private static void assign(String x, String y, JSONObject v) {
		//System.out.println("assigning "+x+" to "+y);
		if (v.has(x)) {
			//System.out.println("looked up and present");
			v.remove(x);
		}
		
		v.put(x, y);
		//System.out.println(compiler.variables.toString());
	}

	private static String add(String x, String y, JSONObject v) {
		
		return Integer.toString(Integer.parseInt(compiler.lookup(x, v))+Integer.parseInt(compiler.lookup(y, v)));
		
	}

	private static String sub(String x, String y, JSONObject v) {
		
		return Integer.toString(Integer.parseInt(compiler.lookup(x, v))-Integer.parseInt(compiler.lookup(y, v)));
		
	}
	
	private static String print(String x, JSONObject v) {
		//System.out.println("printing: ");
		System.out.println(compiler.lookup(x, v)); //change to real printing
		return ""; //or x
		
	}
	
	private static String lookup(String x, JSONObject v) {
		//System.out.println("lookup: "+x);
		if (v.has(x)) {
			//System.out.println("is present: "+compiler.variables.getString(x));
			return v.getString(x);
			
		}
		
		else {
			//System.out.println("not present");
			return x;
			
		}
		
	}

}
