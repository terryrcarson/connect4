/**
 * @(#)MatchMaking.java
 *
 *
 * @author 
 * @version 1.00 2015/3/25
 */
import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;

public class MatchMaking extends JPanel implements ActionListener {
	
	private String name;
	JFrame frame = new JFrame();
	Client client;
	JList<String> pList;
	JButton challengeButton;
	Timer timer;
	String challengerName, targetPlayer;
	Boolean challenged = false, isChallenger = false;
	
	public MatchMaking(){}
	
	public MatchMaking(String name, Client client) {
		this.client = client;
		this.name = name;
		//client.sendMsg("NAME " + name);
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
					case "UNAVAIL":
						new ErrorDialog(targetPlayer + " is currently unavailable");
						isChallenger = false;
						break;
					case "NO":
						new ChallengeRejected(targetPlayer);
						isChallenger = false;
						break;
					case "STARTGAME":
						frame.setVisible(false);
						new GUI(client, name, targetPlayer, name);
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
}