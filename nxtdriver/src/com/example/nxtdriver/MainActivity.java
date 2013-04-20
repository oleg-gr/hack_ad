package com.example.nxtdriver;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;  
import android.widget.Button;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity {
	
	TextView status;
    //EditText myTextbox;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    DataOutputStream mmOutputStream;
    //DataInputStream mmInputStream;
    InputStream mmInputStream;
    Thread workerthread;
    Thread destroythread;
    
    TextView sensor;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);
    	
    	Button forwardButton = (Button)findViewById(R.id.forward);
    	Button reverseButton = (Button)findViewById(R.id.reverse);
    	Button leftButton = (Button)findViewById(R.id.left);
    	Button rightButton = (Button)findViewById(R.id.right);
    	Button connectButton = (Button)findViewById(R.id.connect);
    	Button stopButton = (Button)findViewById(R.id.stop);
    	Button scanButton = (Button)findViewById(R.id.scan);
    	Button readButton = (Button)findViewById(R.id.read);
    	
    	status = (TextView)findViewById(R.id.status);
    	sensor = (TextView)findViewById(R.id.sensor);
    	
    	//ConnectButton
    	connectButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				
				try
				{
					findBT();
					connectBT();
				} catch(IOException e) {
					//e.printStackTrace();
				}
				
				
				
			}
		});
    	
    	readButton.setOnClickListener(new View.OnClickListener() {
    		
    		public void onClick(View v)
    		{	
    			status.setText("waiting");
				byte[] b = new byte[1];
				int res;
				try {
					//mmInputStream.read(b);
					res = mmInputStream.available();
					sensor.setText(res);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					res = -1;
					e.printStackTrace();
					sensor.setText("failed to read");
				}
				
				//sensor.setText(res);	
    		}
    	});
    
    	
    	//ForwardButton
    	forwardButton.setOnClickListener(new View.OnClickListener() 
    	{	
    		public void onClick(View v) 
    		{
    			//a=8;
				try {
					forwardsend();
					forwardsend();
					forwardsend();
					forwardsend();
					forwardsend();
					/*status.setText("waiting");
					byte[] b = new byte[1];
					mmInputStream.read(b);
					int res = (int) b[0];
					sensor.setText(res);*/
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
    		}
    	});
    	
    	//ReverseButton
    	reverseButton.setOnClickListener(new View.OnClickListener() 
    	{	
    		public void onClick(View v) 
    		{
    			//a=2;
				try {
					reversesend();
					reversesend();
					/*byte[] b = new byte[1];
					int n = mmInputStream.read(b);
					sensor.setText(n);*/
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
    		}
    		
    });
    	
    	leftButton.setOnClickListener(new View.OnClickListener() 
    	{	
    		public void onClick(View v) 
    		{
    			//a=4;
				try {
					leftsend();
					leftsend();
					/*byte[] b = new byte[1];
					int n = mmInputStream.read(b);
					sensor.setText(n);*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
    		}
    		
    	});
    	
    	rightButton.setOnClickListener(new View.OnClickListener() 
    	{	
    		public void onClick(View v) 
    		{
    			//a=6;
				try {
					rightsend();
					rightsend();
					/*byte[] b = new byte[1];
					int n = mmInputStream.read(b);
					sensor.setText(n);*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
    		}

    	});
    	
    	scanButton.setOnClickListener(new View.OnClickListener()
    	{
    		public void onClick(View v)
    		{
    			//a=5;
				try {
					scansend();
					scansend();
					/*byte[] b = new byte[1];
					int n = mmInputStream.read(b);
					sensor.setText(n);*/
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				
    		}
    	});
    	
    	/*public static int byteArrayToInt(byte[] b) 
    	{
    	    int value = 0;
    	    for (int i = 0; i < 4; i++) {
    	        int shift = (4 - 1 - i) * 8;
    	        value += (b[i] & 0x000000FF) << shift;
    	    }
    	    return value;
    	}*/
    	
    	
    	
    	
    	
    	
    	
    }
    
    //byte[] b = new byte[1];
    
    
    /*public static int byteArrayToInt(byte[] b) 
    {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }*/
    

    
    	
    	//Functions
    	
    	void findBT() throws IOException
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
    
       void connectBT() throws IOException
       {
    	   status.setText("Step 1");
    	   UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    	   status.setText("Step 2");
    	   mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
    	   status.setText("Step 3");
    	   mmSocket.connect();
           status.setText("Step 4");
           mmOutputStream = new DataOutputStream( mmSocket.getOutputStream());
           status.setText("Step 5");
           //mmInputStream = new DataInputStream( mmSocket.getInputStream());
           mmInputStream = mmSocket.getInputStream();
           status.setText("Step 6");
           
           status.setText("Connection Established");
           
       }
       
       /*void send() throws IOException
       {
    	   mmOutputStream.writeByte(a);
    	   
       }*/
       
       void forwardsend() throws IOException
       {
    	   //String forward = "8$20";
    	   //mmOutputStream.write(forward.getBytes());
    	   //mmOutputStream.flush();
    	   mmOutputStream.writeByte(8);
    	   status.setText("Forward");
       }
    	
       void reversesend() throws IOException
       {
    	   //String reverse = "2$20";
    	   //status.setText(String.valueOf(mmOutputStream.size())); 
    	   //mmOutputStream.write(reverse.getBytes());
    	   mmOutputStream.writeByte(2);
    	   //mmOutputStream.flush();
    	   status.setText("Reverse");
       }
       
       void leftsend() throws IOException
       {
    	   //String left = "4$20";
    	   //mmOutputStream.write(left.getBytes());
    	   //mmOutputStream.flush();
    	   mmOutputStream.writeByte(4);
    	   status.setText("Left");
       }
       
       void rightsend() throws IOException
       {
    	   //String right = "6$20";
    	   //mmOutputStream.write(right.getBytes());
    	   mmOutputStream.writeByte(6);
    	   status.setText("Right");
       }
       
       void scansend() throws IOException
       {
    	   //String scan = "4$24";
    	   //mmOutputStream.write(scan.getBytes());
     	   mmOutputStream.writeByte(5);
    	   status.setText("Scanning");
       }
       

       
       }
    	 
       
    	   
    	   
    	   
    	   
    	
    	
    	
    	
    	
