package Client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;

public class MatchMaking extends JPanel implements ActionListener {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private JFrame frame = new JFrame();
	private Client client;
	private JList<String> pList;
	private JButton challengeButton;
	private Timer timer;
	private String challengerName, targetPlayer;
	private Boolean challenged = false, isChallenger = false;
	private GlassPane glassPane = new GlassPane();
	
	public MatchMaking() {}
	
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
				frame.setEnabled(false);
				glassPane.setVisible(true);
				isChallenger = true;
				targetPlayer = pList.getSelectedValue();
				//This is a little ugly but it gets the job done...
				new Thread() {
					public void run() {
						try {
							switch(client.challengePlayer(name, targetPlayer)) {
								case "NORESPONSE":
									glassPane.setVisible(false);
									frame.setEnabled(true);
									JOptionPane.showMessageDialog(frame, targetPlayer + " did not respond.");
									isChallenger = false;
									break;
								case "UNAVAIL":
									glassPane.setVisible(false);
									frame.setEnabled(true);
									JOptionPane.showMessageDialog(frame, targetPlayer + " is not available.");
									isChallenger = false;
									break;
								case "NO":
									glassPane.setVisible(false);
									frame.setEnabled(true);
									JOptionPane.showMessageDialog(frame, targetPlayer + " has rejected your challenge.");
									isChallenger = false;
									break;
								case "STARTGAME":
									glassPane.setVisible(false);
									frame.setEnabled(true);
									frame.setVisible(false);
									new GUI(client, name, targetPlayer, name);
									break;
							}
						} catch (Exception ex) {
							frame.setEnabled(true);
							client.showDCError(frame);
						}
					}
				}.start();
			}
		} else if (e.getSource() == timer) {
			if(!challenged && !isChallenger) {
				try {
					if (client.isChallenged()) {
					//System.out.println("Challenge from " + client.getChallenger());
					//new ChallengeDialog(client.getChallenger(), client, frame, name);
					challenged = true;
					int response = JOptionPane.showConfirmDialog(null, "You have been challenged by " + (challengerName = client.getChallenger()) + "!" , "Challenge", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					switch (response) {
						case JOptionPane.YES_OPTION:
							client.sendMsg("OK");
							client.sendMsg("READY");
							if (client.readMsg().equals("OK")) {
								frame.setVisible(false);
								new GUI(client, challengerName, name, name);
							} else {
								JOptionPane.showMessageDialog(frame, "Sorry, you took too long to accept the challenge.");
								challenged = false;
							}
							break;
						case JOptionPane.NO_OPTION:
						case JOptionPane.CLOSED_OPTION:
							client.sendMsg("NO");
							client.readMsg(); //Eat the NO from server
							challenged = false;
							break;
					}
					//challenged = false;
				}
				} catch (Exception ex) {
					client.showDCError(frame);
				}
				try {
					if (!challenged) {
						pList.setModel(client.getAvailPlayers());
					}	
				} catch (Exception ex) {
					client.showDCError(frame);
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
    	frame.setGlassPane(glassPane);
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
    	try {
    		pList = new JList<String>(client.getAvailPlayers());
    	} catch (Exception ex) {
    		client.showDCError(frame);
    	}
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
	
	class GlassPane extends JPanel implements MouseListener, ActionListener {

		private static final long serialVersionUID = 1L;
		private Timer timer = new Timer(1000, this);
		private int countDown = 30;
		private JLabel label = new JLabel("Your opponent has 30 seconds to respond.");
		
		public GlassPane() {
			timer.start();
			this.add(label);
		}
		
		@Override
		public void setVisible(boolean b) {
			if (b) {
				label.setText("Your opponent has 30 seconds to respond.");
				timer.start();
				super.setVisible(true);
			} else {
				countDown = 30;
				timer.stop();
				super.setVisible(false);
			}
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (countDown != 0) {
				countDown--;
				label.setText("Your opponent has " + countDown + " seconds to respond.");
			} else {
				this.setVisible(false);
			}
			
		}
		
	}
}