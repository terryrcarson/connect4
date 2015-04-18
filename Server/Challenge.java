//package Server;

import java.net.SocketTimeoutException;

/*************************************************
 * Challenge
 * - Handles the creation of a challenge
 * - If challenge is accepted, creates Game thread
 * - If challenge is denied, terminates
 ************************************************/

public class Challenge extends Thread {
	
	private Player p1;
	private Player p2;
	
	/********************************************************************************
	 * Constructor
	 * Precondition: Players p1 and p2 are initialized
	 * Postcondition: Players p1 and p2 are in a challenge and therefore unavailable
	 *******************************************************************************/
    public Challenge(Player p1, Player p2) {
    	this.p1 = p1;
    	this.p2 = p2;
    	this.p1.setAvail(false);
    	this.p2.setAvail(false);
    	System.out.println("Thread " + Thread.currentThread().getId() + ": Challenge started between " + p1.getPName() + " and " + p2.getPName());
    }
    
    /*************************************************
     * run
     * - If challenge is accepted, creates Game thread
     * - If challenge is denied, terminates
     ************************************************/
    @Override
    public void run() {
    	try {
	    	p2.readMsg(); //Read the WHOCHALLENGED
	    	p2.sendMsg(p1.getPName());
	    	String msg = p2.readMsgTimeout();
	    	if (msg.equals("OK")) {
	    		if (p2.readMsg().equals("READY")) {
	    			p2.sendMsg("OK");
	    		}
				p1.sendMsg("STARTGAME");
				new Game(p1, p2).start();
				p1.setInGame(true);
				p2.setInGame(true);
	    	} else if (msg.equals("NO")) {
	    		p1.sendMsg("NO");
	    		p2.sendMsg("BLAH"); //Just so that p2's readMsg() doesn't block
				p1.setAvail(true);
				p2.setAvail(true);
	    	} else if (msg.equals("Disconnected")) {
	    		p1.sendMsg("NO");
				p1.setAvail(true);
	    	}
    	} catch (SocketTimeoutException e) { //Give them 30 seconds to respond
    		p1.sendMsg("NORESPONSE");
    		p2.sendMsg("NORESPONSE");
    		p1.setAvail(true);
    		p2.setAvail(true);
    	} finally {
    		System.out.println("Thread " + Thread.currentThread().getId() + ": Challenge thread terminating");
    	}
    }
}