
import java.net.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;

public class Client {
	
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	private Cell[][] board = new Cell[7][6];
	private Cell currPieceLoc = new Cell();
	private Boolean isGameOver = false, serverDisconnected = false;
	
	public Client() {
		try {
			System.out.println("Connecting...");
			conn = new Socket("localhost", 6664);
			System.out.println("Connected!");
			out = new PrintWriter(conn.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (Exception e) {
			new ErrorDialog("Unable to connect to server");
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
			serverDisconnected = true;
			new ErrorDialog("You have disconnected from the server");
		} catch (SocketException e) {
			isGameOver = true;
			serverDisconnected = true;
			new ErrorDialog("You have disconnected from the server");
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
	
	public Boolean isNameTaken(String name) {
		try {
			sendMsg("ISNAMETAKEN " + name);
			return Boolean.parseBoolean(readMsg());
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}
	
	class ErrorDialog implements ActionListener {
	
	JButton ok;
	JFrame frame;
	
	public ErrorDialog(String error) {
		frame = new JFrame();
		frame.setTitle("Connect Four");
    	frame.setSize(275, 100);
    	frame.setResizable(false);
    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout());
    	JLabel msg = new JLabel(error);
    	container.add(msg, BorderLayout.NORTH);
    	JPanel bottom = new JPanel();
    	bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
    	ok = new JButton("Ok");
    	ok.addActionListener(this);
    	bottom.add(ok);
    	container.add(bottom, BorderLayout.SOUTH);
    	frame.add(container);
    	frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			frame.setVisible(false);
		}
	}
	
}
	
}
