package Client;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Painting extends JComponent {
	
	public Client client;
	private final int EMPTY = 0, RED = 1, BLACK = 2;
	private int response;
	private Boolean isGameOver = false;
	private String p1Name, p2Name, thisName;
	private Board board = new Board();
	private JFrame prevFrame;
	private Object[] options = {"Play again", "Quit"};
	
	public Painting() {}
	
	public Painting(Client c, String p1Name, String p2Name, String thisName, JFrame prevFrame) {
		client = c;
		this.prevFrame = prevFrame;
		this.thisName = thisName;
		this.p1Name = p1Name;
		this.p2Name = p2Name;
		isGameOver = false;
	}
	
	public Boolean getGameOver() {
		return isGameOver;
	}
	
	public void resetGameOver() {
		isGameOver = false;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		//Cell board[][] = new Cell[7][6];
		super.paintComponent(g);
		try {
			board.board = client.getBoard();
		} catch (Exception ex) {
			client.showDCError(prevFrame);
		}
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
		try {
			switch (client.getCurrPlayer()) {
				case RED:
					g.setColor(Color.RED);
					break;
				case BLACK:
					g.setColor(Color.BLACK);
					break;
			}
		} catch (Exception ex) {
			client.showDCError(prevFrame);
		}
		try {
			g.fillOval((client.getPieceLoc().getX() * 75) + 55, (client.getPieceLoc().getY() * 52) + 15, 35, 35);
		} catch (Exception ex) {
			client.showDCError(prevFrame);
		}
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
		if (isGameOver) {
			client.sendMsg("DONE");
		}
		if (client.getGameOver() && !isGameOver) {
			System.out.println("Gameover if entered");
			System.out.println("winner found");
			isGameOver = true;
			try {
				switch (client.getWinner()) {
					case 1:
						repaint();
						response = JOptionPane.showOptionDialog(prevFrame, p1Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						//new GameOverDialog(p1Name, false, thisName, client, false, prevFrame);
						break;
					case 2:
						repaint();
						response = JOptionPane.showOptionDialog(prevFrame, p2Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						//new GameOverDialog(p2Name, false, thisName, client, false, prevFrame);
						break;
					case 3:
						//new GameOverDialog(p1Name, true, thisName, client, false, prevFrame);
						repaint();
						response = JOptionPane.showOptionDialog(prevFrame, "Tie game!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						break;
					case 4:
						response = JOptionPane.showOptionDialog(prevFrame, "Your opponent has disconnected!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						client.sendMsg("DONE");
						//new GameOverDialog(p1Name, false, thisName, client, true, prevFrame);
						break;
				}
			} catch (Exception ex) {
				client.showDCError(prevFrame);
			}
			switch (response) {
				case JOptionPane.YES_OPTION:
					prevFrame.setVisible(false);
					client.sendMsg("REPEAT");
					new MatchMaking(thisName, client);
					client.resetGameOver();
					break;
				case JOptionPane.NO_OPTION:
				case JOptionPane.CLOSED_OPTION:
					client.sendMsg("NOREPEAT");
					System.exit(0);
					break;
			}
		}
	}
}

class GameOverDialog implements ActionListener {
	
	JButton ok, quit;
	JFrame frame, prevFrame;
	JLabel msg;
	String thisName;
	final Client client;
	Boolean Dced;
	
	public GameOverDialog(String name, Boolean tieGame, String thisName, final Client client, Boolean Dced, JFrame prevFrame) {
		this.thisName = thisName;
		this.client = client;
		this.Dced = Dced;
		this.prevFrame = prevFrame;
		frame = new JFrame();
		frame.setTitle("Connect Four");
    	frame.setSize(275, 100);
    	frame.setResizable(false);
    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout());
    	if (!tieGame && !Dced) {
    		msg = new JLabel(name + " has won the game!");
    	} else if (tieGame) {
    		msg = new JLabel("Tie game!");
    	} else if (Dced) {
    		msg = new JLabel("Your opponent has disconnected!");
    	}
    	container.add(msg, BorderLayout.NORTH);
    	JPanel bottom = new JPanel();
    	bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
    	ok = new JButton("Play again");
    	quit = new JButton("Quit");
    	quit.addActionListener(this);
    	ok.addActionListener(this);
    	bottom.add(ok);
    	bottom.add(quit);
    	container.add(bottom, BorderLayout.SOUTH);
    	frame.add(container);
    	frame.setVisible(true);
    	Runtime.getRuntime().addShutdownHook(new Thread()
		{
    		@Override
    		public void run()
    		{
        		client.sendMsg("NOREPEAT");
    		}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == ok) {
			prevFrame.setVisible(false);
			frame.setVisible(false);
			client.sendMsg("REPEAT");
			new MatchMaking(thisName, client);
			client.resetGameOver();
			frame.dispose();
		} else if (e.getSource() == quit) {
			System.exit(0);
		}
	}
	
}
