
import java.net.*;
import java.util.*;
import java.io.*;

import javax.swing.DefaultListModel;

public class Client {
	
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	private Cell[][] board = new Cell[7][6];
	private Cell currPieceLoc = new Cell();
	
	public Client() {
		try {
			System.out.println("Connecting...");
			conn = new Socket("localhost", 6666);
			System.out.println("Connected!");
			out = new PrintWriter(conn.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (Exception e) {
			System.err.println("Constructor error:" + e);
		}
	}
	
	public String readMsg() {
		try {
			String msg = in.readLine();
			System.out.println(msg.replaceAll("[^0-9 A-Za-z.]", "") + " received");
			if (msg.startsWith("GAMEOVER")) {
				
			}
			return msg.replaceAll("[^0-9 A-Za-z.]", "");
		} catch (IOException e) {
			System.err.println(e);
		}
		return null;
	}
	
	 public void sendMsg(String msg) {
		 try {
	    	out.println(msg);
	    	System.out.println(msg + " sent");
		 } catch (Exception e) {
			System.err.println(e);
		 }
	}
	
	public int getCurrPlayer() {
		try {
			out.println("REQUESTCURRPLAYER");
			String msg = readMsg();
			//System.out.println(msg);
			return Character.getNumericValue(msg.charAt(0));
		} catch (Exception e) {
			System.err.println(e);
		}
		return 0;
	}
	
	public Cell getPieceLoc() {
		try {
			out.println("REQUESTPIECELOC");
			String msg = readMsg();
			int x = Character.getNumericValue(msg.charAt(0));
			int y = Character.getNumericValue(msg.charAt(1));
			System.out.println("x: " + x + ", y: " + y);
			return new Cell(x, y);
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public Cell[][] getBoard() {
		Cell[][] board = new Cell[7][6];
		int i = 0;
		try {
			out.println("REQUESTBOARD");
			String msg = readMsg();
			//System.out.println(msg);
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 6; y++) {
					board[x][y] = new Cell(x, y);
					board[x][y].setType(Character.getNumericValue(msg.charAt(i)));
					i++;
				}
			}
			return board;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public DefaultListModel<String> getAvailPlayers() {
		DefaultListModel<String> players = new DefaultListModel<String>();
		try {
			sendMsg("REQUESTPLAYERS");
			String msg = readMsg();
			StringTokenizer tokenizer = new StringTokenizer(msg, " ");
			while (tokenizer.hasMoreElements()) {
				players.addElement(tokenizer.nextToken());
			}
			return players;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public String challengePlayer(String thisPlayer, String targetPlayer) {
		try {
			sendMsg("CHALLENGE " + thisPlayer + " " + targetPlayer);
			return readMsg();
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public boolean isChallenged() {
		try {
			sendMsg("AMICHALLENGED");
			switch (readMsg()) {
				case "YES":
					System.out.println("challenged");
					return true;
					
				case "NO":
					return false;
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}
	
	public String getChallenger() {
		try {
			sendMsg("WHOCHALLENGED");
			return readMsg();
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	
	/*public static void main(String args[]) {
		Client client = new Client();
		
		//client.getBoard();
		client.getBoard();
		client.getPieceLoc();
		client.getCurrPlayer();
	}*/
	
}
