package Server;

import java.net.SocketTimeoutException;

//Creates a challenge, if accepted begins a game thread, if denied then terminates

public class Challenge extends Thread {
	
	private Player p1;
	private Player p2;
	
    public Challenge(Player p1, Player p2) {
    	this.p1 = p1;
    	this.p2 = p2;
    	this.p1.setAvail(false);
    	this.p2.setAvail(false);
    	System.out.println("Challenge started between " + p1.getPName() + " and " + p2.getPName());
    }
    
    @Override
    public void run() {
    	try {
	    	System.out.println(p2.readMsgTimeout());
	    	p2.sendMsg(p1.getPName());
	    	String msg = p2.readMsgTimeout();
	    	if (msg.equals("OK")) {
	    		if (p2.readMsg().equals("READY")) {
	    			p2.sendMsg("OK");
	    		}
	    		new Game(p1, p2).start();
				p1.sendMsg("STARTGAME");
				p1.setInGame(true);
				p2.setInGame(true);
				//p1.interrupt();
				//p2.interrupt();
	    	} else if (msg.equals("NO")) {
	    		p1.sendMsg("NO");
	    		p2.sendMsg("BLAH");
				p1.setAvail(true);
				p2.setAvail(true);
	    	} else if (msg.equals("Disconnected")) {
	    		p1.sendMsg("NO");
				p1.setAvail(true);
	    	}
    	} catch (SocketTimeoutException e) {
    		p1.sendMsg("NORESPONSE");
    		p2.sendMsg("NORESPONSE");
    		p1.setAvail(true);
    		p2.setAvail(true);
    	} 
    }
}