package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class GUI extends JPanel implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private JFrame frame = new JFrame();
	private Painting painter;
	private Timer timer;
	
	public GUI() {}
	
	public GUI(Client c, String p1Name, String p2Name, String thisName) {
		painter = new Painting(c, p1Name, p2Name, thisName, frame);
    	frame.setTitle("Connect Four");
    	frame.setSize(600, 450);
    	frame.setResizable(false);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	frame.add(painter);
    	frame.addKeyListener(this);
    	timer = new Timer(250, this);
    	timer.start();
    	frame.setVisible(true);
    	painter.resetGameOver();
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!painter.getGameOver()) {
			frame.repaint();
		} else {
			timer.stop();
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int i = e.getKeyCode();
		
		if ((i == KeyEvent.VK_RIGHT))
		{
			painter.client.sendMsg("MOVE 0");
			frame.repaint();
		} else if ((i == KeyEvent.VK_LEFT))
		{
			painter.client.sendMsg("MOVE 1");
			frame.repaint();
		} else if ((i == KeyEvent.VK_DOWN))
		{
			painter.client.sendMsg("PLACE");
			frame.repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	public static void main(String args[]) {
		new Intro();
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
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(name);
		boolean hasSpaces = matcher.find();
		if (name.length() > 15) {
			JOptionPane.showMessageDialog(frame, "This name is too long.");
		} else if (name.length() == 0) {
			JOptionPane.showMessageDialog(frame, "Your name must be longer.");
		} else if (hasSpaces) {
			JOptionPane.showMessageDialog(frame, "Names cannot have spaces.");
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