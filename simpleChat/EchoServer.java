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
	
	private boolean isClosed;
	
	final private String loginKey = "loginID";
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
    
	  if(msg.toString().startsWith("#login")) {
		  
		  
		  handleLogin(msg.toString(),client);
	  }
	  
	  else if(msg.toString().equals("#logoff")) {
		  serverUI.display("<"+client.getInfo(loginKey).toString()+"> has logged off");
		  this.sendToAllClients("Goodbye <" + client.getInfo(loginKey).toString()+">!");
		  return;
	  }
	  
	  else {
		String loginID = client.getInfo(loginKey).toString();
		
		String message = loginID + "> " + msg.toString();
;	   
		System.out.println("Message received: " + msg + " from <" + loginID + ">");
	    
		
		this.sendToAllClients(message);
	  }
  }
  
  /**
   * Helper function that handles the login command from the client.
   * @param login. The login information of the client passed on from
   * handleMessageFromClient function.
   * @param client. The connection from which the command originated.
   */
  private void handleLogin(String login, ConnectionToClient client) {
	  
	  if(login.length()<7) {
		  return;
	  }
	  serverUI.display("Message received: " + login + " from " + client.getInfo(loginKey));
	  
	  if((login.substring(0,6)).equals("#login")) {
		  
		  String loginID = login.substring(7,login.length()-1);
		  
		  if(client.getInfo(loginKey)==null) {
			  client.setInfo(loginKey, loginID);
			  serverUI.display("<" + client.getInfo(loginKey)+"> has logged on.");
		  }
		  else {
			  
			 try {
				client.sendToClient("Error cannot login again. Terminating connection");
			} catch (IOException e) {
				serverUI.display("Error sending message to client");
			}
			 
			 try {
				client.close();
			} catch (IOException e) {
				//method that makes client close
			}			 
		  }
		  
	  }
  }

  /**
   * Handles message from the server UI
   * @param message. The message from the UI.
   */
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
			 serverUI.display("Server has quit");
			close();
			System.exit(1);
		} catch (Exception e) {
			serverUI.display("Error quitting");
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
				 serverUI.display("Now listening on port " + getPort());
			 }
			 catch(StringIndexOutOfBoundsException e) {
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
	 else if(command.equals("#numclients")) {
		 serverUI.display("Number of clients: "+getNumberOfClients());
	 }
	 else {
		 serverUI.display("Not a valid command");
	 }
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  @Override
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
  @Override
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
		serverUI.display("Error sending welcome message to client");
		
	}
	  
	  serverUI.display("A new client has connected to the server.");  
	  
  }
  
  

  /**
   * Implements method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display("<"+client.getInfo(loginKey)+"> has disconnected");

	  try {
		client.sendToClient("You have disconnected! Goodbye!");
	} catch (Exception e) {
		
	}
  }
  
  /**
   * Implements method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  @Override
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {}
  
  	
}
  
  
 
