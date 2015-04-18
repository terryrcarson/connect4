package Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.*;

public class GUI extends JPanel implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JFrame frame = new JFrame();
	private String p1Name, p2Name, thisName;
	private BoardGUI boardGUI = new BoardGUI();
	private Timer timer, fallTimer;
	private Client client;
	private Painter painter = new Painter();
	
	public GUI(Client c, String p1Name, String p2Name, String thisName) {
		//painter = new Painting(c, p1Name, p2Name, thisName, frame);
		this.client = c;
		this.p1Name = p1Name;
		this.p2Name = p2Name;
		this.thisName = thisName;
    	frame.setTitle("Connect Four");
    	frame.setSize(600, 450);
    	frame.setResizable(false);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	frame.setGlassPane(boardGUI);
    	boardGUI.setVisible(true);
    	frame.addKeyListener(this);
    	timer = new Timer(250, this);
    	fallTimer = new Timer(12, this);
    	//timer.start();
    	fallTimer.start();
    	frame.add(painter);
    	frame.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!painter.getGameOver()) {
			System.out.println("repainting");
			painter.repaint();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int i = e.getKeyCode();
		
		if ((i == KeyEvent.VK_RIGHT))
		{
			if (!painter.getFalling()) {
				client.sendMsg("MOVE 0");
				frame.repaint();
			}	
		} else if ((i == KeyEvent.VK_LEFT))
		{
			if (!painter.getFalling()) {
				client.sendMsg("MOVE 1");
				frame.repaint();
			}
		} else if ((i == KeyEvent.VK_DOWN))
		{
			if (!painter.getFalling()) {
				client.sendMsg("PLACE");
				frame.repaint();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	public static void main(String args[]) {
		new Intro();
		//new GUI();
	}
	
	class Painter extends JComponent {
		
		private Piece fallPiece = new Piece();
		private Board board = new Board();
		private boolean isFalling = false, fallingOver = true, isGameOver, alreadyRan = false, dontSwitch = false;
		private int fallToRow, cPlayer, fallingCol, response;
		private Object[] options = {"Play again", "Quit"};
		
		public Painter() {
			this.setLayout(null);
			this.setPreferredSize(new Dimension(600, 450));
			isGameOver = false;
		}
		
		public boolean getFalling() {
			return isFalling;
		}
		
		public boolean getGameOver() {
			return isGameOver;
		}
		
		public void reset() {
			isFalling = false;
			fallPiece.resetY();
		}
		@Override
		protected void paintComponent(Graphics g) {
			if (isFalling) {
				super.paintComponent(g);
				if (fallPiece.getY() < (fallToRow * 52) + 80) {
					fallPiece.addToY(4);
					System.out.println(fallPiece.getY());
					switch (cPlayer) {
						case 1:
							drawPieceFalling(g, Color.RED, fallingCol, fallPiece.getY());
							break;
						case 2:
							drawPieceFalling(g, Color.BLACK, fallingCol, fallPiece.getY());
							break;
					}
					for (int x = 0; x < 7; x++) {
						for (int y = 0; y < 6; y++) {
							switch(board.board[x][y].getType()) {
								case 0:
									break;
								case 1:
									drawPiece(g, Color.RED, x, y);
									break;
								case 2:
									drawPiece(g, Color.BLACK, x, y);
									break;
								default:
									System.err.println("Error with drawing checkers");
							}
						}
					}
				} else {
					isFalling = false;
					fallPiece.resetY();
				}
			} else {
				System.out.println("not falling");
				getData(g);
			}
		}
		
		public void drawPieceFalling(Graphics g, Color c, int col, int y) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setColor(c);
			g2.fillOval((col * 75) + 55, y, 35, 35);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Ellipse2D.Double((col * 75) + 55, y, 35, 35));
		}
		
		public void drawPiece(Graphics g, Color c, int col) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setColor(c);
			g2.fillOval((col * 75) + 55, 14, 35, 35);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Ellipse2D.Double((col * 75) + 55, 14, 35, 35));
		}
		
		public void drawPiece(Graphics g, Color c, int col, int row) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			g2.setColor(c);
			g2.fillOval((col * 75) + 55, (row * 52) + 80, 35, 35);
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Ellipse2D.Double((col * 75) + 55, (row * 52) + 80, 35, 35));
		}
		
		
		private void getData(Graphics g) {
			int col, response2 = 0;
			Graphics2D g2 = (Graphics2D) g;
			RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);
	        rh.put(RenderingHints.KEY_RENDERING,
	               RenderingHints.VALUE_RENDER_QUALITY);
	        g2.setRenderingHints(rh);
        	g2.setFont(new Font("Purisa", Font.PLAIN, 13));
			try {
				board.board = client.getBoard();
				col = client.getPieceLoc().getX();
				if (!isGameOver) {
					switch (client.getCurrPlayer()) {
						case 1:
							g2.setColor(Color.BLACK);
							g2.drawString(p1Name + "'s turn", 280, 15);
							g2.setColor(Color.RED);
							break;
						case 2:
							g2.setColor(Color.BLACK);
							g2.drawString(p2Name + "'s turn", 280, 15);
							break;
					}
				} else {
					g2.setColor(Color.BLACK);
					g2.drawString("Game over!", 280, 15);
				}
				switch(cPlayer = client.getCurrPlayer()) {
					case 1:
						drawPiece(g, Color.RED, col);
						break;
					case 2:
						drawPiece(g, Color.BLACK, col);
						break;
				}	
				System.out.println("col " + col);
				for (int x = 0; x < 7; x++) {
					for (int y = 0; y < 6; y++) {
						switch(board.board[x][y].getType()) {
							case 0:
								break;
							case 1:
								drawPiece(g, Color.RED, x, y);
								break;
							case 2:
								drawPiece(g, Color.BLACK, x, y);
								break;
							default:
								System.err.println("Error with drawing checkers");
						}
					}
				}
			} catch (PiecePlacedException ex) {
				try {
					client.readMsg(); 
				} catch (Exception e) {
					client.showDCError(frame);
				}
				fallToRow = ex.getLoc().getY();
				fallingCol = ex.getLoc().getX();
				System.out.println("pieceplaced exception: " + fallToRow + " " + fallingCol);
				isFalling = true;
				fallingOver = false;
				repaint();
			} catch (Exception ex) {
				client.showDCError(frame);
			}
			if (isGameOver) {
				client.sendMsg("DONE");
			}
			if (client.getGameOver() && !isGameOver) {
				System.out.println("Gameover if entered");
				System.out.println("winner found");
				try {
					client.flushSocketStream();
				} catch (Exception e) {
					client.showDCError(frame);
				}
				try {
					System.out.println("already ran: " + alreadyRan);
					switch (client.getWinner()) {
						case 1:
							isGameOver = true;
							//if (!alreadyRan) {
								repaint();
								response2 = JOptionPane.showOptionDialog(frame, p1Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							//}
							alreadyRan = true;
							//new GameOverDialog(p1Name, false, thisName, client, false, prevFrame);
							break;
						case 2:
							isGameOver = true;
							//if (!alreadyRan) {
								repaint();
								response2 = JOptionPane.showOptionDialog(frame, p2Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							//}
							alreadyRan = true;
							//new GameOverDialog(p2Name, false, thisName, client, false, prevFrame);
							break;
						case 3:
							isGameOver = true;
							//new GameOverDialog(p1Name, true, thisName, client, false, prevFrame);
							//if (!alreadyRan) {
								repaint();
								response2 = JOptionPane.showOptionDialog(frame, "Tie game!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
								
							//}
							alreadyRan = true;
							break;
						case 4:
							isGameOver = true;
							alreadyRan = true;
							//if (!alreadyRan)
							response2 = JOptionPane.showOptionDialog(frame, "Your opponent has disconnected!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
							alreadyRan = true;
							client.sendMsg("DONE");
							//new GameOverDialog(p1Name, false, thisName, client, true, prevFrame);
							break;
						default:
							System.out.println("default case");
							alreadyRan = true;
							try {
								switch (Integer.parseInt(client.readMsg())) {
									case 1:
										
										alreadyRan = true;
										repaint();
										response = JOptionPane.showOptionDialog(frame, p1Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
										
										
										//isGameOver = true;
										//new GameOverDialog(p1Name, false, thisName, client, false, prevFrame);
										break;
									case 2:
										alreadyRan = true;
										repaint();
										response = JOptionPane.showOptionDialog(frame, p2Name + " wins!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
										
										
										//isGameOver = true;
										//new GameOverDialog(p2Name, false, thisName, client, false, prevFrame);
										break;
									case 3:
										//new GameOverDialog(p1Name, true, thisName, client, false, prevFrame);
										alreadyRan = true;
										repaint();
										response = JOptionPane.showOptionDialog(frame, "Tie game!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
										
										
										//isGameOver = true;
										break;
									case 4:
										alreadyRan = true;
										response = JOptionPane.showOptionDialog(frame, "Your opponent has disconnected!", "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
										
										//isGameOver = true;
										client.sendMsg("DONE");
										//new GameOverDialog(p1Name, false, thisName, client, true, prevFrame);
										break;
								}
							} catch (Exception e) {
								client.showDCError(frame);
							}
					}
					
				} catch (Exception ex) {
					client.showDCError(frame);
				}
				System.out.println("response was: " + response2);
				if (!dontSwitch) {
					switch (response2) {
						case JOptionPane.YES_OPTION:
							frame.setVisible(false);
							client.sendMsg("REPEAT");
							new MatchMaking(thisName, client);
							client.resetGameOver();
							dontSwitch = true;
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
	}
	
	class Piece {
		 
		private int col, y;
		
		public Piece() {
			y = 14;
		}
		
		public void resetY() {
			y = 14;
		}
		
		public void addToY(int add) {
			y += add;
		}
		
		public int getCol() {
			return col;
		}
		
		public int getY() {
			return y;
		}
		
	}
	
	class BoardGUI extends JComponent {
		
		public BoardGUI() {}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			Area a = new Area(new Rectangle(40, 70, 520, 325));
			for (int x = 0; x < 7; x++) {
				for (int y = 0; y < 6; y++) {
					a.subtract(new Area(new Ellipse2D.Double((x * 75) + 55, (y * 52) + 80, 35, 35)));
				}
			}
			g2.setColor(Color.YELLOW);
			g2.fill(a);
			Stroke noStroke = g2.getStroke();
			g2.setColor(Color.BLACK);
    		g2.setStroke(new BasicStroke(3));
    		g2.draw(a);
			g2.setColor(Color.BLUE);
			Area p1 = new Area(new Rectangle(10, 300, 30, 150));
			Area p2 = new Area(new Rectangle(560, 300, 30, 150));
			g2.fillRect(10, 300, 30, 150);
			g2.fillRect(560, 300, 30, 150);
			g2.setColor(Color.BLACK);
			g2.draw(p1);
			g2.draw(p2);
		}
	}
}

class Intro extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JTextField nameinput;
	private JButton startButton;
	private JFrame frame = new JFrame();
	private Client client;
	
	public Intro() {
		try {
			client = new Client();
			frame.setTitle("Connect Four");
	    	frame.setSize(600, 450);
	    	frame.setResizable(false);
	    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    	frame.setLocationRelativeTo(null);
			setLayout(new BorderLayout());
			JPanel top = new JPanel();
			top.setPreferredSize(new Dimension(400, 250));
			top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));
			nameinput = new JTextField("Enter your name", 20);
			JLabel instructions = new JLabel("<html><center><font face='verdana' size=30>Instructions</font><p><ul><li><font size=3>Move using the left and right arrow keys</li><li><font size=3>Use the down key to place your piece</center></html>");
			startButton = new JButton("Start");
			startButton.addActionListener(this);
			top.add(instructions);
			top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			JPanel bottom = new JPanel();
			bottom.setPreferredSize(new Dimension(400, 60));
			bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
			bottom.add(nameinput);
			bottom.add(startButton);
			bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			add(top, BorderLayout.CENTER);
			add(bottom, BorderLayout.SOUTH);
			frame.add(this);
			frame.setVisible(true);
		} catch (Exception ex) {
			
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		String name = nameinput.getText();
		Pattern pattern1 = Pattern.compile("\\s");
		Matcher matcher = pattern1.matcher(name);
		boolean hasSpaces = matcher.find();
		Pattern pattern2 = Pattern.compile("^[a-zA-Z0-9]*$");
		Matcher matcher2 = pattern2.matcher(name);
		boolean validChars = matcher2.find();
		if (name.length() > 15) {
			JOptionPane.showMessageDialog(frame, "This name is too long.");
		} else if (name.length() == 0) {
			JOptionPane.showMessageDialog(frame, "Your name must be longer.");
		} else if (hasSpaces) {
			JOptionPane.showMessageDialog(frame, "Names cannot have spaces.");
		} else if (!validChars) {
			JOptionPane.showMessageDialog(frame, "Please only use letters and numbers in your name.");
		} else {
			try {
				if (client.isNameTaken(name)) {
					JOptionPane.showMessageDialog(frame, "This name is already taken.");
				} else {
					frame.getContentPane().removeAll();
					client.sendMsg("NAME " + name);
					new MatchMaking(name, client);
					frame.repaint();
					frame.setVisible(false);
				}
			} catch (Exception ex) {
				client.showDCError(frame);
			}
		}
	}
}