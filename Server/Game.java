//package Server;

/**************************************
 * Game
 * - Handles the game logic of Connect 4
 *************************************/

public class Game extends Thread {
	
	private Board board = new Board();
	private String msg = "";
	private int currPlayer;
	private final int RED = 1, BLACK = 2;
	private Cell currPieceLoc;
	private Player player[];
	private Boolean playerDced = false;
	
	/***************************************************************
	 * Constructor
	 * Precondition: Players p1 and p2 are initialized and connected
	 * Postcondition: The game thread is ready to play 
	 ***************************************************************/
	public Game(Player p1, Player p2) {
		currPlayer = RED;
		currPieceLoc = new Cell(0, 0);
		player = new Player[3];
		player[RED] = p1;
		player[BLACK] = p2;
		System.out.println("Thread " + Thread.currentThread().getId() + ": Game thread started");
	}
	
	/***********************************************************************************
	 * run
	 * Precondition: The game has been initialized
	 * Postcondition: When the game is over, the players' Sockets are passed back to the 
	 * 				  Player threads
	 **********************************************************************************/
	@Override
	public void run() {
		System.out.println("Game thread is here");
		String msg;
		while (checkWinner() == 0 && !Thread.currentThread().isInterrupted()) { //While the game is not over and nobody has disconnected
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
			new FinalMessageHandler(player[RED]);
		} else if (player[RED].getDisconnected()) {
			playerDced = true;
			player[BLACK].sendMsg("GAMEOVER 4");
			new FinalMessageHandler(player[BLACK]);
		} else {
			for (int i = 1; i < 3; i++) {
				player[i].sendMsg("GAMEOVER " + checkWinner());
			}
			new FinalMessageHandler(player[BLACK]);
			new FinalMessageHandler(player[RED]);
		}
		System.out.println("Thread " + Thread.currentThread().getId() + ": Game thread terminating");
	}
	
	/***********************************************
	 * getCurrPlayer
	 * Precondition: The game is in progress
	 * Postcondition: The current player is returned
	 **********************************************/
	private int getCurrPlayer() {
		return currPlayer;
	}
	
	/*********************************************************************************************
	 * movePiece
	 * Precondition: The game is in progress and the player moving the piece is the current player
	 * Postcondition: The piece is moved
	 ********************************************************************************************/
	private void movePiece(int dir) {
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
	
	/***********************************************************
	 * switchPlayers
	 * Precondition: The game is in progress
	 * Postcondition: The next player becomes the current player
	 **********************************************************/
	private void switchPlayers() {
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
	
	/**************************************************
	 * gameOver
	 * Precondition: The game is over
	 * Postcondition: The piece's location is invisible
	 *************************************************/
	private void gameOver() {
		currPieceLoc.setX(-1);
		currPieceLoc.setY(-1);
	}
	
	/***********************************************
	 * checkWinner
	 * Precondition: The game is in progress or over
	 * Postcondition: The game's status is returned
	 **********************************************/
	private int checkWinner() {
		if (board.checkDiag() > 0) {
			return board.checkDiag(); //The winner is player 1 or 2
		} else if (board.checkHoriz() > 0) {
			return board.checkHoriz(); //The winner is player 1 or 2
		} else if (board.checkVert() > 0) {
			return board.checkVert(); //The winner is player 1 or 2
		} else if (board.isTied()) {
			return 3; //Game is over and tied
		}
		return 0; //Game is still in progress
	}
	
	/*******************************************************************************
	 * handleMsg
	 * Precondition: The game is in progress and the server and client are connected
	 * Postcondition: The client's request is handled
	 ******************************************************************************/
	public void handleMsg(String msg, int i) {
		int row;
		//System.out.println(msg);
		if (msg.startsWith("MOVE") && currPlayer == i) {
			int dir = Integer.valueOf(msg.substring(5, 6));
			movePiece(dir);
		} else if (msg.startsWith("PLACE") && currPlayer == i) {
			if (board.isColFull(currPieceLoc.getX())) {
			} else {
				row = board.addPiece(currPieceLoc.getX(), currPlayer);
				for (int x = 1; x < 3; x++) {
					player[x].sendMsg("PLACED " + currPieceLoc.getX() + " " + row);
				}
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
	
	/*****************************************************************************************
	 * serializePieceLoc
	 * Precondition: The game is in progress
	 * Postcondition: The piece's location is converted into a string to be sent to the client
	 ****************************************************************************************/
	public String serializePieceLoc() {
		return String.valueOf(currPieceLoc.getX()) + String.valueOf(currPieceLoc.getY());
	}
	
	/******************************************************************************
	 * serializeBoard
	 * Precondition: The game is in progress
	 * Postcondition: The board is converted into a string to be sent to the client
	 *****************************************************************************/
	public String serializeBoard() {
		String msg = "";
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				msg += board.board[x][y].getType();
			}
		}
		return msg;
	}
	
	/*
	 * FinalMessageHandler
	 * - Finishes the game cleanly with each player
	 */
	class FinalMessageHandler extends Thread {
		
		private Player p;
		
		/*******************************************************
		 * Constructor
		 * Precondition: The game is over
		 * Postcondition: The FinalMessageHandler is initialized
		 ******************************************************/
		public FinalMessageHandler(Player p) {
			this.p = p;
			start();
		}
		
		/***************************************************************************************************
		 * run
		 * Precondition: The game is over and the FinalMessageHandler is ready
		 * Postcondition: Each player's final requests are handled and control is returned to Player threads
		 **************************************************************************************************/
		@Override
		public void run() {
			System.out.println("Thread " + Thread.currentThread().getId() + ": final thread started");
			while (!(msg = p.readMsg()).equals("DONE") && (!Thread.currentThread().isInterrupted())) {
				//System.out.println(msg);
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
			if (p.readMsg().equals("REPEAT")) { 
				synchronized (p.syncObj) {
					p.syncObj.notify(); //If they want to play again, wake up the Player thread
				}
			} else {
				p.interrupt(); //Otherwise, kill the Player thread
			}
			System.out.println("Thread " + Thread.currentThread().getId() + ": DONE received, terminating");
			Thread.currentThread().interrupt(); //Terminate this thread
		}
	}
}

