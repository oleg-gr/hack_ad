

import java.io.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

public class MovingFinal {


	public static void main(String[] args) throws Exception{

		byte[] byteIN = new byte[]{0, 0, 0};
		byte[] byteOUT = new byte [1];



		//escape button
		boolean esc = false;

		while (esc==false) 
		{
			//Variables
			int speedA= 2;
			int speedB= 2;
			int speedC= 2;
			int distance=0;
			String connected = "Connected";
			String waiting = "Waiting...";

			
			
			//Connecting Android and NXT
			LCD.drawString(waiting,0,0);
			NXTConnection connection = Bluetooth.waitForConnection(); 
			LCD.clear();
			LCD.drawString(connected,0,0);
			Delay.msDelay(700);
			



			//Objects
			UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S2);
			

			//Acceleration doesn't change
			//Set Acceleration (Recommended less than 3000 for smooth transitions (default 6000))
			Motor.A.setAcceleration(2000);
			Motor.B.setAcceleration(2000);

			LCD.clear();
			// Driving
			while (esc==false){


				LCD.drawString("Driving", 0, 0);

				//from byte array into integers (for easier manipulation)
				speedC = (int) byteIN[0];
				speedB = (int) byteIN[1];
				speedA = (int) byteIN[2];


				//Set speeds according to variables read (the reading occurs at the end so that it happens right before the UltraSound distance writing
				setSpeeds(speedA,speedB,speedC);


				//Motor Directions  
				setDirections (speedA,speedB,speedC);

				distance=sonic.getDistance();
				byteOUT[0]=(byte) distance;

				//Display distance
				LCD.drawString("Distance:", 0, 2);
				LCD.drawInt(distance, 0, 3);
				//if(connection.available()!=0)                                       *
				connection.read(byteIN,3);
				//Delay.msDelay(5);


				//Print speeds
				displaySpeeds (byteIN);

				

				//Writes to master
				
				
				try {
					connection.write(byteOUT,1);
					Delay.msDelay(5);
					} catch (Exception e) {}

				// Delay.msDelay(200); //so that read/write from master and NXT is in sync 
				 

				//escape function  
				if (Button.ESCAPE.isDown()) 
				{
					LCD.clear();
					LCD.drawString("ESCAPE", 0, 0);
					esc=true;
					Delay.msDelay(500);

				}


			}//finishes Drive			
		}//finishes connection


	}//finishes main

	public static void setSpeeds (int speedA, int speedB,int speedC){
		Motor.A.setSpeed(Math.abs(speedA)*(900/127));
		Motor.B.setSpeed(Math.abs(speedB)*(900/127));
		Motor.C.setSpeed(Math.abs(speedC)*(900/127));
	}
	public static void setDirections (int speedA, int speedB,int speedC){

		if (speedA>0)
		{
			Motor.A.forward();
		}
		if (speedB>0)
		{
			Motor.B.forward();
		}

		if (speedC>0)
		{
			Motor.C.forward();
		}

		if (speedA<0)
		{
			Motor.A.backward();
		}
		if (speedB<0)
		{
			Motor.B.backward();
		}
		if (speedC<0)
		{
			Motor.C.backward();
		}

	}
	public static void displaySpeeds (byte[] byteIN) {
		LCD.clear();
		for (int i = 0; i<3; i++){
			LCD.drawString("Speeds", 1, 4);
			LCD.drawString("A", 1, 5);
			LCD.drawString("B", 1, 6);
			LCD.drawString("C", 1, 7);
			LCD.drawInt((int)((byteIN[i])*(900/127)), 2, i+5);
			Delay.msDelay(10);
		}

	}

} //finishes class