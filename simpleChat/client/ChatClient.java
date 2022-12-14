// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, String loginID, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if(message.startsWith("#")){
		 handleCommand(message);
	  }
	  else {
		  try
		    {
		      sendToServer(message);
		    }
		   catch(IOException e)
		    {
		      clientUI.display
		        ("Could not send message to server.  Terminating client.");
		      quit();
		    }
	  }
	
  }
  /**
   * Helper function that handles commands from the UI.
   * @param message. The message sent from the UI passed on from
   * handleMessageFromClientUI function
   * @return boolean. If the passed on message is a valid command
   * return value is true. Otherwise it is false.
   */
 
  private void handleCommand(String message) {
	  
	  String commandSubstring ="";
	  String portOrHost = "";
	  
	  if(message.length()>9) {
		  commandSubstring = message.substring(0,8);
		  portOrHost = message.substring(9,message.length()-1);
	  }
	  
	if(message.compareTo("#quit") == 0) {
		quit();
	}
	else if(message.compareTo("#logoff") == 0) {
		try {
			sendToServer("#logoff");
			closeConnection();
		} catch (Exception e) {
			clientUI.display("Error logging out");
		}
		
	}
	else if(message.compareTo("#login")==0) {
		
		if(isConnected()) {
			clientUI.display("Already logged in");
		}
		else{
			try {
				openConnection();
			} catch (Exception e) {
				clientUI.display("Error logging in");
			}
		}
		
	}
	else if(commandSubstring .compareTo("#setport")==0) {
		
		if(!isConnected()) {
			
			setPort(Integer.parseInt(portOrHost));
		}
		else {
			clientUI.display("Please logoff before setting port");
		}
	}
	else if(commandSubstring .compareTo("#sethost")==0) {
		if(!isConnected()) {
			setHost(portOrHost);
		}
		else {
			clientUI.display("Please logoff before setting host");
		}
	}
	else if(message.compareTo("#gethost") == 0) {
		clientUI.display("HOST: "+getHost());
	}
	else if(message.compareTo("#getport") == 0) {
		clientUI.display("PORT: "+getPort());
	}
	
	else {
		clientUI.display("Not a valid command");
	}
		  
  }
  
  /**
	 * Implements method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  
  @Override
  public void connectionException(Exception exception) {
	 clientUI.display("Server had shutdown");
	 quit();
  }
  

	/**
	 * Implements method from superclass AbstractClient
	 * When server closes connection, function prints that the server has
	 * closed and the program is quitting
	 * Program then quits
	 */
  @Override
  public void connectionClosed() {
	  
	  clientUI.display("Connection closed");
	  
	}
  

	/**
	 * Implemented method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  @Override
	protected void connectionEstablished() {
	  try {
		sendToServer("#login<"+loginID+">");
		clientUI.display("<"+loginID+"> has logged on.");
	} catch (IOException e) {
		clientUI.display("Could not send message to server");
	}
	}

  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
}
//End of ChatClient class
