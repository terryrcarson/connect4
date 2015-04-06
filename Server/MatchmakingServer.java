package Server;

import java.util.Vector;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;

/**********************************
 * MatchMakingServer
 * - Handles master list of players
 * - Creates new challenges
 *********************************/

public class MatchmakingServer extends Thread {
	
	private Vector<Player> players = new Vector<Player>(); //Master list of players
	private final BlockingQueue<String> playerQueue, challengeQueue; 
	
	/************************************************** 
	 * Constructor
	 * Precondition: Two BlockingQueues are initialized
	 * Postcondition: Matchmaking server is ready
	 *************************************************/
    public MatchmakingServer(BlockingQueue<String> cQ, BlockingQueue<String> pQ) {
    	challengeQueue = cQ;
    	playerQueue = pQ;
    	System.out.println("Thread " + Thread.currentThread().getId() + ": Matchmaking server started");
    }
    
    /********************************************************************** 
     * run
     * Precondition: MatchMakingServer is initialized
     * Postcondition: PlayerHandler and ChallengeHandler threads are active
     *********************************************************************/
    @Override
    public void run() {
    	new ChallengeHandler().start();
    	new PlayerHandler().start();
    }
    
    /************************************************************************************** 
     * addPlayer - Used by LoginServer to add to the master list when a new player connects
     * Precondition: Player p is initialized
     * Postcondition: Player p is added to the master list
     *************************************************************************************/
    public void addPlayer(Player p) {
    	players.addElement(p);
    	System.out.println("Player added, there are now " + players.size() + " players");
    }
    
    /******************************************************************************  
     * getPlayerByName
     * Precondition: There is a player in the list with the name being searched for
     * Postcondition: The player that was being searched for for is returned
     *****************************************************************************/
    private Player getPlayerByName(String name) {
    	for (int i = 0; i < players.size(); i++) {
    		if (players.get(i).getPName().equals(name)) {
    			return players.get(i);
    		}
    	}
    	return null;
    }

    /****************************************
     * ChallengeHandler
     * Handles the creation of new challenges
     ***************************************/
	class ChallengeHandler extends Thread {
		
		private String msg = "";
		String[] playerarr = new String[2];

		public ChallengeHandler() {}
		
		@Override
		public void run() {
			while (true) {
				synchronized (challengeQueue) {
					try {
						challengeQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						while (challengeQueue.peek() != null) {
							msg = (String) challengeQueue.poll();
				    		if (msg != null) {
				    			StringTokenizer strtok = new StringTokenizer(msg, " ");
				    			for (int i = 0; i < 2; i++) {
				    				playerarr[i] = strtok.nextToken();
				    			}
				    			if (getPlayerByName(playerarr[0]).getAvail() && getPlayerByName(playerarr[1]).getAvail()) {
				    				new Challenge(getPlayerByName(playerarr[0]), getPlayerByName(playerarr[1])).start();
				    			} else {
				    				getPlayerByName(playerarr[0]).sendMsg("UNAVAIL");
				    			}
				    		}
						}
					}
				} 
			}
		}	
	}	
	
	/****************************************************************************** 
	 * PlayerHandler
	 * Manages the master list of players and updates each player's individual list	
	 *****************************************************************************/
	class PlayerHandler extends Thread {
		
		private String qMsg;
		
		public PlayerHandler() {}
		
		/********************************************************************** 
		 * removeDeadThreads
		 * Precondition: There are players that have disconnected
		 * Postcondition: Disconnected players are no longer in the master list
		 **********************************************************************/
		private void removeDeadThreads() {
	    	for (int i = 0; i < players.size(); i++) {
				if (players.get(i).getDisconnected()) {
	    			System.out.println("Player " + players.get(i).getPName() + " removed from list");
	    			players.removeElementAt(i);
	    		}
	    	}
	    }
		
		/********************************************************
		 *  updateIndivLists
		 * Precondition: There are players in the list
		 * Postcondition: Every active players list is up to date
		 *******************************************************/
		private void updateIndivLists() {
			for (int i = 0; i < players.size(); i++) {
				players.get(i).updateAvailPlayers(players);
			}
		}
		
		@Override
		public void run() {
			while (true) {
				synchronized (playerQueue) {
					try {
						playerQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						while (playerQueue.peek() != null) {
							qMsg = (String) playerQueue.poll();
							if (qMsg.startsWith("UPDATE")) {
								updateIndivLists();
							} else if (qMsg.startsWith("DISCONNECTED")) {
								removeDeadThreads();
								updateIndivLists();
							}
						}
					}
				}
			}
		}
	}
}