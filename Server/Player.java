
/**
 * @(#)Player.java
 *
 *
 * @author 
 * @version 1.00 2015/3/10
 */
import java.net.*;
import java.io.*;

public class Player {
	
	private Boolean isAvail;
	private String name;
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	
    public Player() {}
    
    public Player(Socket sock) {
    	conn = sock;
    	isAvail = true;
    	try {
    		out = new PrintWriter(conn.getOutputStream(), true);
    		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	} catch (Exception e) {
    		System.err.println(e);
    	}
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getName() {
    	return name;
    }
    
    public void setAvail(Boolean isAvail) {
    	this.isAvail = isAvail;
    }
    
    public Boolean getAvail() {
    	return isAvail;
    }
    
    public Socket getSock() {
    	return conn;
    }
    
    public void sendMsg(String msg) {
		 try {
	    	out.println(msg);
	    	System.out.println("Thread " + Thread.currentThread().getId() + ": " + msg + " sent");
		 } catch (Exception e) {
			System.err.println(e);
		 }
	}
    
    public String readMsg() {
		try {
			return in.readLine().replaceAll("[^0-9 A-Z.]", "");
		} catch (IOException e) {
			System.err.println(e);
		}
		return null;
	}    
}