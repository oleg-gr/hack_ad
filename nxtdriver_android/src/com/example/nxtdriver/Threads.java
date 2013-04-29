package com.example.nxtdriver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.util.Log;

public class Threads {

	MainActivity act;
	ScheduledExecutorService sendNXT;
	ScheduledExecutorService readNXT;
	ScheduledExecutorService server;
	ScheduledExecutorService background;
	ScheduledExecutorService compile;
	String get;
	Compiler compiler;
	byte[][] motors = new byte[2][3];
	byte[] received = new byte[3];
	boolean sending = false;

	Threads(MainActivity act)
	{
		this.act = act;
		sendNXT = Executors.newSingleThreadScheduledExecutor();
		readNXT = Executors.newSingleThreadScheduledExecutor();
		server = Executors.newSingleThreadScheduledExecutor();
		background = Executors.newSingleThreadScheduledExecutor();
		compile = Executors.newSingleThreadScheduledExecutor();
	}

	public class dotbot implements Runnable{
		public void run(){
			getHTML htmlclass = new getHTML();
			Log.v("nxtget", "starting get");
			htmlclass.run();
			String code = get;
			Log.v("nxtget", get);
			Compiler compiler = new Compiler(code, Threads.this);
			server.scheduleAtFixedRate(htmlclass, 0, 1, TimeUnit.SECONDS);
			server.scheduleAtFixedRate(new postHTML(), 0, 1, TimeUnit.SECONDS);
			compiler.compile();
			change_motor(new boolean[] {true, true, true}, new byte[] {0,0,0}, 1);
		}
	}

	public class postHTML implements Runnable
	{
		postHTML()
		{
			
		}
		public void run()
		{
			String urlParameters = null;
			try {
				urlParameters = "from=" + URLEncoder.encode("slave", "UTF-8") +
						"&id=" + URLEncoder.encode(String.valueOf(act.id), "UTF-8") +
						"&status=" + URLEncoder.encode("ok", "UTF-8") +
						"&sensors=" + URLEncoder.encode(String.valueOf(received[2]), "UTF-8") +
						"&msg=" + URLEncoder.encode("ok", "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			URL url;
			HttpURLConnection connection = null;  
			try {
				//Create connection
				url = new URL("http://discos.herokuapp.com/io");
				connection = (HttpURLConnection)url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", 
						"application/x-www-form-urlencoded");

				connection.setRequestProperty("Content-Length", "" + 
						Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");  

				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				//Send request
				DataOutputStream wr = new DataOutputStream (
						connection.getOutputStream ());
				wr.writeBytes (urlParameters);
				wr.flush ();
				wr.close ();

				//Get Response	
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer(); 
				while((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();

			} catch (Exception e) {

				e.printStackTrace();

			} finally {

				if(connection != null) {
					connection.disconnect(); 
				}
			}
		}	
	}
	
	public class read implements Runnable{
		public void run(){
			try {

				if (act.mmInputStream.available()>0){
					act.mmInputStream.read(received);
					Log.v("nxtdriverread", "distance: "+String.valueOf(received[2]));}
			} catch (Exception e) {
				String error = e.getMessage();
				Log.v("nxtdriverread", error);
			}
		}
	}
	
	public class send implements Runnable{
		public void run()
		{
			try {
				act.mmOutputStream.write(motors[0]);
				Log.v("nxtdriversend", "size: "+act.mmOutputStream.size());
				Log.v("nxtdriversend",String.valueOf(motors[0][0])+ " "+String.valueOf(motors[0][1])+ " "+String.valueOf(motors[0][2]));
			} catch (Exception e) {
				Log.v("nxtdriversend",e.getMessage());
			}
		}
	}

	public class getHTML implements Runnable{
		URL url ;
		HttpURLConnection conn;
		BufferedReader rd;
		String line;

		public getHTML(){
			try {
				url = new URL("http://discos.herokuapp.com/io?from=slave&id="+String.valueOf(act.id));
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.v("nxtget", "error is: "+ e.getMessage());
			}
		}

		public void run() {
			get = "";
			try {
				while ((line = rd.readLine()) != null) {
					get += line;
				}
			} catch (Exception e) {
				Log.v("nxtget", "error in run: " + e.getMessage());
			}
		}
	}

	public void change_motor(boolean[] m, byte[] s, int d)
	{
		if(!sending)
		{
			sendNXT.scheduleAtFixedRate(new send(), 0, 500, TimeUnit.MILLISECONDS);
			sending = true;
		}

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
}
