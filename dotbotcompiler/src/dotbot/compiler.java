package dotbot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class compiler {

	static String CHECK = "";

	static ArrayList <JSONObject> variables = new ArrayList <>(); 


	public static void main (String args[]) throws IOException {

		//System.out.println(Boolean.toString(!("true".equals("false"))));

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
		compile (content);

	}

	private static void compile (String x) {

		JSONObject superJSON = new JSONObject(x);

		JSONObject definitions = superJSON.getJSONObject("definitions");		

		JSONArray main = superJSON.getJSONArray("main");


		variables.add(new JSONObject());


		for (int i = 0; i < main.length(); i++) {

			evaluateObject(main.getJSONObject(i), definitions);

		}

	}

	private static String evaluateObject(JSONObject jsonObject, JSONObject d) {
		String name = (String) jsonObject.keys().next();

		JSONObject function = jsonObject.getJSONObject(name);
		//System.out.print(name);
		//System.out.println(function.toString());

		JSONArray keys = function.names();
		if (keys != null) {
			for (int i = 0; i < keys.length(); i++) { 
				String temp_str = keys.getString(i);
				if (function.get(temp_str).getClass() != CHECK.getClass()) {
					function.put(temp_str, evaluateObject(function.getJSONObject(temp_str), d));

				}

			}
		}
		//System.out.println(jsonObject.toString());
		switch (name) {

		case "add": return add((String) function.get("arg0"),(String) function.get("arg1"));

		case "sub": return sub((String) function.get("arg0"),(String) function.get("arg1"));

		case "assign": assign((String) function.get("arg0"),(String) function.get("arg1")); 
		break;

		case "print": print((String) function.get("arg0"));
		break;

		case "greater_than": return greater((String) function.get("arg0"),(String) function.get("arg1"));

		case "less_than": return greater((String) function.get("arg1"),(String) function.get("arg0"));

		case "equals": return equals((String) function.get("arg1"),(String) function.get("arg0"));

		case "and": return and((String) function.get("arg0"),(String) function.get("arg1"));

		case "or": return or((String) function.get("arg0"),(String) function.get("arg1"));

		case "not": return not((String) function.get("arg0"));

		case "mult": return mult((String) function.get("arg0"),(String) function.get("arg1"));

		case "div": return div((String) function.get("arg0"),(String) function.get("arg1"));

		case "mod": return mod((String) function.get("arg0"),(String) function.get("arg1"));

		case "forward": forward((String) function.get("arg0"));
		break;

		case "fd": forward((String) function.get("arg0"));
		break;
		
		case "backward": backward((String) function.get("arg0"));
		break;
		
		case "bd": backward((String) function.get("arg0"));
		break;
		
		case "left": left((String) function.get("arg0"));
		break;
		
		case "lt": left((String) function.get("arg0"));
		break;
		
		case "right": right((String) function.get("arg0"));
		break;
		
		case "rt": right((String) function.get("arg0"));
		break;


		default: 
			if (d.has(name)) {
				//put all arguments into an array
				JSONObject definedfunction = d.getJSONObject(name);
				//System.out.println("User-defined function: " + definedfunction.toString());
				JSONObject localvariables = new JSONObject();
				JSONObject argumentsdefinitions = definedfunction.getJSONObject("args"); //definition of arguments
				//System.out.println("User-defined function arguments: " + argumentsdefinitions.toString());
				//System.out.println("Our function:" + function.toString());
				if (function.length() == argumentsdefinitions.length()) {

					for (int i = 0; i < function.length(); i++) {
						//System.out.println("sad");
						String argumentnum = "arg" + i;
						localvariables.put(argumentsdefinitions.getString(argumentnum), lookup(function.getString(argumentnum)));
					}

					variables.add(localvariables);

					//System.out.println("Local variables: " + getScope().toString());

					JSONArray ourcode = definedfunction.getJSONArray("code");

					for (int i = 0; i < ourcode.length(); i++) {

						evaluateObject(ourcode.getJSONObject(i), d);

					}

					variables.remove(variables.size()-1);

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

	private static void forward(String x) {
		
		//implement forward function, x is distance or duration
		
	}
	
	private static void backward(String x) {
		
		//implement forward function, x is distance or duration
		
	}
	
	private static void left(String x) {
		
		//implement forward function, x is angles or duration
		
	}
	
	private static void right(String x) {
		
		//implement forward function, x is angles or duration
		
	}
	
	private static void assign(String x, String y) {
		//System.out.println("assigning "+x+" to "+y);
		if (getScope().has(x)) {
			//System.out.println("looked up and present");
			getScope().remove(x);
		}


		getScope().put(x, y);
		//System.out.println(variables.toString());
	}

	private static String add(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))+Integer.parseInt(lookup(y)));

	}

	private static String sub(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))-Integer.parseInt(lookup(y)));

	}

	private static String mult(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))*Integer.parseInt(lookup(y)));

	}

	private static String div(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))/Integer.parseInt(lookup(y)));

	}

	private static String mod(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x)) % Integer.parseInt(lookup(y)));

	}

	private static String print(String x) {
		//System.out.println("printing: ");
		System.out.println(lookup(x)); //change to real printing
		return ""; //or x

	}

	private static String greater(String x, String y) {

		return Boolean.toString(Integer.parseInt(lookup(x))>Integer.parseInt(lookup(y)));

	}

	private static String equals(String x, String y) {

		return Boolean.toString(Integer.parseInt(lookup(x))==Integer.parseInt(lookup(y)));

	}

	private static String and(String x, String y) {

		return Boolean.toString(x.equals("true") && y.equals("true"));

	}

	private static String or(String x, String y) {

		return Boolean.toString(x.equals("true") || y.equals("true"));

	}

	private static String not(String x) {

		return Boolean.toString(!(x.equals("true")));

	}

	private static String lookup(String x) {
		//System.out.println("lookup: "+x);
		if (x.length() > 0) {
			if (x.substring(0, 1).equals("^")) {

				return x.substring(1, x.length());

			}
		}

		for (int i = variables.size()-1; i > -1; i--) {
			//System.out.println(variables.get(i).toString());
			if (variables.get(i).has(x)) {
				//System.out.println("is present: "+variables.getString(x));
				return (String) variables.get(i).getString(x);


			}
		}

		return x;
	}

	private static JSONObject getScope() {

		return variables.get(variables.size()-1);

	}


}
