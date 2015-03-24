/**
 * @(#)MatchmakingServer.java
 *
 *
 * @author 
 * @version 1.00 2015/3/17
 */

/*needs to create challenges between players
 *objectives:
 *1. listen for challenges
 *2. push available players to player threads
 */

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;

public class MatchmakingServer extends Thread {
	
	private Vector<Player> players = new Vector<Player>();
	private final BlockingQueue queue;
	
    public MatchmakingServer(BlockingQueue q) {
    	queue = q;
    	System.out.println("Thread " + Thread.currentThread().getId() + ": Matchmaking server started");
    }
    
    @Override
    public void run() {
    	String msg;
    	String[] playerarr = new String[2];
    	while (true) {
    		msg = (String) queue.poll();
    		if (msg != null) {
    			StringTokenizer strtok = new StringTokenizer(msg, " ");
    			for (int i = 0; i < 2; i++) {
    				playerarr[i] = strtok.nextToken();
    				//System.out.println(playerarr[i]);
    			}
    			if (getPlayerByName(playerarr[0]).getAvail() && getPlayerByName(playerarr[1]).getAvail()) {
    				new Challenge(getPlayerByName(playerarr[0]), getPlayerByName(playerarr[1])).start();
    			} else {
    				getPlayerByName(playerarr[0]).sendMsg("UNAVAIL");
    			}
    		}
    		removeDeadThreads();
    		for (int i = 0; i < players.size(); i++) {
    			players.get(i).updateAvailPlayers(players);
    		}
    	}
    }
    
    public void removeDeadThreads() {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getInGame()) {
    			players.removeElementAt(i);
    		} else if (!players.get(i).isAlive()) {
    			players.removeElementAt(i);
    		}
    	}
    }
    
    public void removeDisconnectedPlayer(int i) {
    	players.removeElementAt(i);
    	System.out.println("Player " + i + " has disconnected");
    }
    
    public void addPlayer(Player p) {
    	players.addElement(p);
    	System.out.println("Player added, there are now " + players.size() + " players");
    }
    
    public Player getPlayerByID(int ID) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPID() == ID) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    public Player getPlayerByName(String name) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPName().equals(name)) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    public String getAvailablePlayers() {
    	String msg = "";
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getAvail()) {
    			msg += players.get(i).getPName() + " ";
    		}
    	}
    	return msg;
    }
}