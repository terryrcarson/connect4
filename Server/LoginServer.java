//package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

/************************************************************************
 * LoginServer
 * - Listens for new connections and passes them off to MatchMakingServer
 ***********************************************************************/

public class LoginServer {
	
	private final int PORT_NUMBER = 6664; //Edit this if needed
	private final int MAX_CHALLENGES = 50; //Edit this if needed
	private BlockingQueue<String> challengeQueue = new ArrayBlockingQueue<String>(MAX_CHALLENGES);
	private BlockingQueue<String> playerQueue = new ArrayBlockingQueue<String>(50);
	private ServerSocket servSock;
	private MatchmakingServer matchmaker;
	private int ID = 0;
	
	
	/*****************************************************************
	 * Constructor
	 * Precondition: N/A
	 * Postcondition: The ServerSocket is ready to accept connections.
	 * 				  If there was an error, the program exits
	 ****************************************************************/
    public LoginServer() {
    	try {
    		servSock = new ServerSocket(PORT_NUMBER);
    		System.out.println("Login server started");
    	} catch (Exception e) {
    		System.err.println("Error creating socket: " + e);
    		System.exit(1);
    	}
    }
    
    /******************************************************
     * initMatchmaker
     * Precondition: The two BlockingQueues are initialized
     * Postcondition: The MatchMakingServer is running
     ******************************************************/
    public void initMatchmaker() {
    	matchmaker = new MatchmakingServer(challengeQueue, playerQueue);
    	matchmaker.start();
    }
    
    /****************************************************************************************
     * listenConnect
     * Precondition: The LoginServer and MatchMakingServer are running
     * Postcondition: When a new player connects, their Socket is passed to MatchMakingServer
     ***************************************************************************************/
    public void listenConnect() {
    	try {
    		Socket conn = servSock.accept();
    		System.out.println("LoginServer: Connection from " + conn.getInetAddress());
    		matchmaker.addPlayer(new Player(conn, challengeQueue, ID, playerQueue));
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