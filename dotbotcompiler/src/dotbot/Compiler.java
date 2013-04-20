package dotbot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Compiler {

	String CHECK = "";

	ArrayList <JSONObject> variables = new ArrayList <>(); 

	public JSONObject definitions;
	public JSONArray main;
	int count = 0;


	public static void main (String args[]) throws IOException {

		//System.out.println(Boolean.toString(!("true".equals("false"))));

		File file = new File("test.txt");
		String jsonstring = null;
		try {
			FileReader reader = new FileReader(file);
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			jsonstring = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Compiler compiler = new Compiler (jsonstring);  
		compiler.compile();

	}


	public Compiler(String jsonstring) {

		JSONObject superJSON = new JSONObject(jsonstring);

		this.definitions = superJSON.getJSONObject("definitions");		

		this.main = superJSON.getJSONArray("main");


		variables.add(new JSONObject());

	}

	public void compile() {
		
		this.compile(this.main);
		
	}
	
	public void compile(JSONArray code) {

		for (int i = 0; i < code.length(); i++) {

			evaluateObject(code.getJSONObject(i));

		}

	}

	public void next() {
		
		evaluateObject(this.main.getJSONObject(this.count++));

	}



	private String evaluateObject(JSONObject jsonObject) {
		String name = (String) jsonObject.keys().next();

		JSONObject function = jsonObject.getJSONObject(name);
		//System.out.print(name);
		//System.out.println(function.toString());

		JSONArray keys = function.names();
		if (keys != null && !name.equals("if")) {
			for (int i = 0; i < keys.length(); i++) { 
				String temp_str = keys.getString(i);
				if (function.get(temp_str).getClass() != CHECK.getClass()) {
					function.put(temp_str, evaluateObject(function.getJSONObject(temp_str)));

				}

			}
		}
		else {
			
			System.out.println("if!?");
			
			if (evaluateObject(function.getJSONObject("condition")).equals("true")) {
				
				this.compile(function.getJSONArray("code"));
				
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
			if (this.definitions.has(name)) {
				//put all arguments into an array
				JSONObject definedfunction = this.definitions.getJSONObject(name);
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

						evaluateObject(ourcode.getJSONObject(i));

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

	private void forward(String x) {

		//implement forward function, x is distance or duration

	}

	private  void backward(String x) {

		//implement forward function, x is distance or duration

	}

	private  void left(String x) {

		//implement forward function, x is angles or duration

	}

	private  void right(String x) {

		//implement forward function, x is angles or duration

	}

	private  void assign(String x, String y) {
		//System.out.println("assigning "+x+" to "+y);
		if (getScope().has(x)) {
			//System.out.println("looked up and present");
			getScope().remove(x);
		}


		getScope().put(x, y);
		//System.out.println(variables.toString());
	}

	private  String add(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))+Integer.parseInt(lookup(y)));

	}

	private  String sub(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))-Integer.parseInt(lookup(y)));

	}

	private  String mult(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))*Integer.parseInt(lookup(y)));

	}

	private  String div(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x))/Integer.parseInt(lookup(y)));

	}

	private  String mod(String x, String y) {

		return Integer.toString(Integer.parseInt(lookup(x)) % Integer.parseInt(lookup(y)));

	}

	private  String print(String x) {
		//System.out.println("printing: ");
		System.out.println(lookup(x)); //change to real printing
		return ""; //or x

	}

	private  String greater(String x, String y) {

		return Boolean.toString(Integer.parseInt(lookup(x))>Integer.parseInt(lookup(y)));

	}

	private  String equals(String x, String y) {

		return Boolean.toString(Integer.parseInt(lookup(x))==Integer.parseInt(lookup(y)));

	}

	private  String and(String x, String y) {

		return Boolean.toString(x.equals("true") && y.equals("true"));

	}

	private  String or(String x, String y) {

		return Boolean.toString(x.equals("true") || y.equals("true"));

	}

	private  String not(String x) {

		return Boolean.toString(!(x.equals("true")));

	}

	private  String lookup(String x) {
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

	private  JSONObject getScope() {

		return variables.get(variables.size()-1);

	}


}