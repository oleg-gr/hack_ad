package com.example.nxtdriver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends Activity implements OnClickListener{

	TextView status;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	DataOutputStream mmOutputStream;
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
                        } catch(Exception e) {
                        }
                    }
		});

		readButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {
                        status.setText("Waiting...");
                        byte[] b = new byte[3];
                        try {
                            mmInputStream.read(b);
                            sensor.setText(String.valueOf(b[2]));
                        } catch (Exception e) {
                            String error = e.getMessage();
                            Log.v("nxtdriver", error);
                            sensor.setText("Failed to read");
                        }
                    }
		});
/*
		Button forwardButton = (Button)findViewById(R.id.forward);
		Button reverseButton = (Button)findViewById(R.id.reverse);
		Button leftButton = (Button)findViewById(R.id.left);
		Button rightButton = (Button)findViewById(R.id.right);
		Button connectButton = (Button)findViewById(R.id.connect);
		Button stopButton = (Button)findViewById(R.id.stop);
		Button readButton = (Button)findViewById(R.id.read);*/
		
		forwardButton.setOnClickListener(this);
		reverseButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		rightButton.setOnClickListener(this);
		stopButton.setOnClickListener(this);
	}
		
		@Override
		public void onClick(View v){
			try{
		 switch(v.getId())
		 {
		   case R.id.forward:
		   { 
		     send(8, "forward");
		     break;
		   }

		   case R.id.reverse:
		   { 
		     send(2, "reverse");
		     break;
		   }
		   case R.id.left:
		   { 
		     send(4, "left");
		     break;
		   }
		   case R.id.right:
		   { 
		     send(6, "right");
		     break;
		   }
		   case R.id.stop:
		   { 
		     send(5, "stop");
		     break;
		   }

		 }}catch(Exception e){}
		}

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

	void connectBT() throws IOException
	{
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
		mmSocket.connect();
		mmOutputStream = new DataOutputStream(mmSocket.getOutputStream());
		mmInputStream = mmSocket.getInputStream();
		status.setText("Connection Established");

	}

    void send(int code, String msg) throws Exception
    {
        mmOutputStream.writeByte(code);
        mmOutputStream.writeByte(code);
        status.setText(msg);
    }   
}