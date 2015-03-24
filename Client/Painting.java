import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Painting extends JComponent {
	Client client;
	//Game game = new Game();
	private final int EMPTY = 0, RED = 1, BLACK = 2;
	private Boolean isGameOver = false;
	private String p1Name, p2Name;
	Board board = new Board();
	
	public Painting() {}
	
	public Painting(Client c, String p1Name, String p2Name) {
		client = c;
		this.p1Name = p1Name;
		this.p2Name = p2Name;
	}
	
	public Boolean getGameOver() {
		return isGameOver;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//Cell board[][] = new Cell[7][6];
		super.paintComponent(g);
		board.board = client.getBoard();
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
				switch(board.board[x][y].getType()) {
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
		if (client.getGameOver() && !isGameOver) {
			System.out.println("Gameover if entered");
			System.out.println("winner found");
			repaint();
			switch (client.getWinner()) {
				case 1:
					new GameOverDialog(p1Name, false);
					break;
				case 2:
					new GameOverDialog(p2Name, false);
					break;
				case 3:
					new GameOverDialog(p1Name, true);
					break;
			}
			isGameOver = true;
		}
	}
}

class GameOverDialog implements ActionListener {
	
	JButton ok;
	JFrame frame;
	JLabel msg;
	
	public GameOverDialog(String name, Boolean tieGame) {
		frame = new JFrame();
		frame.setTitle("Connect Four");
    	frame.setSize(275, 100);
    	frame.setResizable(false);
    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout());
    	if (!tieGame) {
    		msg = new JLabel(name + " has won the game!");
    	} else {
    		msg = new JLabel("Tie game!");
    	}
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
