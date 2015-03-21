
/**
 * @(#)Player.java
 *
 *
 * @author 
 * @version 1.00 2015/3/10
 */
import java.net.*;
import java.io.*;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
/*
 * objectives:
 * 1. handle communication between each player and the server
 * 2. push challenges to matchmaking server
 */

public class Player extends Thread {
	
	private Boolean isAvail, inGame;
	private String name;
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	private Vector<Player> players = new Vector<Player>();
	private final BlockingQueue queue;
	private final int ID;
	
    //public Player() { queue = null; }
    
    public Player(Socket sock, BlockingQueue q, int ID) {
    	queue = q;
    	conn = sock;
    	isAvail = true;
    	inGame = false;
    	this.ID = ID;
    	try {
    		out = new PrintWriter(conn.getOutputStream(), true);
    		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    	start();
    }
    
    @Override
    public void run() {
    	String msg, pName;
    	while (!inGame && !Thread.currentThread().isInterrupted()) {
    		if (!isAvail) { }
    		else if ((msg = readMsg()) != null) {
    			if (!msg.equals("REQUESTPLAYERS")) {
    				System.out.println("Thread " + Thread.currentThread().getId() + ": " + msg + " received from player " + name);
    			}
    			if (msg.startsWith("NAME")) {
    				setPName(msg.substring(5, msg.length()));
    			} else if (msg.startsWith("CHALLENGE")) {
    				pName = msg.substring(10, msg.length());
    					try {
    						queue.add(pName);
    					} catch (Exception e) {
    						
    					}
    			} else if (msg.equals("REQUESTPLAYERS")) {
    				sendMsg(getAvailablePlayers());
    			} else if (msg.equals("AMICHALLENGED")) {
    				if (isAvail) {
    					sendMsg("NO");
    				} else {
    					sendMsg("YES");
    				}
    			}
    		}
    	}
    }
    
    public synchronized void updateAvailPlayers(Vector<Player> players) {
    	this.players = players;
    }
    
    public String getAvailablePlayers() {
    	String msg = "";
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getAvail() && players.get(i).getPName() != null && players.get(i).getPName() != name) {
    			msg += players.get(i).getPName() + " ";
    		}
    	}
    	return msg;
    }
    
    
    public void setPName(String name) {
    	this.name = name;
    }
    
    public String getPName() {
    	return name;
    }
    
    public void setInGame(Boolean inGame) {
    	this.inGame = inGame;
    }
    
    public Boolean getInGame() {
    	return inGame;
    }
    
    public void setAvail(Boolean isAvail) {
    	this.isAvail = isAvail;
    }
    
    public Boolean getAvail() {
    	return isAvail;
    }
    
    public int getPID() {
    	return ID;
    }
    
    public Socket getSock() {
    	return conn;
    }
    
    public void sendMsg(String msg) {
		 try {
	    	out.println(msg);
	    	System.out.println("Thread " + Thread.currentThread().getId() + ": " + msg + " sent");
		 } catch (Exception e) {
			System.err.println(e);
		 }
	}
    
    public String readMsg() {
		try {
			return in.readLine().replaceAll("[^0-9 A-Za-z.]", "");
		} catch (NullPointerException e) {
			System.out.println("Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			return "Disconnected";
    	} catch (SocketException e) {
    		System.out.println("Player " +  ID + " has disconnected");
			Thread.currentThread().interrupt();
			return "Disconnected";
    	} catch (IOException e) {
			System.err.println(e);
		}
		return null;
	}    
}