/**
 * @(#)MatchmakingServer.java
 *
 *
 * @author 
 * @version 1.00 2015/3/17
 */


import java.util.Vector;

public class MatchmakingServer extends Thread {
	
	Vector<Player> players = new Vector<Player>();
	
    public MatchmakingServer() {
    	System.out.println("Thread " + Thread.currentThread().getId() + ": Matchmaking server started");
    }
    
    @Override
    public void run() {
    	String msg, pName;
    	Player target = new Player();
    	//receive challenge requests
    	//make new challenge thread if requested player is available
    	//also sends list of available players to clients
    	while (true) {
    		for (int i = 0; i < players.size(); i++) {
    			if ((msg = players.get(i).readMsg()) != null) {
    				if (msg.startsWith("CHALLENGE")) {
    					pName = msg.substring(10, msg.length() - 1);
    					if (getPlayerByName(pName).getAvail()) {
    						new Challenge(players.get(i), getPlayerByName(pName));
    					} else {
    						players.get(i).sendMsg("UNAVAIL");
    					}	
    				} else if (msg.equals("REQUESTPLAYERS")) {
    					players.get(i).sendMsg(getAvailablePlayers());
    				}
    			}
    		}
    	}
    }
    
    public void addPlayer(Player p) {
    	players.addElement(p);
    }
    
    public Player getPlayerByName(String name) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getName().equals(name)) {
    			return players.get(i);
    		}
    	}
    	return null;
    }
    
    public String getAvailablePlayers() {
    	String msg = "";
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getAvail()) {
    			msg += players.get(i).getName() + " ";
    		}
    	}
    	return msg;
    }
}