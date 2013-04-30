package com.example.nxtdriver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
	ScheduledExecutorService sendserver;
	ScheduledExecutorService getserver;
	ScheduledExecutorService background;
	ScheduledExecutorService compile;
	ScheduledExecutorService override;
	ScheduledExecutorService state;
	String get;
	String code;
	compileThread cpt;
	byte[][] motors = new byte[2][3];
	byte[] received = new byte[6];
	boolean sending = false;
	boolean stop = false;
	
	Threads(MainActivity act)
	{
		this.act = act;
		sendNXT = Executors.newScheduledThreadPool(1);
		readNXT = Executors.newScheduledThreadPool(1);
		sendserver = Executors.newScheduledThreadPool(1);
		getserver = Executors.newScheduledThreadPool(1);
		background = Executors.newScheduledThreadPool(1);
		compile = Executors.newScheduledThreadPool(1);
		override = Executors.newScheduledThreadPool(1);
		state = Executors.newScheduledThreadPool(1);
	}

	public class dotbot implements Runnable{
		public void run(){
			//getHTML htmlclass = new getHTML();
			read read = new read();		
			//stateCheck statet = new stateCheck();
			readNXT.scheduleAtFixedRate(read, 0, 10, TimeUnit.MILLISECONDS);
			getserver.scheduleAtFixedRate(new getHTML(), 0, 500, TimeUnit.MILLISECONDS);
			sendserver.scheduleAtFixedRate(new postHTML(true), 0, 1, TimeUnit.SECONDS);
			sendserver.scheduleAtFixedRate(new postHTML(false), 0, 1, TimeUnit.SECONDS);
			state.scheduleAtFixedRate(new stateCheck(), 0, 100, TimeUnit.MILLISECONDS);
			//change_motor(new boolean[] {true, true, true}, new byte[] {0,0,0}, 1);
		}
	}

	public class postHTML implements Runnable
	{
		String customMsg = "";
		HttpURLConnection connection;
		String urlParameters;
		String line;
		URL postUrl; 
		StringBuffer response = new StringBuffer(); 
		boolean handshake;

		public postHTML(String msg) {

			this(false, msg);

		}

		public postHTML(boolean x) {
			this(x, "");
		}

		public postHTML(boolean x, String msg) {  
			handshake = x;
			try {
				customMsg = "&msg="+ URLEncoder.encode(msg, "UTF-8");
			}
			catch (Exception e) {
				Log.v("nxtdriverposthtmlerror", e.getMessage());
				if(msg.length() != 0) customMsg = "&msg="+ msg;
			}

		}

		public void run()
		{
			try {
				urlParameters = "from=" + URLEncoder.encode("slave", "UTF-8") +
						"&id=" + URLEncoder.encode(String.valueOf(act.id), "UTF-8");
				if (!handshake) {
					urlParameters += "&sensors=" + "[" +'"'+String.valueOf(received[2])  
							+'"'+","+'"' + String.valueOf(received[3]) 
							+'"'+","+'"' + String.valueOf(received[4])  
							+'"'+","+'"' + String.valueOf(received[5])  
							+'"'+"]" +
							customMsg;
					Log.v("params", urlParameters);
					postUrl = new URL("http://discos.herokuapp.com/io");
				}
				else {
					postUrl = new URL("http://discos.herokuapp.com/io/alive");
				}
				//Create connection
				connection = (HttpURLConnection)postUrl.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");  
				connection.setUseCaches (false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				//Send request
				DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
				wr.writeBytes (urlParameters);
				wr.flush ();
				wr.close ();

				//Get Response
				BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while((line = rd.readLine()) != null)
				{
					response.append(line);
					response.append('\r');
				}
				rd.close();
			} catch (Exception e) {
				Log.v("nxtdriverposthtmlerror", e.getMessage());
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
				if (act.mmInputStream.available()>0)
				{
					act.mmInputStream.read(received);
					//Log.v("nxtdriverread", "distance: "+String.valueOf(received[2]));
				}
			} catch (Exception e) {
				Log.v("nxtdriverreaderror", e.getMessage());
			}
		}
	}


	public class send implements Runnable{
		public void run()
		{
			try 
			{
				//Log.v("nxtdriversend", String.valueOf(motors[0][0])+ " "+String.valueOf(motors[0][1])+" "+String.valueOf(motors[0][2]));
				act.mmOutputStream.write(motors[0]);
			} catch (Exception e) {
				Log.v("nxtdriversenderror",e.getMessage());
			}

		}
	}

	public class getHTML implements Runnable{

		public void run() {
			try{
				URL url = new URL("http://discos.herokuapp.com/io?from=slave&id="+String.valueOf(act.id));
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				String line;
				//Log.v("nxtdrivergethtml", "starting get");
				get = "";
				BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				while ((line = rd.readLine()) != null) {
					get += line;
				}
				//Log.v("nxtdrivergetmsg", get);

				rd.close();
			} catch (Exception e) {
				Log.v("nxtdrivergethtmlerror", e.getMessage());
			}
		}

	}

	public class compileThread implements Runnable{
		String code;
		Compiler compiler;
		compileThread(String code)
		{
			Log.v("nxtdrivercompiler", "initiated");
			Log.v("nxtdrivercompilercode", code);
			this.code = code;
			compiler = new Compiler(code, Threads.this);
		}
		public void stop(){
			compiler = null;
		}

		@Override
		public void run() {
			compiler.compile();
		}
	}

	public class stateCheck implements Runnable{
		boolean wait = false;
		boolean first = true;
		

		public void run(){
			try{
			//Log.v("statetag", "running state");
			if(get.startsWith("active", 10) && cpt != null) 
			{
				Log.v("statetag", "active");
				wait = false;
				if(first) {first = false;compile = Executors.newScheduledThreadPool(1);compile.submit(cpt);}
				//else compile.shutdownNow();
				
				//else compile.notify();
			}
			else if(get.startsWith("active", 10) && cpt == null); //some sort of error?
			else if(get.startsWith("inactive", 10) && get.contains("main")) 
			{
				Log.v("statetag", "inactive");
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
				if(!first) {compile.shutdownNow();stop=true;compile.awaitTermination(100, TimeUnit.SECONDS);
				Log.v("statetag", "termintated"); first = true;change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);}
				cpt = new compileThread(get);
				wait = true;
			}
			else if(get.startsWith("inactive", 10) && !get.contains("main")) 
			{
				Log.v("statetag", "stopping");
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
				compile.shutdownNow(); 
				stop=true;
				compile.awaitTermination(100, TimeUnit.SECONDS);
				Log.v("statetag", "termintated");
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
				cpt = null;
				wait = true;
				first = true;
			}
			else if(get.startsWith("paused", 10)) 
			{
				Log.v("statetag", "paused");
				wait = true;
					compile.wait();
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
			}
			else if(get.startsWith("override", 10))
			{
				Log.v("statetag", "overriding");
				wait = true;
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
				compile.shutdownNow();
				stop=true;
				compile.awaitTermination(100, TimeUnit.SECONDS);
				Log.v("statetag", "terminated");
				change_motor(new boolean[] {true, true, true}, new byte[] {1,1,1}, 1);
				override.submit(new compileThread(get));
			}
			/*if(wait)
			{
				try {
					compile.wait();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Log.v("statetage", e.getMessage());

				}
			}*/
			get = "";
			}catch (Exception e){
				Log.v("statetage", e.getMessage());
			}
		}
	}

	public void change_motor(boolean[] m, byte[] s, int d)
	{
		try{
		Log.v("nxtdrivercompiler", "starting change motor");
		Log.v("nxtdrivercompiler", String.valueOf(s[0])+" "+String.valueOf(s[1])+" "+String.valueOf(s[2]));
		if(!sending)
		{
			sendNXT.scheduleAtFixedRate(new send(), 0, 50, TimeUnit.MILLISECONDS);
			sending = true;
		}

		for (int i = 0; i<3; i++)
		{
			if(m[i]) motors[0][i] = s[i];
			else motors[0][i] = 1;
		}
		try {
			while(d>0 && stop == false){
			Thread.sleep(10);d-=10;}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		}catch (Exception e){
			Log.v("nxtdrivercompiler", e.getMessage());
		}
	}
}

