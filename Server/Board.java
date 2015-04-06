package Server;

/******************************************************* 
 * Board
 * - Abstract representation of the Connect 4 game board
 ******************************************************/

public class Board {
	
	Cell[][] board = new Cell[7][6]; 
	
	/***************************************************** 
	 * Constructor
	 * Precondition: N/A
	 * Postcondition: The board is initialized to be empty
	 ****************************************************/
	public Board() {
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				board[x][y] = new Cell(x, y);
				board[x][y].setType(0);
			}
		}
	}
	
	/********************************************************
	 * isTied
	 * Precondition: The board is initialized
	 * Postcondition: Returns whether or not the game is tied
	 *******************************************************/
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
	
	/*************************************************************
	 * addPiece
	 * Precondition: The column that is being added to is not full
	 * Postcondition: The piece is added
	 ************************************************************/
	public void addPiece(int col, int type) {
		for (int y = 5; y >= 0; y--) {
			if (board[col][y].getType() == 0) {
				board[col][y].setType(type);
				break;
			}
		}
	}
	
	/********************************************************************
	 * isColFull
	 * Precondition: The board is initialized
	 * Postcondition: Returns whether or not the requested column is full
	 *******************************************************************/
	public Boolean isColFull(int col) {
		for (int y = 5; y >= 0; y--) {
			if (board[col][y].getType() == 0) {
				return false;
			}
		}
		return true;
	}
	
	/*************************************************************************************
	 * checkHoriz
	 * Precondition: The board is initialized
	 * Postcondition: Returns 0 if there is no horizontal winner, otherwise returns winner
	 ************************************************************************************/
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
	
	/***********************************************************************************
	 * checkVert
	 * Precondition: The board is initialized
	 * Postcondition: Returns 0 if there is no vertical winner, otherwise returns winner
	 **********************************************************************************/
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
	
	/***********************************************************************************
	 * checkDiag
	 * Precondition: The board is initialized
	 * Postcondition: Returns 0 if there is no diagonal winner, otherwise returns winner
	 **********************************************************************************/
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
}
