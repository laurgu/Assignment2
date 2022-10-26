import java.io.*;
import java.util.*;

import common.*;

public class ServerConsole implements ChatIF{
	
	Scanner fromConsole;
	
	EchoServer echoServer;
	
	final public static int DEFAULT_PORT = 5555;

	
	public ServerConsole(int port) {
		
		try {
			
			echoServer = new EchoServer(port,this);
			
		}
		catch(Exception e) {
			System.out.println("Error: can't set up connection. Terminating"
					+ "server console.");
			
			System.exit(1);
			
		}
		
		fromConsole = new Scanner(System.in);
		
	}
	
	public void accept() {
		
			try {
				
				String message;
				
				while(true) {
					message = fromConsole.nextLine();
					echoServer.handleMessageFromServerUI(message);
				}
			}
			catch(Exception ex) {
				System.out.println("Unexpected error reading from console!");
			}
	}

	public void display(String message) {
		
		System.out.println("SERVER MSG> " + message);
	}
	
	private void listen() {
		try {
			echoServer.listen();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		      System.out.println("ERROR - Could not listen for clients!");
		}
	}
	

	
	public static void main(String args[]) {
		
		int port = 5555; //Port to listen on

	    try
	    {
	      port = Integer.parseInt(args[0]); //Get port from command line
	    }
	    catch(NumberFormatException e)
	    {
	      port = DEFAULT_PORT; //Set port to 5555
	    }
	    catch(ArrayIndexOutOfBoundsException e) {
	    	port = DEFAULT_PORT;
	    }
		
	    //EchoServer sv = new EchoServer(port);
	    
	    ServerConsole serverConsole = new ServerConsole(port);
	    
	    serverConsole.listen();
	    
		serverConsole.accept();
	}

}
