import java.awt.*;

import javax.swing.*;

public class Painting extends JComponent {
	Client client;
	//Game game = new Game();
	private final int EMPTY = 0, RED = 1, BLACK = 2;
	
	public Painting() {}
	
	public Painting(Client c) {
		client = c;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Cell board[][] = new Cell[7][6];
		board = client.getBoard();
		/*for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				System.out.print(board[x][y].getType());
			}
		}*/
		g.setColor(Color.BLACK);
		//g.drawString(game.msg, 225, 35);
		g.setColor(Color.YELLOW);
		g.fillRect(40, 70, 520, 325);
		g.setColor(Color.BLUE);
		g.fillRect(10, 300, 30, 150);
		g.fillRect(560, 300, 30, 150);
		switch (client.getCurrPlayer()) {
			case RED:
				g.setColor(Color.RED);
				break;
			case BLACK:
				g.setColor(Color.BLACK);
				break;
		}
		g.fillOval((client.getPieceLoc().getX() * 75) + 55, (client.getPieceLoc().getY() * 52) + 15, 35, 35);
		for (int x = 0; x < 7; x++) {
			for (int y = 0; y < 6; y++) {
				switch(board[x][y].getType()) {
					case EMPTY:
						g.setColor(getBackground());
						g.fillOval((x * 75) + 55, (y * 52) + 80, 35, 35);
						break;
					case RED:
						g.setColor(Color.RED);
						g.fillOval((x * 75) + 55, (y * 52) + 80, 35, 35);
						break;
					case BLACK:
						g.setColor(Color.BLACK);
						g.fillOval((x * 75) + 55, (y * 52) + 80, 35, 35);
						break;
					default:
						System.err.println("Error with drawing checkers");
				}
			}
		}
	}
}
