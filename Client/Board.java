package Client;

public class Board {
	//ArrayList<Cell> coords = new ArrayList<Cell>();
	private final int EMPTY = 0, RED = 1, BLACK = 2;
	Cell[][] board = new Cell[7][6];
	
	public Board() {
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				board[x][y] = new Cell(x, y);
				board[x][y].setType(0);
			}
		}
	}
	
	public Boolean isTied() {
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				if (board[x][y].getType() == 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void addPiece(int col, int type) {
		for (int y = 5; y >= 0; y--) {
			if (board[col][y].getType() == 0) {
				board[col][y].setType(type);
				break;
			}
		}
	}
	
	public Boolean isColFull(int col) {
		for (int y = 5; y >= 0; y--) {
			if (board[col][y].getType() == 0) {
				return false;
			}
		}
		return true;
	}
	
	public int checkHoriz() {
		for (int y = 0; y < 6; y++) {
			for (int x = 0; x < 4; x++) {
				if (board[x][y].getType() != 0 && board[x][y].getType() == board[x+1][y].getType() && board[x][y].getType() == board[x+2][y].getType() && board[x][y].getType() == board[x+3][y].getType()) {
					return board[x][y].getType();
				}
			}
		}
		return 0;
	}
	
	public int checkVert() {
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 3; y++) {
				if (board[x][y].getType() != 0 && board[x][y].getType() == board[x][y+1].getType() && board[x][y].getType() == board[x][y+2].getType() && board[x][y].getType() == board[x][y+3].getType()) {
					return board[x][y].getType();
				}
			}
		}
		return 0;
	}
	
	public int checkDiag() {
		for (int x = 0; x < 4; x++) {
			for (int y = 0; y < 3; y++) {
				if (board[x][y].getType() != 0 && board[x][y].getType() == board[x+1][y+1].getType() && board[x][y].getType() == board[x+2][y+2].getType() && board[x][y].getType() == board[x+3][y+3].getType()) {
					return board[x][y].getType();
				}
			}
		}
		for (int x = 0; x < 4; x++) {
			for (int y = 5; y > 2; y--) {
				if (board[x][y].getType() != 0 && board[x][y].getType() == board[x+1][y-1].getType() && board[x][y].getType() == board[x+2][y-2].getType() && board[x][y].getType() == board[x+3][y-3].getType()) {
					return board[x][y].getType();
				}
			}
		}
		return 0;
	}
	
	public int checkWinner() {
		if (checkDiag() > 0) {
			return checkDiag();
		} else if (checkHoriz() > 0) {
			return checkHoriz();
		} else if (checkVert() > 0) {
			return checkVert();
		} else if (isTied()) {
			return 3;
		}
		return 0;
	}
	
	public void printCoords() {
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				System.out.println("(" + x + ", " + y + "): " + board[x][y].getType());
			}
		}
	}
}
