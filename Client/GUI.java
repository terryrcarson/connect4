import java.awt.*;

import javax.swing.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUI extends JPanel implements KeyListener, ActionListener {
	JFrame frame = new JFrame();
	Painting painter;
	Timer timer;
	//Intro intro = new Intro();
	private String p1Name, p2Name, thisName;
	
	public GUI() {}
	
	public GUI(Client c, String p1Name, String p2Name, String thisName) {
		this.p1Name = p1Name;
		this.p2Name = p2Name;
		this.thisName = thisName;
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
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!painter.getGameOver()) {
			frame.repaint();
		} else {
			timer.stop();
		}
		//System.out.println("terminated");
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
		new Intro();
	}
}

class Intro extends JPanel implements ActionListener {
	
	JTextField nameinput;
	JButton startButton;
	JFrame frame = new JFrame();
	Client client = new Client();
	
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
	
	public void actionPerformed(ActionEvent e) {
		String name = nameinput.getText();
		Pattern pattern = Pattern.compile("\\s");
		Matcher matcher = pattern.matcher(name);
		boolean hasSpaces = matcher.find();
		if (name.length() > 15) {
			new ErrorDialog("This name is too long");
		} else if (name.length() == 0) {
			new ErrorDialog("This name is too short");
		} else if (hasSpaces) {
			new ErrorDialog("Names cannot have spaces");
		} else {
			if (client.isNameTaken(name)) {
				new ErrorDialog("This name is already taken");
			} else {
				frame.getContentPane().removeAll();
				client.sendMsg("NAME " + name);
				new MatchMaking(name, client);
				frame.repaint();
				frame.setVisible(false);
			}
			
		}
	}
	
}

/*class MatchMaking extends JPanel implements ActionListener {
	
	private String name;
	JFrame frame = new JFrame();
	Client client = new Client();
	JList<String> pList;
	JButton challengeButton;
	Timer timer;
	String challengerName, targetPlayer;
	Boolean challenged = false, isChallenger = false;
	
	public MatchMaking(){}
	
	public MatchMaking(String name) {
		this.name = name;
		client.sendMsg("NAME " + name);
		initGUI();
		timer = new Timer(5000, this);
		timer.start();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == challengeButton) {
			if (pList.getSelectedValue() == null) {
				//do nothing
			} else {
				isChallenger = true;
				switch(client.challengePlayer(name, (targetPlayer = pList.getSelectedValue()))) {
					case "NO":
						new ChallengeRejected(targetPlayer);
						isChallenger = false;
						break;
					case "STARTGAME":
						frame.setVisible(false);
						new GUI(client, name, targetPlayer);
						break;
				}
			}
		} else if (e.getSource() == timer) {
			if(!challenged && !isChallenger) {
				if (client.isChallenged()) {
					//System.out.println("Challenge from " + client.getChallenger());
					new ChallengeDialog(client.getChallenger(), client, frame, name);
					challenged = true;
					
				}
				if (!challenged) {
					pList.setModel(client.getAvailPlayers());
				}
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void initGUI() {
		frame.setTitle("Connect Four");
    	frame.setSize(600, 450);
    	frame.setResizable(false);
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	setLayout(new BorderLayout());
    	JPanel top = new JPanel();
		top.setPreferredSize(new Dimension(400, 75));
		top.setLayout(new BoxLayout(top, BoxLayout.PAGE_AXIS));
    	JLabel players = new JLabel("Welcome " + name + "!");
    	//JLabel msg = new JLabel("<html><br>Available players:</html>");
    	//players.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    	//msg.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    	top.add(players);
    	//top.add(msg);
    	top.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    	JPanel bottom = new JPanel();
    	bottom.setPreferredSize(new Dimension(400, 50));
    	bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
    	JPanel middle = new JPanel();
    	pList = new JList<String>(client.getAvailPlayers());
    	pList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pList.setVisibleRowCount(12);
        JScrollPane scrollPane = new JScrollPane(pList);
        add(scrollPane);
        pList.setBorder(BorderFactory.createTitledBorder("Available Players"));
        add(pList);
    	//middle.add(pList);
    	middle.setPreferredSize(new Dimension(500, 300));
    	middle.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    	challengeButton = new JButton("Challenge");
    	challengeButton.addActionListener(this);
    	bottom.add(challengeButton);
    	add(top, BorderLayout.NORTH);
    	//add(middle, BorderLayout.CENTER);
    	add(bottom, BorderLayout.SOUTH);
    	frame.add(this);
    	frame.setVisible(true);
    }
}*/

class ChallengeDialog implements ActionListener {
	
	JButton yes;
	JButton no;
	Client client;
	JFrame frame, prevFrame;
	String name, thisPlayerName;
	
	public ChallengeDialog(String name, Client client, JFrame prevFrame, String thisPlayerName) {
		this.name = name;
		this.thisPlayerName = thisPlayerName;
		this.client = client;
		this.prevFrame = prevFrame;
		frame = new JFrame();
		frame.setTitle("Connect Four");
    	frame.setSize(275, 100);
    	frame.setResizable(false);
    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout());
    	JLabel msg = new JLabel("You have been challenged by " + name);
    	container.add(msg, BorderLayout.NORTH);
    	JPanel bottom = new JPanel();
    	bottom.setLayout(new BoxLayout(bottom, BoxLayout.LINE_AXIS));
    	yes = new JButton("Accept");
    	no = new JButton("Reject");
    	yes.addActionListener(this);
    	no.addActionListener(this);
    	bottom.add(yes);
    	bottom.add(no);
    	container.add(bottom, BorderLayout.SOUTH);
    	frame.add(container);
    	frame.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yes) {
			client.sendMsg("OK");
			frame.setVisible(false);
			prevFrame.setVisible(false);
			new GUI(client, name, thisPlayerName, thisPlayerName);
		} else if (e.getSource() == no) {
			client.sendMsg("NO");
			frame.setVisible(false);
		}
		
	}
	
}

class ChallengeRejected implements ActionListener {
	
	JButton ok;
	JFrame frame;
	
	public ChallengeRejected(String name) {
		frame = new JFrame();
		frame.setTitle("Connect Four");
    	frame.setSize(275, 100);
    	frame.setResizable(false);
    	//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.setLocationRelativeTo(null);
    	JPanel container = new JPanel();
    	container.setLayout(new BorderLayout());
    	JLabel msg = new JLabel(name + " has rejected your challenge.");
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