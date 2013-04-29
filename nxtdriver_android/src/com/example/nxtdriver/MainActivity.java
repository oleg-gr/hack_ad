package com.example.nxtdriver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;
import android.util.Log;
import android.view.View;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity{

	TextView status;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	DataOutputStream mmOutputStream;
	InputStream mmInputStream;
	TextView sensor;
	int id = 1;
	NumberPicker idpicker;


	@SuppressLint("CutPasteId")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button connectButton = (Button)findViewById(R.id.connect);
		NumberPicker nbpicker = (NumberPicker)findViewById(R.id.idpicker);
		nbpicker.setMaxValue(6);
		nbpicker.setMinValue(1);
		nbpicker.setWrapSelectorWheel(false);
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
					Connect connect = new Connect(MainActivity.this);
					connect.findBT();
					connect.connectBT();
					Threads threads = new Threads(MainActivity.this);
					threads.compile.execute(threads.new dotbot());
					threads.readNXT.scheduleAtFixedRate(threads.new read(), 0, 10, TimeUnit.MILLISECONDS);
				} catch(Exception e) {
					Log.v("nxtdriver", e.getMessage());
				}
			}
		});
	}
}