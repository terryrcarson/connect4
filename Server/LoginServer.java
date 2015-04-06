package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

public class LoginServer {
	
	private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(50);
	private ServerSocket servSock;
	private MatchmakingServer matchmaker;
	private int ID = 0;
	
    public LoginServer() {
    	try {
    		servSock = new ServerSocket(6664);
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
    		//conn.setSoTimeout(5000);
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