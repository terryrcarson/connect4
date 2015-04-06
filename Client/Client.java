package Client;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Client {
	
	private final String SERVER_ADDRESS = "localhost"; //Edit this if needed
	private final int PORT_NUMBER = 6664; //Edit this if needed
	private Socket conn;
	private PrintWriter out;
	private BufferedReader in;
	private Boolean isGameOver = false;
	
	public Client() throws Exception {
		try {
			System.out.println("Connecting...");
			conn = new Socket(SERVER_ADDRESS, PORT_NUMBER);
			System.out.println("Connected!");
			out = new PrintWriter(conn.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (Exception e) {
			new ErrorDialog("Unable to connect to server");
			System.err.println("Constructor error:" + e);
			throw new Exception("Unable to connect to server");
		}
	}
	
	public void resetGameOver() {
		isGameOver = false;
	}
	
	public String readMsg() throws ServerDisconnectedException {
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
			throw new RuntimeException("Disconnected from server");
		} catch (SocketException e) {
			isGameOver = true;
			throw new RuntimeException("Disconnected from server");
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
	
	public int getCurrPlayer() throws Exception {
		try {
			sendMsg("REQUESTCURRPLAYER");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			return Character.getNumericValue(msg.charAt(0));
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return 0;
	}
	
	public Cell getPieceLoc() throws Exception {
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
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public Cell[][] getBoard() throws Exception {
		Cell[][] board = new Cell[7][6];
		int i = 0;
		try {
			sendMsg("REQUESTBOARD");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 6; y++) {
					board[x][y] = new Cell(x, y);
					board[x][y].setType(Character.getNumericValue(msg.charAt(i)));
					i++;
				}
			}
			return board;
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public int getWinner() throws Exception {
		try {
			sendMsg("REQUESTWINNER");
			String msg = readMsg();
			if (msg.startsWith("GAMEOVER")) {
				msg = readMsg();
			}
			return Character.getNumericValue(msg.charAt(0));
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (NullPointerException e) {
			isGameOver = true;
		} catch (Exception e) {
			System.err.println(e);
		}
		return 0;
	}
	
	public DefaultListModel<String> getAvailPlayers() throws Exception {
		DefaultListModel<String> players = new DefaultListModel<String>();
		try {
			sendMsg("REQUESTPLAYERS");
			String msg = readMsg();
			StringTokenizer tokenizer = new StringTokenizer(msg, " ");
			while (tokenizer.hasMoreElements()) {
				players.addElement(tokenizer.nextToken());
			}
			return players;
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public String challengePlayer(String thisPlayer, String targetPlayer) throws Exception {
		try {
			sendMsg("CHALLENGE " + thisPlayer + " " + targetPlayer);
			return readMsg();
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public boolean isChallenged() throws Exception {
		try {
			sendMsg("AMICHALLENGED");
			switch (readMsg()) {
				case "YES":
					System.out.println("challenged");
					return true;
					
				case "NO":
					return false;
			}
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}
	
	public String getChallenger() throws Exception {
		try {
			sendMsg("WHOCHALLENGED");
			return readMsg();
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (Exception e) {
			System.err.println(e);
		}
		return null;
	}
	
	public Boolean getGameOver() {
		return isGameOver;
	}
	
	public Boolean isNameTaken(String name) throws Exception {
		try {
			sendMsg("ISNAMETAKEN " + name);
			return Boolean.parseBoolean(readMsg());
		} catch (ServerDisconnectedException e) {
			throw new Exception("Server disconnected");
		} catch (Exception e) {
			System.err.println(e);
		}
		return false;
	}
	
	class ServerDisconnectedException extends Exception {

		private static final long serialVersionUID = 1L;

		public ServerDisconnectedException() {}
	}
	
	public void showDCError(JFrame frame) {
		Object[] options = {"Ok"};
		int n = JOptionPane.showOptionDialog(frame, "You have disconnected from the server.", "Disconnected", JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		switch (n) {
			case JOptionPane.OK_OPTION:
			case JOptionPane.CLOSED_OPTION:
				System.exit(1);
		}
	}
	
	class ErrorDialog implements ActionListener {
	
		JButton ok;
		JFrame frame;
		
		public ErrorDialog(String error) {
			frame = new JFrame();
			frame.setTitle("Connect Four");
	    	frame.setSize(275, 100);
	    	frame.setResizable(false);
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
				System.exit(1);
			}
		}
	}
}
