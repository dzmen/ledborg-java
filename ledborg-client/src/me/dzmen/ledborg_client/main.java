package me.dzmen.ledborg_client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.*;
import java.awt.event.*;

public class main extends JFrame implements KeyListener, ActionListener {

	//The log display
	JTextArea displayArea;
	//The Typing field
    JTextField typingArea;
    // Makes enters
    static final String newline = System.getProperty("line.separator");
    //our port to host on
	static int port = 13579;
	//The connection
	static Socket con;
	//Send data
	static PrintWriter out;
	//Receive data
	static BufferedReader in;
	//Check if key is up
    boolean c_up = false;
    //Check if key is down
    boolean c_down = false;
	
	public static void main(String[] args) {
		 //Create the connection with the raspberry pi server
		boolean conn = false;
		while(conn == false){
			String ip = null;
			int c = -1;
			while(c < 0){
				ip = JOptionPane.showInputDialog("Please enter your raspberry pi ip:");
				if(ip.length() > 0){
					c++;
				}
			}
			conn = createConnection(ip);
		}
		
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
         } catch (UnsupportedLookAndFeelException ex) {
             ex.printStackTrace();
         } catch (IllegalAccessException ex) {
             ex.printStackTrace();
         } catch (InstantiationException ex) {
             ex.printStackTrace();
         } catch (ClassNotFoundException ex) {
             ex.printStackTrace();
         }
         /* Turn off metal's use of bold fonts */
         UIManager.put("swing.boldMetal", Boolean.FALSE);
          
         //Schedule a job for event dispatch thread:
         //creating and showing this application's GUI.
         javax.swing.SwingUtilities.invokeLater(new Runnable() {
             public void run() {
                 createAndShowGUI();
             }
         });
	}

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        main frame = new main("Ledborg-client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        //Set up the content pane.
        frame.addComponentsToPane();
         
         
        //Display the window.
        frame.pack();
        frame.setVisible(true);
        
    }
     
    private void addComponentsToPane() {
         
        JButton clearBT = new JButton("Clear");
        clearBT.addActionListener(this);
        
        JButton exitBT = new JButton("Exit");
        exitBT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
            	quit();
           }
        });
         
        typingArea = new JTextField(20);
                 
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.addKeyListener(this);
        displayArea.requestFocusInWindow();
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));
         
        //getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(clearBT, BorderLayout.AFTER_LINE_ENDS);
        getContentPane().add(exitBT, BorderLayout.PAGE_END);
        
       
    }
     
    public main(String name) {
        super(name);
    }
     
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
        //displayInfo(e, "KEY TYPED: ");
    }
     
    /** Handle the key pressed event from the text field. */
    public void keyPressed(KeyEvent e) {
        displayInfo(e, 1);
    }
     
    /** Handle the key released event from the text field. */
    public void keyReleased(KeyEvent e) {
        displayInfo(e, 2);
    }
     
    /** Handle the button click. */
    public void actionPerformed(ActionEvent e) {
        //Clear the text components.
        displayArea.setText("");
        typingArea.setText("");
         
        //Return the focus to the display area.
        displayArea.requestFocusInWindow();
    }
     
    private void displayInfo(KeyEvent e, int kS){
    	/*
    	 * 37 = left
    	 * 38 = up
    	 * 39 = right
    	 * 40 = down
    	 * */
         
        //You should only rely on the key char if the event
        //is a key typed event.
        int keyCode = e.getKeyCode();
        boolean read = false;
        if(keyCode == 39){
        	if(kS == 2 && c_up == false){
        		out.println("RED");
        		c_up = true;  
        		c_down = false;
        		read = true;

        	}
        	if(kS == 1 && c_down == false){
        		out.println("RED");
        		c_down = true;  
        		c_up = false;
        		read = true;
        	}
        	
        }
                
        if(read){
	        String request = "";
			try {
				request = in.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	        displayArea.append(request + newline);
	        displayArea.setCaretPosition(displayArea.getDocument().getLength());
        }
    }
	
    private static boolean createConnection(String ip){
    	try {
			con = new Socket(ip, port);
		} catch (UnknownHostException e) {
			//e.printStackTrace();
			if (JOptionPane.showConfirmDialog(null, "De verbinding is mislukt! Ander ip proberen?", "Request", 
				    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				    == JOptionPane.YES_OPTION)
			{
				return false;
				 //If you press yes
			}
			else{
				//If you press no
				quit();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			//Give this message if connection failed
			if (JOptionPane.showConfirmDialog(null, "De verbinding is mislukt! Ander ip proberen?", "Request", 
				    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
				    == JOptionPane.YES_OPTION)
			{
				return false;
				 //If you press yes
			}
			else{
				//If you press no
				quit();
			}
		}
    	try {
			out = new PrintWriter(con.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return true;
    }
	
    private static void quit(){
		System.exit(0);
    }
}