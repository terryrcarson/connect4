
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.io.*;

public class Game extends Thread {
	
	Board board = new Board();
	public String msg = "";
	private int currPlayer;
	private final int RED = 1, BLACK = 2;
	private Cell currPieceLoc;
	private Player player[];
	private Boolean playerDced = false;
	
	public Game() {
		currPlayer = RED;
		currPieceLoc = new Cell(0, 0);
	}
	
	public Game(Player p1, Player p2) {
		currPlayer = RED;
		currPieceLoc = new Cell(0, 0);
		player = new Player[3];
		player[RED] = p1;
		player[BLACK] = p2;
		System.out.println("Thread " + Thread.currentThread().getId() + ": Game thread started");
	}
	
	@Override
	public void run() {
		String msg;
		while (checkWinner() == 0 && !Thread.currentThread().isInterrupted()) {
			for (int i = 1; i < 3; i++) {
				if ((msg = player[i].readMsg()) != null) {
					handleMsg(msg, i);
				}
			}
		}
		gameOver();
		if (player[BLACK].getDisconnected()) {
			playerDced = true;
			player[RED].sendMsg("GAMEOVER 4");
			FinalMessageHandler handler2 = new FinalMessageHandler(player[RED]);
		} else if (player[RED].getDisconnected()) {
			playerDced = true;
			player[BLACK].sendMsg("GAMEOVER 4");
			FinalMessageHandler handler1 = new FinalMessageHandler(player[BLACK]);
		} else {
			for (int i = 1; i < 3; i++) {
				player[i].sendMsg("GAMEOVER " + checkWinner());
			}
			FinalMessageHandler handler1 = new FinalMessageHandler(player[BLACK]);
			FinalMessageHandler handler2 = new FinalMessageHandler(player[RED]);
		}
		/*while((!handler1.isInterrupted() && !handler2.isInterrupted()) || (handler1.isInterrupted() && !handler2.isInterrupted()) || (!handler1.isInterrupted() && handler2.isInterrupted())) {
			//do nothing
		}*/
		System.out.println("Thread " + Thread.currentThread().getId() + ": Game thread terminating");
		//player[BLACK].resetBools();
		//player[RED].resetBools();
		
	}
	
	public void setCurrPlayer(int player) {
		currPlayer = player;
	}
	
	public int getCurrPlayer() {
		return currPlayer;
	}
	
	public Cell getPieceLoc() {
		return currPieceLoc;
	}
	
	public void movePiece(int dir) {
		switch (dir) {
		case 0: //Right
			if (currPieceLoc.getX() < 6) {
				currPieceLoc.incX();
			}
			break;
		case 1: //Left
			if (currPieceLoc.getX() > 0) {
				currPieceLoc.decX();
			}
			break;
		}
	}
	
	public void switchPlayers() {
		currPieceLoc.setX(0);
		currPieceLoc.setY(0);
		switch (currPlayer) {
			case RED:
				currPlayer = BLACK;
				break;
			case BLACK:
				currPlayer = RED;
				break;
		}
	}
	
	public void gameOver() {
		currPieceLoc.setX(-1);
		currPieceLoc.setY(-1);
	}
	
	public int checkWinner() {
		if (board.checkDiag() > 0) {
			return board.checkDiag();
		} else if (board.checkHoriz() > 0) {
			return board.checkHoriz();
		} else if (board.checkVert() > 0) {
			return board.checkVert();
		} else if (board.isTied()) {
			return 3;
		}
		return 0;
	}
	
	public Boolean addPiece() {
		if (board.isColFull(currPieceLoc.getX())) {
			System.out.println("This column is full.");
			return false;
		}
		board.addPiece(currPieceLoc.getX(), currPlayer);
		return true;
	}
	
	public void handleMsg(String msg, int i) {
		System.out.println("Thread " + Thread.currentThread().getId() + ": Player " + i+ ": " + msg);
		if (msg.startsWith("MOVE") && currPlayer == i) {
			int dir = Integer.valueOf(msg.substring(5, 6));
			movePiece(dir);
		} else if (msg.startsWith("PLACE") && currPlayer == i) {
			if (board.isColFull(currPieceLoc.getX())) {
			} else {
				board.addPiece(currPieceLoc.getX(), currPlayer);
				switchPlayers();
			}
		} else if (msg.equals("REQUESTCURRPLAYER")) {
			player[i].sendMsg(String.valueOf(getCurrPlayer()));
		} else if (msg.equals("REQUESTBOARD")) {
			player[i].sendMsg(serializeBoard());
		} else if (msg.equals("REQUESTPIECELOC")) {
			player[i].sendMsg(serializePieceLoc());
		} else if (msg.equals("Disconnected")) {
			Thread.currentThread().interrupt();
		} else if (msg.equals("REQUESTWINNER")) {
			if (playerDced) {
				player[i].sendMsg("4");
			} else {
				player[i].sendMsg(String.valueOf(checkWinner()));
			}
		}
	}
	
	public String serializePieceLoc() {
		return String.valueOf(currPieceLoc.getX()) + String.valueOf(currPieceLoc.getY());
	}
	
	public String serializeBoard() {
		String msg = "";
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				msg += board.board[x][y].getType();
			}
		}
		return msg;
	}

	class FinalMessageHandler extends Thread {
		
		private Player p;
		
		public FinalMessageHandler(Player p) {
			this.p = p;
			start();
		}
		
		@Override
		public void run() {
			System.out.println("Thread " + Thread.currentThread().getId() + ": final thread started");
			while (!(msg = p.readMsg()).equals("DONE") && (!Thread.currentThread().isInterrupted())) {
				if (msg.equals("REQUESTCURRPLAYER")) {
					p.sendMsg(String.valueOf(getCurrPlayer()));
				} else if (msg.equals("REQUESTBOARD")) {
					p.sendMsg(serializeBoard());
				} else if (msg.equals("REQUESTPIECELOC")) {
					p.sendMsg(serializePieceLoc());
				} else if (msg.equals("Disconnected")) {
					Thread.currentThread().interrupt();
					System.out.println("d/c");
				} else if (msg.equals("REQUESTWINNER")) {
					if (playerDced) {
						p.sendMsg("4");
					} else {
						p.sendMsg(String.valueOf(checkWinner()));
					}
				}
			}
			synchronized (p.syncObj) {
				p.syncObj.notify();
				
			}
			System.out.println("Thread " + Thread.currentThread().getId() + ": DONE received, terminating");
			Thread.currentThread().interrupt();
		}
	}
}

