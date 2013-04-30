import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;

public class NXTDriver {
	
	int[] sensors;
	byte[] byteIN = new byte[]{0, 0, 0};
	byte[] byteOUT = new byte [1];
	boolean esc = false;
	NXTConnection connection;
	UltrasonicSensor sonic;

	public NXTDriver()
	{
		LCD.drawString("Waiting...",0,0);
		connection = Bluetooth.waitForConnection(); 
		LCD.clear();
		LCD.drawString("Connected",0,0);
		Delay.msDelay(1000);
		sonic = new UltrasonicSensor(SensorPort.S2);
		Motor.A.setAcceleration(2000);
		Motor.B.setAcceleration(2000);
		Motor.C.setAcceleration(2000);
		LCD.clear();
	}

	public void run(){
		while (!esc){
			LCD.drawString("Driving", 0, 0);
			setSpeeds(byteIN);
			Delay.msDelay(0);
			setDirections(byteIN);
			byteOUT[0]=(byte) sonic.getDistance();
			LCD.drawString("Distance:", 0, 2);
			LCD.drawInt(byteOUT[0], 0, 3);
			if(connection.available()!=0) {
				connection.read(byteIN,3);
			}
			displaySpeeds(byteIN);
				connection.write(byteOUT,1);
				Delay.msDelay(10);
			if (Button.ESCAPE.isDown()) 
			{
				LCD.clear();
				LCD.drawString("ESCAPE", 0, 0);
				esc=true;
				Delay.msDelay(500);
			}
		}			
	}
	
	public void setSpeeds (byte[] byteIN){
		Motor.A.setSpeed(Math.abs(byteIN[2])*(900/127));
		Motor.B.setSpeed(Math.abs(byteIN[1])*(900/127));
		Motor.C.setSpeed(Math.abs(byteIN[0])*(900/127));
	}

	public void setDirections (byte[] byteIN){
		if(byteIN[0]>0)Motor.A.forward();
		else Motor.A.backward();
		if(byteIN[0]>0)Motor.B.forward();
		else Motor.B.backward();
		if(byteIN[0]>0)Motor.C.forward();
		else Motor.C.backward();
	}

	public void displaySpeeds (byte[] byteIN) {
		LCD.clear();
		LCD.drawString("Speeds", 1, 4);
		LCD.drawString("A", 1, 5);
		LCD.drawString("B", 1, 6);
		LCD.drawString("C", 1, 7);
		for (int i = 0; i<3; i++){
			LCD.drawInt((int)byteIN[i], 2, i+5);
		}
	}
	
	public static void main(String[] args) throws Exception{
		NXTDriver nxt = new NXTDriver();
		try{
		nxt.run();}catch(Exception e){
			LCD.clear();
			LCD.drawString(e.getMessage(), 0, 0);
		}
	}
}