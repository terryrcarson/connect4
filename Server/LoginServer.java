package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

//Listens for connections and passes them off to MatchMakingServer

public class LoginServer {
	
	private final int PORT_NUMBER = 6664; //Edit this if needed
	private final int MAX_CHALLENGES = 50; //Edit this if needed
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(MAX_CHALLENGES);
	private ServerSocket servSock;
	private MatchmakingServer matchmaker;
	private int ID = 0;
	
    public LoginServer() {
    	try {
    		servSock = new ServerSocket(PORT_NUMBER);
    		System.out.println("Login server started");
    	} catch (Exception e) {
    		System.err.println("Error creating socket: " + e);
    		System.exit(1);
    	}
    }
    
    public void initMatchmaker() {
    	matchmaker = new MatchmakingServer(queue);
    	matchmaker.start();
    }
    
    public void listenConnect() {
    	try {
    		Socket conn = servSock.accept();
    		System.out.println("Connection from " + conn.getInetAddress());
    		matchmaker.addPlayer(new Player(conn, queue, ID));
    		ID++;
    	} catch (Exception e) {
    		System.err.println(e);
    	}	
    }
    
    public static void main(String args[]) {
    	
    	LoginServer serv = new LoginServer();
    	serv.initMatchmaker();
    	while (true) {
    		serv.listenConnect();
    	}
    }
}