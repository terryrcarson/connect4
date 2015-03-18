
/**
 * @(#)LoginServer.java
 *
 *
 * @author 
 * @version 1.00 2015/3/10
 */
import java.net.*;
import java.util.*;

public class LoginServer {
	
	Vector<Player> players = new Vector<Player>();
	ServerSocket servSock;
	MatchmakingServer matchmaker;
	
    public LoginServer() {
    	try {
    		servSock = new ServerSocket(6666);
    		System.out.println("Login server started");
    	} catch (Exception e) {
    		System.err.println("Error creating socket: " + e);
    	}
    }
    
    public void initMatchmaker() {
    	matchmaker = new MatchmakingServer();
    }
    
    public void listenConnect() {
    	try {
    		Socket conn = servSock.accept();
    		players.addElement(new Player(conn));
    		matchmaker.addPlayer(new Player(conn));
    		System.out.println("Connection from " + conn.getInetAddress());
    	} catch (Exception e) {
    		System.err.println(e);
    	}	
    }
    
    public static void main(String args[]) throws Exception {
    	
    	LoginServer serv = new LoginServer();
    	
    	while (serv.players.size() < 2) {
    		serv.listenConnect();
    	}
    	new Game(serv.players.get(0), serv.players.get(1)).start();
    	
    	/*serv.initMatchmaker();
    	
    	while (true) {
    		serv.listenConnect();
    	}*/
    	
    	
    }
    
}