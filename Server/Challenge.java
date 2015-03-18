/**
 * @(#)Challenge.java
 *
 *
 * @author 
 * @version 1.00 2015/3/17
 */

//Creates a challange, if accepted begins a game thread, if denied then terminates
public class Challenge extends Thread {
	
	Player p1 = new Player();
	Player p2 = new Player();
	
    public Challenge(Player p1, Player p2) {
    	this.p1 = p1;
    	this.p2 = p2;
    }
    
    @Override
    public void run() {
    	p2.sendMsg("CHALLENGE " + p1.getName());
    	switch(p2.readMsg()) {
    		case "OK":
    			new Game(p1, p2).start();
    			p1.setAvail(false);
    			p2.setAvail(false);
    			break;
    		case "NO":
    			p1.sendMsg("NO");
    			break;
    	}
    }
    
    
}