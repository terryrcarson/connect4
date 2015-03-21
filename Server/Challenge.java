/**
 * @(#)Challenge.java
 *
 *
 * @author 
 * @version 1.00 2015/3/17
 */

//Creates a challange, if accepted begins a game thread, if denied then terminates
public class Challenge extends Thread {
	
	Player p1;
	Player p2;
	
    public Challenge(Player p1, Player p2) {
    	this.p1 = p1;
    	this.p2 = p2;
    	this.p1.setAvail(false);
    	this.p2.setAvail(false);
    	System.out.println("Challenge started between " + p1.getPName() + " and " + p2.getPName());
    }
    
    @Override
    public void run() {
    	while (!p2.readMsg().equals("WHOCHALLENGED")) {
    		//do nothing
    	}
    	p2.sendMsg(p1.getPName());
    	String msg = p2.readMsg();
    	switch(msg) {
    		case "OK":
    			new Game(p1, p2).start();
    			p1.sendMsg("STARTGAME");
    			p1.setInGame(true);
    			p2.setInGame(true);
    			break;
    		case "NO":
    			p1.sendMsg("NO");
    			p1.setAvail(true);
    			p2.setAvail(true);
    			break;
    		case "Disconnected":
    			p1.sendMsg("NO");
    			p1.setAvail(true);
    			break;
    	}
    }
    
    
}