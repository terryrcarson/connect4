
import java.net.*;
import java.io.*;

public class Game extends Thread {
	
	Board board = new Board();
	public String msg = "";
	private int currPlayer;
	private final int RED = 1, BLACK = 2;
	private Cell currPieceLoc;
	private Player player[];
	
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
		while (checkWinner() == 0) {
			//System.out.println(player[currPlayer].readMsg());
			//for each player, check if they sent something and handle it
			for (int i = 1; i < 3; i++) {
				if ((msg = player[i].readMsg()) != null) {
					handleMsg(msg, i);
				}
			}
		}
		for (int i = 1; i < 3; i++) {
			player[i].sendMsg("GAMEOVER " + checkWinner());
		}
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
		msg += "Game over! ";
		switch(checkWinner()) {
			case RED:
				msg += "Red wins!";
				break;
			case BLACK:
				msg += "Black wins!";
				break;
			case 3:
				msg += "Tie game!";
				break;
		}
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
		//System.out.println("Thread " + Thread.currentThread().getId() + ": Player " + i+ ": " + msg);
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
	
	/*public static void main(String args[]) {
		Game g = new Game();
		System.out.println(g.serializeBoard());
		System.out.println(g.serializePieceLoc());
	}*/
}
