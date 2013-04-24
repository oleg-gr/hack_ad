package com.example.nxtdriver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity{

	TextView status;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	DataOutputStream mmOutputStream;
	InputStream mmInputStream;
	ScheduledExecutorService sendNXT;
	ScheduledExecutorService readNXT;
	ScheduledExecutorService server;
	ScheduledExecutorService background;
	ScheduledExecutorService compile;
	TextView sensor;
	Button connectButton;
	NumberPicker idpicker;
	byte[][] motors = new byte[2][3];
	byte[] received = new byte[3];
	Compiler compiler;
	boolean sending = false;
	int id;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sendNXT = Executors.newSingleThreadScheduledExecutor();
		readNXT = Executors.newSingleThreadScheduledExecutor();
		server = Executors.newSingleThreadScheduledExecutor();
		background = Executors.newSingleThreadScheduledExecutor();
		compile = Executors.newSingleThreadScheduledExecutor();
		connectButton = (Button)findViewById(R.id.connect);
		//Button readButton = (Button)findViewById(R.id.read);
		status = (TextView)findViewById(R.id.status);
		sensor = (TextView)findViewById(R.id.sensor);
		idpicker = (NumberPicker) findViewById(R.id.idpicker);
		
		idpicker.setOnValueChangedListener(new OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				id = arg2;
			}
			
		});

		connectButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try
				{
					findBT();
					connectBT();
					compile.execute(new dotbot());
					
					//readNXT.scheduleAtFixedRate(new read(), 0, 10, TimeUnit.MILLISECONDS);
				} catch(Exception e) {
					Log.v("nxtdriver", e.getMessage());
				}
			}
		});

		/*readButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.v("nxtdriver", "read initiated");
				try {
					Log.v("nxtdriverdistance", String.valueOf(mmInputStream.available()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				readNXT.scheduleAtFixedRate(new Runnable() {
					
				}, 0, 5, TimeUnit.MILLISECONDS);}});*/
	}
	
	public class read implements Runnable{
		public void run(){
			try {

				if (mmInputStream.available()>0){
				mmInputStream.read(received);
				//Log.v("nxtdriver", received.toString());
				Log.v("nxtdriverread", "distance: "+String.valueOf(received[2]));}
				//else Log.v("nxtdriverread", "no bytes");
			} catch (Exception e) {
				String error = e.getMessage();
				Log.v("nxtdriverread", error);
			}}
	}

	public class dotbot implements Runnable{
	public void run(){
		String code = "{\"status\":\"\",\"definitions\":{},\"main\":[{\"fd\":{\"arg0\":\"3\"}},{\"rt\":{\"arg0\":\"1\"}},{\"fd\":{\"arg0\":\"5\"}}]}";
		Log.v("nxtdriver","dotbot started");
		compiler = new Compiler(code);
		Log.v("nxtdriver","compiler instantiated");
		
		compiler.compile(compiler.main);
		Log.v("nxtdriver","finished compiling");
		change_motor(new boolean[] {true, true, true}, new byte[] {0,0,0}, 1);
	}}
	
	public static String getHTML(String urlToRead) {
		URL url;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;
		String result = "";
		try {
			url = new URL(urlToRead);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((line = rd.readLine()) != null) {
				result += line;
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void postHTML () throws IOException {
		
		URL url = new URL("http://discos.herokuapp.com/io?from=slave&id=1");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("POST");
		OutputStreamWriter out = new OutputStreamWriter(
		    httpCon.getOutputStream());
		//needs to be changed to actuall sensors' readings
		out.write("{status: \"active\", sensors: [22, 14, 0, 0], msg: \"ok\"}");
		out.close();
		
	}
	
	public static int get_status() {
		
		JSONObject response = new JSONObject(getHTML("http://discos.herokuapp.com/io?from=slave&id=1"));
		return response.getInt("status");
		
	}
	
	public static String get_code() {
		
		JSONObject response = new JSONObject(getHTML("http://discos.herokuapp.com/io?from=slave&id=1"));
		return response.getString("code");
		
	}	

	public void change_motor(boolean[] m, byte[] s, int d)
	{
		if(!sending)
		{
			sendNXT.scheduleAtFixedRate(new send(), 0, 500, TimeUnit.MILLISECONDS);
			sending = true;
		}
		
		Log.v("nxtdriver","changing motor speed");
		for (int i = 0; i<m.length; i++)
		{
			if(m[i]) motors[0][i] = s[i];
		}
		try {
			Thread.sleep(d);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public class send implements Runnable{
	public void run()
	{
		try {
			mmOutputStream.write(motors[0]);
			Log.v("nxtdriversend", "size: "+mmOutputStream.size());
			//Log.v("nxtdriversend",String.valueOf(motors[0][0])+ " "+String.valueOf(motors[0][1])+ " "+String.valueOf(motors[0][2]));
		} catch (Exception e) {
			Log.v("nxtdriversend",e.getMessage());
		}
	}}


	void findBT() throws Exception
	{

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null) {
			status.setText("NO NXT In Range");
		}
		if(!mBluetoothAdapter.isEnabled())
		{
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, 0);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

		if(pairedDevices.size() > 0);
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if(device.getName().equals("NXT"))
				{
					mmDevice = device;
					status.setText("NXT Found");
					break;
				}
			}
		}
	}

	void connectBT() throws Exception
	{
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		mmSocket.connect();
		mmOutputStream = new DataOutputStream(mmSocket.getOutputStream());
		mmInputStream = mmSocket.getInputStream();
		status.setText("Connection Established");
	}
	
	private class Compiler {

		String CHECK = "";
		ArrayList <JSONObject> variables = new ArrayList <JSONObject>(); 
		public JSONObject definitions;
		public JSONArray main;
		int count = 0;

		public Compiler(String jsonstring) 
		{
			JSONObject superJSON = new JSONObject(jsonstring);
			this.definitions = superJSON.getJSONObject("definitions");		
			this.main = superJSON.getJSONArray("main");
			variables.add(new JSONObject());
		}

		public void compile(JSONArray code) 
		{
			JSONArray copy = new JSONArray(code.toString());
			for (int i = 0; i < code.length(); i++) 
			{
				Log.v("nxtdrivercompile", "evaluating code line " + i);
				evaluateObject(copy.getJSONObject(i));
				Log.v("nxtdrivercompile", "finished evaluating code line " + i);
			}
		}

		public void next() 
		{
			evaluateObject(this.main.getJSONObject(this.count++));
		}

		private String evaluateObject(JSONObject jsonObject) 
		{
			String name = (String) jsonObject.keys().next();
			JSONObject function = jsonObject.getJSONObject(name);
			JSONArray keys = function.names();
			JSONObject copy = new JSONObject(function.toString());
			Log.v("nxtdrivercompile", "initialised evaluateObject");
			if (keys != null && !name.equals("if") &&!name.equals("while")) {
				for (int i = 0; i < keys.length(); i++) { 
					String temp_str = keys.getString(i);
					if (function.get(temp_str).getClass() != CHECK.getClass()) 
					{
						function.put(temp_str, evaluateObject(copy.getJSONObject(temp_str)));
					}
				}
				Log.v("nxtdrivercompile", "finished evaluating args");
			}
			else if (name.equals("if")) 
			{
				if (evaluateObject(copy.getJSONObject("condition")).equals("true")) 
				{
					this.compile(copy.getJSONArray("code"));
				}
				else 
				{
					this.compile(copy.getJSONArray("else"));
				}
			}

			else if (name.equals("while")) 
			{
				while (evaluateObject(copy.getJSONObject("condition")).equals("true"))
				{
					this.compile(copy.getJSONArray("code"));
				}
			}
			
			Log.v("nxtdrivercompile", "function name is:"+name+".");

			if (name.equals("add")) { return add((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("sub")) { return sub((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("assign")) { assign((String) function.get("arg0"),(String) function.get("arg1")); 

			} else if (name.equals("print")) { print((String) function.get("arg0"));

			} else if (name.equals("greater_than")) { return greater((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("less_than")) { return greater((String) function.get("arg1"),(String) function.get("arg0"));

			} else if (name.equals("equals")) { return equals((String) function.get("arg1"),(String) function.get("arg0"));

			} else if (name.equals("and")) { return and((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("or")) { return or((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("not")) { return not((String) function.get("arg0"));

			} else if (name.equals("mult")) { return mult((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("div")) { return div((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("mod")) { return mod((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("motorA")) { motorA((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("motorB")) { motorB((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("motorC")) { motorC((String) function.get("arg0"),(String) function.get("arg1"));

			} else if (name.equals("motorAB")) { motorAB((String) function.get("arg0"),(String) function.get("arg1"), (String) function.get("arg2"));

			} else if (name.equals("motorAC")) { motorAC((String) function.get("arg0"),(String) function.get("arg1"), (String) function.get("arg2"));

			} else if (name.equals("motorBC")) { motorBC((String) function.get("arg0"),(String) function.get("arg1"), (String) function.get("arg2"));

			} else if (name.equals("motorABC")) { motorABC((String) function.get("arg0"),(String) function.get("arg1"), (String) function.get("arg2"), (String) function.get("arg3"));

			} else if (name.equals("forward") || name.equals("fd")) {motorAB("80", "80", String.valueOf(Integer.parseInt((String)function.get("arg0"))*1000));

			} else if (name.equals("backward") || name.equals("bd")) { motorAB("-80", "-80", String.valueOf(Integer.parseInt((String)function.get("arg0"))*1000));

			} else if (name.equals("right") || name.equals("rt")) { motorAB("0", "80", String.valueOf(Integer.parseInt((String)function.get("arg0"))*1000));

			} else if (name.equals("left") || name.equals("lt")) { motorAB("80", "0", String.valueOf(Integer.parseInt((String)function.get("arg0"))*1000));

			}

			else{ 
				if (this.definitions.has(name)) {
					//put all arguments into an array
					JSONObject definedfunction = this.definitions.getJSONObject(name);
					JSONObject localvariables = new JSONObject();
					JSONObject argumentsdefinitions = definedfunction.getJSONObject("args"); //definition of arguments
					if (function.length() == argumentsdefinitions.length()) {

						for (int i = 0; i < function.length(); i++) {
							String argumentnum = "arg" + i;
							localvariables.put(argumentsdefinitions.getString(argumentnum), lookup(function.getString(argumentnum)));
						}

						variables.add(localvariables);
						JSONArray ourcode = definedfunction.getJSONArray("code");

						for (int i = 0; i < ourcode.length(); i++) {
							evaluateObject(ourcode.getJSONObject(i));
						}

						variables.remove(variables.size()-1);
					}
					else {

						//"different number of arguments" error

					}
				}
				else {

					//put an error statement "not a function" here

				}
			}

			return "";		

		}

		private void motorA(String x, String y) {
			change_motor(new boolean[] {true, false, false}, new byte[] {Byte.parseByte(x)}, Integer.parseInt(y));
		}

		private void motorB(String x, String y) {
			change_motor(new boolean[] {false, true, false}, new byte[] {Byte.parseByte(x)}, Integer.parseInt(y));
		}

		private void motorC(String x, String y) {
			change_motor(new boolean[] {false, false, true}, new byte[] {Byte.parseByte(x)}, Integer.parseInt(y));
		}

		private void motorAB(String x1, String x2, String y) {
			change_motor(new boolean[] {true, true, false}, new byte[] {Byte.parseByte(x1), Byte.parseByte(x2)}, Integer.parseInt(y));
		}

		private void motorAC(String x1, String x2, String y) {
			change_motor(new boolean[] {true, false, true}, new byte[] {Byte.parseByte(x1), Byte.parseByte(x2)}, Integer.parseInt(y));
		}

		private void motorBC(String x1, String x2, String y) {
			change_motor(new boolean[] {false, true, true}, new byte[] {Byte.parseByte(x1), Byte.parseByte(x2)}, Integer.parseInt(y));
		}

		private void motorABC(String x1, String x2, String x3, String y) {
			change_motor(new boolean[] {true, true, true}, new byte[] {Byte.parseByte(x1), Byte.parseByte(x2), Byte.parseByte(x3)}, Integer.parseInt(y));
		}

		private void assign(String x, String y) 
		{
			if (getScope().has(x)) 
			{
				getScope().remove(x);
			}
			getScope().put(x, y);
		}

		private  String add(String x, String y) 
		{
			return Integer.toString(Integer.parseInt(lookup(x))+Integer.parseInt(lookup(y)));
		}

		private  String sub(String x, String y) 
		{
			return Integer.toString(Integer.parseInt(lookup(x))-Integer.parseInt(lookup(y)));
		}

		private  String mult(String x, String y) 
		{
			return Integer.toString(Integer.parseInt(lookup(x))*Integer.parseInt(lookup(y)));
		}

		private  String div(String x, String y) 
		{
			return Integer.toString(Integer.parseInt(lookup(x))/Integer.parseInt(lookup(y)));
		}

		private  String mod(String x, String y) 
		{
			return Integer.toString(Integer.parseInt(lookup(x)) % Integer.parseInt(lookup(y)));
		}

		private  String print(String x) 
		{
			System.out.println(lookup(x));
			return "";
		}

		private  String greater(String x, String y) 
		{
			return Boolean.toString(Integer.parseInt(lookup(x))>Integer.parseInt(lookup(y)));
		}

		private  String equals(String x, String y) 
		{
			return Boolean.toString(Integer.parseInt(lookup(x))==Integer.parseInt(lookup(y)));
		}

		private  String and(String x, String y) 
		{
			return Boolean.toString(x.equals("true") && y.equals("true"));
		}

		private  String or(String x, String y) 
		{
			return Boolean.toString(x.equals("true") || y.equals("true"));
		}

		private  String not(String x) 
		{
			return Boolean.toString(!(x.equals("true")));
		}

		private  String lookup(String x) {
			if (x.length() > 0) {
				if (x.substring(0, 1).equals("^")) 
				{
					return x.substring(1, x.length());
				}
			}

			for (int i = variables.size()-1; i > -1; i--) 
			{
				if (variables.get(i).has(x)) 
				{
					return (String) variables.get(i).getString(x);
				}
			}
			return x;
		}

		private  JSONObject getScope() 
		{
			return variables.get(variables.size()-1);
		}
	}
}