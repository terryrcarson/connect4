package Server;

import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

/************************************************************
 * Player
 * - Contains vital functions for client-server communication
 * - Handles player requests outside of Game and Challenge
 ***********************************************************/

public class Player extends Thread {
	
	private volatile Boolean isAvail, inGame, isDisconnected;
	private String name;
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	private Vector<Player> players = new Vector<Player>(); //Individual list of players
	private final BlockingQueue<String> challengeQueue, playerQueue;
	private final int ID;
	public final Object syncObj = new Object();
    
	/**************************************************************************************** 
	 * Constructor
	 * Precondition: Socket is bound to server & client, BlockingQueues have been initialized
	 * Postcondition: Player object is initialized and ready to use
	 ***************************************************************************************/
    public Player(Socket sock, BlockingQueue<String> cQ, int ID, BlockingQueue<String> pQ) {
    	challengeQueue = cQ;
    	playerQueue = pQ;
    	conn = sock;
    	isAvail = true;
    	inGame = false;
    	isDisconnected = false;
    	this.ID = ID;
    	try {
    		out = new PrintWriter(conn.getOutputStream(), true);
    		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    	start();
    }
    
    /*****************************************************************************************
     * run
     * Precondition: Player has been initialized
     * Postcondition: Player has disconnected from the server and is removed from master list
     ****************************************************************************************/
    @Override
    public void run() {
    	String msg, pName;
    	while (!isDisconnected && !Thread.currentThread().isInterrupted()) {
    		System.out.println("Thread " + Thread.currentThread().getId() + ": player thread starting");
	    	while (!inGame && !Thread.currentThread().isInterrupted()) {
	    		if (!isAvail) { 
	    			//Do nothing if player is not available
	    		} else if ((msg = readMsg()) != null) {
	    			if (msg.startsWith("NAME")) { //If player is declaring their name
	    				setPName(msg.substring(5, msg.length()));
	    				synchronized (playerQueue) { //Update the master list
	    					try {
	    						playerQueue.add("UPDATE");
	    					} catch (Exception e) {
	    						System.err.println(e);
	    					}
	    					playerQueue.notify();
	    				}
	    			} else if (msg.startsWith("CHALLENGE")) { //If player is starting a challenge
	    				pName = msg.substring(10, msg.length());
	    				synchronized (challengeQueue) { //Add it to the challenge queue
	    					try {
	    						challengeQueue.add(pName);
	    					} catch (Exception e) {}	
	    					challengeQueue.notify();
	    				}
	    			} else if (msg.equals("REQUESTPLAYERS")) { //If player requests available players
	    				sendMsg(getAvailablePlayers());
	    			} else if (msg.equals("AMICHALLENGED")) { //If player requests if they are challenged
	    				if (isAvail) {
	    					sendMsg("NO");
	    				} else {
	    					sendMsg("YES");
	    				}
	    			} else if (msg.equals("REQUESTBOARD")) { //If player requests the game board before game thread is operational
	    				sendMsg("000000000000000000000000000000000000000000000000");
	    			} else if (msg.startsWith("ISNAMETAKEN")) { //If player is checking if their name is taken
	    				pName = msg.substring(12, msg.length());
	    				if (getPlayerByName(pName) != null) {
	    					sendMsg("true");
	    				} else {
	    					sendMsg("false");
	    				}
	    			}
	    		}
	    	}
	    	synchronized (syncObj) {
	    		try {
	    			syncObj.wait(); //While the player is in a game, wait
	    		} catch (InterruptedException e) {}
	    	}
	    	resetBools(); //Reset inGame and isAvail
    	}
	    System.out.println("Thread " + Thread.currentThread().getId() + " terminating"); //When the player disconnects, it's over
    }
    
    /***************************************************************
     * updateAvailPlayers 
     * Precondition: This player's individual list is not up to date
     * Postcondition: This player's individual list is up to date
     ***************************************************************/
    public void updateAvailPlayers(Vector<Player> players) {
    	this.players = players;
    }
    
    /************************************************************************************
     * getAvailablePlayers
     * Precondition: The individual list of players is initialized
     * Postcondition: The available players are returned as a string to be sent to client
     ***********************************************************************************/
    private String getAvailablePlayers() {
    	String msg = "";
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getAvail() && players.get(i).getPName() != null && players.get(i).getPName() != name) {
    			msg += players.get(i).getPName() + " ";
    		}
    	}
    	return msg;
    }
    
    /******************************************************************************  
     * getPlayerByName
     * Precondition: There is a player in the list with the name being searched for
     * Postcondition: The player that was being searched for for is returned
     *****************************************************************************/
    private Player getPlayerByName(String name) {
    	if (players.size() == 0) {
    		return null;
    	}
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPName() == null) {
			}
    		else if (players.get(i).getPName().equals(name)) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    /**********************************************************
     * setPName
     * Precondition: Name being requested is not already in use
     * Postcondition: This players name is set
     *********************************************************/
    private void setPName(String name) {
    	this.name = name;
    }
    
    /*************************************************
     * getPName
     * Precondition: This player's name is initialized
     * Postcondition: This player's name is returned
     ************************************************/
    public String getPName() {
    	return name;
    }
    
    /************************************************************
     * sendDisconnect
     * Precondition: This player has disconnected
     * Postcondition: This player is removed from the master list
     ***********************************************************/
    private void sendDisconnect() {
    	synchronized (playerQueue) {
    		try {
    			playerQueue.add("DISCONNECTED");
    			System.out.println("termination sent");
    		} catch (Exception e) {}
    		playerQueue.notify();
    	}
    }
    
    /**************************************************************************************
     * setInGame
     * Precondition: N/A
     * Postcondition: This player's inGame status is updated locally and in the master list
     *************************************************************************************/
    public void setInGame(Boolean inGame) {
    	this.inGame = inGame;
    	synchronized (playerQueue) {
    		playerQueue.add("UPDATE");
    		playerQueue.notify();
    	}
    }
    
    /********************************************************
     * getInGame
     * Precondition: N/A
     * Postcondition: This player's inGame status is returned
     ********************************************************/
    public Boolean getInGame() {
    	return inGame;
    }
    
    /*************************************************************************************** 
     * setAvail
     * Precondition: N/A
     * Postcondition: This player's isAvail status is updated locally and in the master list
     **************************************************************************************/
    public void setAvail(Boolean isAvail) {
    	this.isAvail = isAvail;
    	synchronized (playerQueue) {
    		playerQueue.add("UPDATE");
    		playerQueue.notify();
    	}
    }
    
    /********************************************************* 
     * getAvail
     * Precondition: N/A
     * Postcondition: This player's isAvail status is returned
     *********************************************************/
    public Boolean getAvail() {
    	return isAvail;
    }
    
    /**************************************************************************************
     * resetBools
     * Precondition: This player has just finished a game
     * Postcondition: This player's booleans are reset and they are ready for another game
     *************************************************************************************/
    private void resetBools() {
    	isAvail = true;
    	inGame = false;
    }
    
    /*************************************************************
     * getDisconnected
     * Precondition: N/A
     * Postcondition: Returns if the player is disconnected or not
     ************************************************************/
    public Boolean getDisconnected() {
    	return isDisconnected;
    }
    
    /***************************************************
     * sendMsg
     * Precondition: The server and client are connected
     * Postcondition: The message is sent to the client
     **************************************************/
    public void sendMsg(String msg) {
		 try {
	    	out.println(msg);
	    	System.out.println("Thread " + Thread.currentThread().getId() + ": " + msg + " sent");
		 } catch (Exception e) {
			System.err.println(e);
		 }
	}
    
    /*******************************************************************
     * readMsg
     * Precondition: The server and client are connected
     * Postcondition: Returns the message received the client, 
     * 				  or returns that the user has disconnected, 
     * 			      terminating the player thread and removing it from 
     * 				  the master list
     ******************************************************************/
    public String readMsg() {
		try {
			return in.readLine().replaceAll("[^0-9 A-Za-z.]", "");
		} catch (NullPointerException e) {
			System.out.println("Thread " + Thread.currentThread().getId() + ": Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			isDisconnected = true;
			sendDisconnect();
			return "Disconnected";
    	} catch (SocketException e) {
    		System.out.println("Thread " + Thread.currentThread().getId() + ": Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			isDisconnected = true;
			sendDisconnect();
			return "Disconnected";
    	} catch (IOException e) {
			System.err.println(e);
		}
		return null;
	}   
    
    /********************************************************************
     * readMsgTimeout
     * Precondition: The client and server are connected
     * Postcondition: Returns the message received the client, 
     * 				  or returns that the user has disconnected, 
     * 			      terminating the player thread and removing it from 
     * 				  the master list, or throws a SocketTimeoutException
     * 				  if there was no response with 30 seconds
     *******************************************************************/
    public String readMsgTimeout() throws SocketTimeoutException {
		try {
			conn.setSoTimeout(30000);
			return in.readLine().replaceAll("[^0-9 A-Za-z.]", "");
		} catch (SocketTimeoutException e) {
			throw new SocketTimeoutException();
		} catch (NullPointerException e) {
			System.out.println("Thread " + Thread.currentThread().getId() + ": Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			isDisconnected = true;
			sendDisconnect();
			return "Disconnected";
    	} catch (SocketException e) {
    		System.out.println("Thread " + Thread.currentThread().getId() + ": Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			isDisconnected = true;
			sendDisconnect();
			return "Disconnected";
    	} catch (IOException e) {
			System.err.println(e);
		} finally {
			try {
				conn.setSoTimeout(0);
			} catch (SocketException e) {}
		}
		return null;
	}
}