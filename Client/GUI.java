import java.awt.*;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GUI extends JPanel implements KeyListener, ActionListener {
	JFrame frame = new JFrame();
	Painting painter = new Painting();
	Timer timer;
	//Intro intro = new Intro();
	private Boolean introUp = true;
	
	public GUI() {
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
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		frame.repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		int i = e.getKeyCode();
		/*if (introUp) { 
			frame.getContentPane().removeAll();
	    	frame.add(painter);
	    	frame.repaint();
	    	frame.setVisible(true);
	    	introUp = false;
		} else */
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
	
	/*public static void main(String args[]) {
		GUI gui = new GUI();
	}*/

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	public static void main(String args[]) {
		new GUI();
	}
}

class Intro extends JPanel implements ActionListener {
	
	JTextField nameinput;
	JButton startButton;
	JFrame frame = new JFrame();
	
	public Intro() {
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
		//nameinput.setPreferredSize(new Dimension(40, 100));
		JLabel instructions = new JLabel("<html><center><font face='verdana' size=30>Instructions</font><p><ul><li><font size=3>Move using the left and right arrow keys</li><li><font size=3>Use the down key to place your piece</center></html>");
		//instructions.setPreferredSize(new Dimension(600, 350));
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		top.add(instructions);
		top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JPanel bottom = new JPanel();
		//bottom.add(Box.createRigidArea(new Dimension(50,50)));
		bottom.setPreferredSize(new Dimension(400, 60));
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
		bottom.add(nameinput);
		bottom.add(startButton);
		bottom.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(top, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
		frame.add(this);
		frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String name = nameinput.getText();
		if (name.length() > 15) {
			System.out.println("This name is too long");
		} else if (name.length() == 0) {
			System.out.println("This name is too short");
		} else {
			frame.getContentPane().removeAll();
			frame.add(new GUI());
			frame.repaint();
		}
	}
	
}
