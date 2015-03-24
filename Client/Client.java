
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
	private Boolean isGameOver = false;
	
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
			if (msg.startsWith("GAMEOVER")) {
				isGameOver = true;
				System.out.println("Game over is now true");
			}
			System.out.println(msg.replaceAll("[^-0-9 A-Za-z.]", "") + " received");
			return msg.replaceAll("[^-0-9 A-Za-z.]", "");
		} catch (NullPointerException e) {
			isGameOver = true;
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
			sendMsg("REQUESTCURRPLAYER");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			//System.out.println(msg);
			return Character.getNumericValue(msg.charAt(0));
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return 0;
	}
	
	public Cell getPieceLoc() {
		try {
			sendMsg("REQUESTPIECELOC");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			} 
			if (msg.equals("-1-1")) {
				return new Cell(-1, -1);
			}
			int x = Character.getNumericValue(msg.charAt(0));
			int y = Character.getNumericValue(msg.charAt(1));
			System.out.println("x: " + x + ", y: " + y);
			return new Cell(x, y);
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public Cell[][] getBoard() {
		Cell[][] board = new Cell[7][6];
		int i = 0;
		try {
			sendMsg("REQUESTBOARD");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			//System.out.println(msg);
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 6; y++) {
					board[x][y] = new Cell(x, y);
					board[x][y].setType(Character.getNumericValue(msg.charAt(i)));
					i++;
				}
			}
			return board;
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public int getWinner() {
		try {
			sendMsg("REQUESTWINNER");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			//System.out.println(msg);
			return Character.getNumericValue(msg.charAt(0));
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return 0;
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
	
	public Boolean getGameOver() {
		return isGameOver;
	}
	
	/*public static void main(String args[]) {
		Client client = new Client();
		
		//client.getBoard();
		client.getBoard();
		client.getPieceLoc();
		client.getCurrPlayer();
	}*/
	
}
