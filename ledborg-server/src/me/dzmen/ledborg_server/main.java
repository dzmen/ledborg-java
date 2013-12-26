package me.dzmen.ledborg_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class main {
	
	public static void main(String[] args){
		new main().host();
	}
	
	//our port to host on
	static int port = 13579;
	
	public void host(){
		try{
			System.out.println("Starting ledborg server on " +InetAddress.getLocalHost().getHostAddress() + ":" +port);
			ServerSocket server = new ServerSocket(port);
			
	        // create gpio controller
	        final GpioController gpio = GpioFactory.getInstance();
	        
	        final GpioPinDigitalOutput RED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "led_red", PinState.LOW);
	        final GpioPinDigitalOutput GREEN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "led_green", PinState.LOW);
	        final GpioPinDigitalOutput BLUE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "led_blue", PinState.LOW);
	        
			System.out.println("Server started.");
			
			//This continuous loop will keep going, so that it can keep accepting requests!
			while(true){
				Socket socket = server.accept();
				System.out.println("Got a connection from "+socket.getInetAddress().getHostAddress());
				
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				
				//Now that we have established a connection, get the client's request, and say it in console
				String request = dis.readUTF();
				System.out.println(socket.getInetAddress().getHostAddress()+" sent request: "+request);
				
				//Check what the request is
				if(request.equalsIgnoreCase("RED")){
					RED.toggle();
					dos.writeUTF("Enable/Disable RED!");
					dos.flush();
				}else if(request.equalsIgnoreCase("BLUE")){
					BLUE.toggle();
					dos.writeUTF("Enable/Disable BLUE!");
					dos.flush();
				}else if(request.equalsIgnoreCase("GREEN")){
					GREEN.toggle();
					dos.writeUTF("Enable/Disable GREEN!");
					dos.flush();					
				}else if(request.equalsIgnoreCase("exit")){
					gpio.shutdown();
					dos.writeUTF("Quit gpio!");
					dos.flush();	
				}else if(request.equalsIgnoreCase("quit")){
					try{
						dos.close();
						dis.close();
						socket.close();
					}catch(Exception e){
						//We do nothing here, because even if we get errors, it will still disconnect!
					}
					System.exit(0);
				}else{
					dos.writeUTF("Wrong command!");
					dos.flush();
				}
				
				//Client request is complete - disconnecting!
				try{
					dos.close();
					dis.close();
					socket.close();
				}catch(Exception e){
					//We do nothing here, because even if we get errors, it will still disconnect!
				}
				System.out.println("Completed request!");
			}
			
		}catch(Exception e){
			//e.printStackTrace();
		}
	}
}