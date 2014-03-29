package me.dzmen.ledborg_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


public class main{
	
	public static void main(String[] args) {
		new main().host();
	}
	
	//our port to host on
	static int port = 13579;
	
	public void host(){
		
		try{
			Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
		    for (; n.hasMoreElements();)
		    {
		        NetworkInterface e = n.nextElement();

		        Enumeration<InetAddress> a = e.getInetAddresses();
		        for (; a.hasMoreElements();)
		        {
		            InetAddress addr = a.nextElement();
		            System.out.println("Starting ledborg server on ip " + addr.getHostAddress() + ":" +port);
		        }
		    }
			ServerSocket server = new ServerSocket(port);
			Socket socket = server.accept();
			
	        // create gpio controller
	        final GpioController gpio = GpioFactory.getInstance();
	        
	        final GpioPinDigitalOutput RED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "led_red", PinState.LOW);
	        final GpioPinDigitalOutput GREEN = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "led_green", PinState.LOW);
	        final GpioPinDigitalOutput BLUE = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "led_blue", PinState.LOW);
	        
			System.out.println("Server started.");
			
			
			System.out.println("Got a connection from "+socket.getInetAddress().getHostAddress());
			
	        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
	        String input;
	        while ((input = in.readLine()) != null) {
	        	// Read input
				if(input.equalsIgnoreCase("RED")){
					RED.toggle();
					out.println("Enable/Disable RED!");
				}else if(input.equalsIgnoreCase("BLUE")){
					BLUE.toggle();
					out.println("Enable/Disable BLUE!");
				}else if(input.equalsIgnoreCase("GREEN")){
					GREEN.toggle();
					out.println("Enable/Disable GREEN!");				
				}else if(input.equalsIgnoreCase("exit")){
					gpio.shutdown();
					out.println("Quit gpio!");
				}else if(input.equalsIgnoreCase("quit")){
					break;
				}
				System.out.println(socket.getInetAddress().getHostAddress()+" sent request: "+input);
				System.out.println("Completed request!");
			
	    	}

		}catch(Exception e){
			//e.printStackTrace();
		}
	}
}