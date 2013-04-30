package com.example.nxtdriver;

import java.io.DataOutputStream;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

public class Connect {
	MainActivity act;
	public Connect(MainActivity act){this.act = act;}
	public void findBT() throws Exception
	{
		
		act.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(act.mBluetoothAdapter == null) {
			act.status.setText("NO NXT In Range");
		}
		if(!act.mBluetoothAdapter.isEnabled())
		{
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			act.startActivityForResult(enableBluetooth, 0);
		}

		Set<BluetoothDevice> pairedDevices = act.mBluetoothAdapter.getBondedDevices();

		if(pairedDevices.size() > 0);
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if(device.getName().equals("NXT"))
				{
					act.mmDevice = device;
					act.status.setText("NXT Found");
					break;
				}
			}
		}
	}

	public void connectBT() throws Exception
	{
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		act.mmSocket = act.mmDevice.createRfcommSocketToServiceRecord(uuid);
		act.mmSocket.connect();
		act.mmOutputStream = new DataOutputStream(act.mmSocket.getOutputStream());
		act.mmInputStream = act.mmSocket.getInputStream();
		act.status.setText("Connection Established");
	}
}
