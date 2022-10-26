// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.*;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
	
	ChatIF serverUI;
	
	boolean isClosed;
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    boolean isClosed = true;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client) {
    
   /* String message = msg.toString();
    String messageSubstring = message.substring(9,message.length()-1);
  
	if(message == "#quit") {
		clientDisconnected(client);
		client.close();
	}
	else if(message == "#logoff") {
		clientDisconnected(client);
	}
	else if(messageSubstring == "#sethost") {
		setHost(message.substring(9,message.length()-1));
	}
	else if(messageSubstring == "#setport") {
		int port = Integer.parseInt(message.substring(9,message.length()-1)); 
		setPort(port);
	}*/
	  
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }
  

  
  public void handleMessageFromServerUI(String message) {
	  
	  if(message.startsWith("#")){
		  handleCommand(message);
	  }
	  else {
		  this.sendToAllClients("SERVER MSG> " + message);
		  serverUI.display(message);

	  }
	
  }
  
  private void handleCommand(String command) {
	  String setPortCommand = "";
	  String port = "";
	  
	  if(command.length()>9) {
		  setPortCommand = command.substring(0,8);
		  port = command.substring(9,command.length()-1);
	  }
	  
	 if(command.equals("#quit")) {
		 try {
			close();
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Error quitting");
		}
	 }
	 else if(command.equals("#stop")) {
		 stopListening();
	 }
	 else if(command.equals("#close")) {
		 try {
			close();
		} catch (Exception e) {
			serverUI.display("Error closing");
		}
	 }
	 else if(setPortCommand.equals("#setport")) {
		 
		 if(!isClosed) {
			serverUI.display("Server is still active. Please close"
			 		+ " server and try again.");
		 }
		 else {
			 try {
				 setPort(Integer.parseInt(port));
				 return;
			 }
			 catch(StringIndexOutOfBoundsException e) {
				 serverUI.display("HIT");
			 }
			 catch(NumberFormatException ne) {
				 serverUI.display("Invalid port number");
			 }
			 
		 }
	 }
	 else if(command.equals("#start")) {
		 if(isListening()) {
			 serverUI.display("Server already listning");
		 }
		 else {
			 try {
				listen();
			} catch (Exception e) {
				serverUI.display("Error when trying to listen");
			}
		 }
	 }
	 else if(command.equals("#getport")) {
		 serverUI.display("PORT: " + getPort());
	 }
	 else {
		 return;
	 }
  }
  
  /**
   * Implements method called when the server is closed.
   * The default implementation does nothing. This method may be
   * overriden by subclasses. When the server is closed while still
   * listening, serverStopped() will also be called.
   */
  @Override
  protected void serverClosed() {
	  isClosed = true;
  }
  
  //THIS IS FOR TESTING PURPOSES DELETE THIS LATER
  private void getStatus() {
	  if(!isClosed) {
		  serverUI.display("isClosed is false");
	  }
  }

    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
	  isClosed = false;
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
	  isClosed = true;
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Hook method called each time a new client connection is
   * accepted.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {

	  try {
		client.sendToClient("You are now connected! Welcome!");
	} catch (IOException e) {
		
		
	}
	  
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  
	  try {
		client.sendToClient("You have disconnected! Goodbye!");
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}
  
  
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  /*public static void main(String[] args) 
  {
    int port = 5555; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
    	System.out.println(ex.getMessage());
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}*/
//End of EchoServer class
