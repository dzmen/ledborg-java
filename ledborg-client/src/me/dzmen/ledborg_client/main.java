package me.dzmen.ledborg_client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("What is the ip address?");
		String ip = (scanner.nextLine().toString());
		new main().join(ip);
	}

	// our port to host on
	static int port = 13579;

	public void join(String ip) {
		try {
			// Connect to the server
			System.out.println("Attempting to connect to server...");

			while (true) {
				
				Scanner scanner = new Scanner(System.in);
				//System.out.println("[client]What command would you like to send?");
				String cmd = (scanner.nextLine().toString());				
							
				Socket socket = new Socket(ip, port);
				
				DataOutputStream dos = new DataOutputStream(
						socket.getOutputStream());
				DataInputStream dis = new DataInputStream(
						socket.getInputStream());

				if(cmd.equalsIgnoreCase("quit")){
					dos.writeUTF("quit");
					dos.flush();
					
					try {
						dis.close();
						dos.close();
						socket.close();
					} catch (Exception e) {
						// We do nothing here, because even if we get errors, it
						// will still disconnect!
					}
					
					System.out.println("Client shutdown");
					System.exit(0);
				}
				
				dos.writeUTF(cmd);
				dos.flush();
				System.out.println("[server]" + dis.readUTF());

			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
}