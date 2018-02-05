/**
 * @(#)MainInterface.java
 *  
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014
 * @Description: This class will show the main interface of RMOS
 */

package Controller;

import java.awt.*; 
import java.awt.event.*;
import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import javax.swing.*;

import model.ItemType;
import model.RCM;
import model.RCMStatus;
import model.RMOS;
import model.Serialize_Deserialize;

public class MainInterface extends JFrame implements ActionListener {

	private JPanel selectionPanel, displayPanel, buttonPanel, resultPanel;
	private JLabel j1;// , c2, c4;
	private Container contentPane;
	private JButton logoutButton;
	private JRadioButton[] radioButton;
	private String[] btnText2;
	private JCheckBox[] checkBoxList;
	private final static String[] url = { "./picture/1.png", "./picture/2.png", "./picture/3.png" };
	private static final String PINK = null;
	private JTextArea displayInfo;
	private JLabel sl1, sl2;
	DecimalFormat df = new DecimalFormat("#.##");
	private JMenuItem add;
	private RMOS RMOS1;
	private SelectMachineDropDown machineDropDown;
	private CapabilitiesPanel capabilitiesPanel;
	private StatsPanel statsPanel;

	public MainInterface(RMOS rmos) {
		RMOS1 = rmos;
		machineDropDown = new SelectMachineDropDown(RMOS1);
		RMOS1.addObserver(machineDropDown);
		this.pack();
		setForeground(Color.CYAN);
		setFont(new Font("Dialog", Font.PLAIN, 15));
		setTitle("Welcome to Eco Recycle System (Privilage: Admin)");

		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		createSelectionPanel();
		createDisplayPanel();
		createResultPanel();

		capabilitiesPanel = new CapabilitiesPanel(RMOS1);
		machineDropDown.addActionListener(capabilitiesPanel);

		statsPanel = new StatsPanel(RMOS1);
		RMOS1.addObserver(statsPanel);

		// add the panels to the content pane
		contentPane.add(displayPanel, BorderLayout.PAGE_START);
		contentPane.add(selectionPanel, BorderLayout.CENTER);
		contentPane.add(resultPanel, BorderLayout.PAGE_END);

		JMenuBar menuBar = new JMenuBar();

		// Create a menu
		JMenu menu = new JMenu("File");
		menu.setForeground(Color.BLUE);
		menu.setFont(new Font("Verdana", Font.PLAIN, 16));
		menu.setBounds(6, 6, 90, 21);

		JMenu menu2 = new JMenu("Select an option");
		menu2.setForeground(Color.BLUE);
		menu2.setFont(new Font("Verdana", Font.PLAIN, 16));
		menu.setBounds(12, 6, 90, 21);

		// Create a menu item
		JMenuItem statistics = new JMenuItem("View usage statistics of all RCMs");
		statistics.setForeground(new Color(0, 0, 0));
		statistics.setFont(new Font("Verdana", Font.PLAIN, 16));
		statistics.addActionListener(this);

		JMenuItem exit = new JMenuItem("Log out");
		exit.setFont(new Font("Verdana", Font.PLAIN, 16));
		exit.addActionListener(this);

		add = new JMenuItem("Add and activate RCM");
		add.setIcon(null);
		add.setForeground(new Color(0, 0, 0));
		add.setFont(new Font("Verdana", Font.PLAIN, 16));
		add.addActionListener(this);

		JMenu submenu = new JMenu("Keep track of the status of RCM");
		submenu.setForeground(new Color(0, 0, 0));
		submenu.setFont(new Font("Verdana", Font.PLAIN, 16));

		// add menu to the menu bar
		menuBar.add(menu);
		menuBar.add(menu2);

		// Add the menu item to menu
		menu.add(statistics);
		menu.add(exit);
		menu2.add(add);
		menu2.add(generateMenuItem("Remove RCM"));
		menu2.add(generateMenuItem("Deactivate RCM"));
		menu2.add(generateMenuItem("Reactivate RCM"));
		menu2.add(generateMenuItem("Empty RCM"));
		menu2.addSeparator();
		menu2.add(submenu);
		menu2.addSeparator();
		submenu.add(generateMenuItem("Check the operational status of RCM"));
		submenu.add(generateMenuItem("Check current weight of RCM"));
		submenu.add(generateMenuItem("Check amount of money in RCM"));
		submenu.add(generateMenuItem("Check number of items returned by RCM in a month"));
		submenu.add(generateMenuItem("Show the time RCM was emptied last time"));
		submenu.add(generateMenuItem("Show RCM used most frequently in the last n days"));
		menu2.add(generateMenuItem("Update the capabilities of RCMs"));
		setJMenuBar(menuBar);
		pack();
		setSize(640, 1240); 
		setVisible(true); 
	}

	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if (cmd.equals("Add and activate RCM"))
			add();
		else if (cmd.equals("Remove RCM"))
			remove();
		else if (cmd.equals("Empty RCM"))
			emptyRCM();
		else if (cmd.equals("Deactivate RCM"))
			deactivateRCM();
		else if (cmd.equals("Reactivate RCM"))
			reactivateRCM();
		else if (cmd.equals("Check the operational status of RCM"))
			checkStatus();
		else if (cmd.equals("Check current weight of RCM"))
			checkWeight();
		else if (cmd.equals("Check amount of money in RCM"))
			checkAmount();
		else if (cmd.equals("Check number of items returned by RCM in a month"))
			this.checkNumberItemNDays();
		else if (cmd.equals("Show the time RCM was emptied last time"))
			showTimeLastEmptied();
		else if (cmd.equals("Show RCM used most frequently in the last n days"))
			checkMostFreq();
		else if (cmd.equals("Update the capabilities of RCMs"))
			capabilities();
		else if (cmd.equals("Check the operational status of RCM"))
			checkStatus();
		else if (cmd.equals("View usage statistics of all RCMs")) {
			resultPanel.setVisible(false);
			buttonPanel.removeAll();
			displayPanel.removeAll();
			displayPanel.add(statsPanel);
			displayPanel.revalidate();
			displayPanel.repaint();
		} else if (cmd.equals("Log out")) {
			int n = JOptionPane.showConfirmDialog(null,
					"Exit Eco Recycle System? ", "Log out",
					JOptionPane.YES_NO_OPTION);
			if (n == JOptionPane.YES_OPTION) {
				removeAll();
				setVisible(false);
				AdminLogin si = new AdminLogin(RMOS1);
				si.login();
			}
		}
	} 
	
	private JMenuItem generateMenuItem(String title) {
		JMenuItem result = new JMenuItem(title); 
		result.setForeground(new Color(0, 0, 0));
		result.setFont(new Font("Verdana", Font.PLAIN, 16));
		result.addActionListener(this); 
		return result;
	}

	private void serializeRMOS() {
		Serialize_Deserialize.serializeObject(RMOS1);
	}

	private void createSelectionPanel() {
		selectionPanel = new JPanel(); 
		buttonPanel = new JPanel(new GridLayout(18, 1)); 
		
		JLabel sl1 = new JLabel("Welcome to Eco Recycle System");
		sl1.setHorizontalAlignment(SwingConstants.CENTER);
		sl1.setIcon(null);
		sl1.setForeground(Color.BLUE);
		sl1.setFont(new Font("Serif", Font.PLAIN, 25));

		JLabel sl2 = new JLabel("Please select an option at the menu bar");
		sl2.setHorizontalAlignment(SwingConstants.CENTER);
		sl2.setIcon(null);
		sl2.setForeground(Color.BLUE);
		sl2.setFont(new Font("Serif", Font.PLAIN, 25));

		buttonPanel.add(new JLabel(""));
		buttonPanel.add(sl1);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(sl2);
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		selectionPanel.add(buttonPanel);
		selectionPanel.setBorder(BorderFactory.createEtchedBorder());
		pack();
		setLocationRelativeTo(null);
	}

	private void createDisplayPanel() {
		displayPanel = new JPanel(new FlowLayout());
		pack();
		setLocationRelativeTo(null);
	}

	private void createResultPanel() {
		resultPanel = new JPanel(new FlowLayout());
		resultPanel.setPreferredSize(new Dimension(800, 500));
		pack();
		setLocationRelativeTo(null);
	}
	
	private void reactivateRCM() {
		clearPanels(); 
		displayPanel.removeAll();
		resultPanel.removeAll();
		
		setTitle("Welcome to Eco Recycle System (Remove RCM)");
		JLabel sl1 = new JLabel("Reactivate RCM");
		sl1.setForeground(Color.GREEN);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Reactivate");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to reactivate the RCM.");

		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String str = (String) machineDropDown.getSelectedItem();
					{
						if (str.equalsIgnoreCase("")) {
							JOptionPane.showMessageDialog(MainInterface.this,
									"There are no RCMs to reactivate.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						if (str.equalsIgnoreCase("All")){
							RMOS1.reactivateAllRCM();
							JOptionPane.showMessageDialog(null, " All machines are reactivated successfully from the system");
						} else {
							RMOS1.reactivateRCM(str);
						        JOptionPane.showMessageDialog(null, " Machine " + str
								+ " was reactivated successfully from the system");
						}
						serializeRMOS();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		displayPanel.add(sl1); 
		buttonPanel.add(machineDropDown); 
		machineDropDown.toggleHasAll(true); 
		buttonPanel.add(c5);
		buttonPanel.revalidate();
		buttonPanel.repaint(); 
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}
 
	private void deactivateRCM() {
		clearPanels();
		buttonPanel.removeAll();
		displayPanel.removeAll();
		resultPanel.removeAll();
		
		setTitle("Welcome to Eco Recycle System (Remove RCM)");
		JLabel sl1 = new JLabel("Deactivate RCM");
		sl1.setForeground(Color.RED);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Deactivate");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to deactivate the RCM.");

		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String str = (String) machineDropDown.getSelectedItem();
					{
						if (str.equalsIgnoreCase("")) {
							JOptionPane.showMessageDialog(MainInterface.this,
									"There are no RCMs to deactivate.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						if (str.equalsIgnoreCase("All")) {
							RMOS1.deactivateAllRCM();
							JOptionPane.showMessageDialog(null, " All machines are deactivated successfully from the system");
						} else {
							RMOS1.deactivateRCM(str);
					        	JOptionPane.showMessageDialog(null, " Machine " + str
								+ " was deactivated successfully from the system");
						}
						serializeRMOS();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		displayPanel.add(sl1); 
		buttonPanel.add(machineDropDown);
		machineDropDown.toggleHasAll(true);
		buttonPanel.add(c5);
		buttonPanel.revalidate();
		buttonPanel.repaint();
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}

	private void emptyRCM() {
		buttonPanel.removeAll();
		displayPanel.removeAll();
		resultPanel.removeAll();
		 
		setTitle("Welcome to Eco Recycle System (Remove RCM)");
		JLabel sl1 = new JLabel("Empty RCM");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Empty");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to empty the RCM.");
		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String str = (String) machineDropDown.getSelectedItem(); {
						if (str.equalsIgnoreCase("")) {
							JOptionPane.showMessageDialog(MainInterface.this,
									"There are no RCMs to empty.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						if (str.equalsIgnoreCase("All")) {
							RMOS1.emptyAllRCM();
							JOptionPane.showMessageDialog(null, " All machines are emptied successfully from the system");
						} else	{
							RMOS1.emptyRCM(str);
							JOptionPane.showMessageDialog(null, " Machine " + str
								+ " was emptied successfully from the system");
						}
						serializeRMOS();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		displayPanel.add(sl1);

		buttonPanel.add(machineDropDown);

		machineDropDown.toggleHasAll(true); 
		buttonPanel.add(c5);
		buttonPanel.revalidate();
		buttonPanel.repaint();
		resultPanel.revalidate();
		resultPanel.repaint();
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}


	private void checkMostFreq() { 
		clearPanels();
		setTitle("Welcome to Eco Recycle System (Show RCM used most frequently in the last n days");

		JLabel sl1 = new JLabel(
			"Show RCM used most frequently in the last n days");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		buttonPanel.removeAll();
		displayPanel.removeAll(); 
		displayPanel.add(sl1);
		JLabel s1 = new JLabel("");
		buttonPanel.add((s1));

		setVisible(true);

		JButton sl2 = new JButton("Enter a specific n ");		
		sl2.setForeground(Color.BLUE);
		sl2.setFont(new Font("Serif", Font.PLAIN, 22));
		sl2.setHorizontalAlignment(SwingConstants.CENTER);
		buttonPanel.add(sl2);
		
		final JTextField n = new JTextField(10);		
		n.setFont(new Font("Serif", Font.PLAIN, 16));
		
		final JButton show = new JButton("Show");
		
		sl2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPanel.add(n);
				buttonPanel.add(show);
				show.setForeground(Color.BLUE);
				show.setFont(new Font("Serif", Font.PLAIN, 22));
				show.setHorizontalAlignment(SwingConstants.CENTER);

				buttonPanel.revalidate();
				buttonPanel.repaint();
			}
		});
 
		sl2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPanel.add(n);
				buttonPanel.add(show);
				show.setForeground(Color.BLUE);
				show.setFont(new Font("Serif", Font.PLAIN, 22));
				show.setHorizontalAlignment(SwingConstants.CENTER);

				buttonPanel.revalidate();
				buttonPanel.repaint();
			}
		});

		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (n.getText().isEmpty()) {
					JOptionPane.showMessageDialog(MainInterface.this,
						"Textfield cannot be empty. Please enter a number",
						"Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (Double.parseDouble(n.getText()) <= 0) {
					JOptionPane.showMessageDialog(MainInterface.this,
							"Invalid input! Please enter a positive number",
							"Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				int day = Integer.parseInt(n.getText());
				ArrayList<RCM> useMostRCMList = RMOS1.getUsedMostRCMInNDays(day);
				StringBuilder str = new StringBuilder("");

				if (useMostRCMList == null) {
					JOptionPane.showMessageDialog(null,
						"All the RCMs are not used in the last " + day	+ " days\n");
					return;
				}

				if (useMostRCMList.size() > 1  && useMostRCMList.size() == RMOS1.getRCMList().size()) {
					str.append("All the RCMs are used "
						+ useMostRCMList.get(0).getUseTimesInLastNDay(day)
						+ " times in the last " + day + " days\n");
					str.append("RCM ID and Locations are: \n");

					for (int i = 0; i < useMostRCMList.size(); i++) {
						str.append("ID: " + useMostRCMList.get(i).getRCMID()
								+ "; Location: "
								+ useMostRCMList.get(i).getRCMLocation() + "\n");
					}
					JOptionPane.showMessageDialog(null, str);
					return;

				} else if (useMostRCMList.size() > 1) {
					str.append("There are " + useMostRCMList.size()
						+ " RCMs that are used most in the last " + day	+ " days\n");
					str.append("Number of times used is(are): "
							+ useMostRCMList.get(0).getUseTimesInLastNDay(day) + "\n");
					str.append("RCM ID and Locations are: \n");
					for (int i = 0; i < useMostRCMList.size(); i++) {
						str.append("ID: " + useMostRCMList.get(i).getRCMID()
							+ "; Location: " + useMostRCMList.get(i).getRCMLocation() + "\n");
					}
					JOptionPane.showMessageDialog(null, str);
					return;
				}
				str.append("The ID and Location of RCM that is used most in the last "
						+ day + " days are: \n");
				str.append("ID: " + useMostRCMList.get(0).getRCMID()
						+ "; Location: "
						+ useMostRCMList.get(0).getRCMLocation() + "\n");
				str.append("Number of times used is(are): "
						+ useMostRCMList.get(0).getUseTimesInLastNDay(day) +'\n');
				JOptionPane.showMessageDialog(null, str);
				return;
			} 
		}); 
	}

	private void showTimeLastEmptied() {
		resultPanel.setVisible(false);
		setTitle("Welcome to Eco Recycle System (Show the time RCM was emptied last time");
		buttonPanel.removeAll();
		displayPanel.removeAll();
	
		JLabel c4 = new JLabel("Show the time RCM was emptied last time");
		c4.setFont(new Font("Serif", Font.PLAIN, 30));
		c4.setToolTipText("Click this button to show the time RCM was emptied last time.");
		c4.setForeground(Color.BLUE);

		JButton c5 = new JButton("Submit");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to show the time RCM was emptied last time.");
		c5.setForeground(Color.BLUE);

		displayPanel.add(c4);
		buttonPanel.add(new JLabel());
		buttonPanel.add(machineDropDown);
		machineDropDown.toggleHasAll(true); 
		buttonPanel.add(c5);

		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringBuilder sl6 = new StringBuilder();
				try {
					String str = (String) machineDropDown.getSelectedItem();
					if (str.isEmpty()) {
						JOptionPane.showMessageDialog(MainInterface.this,
								"There are no RCMs.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						ArrayList<RCM> list = RMOS1.getRCMList();

						if (str.equalsIgnoreCase("All")) {							
							for (int i = 0; i < list.size(); i++) {
								sl6.append("Time that the RCM "
										+ list.get(i).getRCMID()
										+ " was last emptied is: "
										+ list.get(i).getLastEmptyTime() + "\n");
							}
						} else {
							for (int i = 0; i < list.size(); i++) {
								if (str.equalsIgnoreCase(list.get(i).getRCMID())) {
									sl6.append("Time that the RCM "
											+ list.get(i).getRCMID()
											+ " was last emptied is: "
											+ list.get(i).getLastEmptyTime()
											+ "\n");
									break;
								}
							}
						}
						JOptionPane.showMessageDialog(MainInterface.this, sl6,
								"Information", JOptionPane.INFORMATION_MESSAGE);
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}

	private void clearPanels() {
		buttonPanel.removeAll();
		buttonPanel.revalidate();
		buttonPanel.repaint(); 
		resultPanel.removeAll();
		resultPanel.revalidate();
		resultPanel.repaint();
	}

	private void checkNumberItemNDays() {
		clearPanels();
		resultPanel.setVisible(false);
		buttonPanel.removeAll();
		displayPanel.removeAll();

		setTitle("Welcome to Eco Recycle System (Check number of recyclable items in RCM (in a month))");
		JLabel sl1 = new JLabel("Check number of items in RCM (in a month)");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));
		sl1.setHorizontalAlignment(SwingConstants.CENTER);
		displayPanel.add(sl1);
		buttonPanel.add(machineDropDown);
		
		JButton sl2 = new JButton("Enter a specific n (days)");		
		sl2.setForeground(Color.BLUE);
		sl2.setFont(new Font("Serif", Font.PLAIN, 22));
		sl2.setHorizontalAlignment(SwingConstants.CENTER);
		buttonPanel.add(sl2);
		
		final JTextField n = new JTextField(10);
		n.setFont(new Font("Serif", Font.PLAIN, 16));
		 
		final JButton c5 = new JButton("Submit");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 17));
		c5.setForeground(Color.BLUE);
		c5.setToolTipText("Click this button to check number of recyclable items in RCM in a month.");
		
		sl2.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				buttonPanel.add(n); 
				buttonPanel.add(c5);
				c5.setForeground(Color.BLUE);
				c5.setFont(new Font("Serif", Font.PLAIN, 22));
				c5.setHorizontalAlignment(SwingConstants.CENTER);

				buttonPanel.revalidate();
				buttonPanel.repaint();
			}
		}); 
		
		machineDropDown.toggleHasAll(true); 
		buttonPanel.add(c5);
		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringBuilder sl6 = new StringBuilder();
				try {
					String str = (String) machineDropDown.getSelectedItem();
					if (str.isEmpty()) {
						JOptionPane.showMessageDialog(MainInterface.this,
								"There are no RCMs.", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					ArrayList<RCM> list = RMOS1.getRCMList();
					int day = Integer.parseInt(n.getText());

					if (str.equalsIgnoreCase("All")) {
						for (int i = 0; i < list.size(); i++) {
							sl6.append("Number of items returned by RCM ("
									+ list.get(i).getRCMID() + ") is: "
									+ list.get(i).getNoOfItemInNDays(day) + "\n");
						}
					} else {
						for (int i = 0; i < list.size(); i++) {
							if (str.equalsIgnoreCase(list.get(i).getRCMID())) {
								sl6.append("Number of items returned by RCM ("
									+ list.get(i).getRCMID() + ") is: "
									+ list.get(i).getNoOfItemInNDays(day)
									+ "\n");
								break;
							}
						}
					}

					JOptionPane.showMessageDialog(MainInterface.this, sl6,
							"Information", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}

	private void checkAmount() {
		resultPanel.setVisible(false);

		setTitle("Welcome to Eco Recycle System (Check amount of money in RCM)");
		JLabel sl1 = new JLabel("\t\tCheck amount of money in RCM");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Submit");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 17));
		c5.setForeground(Color.BLUE);
		c5.setToolTipText("Click this button to check amount of money in RCM.");

		buttonPanel.removeAll();
		displayPanel.removeAll();
		ImageIcon icon = new ImageIcon(
				MainInterface.class.getResource("/gui/images/money2"));
		sl1.setIcon(icon);
		displayPanel.add(sl1);
		buttonPanel.add(machineDropDown);

		machineDropDown.toggleHasAll(true);

		buttonPanel.add(c5);
		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringBuilder sl6 = new StringBuilder();
				try {
					String str = (String) machineDropDown.getSelectedItem();
					if (str.isEmpty()) {
						JOptionPane.showMessageDialog(MainInterface.this,
							"There are no RCMs.", "Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					} else {
						ArrayList<RCM> list = RMOS1.getRCMList();

						if (str.equalsIgnoreCase("All")) {
							for (int i = 0; i < list.size(); i++) {
								sl6.append("Current amount of money in RCM "
									+ list.get(i).getRCMID() + " is: $"
									+ df.format(list.get(i).getCurrMoney()) + "\n");
							}
						} else {
							for (int i = 0; i < list.size(); i++) {
								if (str.equalsIgnoreCase(list.get(i).getRCMID())) {
									sl6.append("Current amount of money in RCM "
										+ list.get(i).getRCMID()
										+ " is: $"
										+ df.format(list.get(i).getCurrMoney()) + "\n");
									break;
								}
							} 
						}

					JOptionPane.showMessageDialog(MainInterface.this, sl6,
							"Information", JOptionPane.INFORMATION_MESSAGE);
				        }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		setVisible(true); 
	}

	private void checkWeight() {
		resultPanel.setVisible(false);
		setTitle("Welcome to Eco Recycle System (Check current weight of RCM)");
		JLabel sl1 = new JLabel("\t\tCheck current weight of RCM");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Check weight");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to check current weight of RCM.");

		buttonPanel.removeAll();
		displayPanel.removeAll();
		ImageIcon icon = new ImageIcon(MainInterface.class.getResource("/gui/images/weight2.jpg"));
		sl1.setIcon(icon);
		displayPanel.add(sl1);
		buttonPanel.add(machineDropDown);

		machineDropDown.toggleHasAll(true); 
		buttonPanel.add(c5);
		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				StringBuilder sl6 = new StringBuilder();
				try {
					String str = (String) machineDropDown.getSelectedItem();
					if (str.isEmpty()) {
						JOptionPane.showMessageDialog(MainInterface.this,
								"There are no RCMs", "Error",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					else {
						ArrayList<RCM> list = RMOS1.getRCMList();
						if (str.equalsIgnoreCase("All")) {
							for (int i = 0; i < list.size(); i++) {
								sl6.append("Current weight of RCM "
										+ list.get(i).getRCMID() + " is: "
										+ list.get(i).getCurrWeightInlb()
										+ " lbs;\t");
								sl6.append("Available weight is: "
										+ (list.get(i).getMaxWeight() - list
												.get(i).getCurrWeightInlb())
										+ " lbs \n");
							}
						} else {
							for (int i = 0; i < list.size(); i++) {
								if (str.equalsIgnoreCase(list.get(i).getRCMID())) {
									sl6.append("Current weight of RCM "
											+ list.get(i).getRCMID() + " is: "
											+ list.get(i).getCurrWeightInlb()
											+ " lbs; \n");
									sl6.append("Available weight is: "
											+ (list.get(i).getMaxWeight() - list
												.get(i).getCurrWeightInlb())
											+ " lbs \n");
									break;
								}
							}
						}

						JOptionPane.showMessageDialog(MainInterface.this, sl6,
								"Information", JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
		setVisible(true);
	}

	// changing or adding new types of recyclable items and changing the price
	private void capabilities() {
		resultPanel.setVisible(false);

		setTitle("Welcome to Eco Recycle System (Update the capabilities of RCM)");
		JLabel sl1 = new JLabel("\t\tUpdate the capabilities of RCMs");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));
		sl1.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel sl2 = new JLabel("\t\tSelect machine");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));
		sl1.setHorizontalAlignment(SwingConstants.CENTER);

		JButton c5 = new JButton("Save");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 17));
		c5.setForeground(Color.BLUE);
		c5.setToolTipText("Click this button to save changes.");

		JButton c6 = new JButton("Add new type");
		c6.setFont(new Font("Serif", Font.PLAIN, 17));
		c6.setForeground(Color.BLUE);

		buttonPanel.removeAll();
		displayPanel.removeAll();
		resultPanel.removeAll();

		buttonPanel.add(sl1);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(sl2);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(machineDropDown);
		machineDropDown.toggleHasAll(false); 
		
		JLabel sl6 = new JLabel("Edit item information below");
		sl6.setForeground(Color.MAGENTA);
		sl6.setFont(new Font("Serif", Font.PLAIN, 22));
		sl6.setHorizontalAlignment(SwingConstants.CENTER);
		resultPanel.add(sl6);
		capabilitiesPanel.setVisible(true);
		capabilitiesPanel.updatePanel(RMOS1.getRCMByID((String) machineDropDown.getSelectedItem()));
		resultPanel.add(capabilitiesPanel);

		setVisible(true);
		resultPanel.setVisible(true);
	}

	private void remove() {
		buttonPanel.removeAll();
		displayPanel.removeAll();
		resultPanel.removeAll(); 
		 
		setTitle("Welcome to Eco Recycle System (Remove RCM)");
		JLabel sl1 = new JLabel("Remove RCM");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JButton c5 = new JButton("Remove");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to remove the RCM.");

		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					String str = (String) machineDropDown.getSelectedItem();
					{
						if (str.equalsIgnoreCase("")) {
							JOptionPane.showMessageDialog(MainInterface.this,
									"There are no RCMs to remove.", "Error",
									JOptionPane.ERROR_MESSAGE);
							return;
						}

						if (str.equalsIgnoreCase("All"))
							RMOS1.removeAllRCM();
						else
							RMOS1.removeRCM(str);
						JOptionPane.showMessageDialog(null, " Machine " + str
								+ " was removed successfully from the system");
						serializeRMOS();
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
 
		ImageIcon icon = new ImageIcon(
				MainInterface.class.getResource("/gui/images/remove.png"));
		sl1.setIcon(icon);
		displayPanel.add(sl1);
		buttonPanel.add(machineDropDown);
		machineDropDown.toggleHasAll(true); 
		resultPanel.add(c5);
		buttonPanel.setVisible(true);
		resultPanel.setVisible(true);
		resultPanel.revalidate();
		resultPanel.repaint();
		displayPanel.revalidate();
		displayPanel.repaint();
		setVisible(true);
	}

	private void add() {
		resultPanel.setVisible(false);
		setTitle("Welcome to Eco Recycle System (Add and activate RCM)");
		JLabel sl1 = new JLabel("\t\tAdd and activate RCM");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 30));

		JLabel sl2 = new JLabel("Enter RCM information ");
		sl2.setForeground(Color.BLUE);
		sl2.setFont(new Font("Serif", Font.PLAIN, 22));
		sl2.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel c1 = new JLabel(" RCM ID:");
		c1.setForeground(new Color(0, 0, 0));
		c1.setFont(new Font("Serif", Font.PLAIN, 16));

		final JTextField c2 = new JTextField(10);
		c2.setFont(new Font("Serif", Font.PLAIN, 16));

		JLabel c3 = new JLabel(" RCM location:");
		c3.setForeground(new Color(0, 0, 0));
		c3.setFont(new Font("Serif", Font.PLAIN, 16));

		final JTextField c4 = new JTextField(10);
		c4.setFont(new Font("Serif", Font.PLAIN, 16));

		JLabel c6 = new JLabel(" RCM max weight:");
		c6.setForeground(new Color(0, 0, 0));
		c6.setFont(new Font("Serif", Font.PLAIN, 16));

		final JTextField c7 = new JTextField(10);
		c7.setFont(new Font("Serif", Font.PLAIN, 16));

		JLabel c8 = new JLabel(" Current money in RCM:");
		c6.setForeground(new Color(0, 0, 0));
		c6.setFont(new Font("Serif", Font.PLAIN, 16));

		final JTextField c9 = new JTextField(10);
		c9.setFont(new Font("Serif", Font.PLAIN, 16));

		JButton c5 = new JButton("Add");
		c5.setSize(5, 5);
		c5.setFont(new Font("Serif", Font.PLAIN, 20));
		c5.setToolTipText("Click this button to add the RCM.");

		c5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					if (!c2.getText().isEmpty() && !c4.getText().isEmpty()
						&& !c7.getText().isEmpty()
						&& !c9.getText().isEmpty()) { 
							if (Double.parseDouble(c7.getText().toString()) > 0
								|| Double.parseDouble(c9.getText().toString()) > 0) {
								if (!RMOS1.addRCM(
									c2.getText(),
									c4.getText(),
									Double.parseDouble(c7.getText().toString()),
									Double.parseDouble(c9.getText().toString()))) {
										JOptionPane
										.showMessageDialog(
											MainInterface.this,
											"Invalid input! The RCM is already monitored by this RMOS (ID duplicated)",
											"Error",
											JOptionPane.ERROR_MESSAGE);
								return;
							}

							StringBuilder c6 = new StringBuilder(
								" \nSuccess! RCM "
								+ c2.getText()
								+ " at location "
								+ c4.getText()
								+ " was successfully added to the system.");
							JOptionPane.showMessageDialog(MainInterface.this,
								c6, null, JOptionPane.INFORMATION_MESSAGE);
							serializeRMOS();
							return;
						} else {
							JOptionPane
								.showMessageDialog(
									MainInterface.this,
									"Invalid input! Please enter a positive number",
									"Error", JOptionPane.ERROR_MESSAGE);
							return;
						}
					} else {
						JOptionPane.showMessageDialog(MainInterface.this,
							"Please enter a valid input", "Error",
							JOptionPane.ERROR_MESSAGE);
						return;
					}
				} 
				catch(NumberFormatException exception) {
					JOptionPane
					.showMessageDialog(
						MainInterface.this,
						"Invalid input! Please enter a positive number",
						"Error", JOptionPane.ERROR_MESSAGE);
				}
				catch (IllegalArgumentException exception) {
					JOptionPane
					.showMessageDialog(
						null,
						"Invalid input! Item with duplicated item type name",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		buttonPanel.removeAll();
		displayPanel.removeAll();
		ImageIcon icon = new ImageIcon(MainInterface.class.getResource("/gui/images/add.png"));
		sl1.setIcon(icon);
		displayPanel.add(sl1);
		buttonPanel.add(sl2);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(c1);
		buttonPanel.add(c2);
		buttonPanel.add(c3);
		buttonPanel.add(c4);
		buttonPanel.add(c6);
		buttonPanel.add(c7);
		buttonPanel.add(c8);
		buttonPanel.add(c9);
		buttonPanel.add(new JLabel(""));
		buttonPanel.add(c5);

		setVisible(true);
	}

	public void checkStatus() {
		resultPanel.setVisible(false);
		setTitle("Welcome to Eco Recycle System (RCM Operational Status)");
		JLabel sl1 = new JLabel("Current Operational RCM Status");
		sl1.setForeground(Color.MAGENTA);
		sl1.setFont(new Font("Serif", Font.PLAIN, 25));
		Font textFont = new Font("Serif", Font.PLAIN, 15);
		buttonPanel.removeAll();
		displayPanel.removeAll();
		resultPanel.removeAll();

		JPanel grid = new JPanel(new GridLayout(0, 6));
		grid.setPreferredSize(new Dimension(600, 200));

		String[] headers = { "ID", "Location", "MaxWeight", "CurrWeight", "CurrMoney","Status" };

		for (int i = 0; i < headers.length; i++) {
			JLabel label = new JLabel(headers[i]);
			label.setFont(textFont);
			label.setForeground(Color.BLUE);
			grid.add(label);
		}

		ArrayList<RCM> list = RMOS1.getRCMList();

		for (int i = 0; i < list.size(); i++) {
			RCM rcm = list.get(i);
			JLabel RCMID = new JLabel(rcm.getRCMID());
			RCMID.setFont(textFont);

			JLabel RCMLocation = new JLabel(rcm.getRCMLocation());
			RCMLocation.setFont(textFont);
			
			JLabel RCMMaxWeight = new JLabel(df.format(rcm.getMaxWeight()) + " lbs");
			RCMMaxWeight.setFont(textFont);
			
			JLabel RCMCurrWeight = new JLabel(df.format(rcm.getCurrWeightInlb())+ " lbs");
			RCMCurrWeight.setFont(textFont);
			
			JLabel RCMCurrMoney = new JLabel("$" + df.format(rcm.getCurrMoney()));
			RCMCurrMoney.setFont(textFont);
			

			RCMStatus status = rcm.getRCMStatus();
			JLabel statusLabel = new JLabel(status.toString());
			statusLabel.setFont(textFont);

			grid.add(RCMID);
			grid.add(RCMLocation);
			grid.add(RCMMaxWeight);
			grid.add(RCMCurrWeight);
			grid.add(RCMCurrMoney);			
			grid.add(statusLabel);

			if (status == RCMStatus.OPERATIONAL) {
				statusLabel.setForeground(Color.GREEN);
			} else {
				statusLabel.setForeground(Color.RED);
			}
		}
		buttonPanel.add(sl1);
		resultPanel.add(grid);
		resultPanel.setVisible(true);
	}
}
