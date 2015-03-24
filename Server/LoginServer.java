
/**
 * @(#)LoginServer.java
 *
 *
 * @author 
 * @version 1.00 2015/3/10
 */
import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class LoginServer {
	
	private BlockingQueue queue = new ArrayBlockingQueue<String>(50);
	private Vector<Player> players = new Vector<Player>();
	private ServerSocket servSock;
	private MatchmakingServer matchmaker;
	private int ID = 0;
	
    public LoginServer() {
    	try {
    		servSock = new ServerSocket(6666);
    		System.out.println("Login server started");
    	} catch (Exception e) {
    		System.err.println("Error creating socket: " + e);
    	}
    }
    
    public void initMatchmaker() {
    	matchmaker = new MatchmakingServer(queue);
    	matchmaker.start();
    }
    
    public void listenConnect() {
    	try {
    		Socket conn = servSock.accept();
    		//players.addElement(new Player(conn));
    		System.out.println("Connection from " + conn.getInetAddress());
    		//new Player(conn, queue, ID).start();
    		matchmaker.addPlayer(new Player(conn, queue, ID));
    		ID++;
    	} catch (Exception e) {
    		System.err.println(e);
    	}	
    }
    
    public static void main(String args[]) throws Exception {
    	
    	LoginServer serv = new LoginServer();
    	
    	/*while (serv.players.size() < 2) {
    		serv.listenConnect();
    	}
    	new Game(serv.players.get(0), serv.players.get(1)).start();*/
    	
    	serv.initMatchmaker();
    	
    	while (true) {
    		serv.listenConnect();
    	}
    	
    	
    }
    
}