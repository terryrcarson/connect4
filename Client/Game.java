package Client;

public class Game {
	
	Board board = new Board();
	public String msg = "";
	private int currPlayer;
	private final int EMPTY = 0, RED = 1, BLACK = 2;
	private Cell currPieceLoc;
	
	public Game() {
		currPlayer = RED;
		currPieceLoc = new Cell(0, 0);
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
}
