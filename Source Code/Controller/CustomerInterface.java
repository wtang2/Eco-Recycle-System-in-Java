/*
 * @(#)RCM.java
 * 
 
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 13/3/2014
 * @Description:  THis Class will show the RCM interface for the user
 */

package Controller;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.ItemType;
import model.RCM;
import model.RCMStatus;
import model.RMOS;
import model.RecycleItem;
import model.Serialize_Deserialize;

public class CustomerInterface extends JFrame implements ActionListener, Observer {

	private static final long serialVersionUID = 1L;
	private JPanel rcmPanel, resultPanel;
	private JLabel j1, l;
	private Container contentPane;
	private JButton list, recycle;
	private JTextField weightField;
	private JTextArea displayInfo;
	private JLabel sl1, sl2;
	private JMenu rcmMenu, menu2;
	private JMenuItem menu;
	private JMenuBar menuBar;
	private boolean showlb = true;
	private boolean showingItemTypes = false;
	DecimalFormat df = new DecimalFormat("#.##");
	private SelectItemTypeDropDown itemTypeDropDown;
	private static RCMStatus OPERATIONAL = RCMStatus.OPERATIONAL;
	private static RCMStatus DOWN = RCMStatus.DOWN;

	RMOS RMOS1;
	RCM RCM1 = null;

	public CustomerInterface(RMOS rmos) {
		RMOS1 = rmos;
		setFont(new Font("Dialog", Font.PLAIN, 15));
		updateTitle();

		setBackground(new Color(250, 250, 210));
		setForeground(Color.CYAN);
		contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setBackground(new Color(250, 250, 210));

		rcmPanel = new JPanel(new GridLayout(0, 1));
		rcmPanel.setBackground(new Color(250, 250, 210));

		resultPanel = new JPanel(new FlowLayout());
		resultPanel.setBackground(new Color(250, 250, 210));

		// add the panels to the content pane 
		contentPane.add(rcmPanel, BorderLayout.NORTH);
		contentPane.add(resultPanel, BorderLayout.CENTER);

		JMenuBar menuBar = new JMenuBar();

		menu = new JMenuItem("Back to main menu");
		menu.setForeground(Color.BLUE);
		menu.setFont(new Font("Verdana", Font.PLAIN, 16));
		menu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainMenu();
			}
		});

		rcmMenu = new JMenu("Select a RCM");
		rcmMenu.setForeground(Color.BLUE);
		rcmMenu.setFont(new Font("Verdana", Font.PLAIN, 16));
		rcmMenu.addActionListener(this);
		
		menuBar.add(menu);      // menuBar.add(selectANewRCM);
		menuBar.add(rcmMenu);

		setJMenuBar(menuBar);
		mainMenu();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setSize(640, 440);

		updateRCMMenu(RMOS1);
		RMOS1.addObserver(this); 
	}

	private void updateRCMMenu(RMOS rmos) {
		ArrayList<RCM> rcms = rmos.getRCMList(); 
		rcmMenu.removeAll();

		for (int i = 0; i < rcms.size(); i++) {
			RCM rcm = rcms.get(i);
			JMenuItem item = new JMenuItem(rcm.getRCMID() + "(" + rcm.getRCMLocation() + ")");
			item.setActionCommand(rcm.getRCMID());
			item.setForeground(Color.BLUE);
			item.setFont(new Font("Verdana", Font.PLAIN, 16));

			item.addActionListener(new ActionListener() { 
				public void actionPerformed(ActionEvent e) {  
					RCM oldRCM = RCM1;
					 
					if (oldRCM != null){
						oldRCM.deleteObserver(CustomerInterface.this);
						RCM1.deleteObserver(itemTypeDropDown); 
					}
					
					RCM1 = RMOS1.getRCMByID(e.getActionCommand());
					
					if (RCM1 == null)
						return;

					//add CustomerInterface that contains this class (new ActionListener()) as RCM1's Observer
					RCM1.addObserver(CustomerInterface.this);
					
					if (!isWork(RCM1)) {
						JOptionPane.showMessageDialog(
							null,
							"The RCM is maintaining. Please select another RCM",
							"Warning", JOptionPane.WARNING_MESSAGE); 
						selectRCMScreen();
						setTitle("RCM (User: customer)");
						return;
					}

					if (RCM1.getTypeList().size() == 0) {
						JOptionPane.showMessageDialog(
							null,
							"Item types have not been defined in this RCM. Please select another RCM",
							"Warning", JOptionPane.WARNING_MESSAGE); 
						selectRCMScreen();
						setTitle("RCM (User: customer)");
						return;
					}

					RCM1 = RMOS1.getRCMByID(e.getActionCommand());

					itemTypeDropDown = new SelectItemTypeDropDown(RCM1);
					RCM1.addObserver(itemTypeDropDown);

					resultPanel.removeAll();
					resultPanel.setVisible(false);
					resultPanel.revalidate();
					resultPanel.repaint();

					if (RCM1.getRCMStatus() == DOWN) {
						JOptionPane.showMessageDialog(
							null,
							"The RCM is maintaining. Please select another RCM",
							"Warning", JOptionPane.WARNING_MESSAGE); 
						selectRCMScreen();
						setTitle("RCM (User: customer)");
						return;
					}
					updateTitle();
					mainMenu(); 
				}
			});
		
			this.rcmMenu.add(item);
			rcmPanel.revalidate();
			rcmPanel.repaint();
		}
	}

	private boolean isWork(RCM rcm) {
		if (rcm == null)
		        return true; 
		if (rcm.getRCMStatus() == DOWN)
			return false;
		return true;
	}

	public void actionPerformed(ActionEvent arg0) {
		String cmd = arg0.getActionCommand();
		if (cmd.equals("RecycleItems"))
			startRecycle();
		if (cmd.equals("showList"))
			showTypeList();
	}

	private void updateTitle() {
		String title = "";
		if (RCM1 != null)
			title += RCM1.getRCMID();
		setTitle("RCM " + title + " (User: customer)");
	}

	public void selectRCMScreen() {
		rcmPanel.removeAll();
		resultPanel.setVisible(false);

		JLabel label = new JLabel("Please select a RCM");
		label.setForeground(Color.BLUE);
		label.setFont(new Font("Verdana", Font.PLAIN, 20));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		rcmPanel.add(label);
		rcmPanel.revalidate();
		rcmPanel.repaint(); 
		rcmPanel.setVisible(true);
	}

	private void mainMenu() {
		this.clearPanels();
		if (RCM1 == null) {
			selectRCMScreen();
		} else { 
			JLabel sl1 = new JLabel("Welcome to Eco Recycle Machine");
			sl1.setHorizontalAlignment(SwingConstants.CENTER);
			sl1.setIcon(null);
			sl1.setForeground(Color.BLUE);
			sl1.setFont(new Font("Serif", Font.PLAIN, 20));
			rcmPanel.add(sl1);

			list = new JButton( "Display list of recyclable item types and the price");
			list.setActionCommand("showList");
			list.setForeground(new Color(0, 0, 0));
			list.setFont(new Font("Verdana", Font.PLAIN, 16));
			rcmPanel.add(list);
			list.setToolTipText("Click this button to display list of recyclable item types and the price.");
			list.addActionListener(this);

			recycle = new JButton("Recycle items");
			recycle.setActionCommand("RecycleItems");
			recycle.setForeground(new Color(0, 0, 0));
			recycle.setFont(new Font("Verdana", Font.PLAIN, 16));
			recycle.setToolTipText("Click this button to recycle items.");
			rcmPanel.add(recycle);
			recycle.addActionListener(this);
		}
		rcmPanel.revalidate();
		rcmPanel.repaint();
		setVisible(true);
	}

	private void showTypeList() { 
		clearPanels(); 
		ArrayList<ItemType> list = RCM1.getTypeList();

		if (list.size() == 0) {
			resultPanel.removeAll();
			resultPanel.setVisible(false);
			JOptionPane
			.showMessageDialog(
					null,
					"Item types have not been defined in this RCM. Please select another RCM",
					"Warning", JOptionPane.WARNING_MESSAGE);
			selectRCMScreen();
			setTitle("RCM (User: customer)");
			showingItemTypes = false;
			return;
		}

		showingItemTypes = true;
		JLabel sl1 = new JLabel("List of recyclable item types and");
		JLabel sl2 = new JLabel("corresponding price by weight are as follows");

		sl1.setForeground(Color.BLUE);
		sl1.setFont(new Font("Verdana", Font.PLAIN, 18));
		sl1.setHorizontalAlignment(SwingConstants.CENTER);

		sl2.setForeground(Color.BLUE);
		sl2.setFont(new Font("Verdana", Font.PLAIN, 18));
		sl2.setHorizontalAlignment(SwingConstants.CENTER);

		resultPanel.add(sl1);
		resultPanel.add(sl2);

		Font textFont = new Font("Serif", Font.PLAIN, 18);
		JPanel grid = new JPanel(new GridLayout(0, 3));
		grid.setPreferredSize(new Dimension(300, 100));

		String[] headers = { "Itemtype", "Price for", "Weight" };

		for (int i = 0; i < headers.length; i++) {
			JLabel label = new JLabel(headers[i]);
			label.setFont(textFont);
			label.setForeground(Color.BLUE);
			grid.add(label);
		}

		for (int i = 0; i < list.size(); i++) {
			ItemType type = list.get(i);
			JLabel typeName = new JLabel(type.getType());
			typeName.setFont(textFont);

			JLabel price = new JLabel("$" + df.format(type.getPrice()));
			price.setFont(textFont);

			JLabel weight = new JLabel();
			if (showlb)
				weight.setText(df.format(type.getWeightInlb()) + " lbs");
			else
				weight.setText((df.format(type.getWeightInkg())) + " kg");

			weight.setFont(textFont);
			grid.add(typeName);
			grid.add(price);
			grid.add(weight);
		}

		JButton show = new JButton();

		if (showlb) {
			show.setText("Show weight in kg");
		} else {
			show.setText("Show weight in lbs");
		}

		show.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				showlb = !showlb;
				showTypeList();
			}

		});

		JButton start = new JButton("Start dropping an item");
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startRecycle();
			}

		});

		resultPanel.add(grid);
		resultPanel.add(show);
		resultPanel.add(start);
		resultPanel.revalidate();
		resultPanel.repaint();

		resultPanel.setVisible(true);

	}

	private void startRecycle() {
		rcmPanel.setVisible(false);
		rcmPanel.removeAll();
		resultPanel.setVisible(false);
		resultPanel.removeAll();
		showingItemTypes = false;
		
		RCM1.clearRecycleItemList(); // Make sure we start with an empty list

		ArrayList<ItemType> list = RCM1.getTypeList();

		if (list.size() == 0) {
			resultPanel.removeAll();
			resultPanel.setVisible(false);
			JOptionPane .showConfirmDialog(null,
				"Item types have not been defined in this RCM. Please select another RCM");
			selectRCMScreen();
			setTitle("RCM (User: customer)");
			return;
		}

		addRecycleItemScreen(false);

	}

	private void addRecycleItemScreen(boolean hasItemAlready) {
		clearPanels();
		JLabel itemSelectHeader = generateFormattedLabel("Select an item type");
		weightField = new JTextField(10);
		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addItemHandler();
			}

		});

		rcmPanel.add(itemSelectHeader);
		rcmPanel.add(itemTypeDropDown);
		rcmPanel.add(generateFormattedLabel("Please enter a weight"));
		rcmPanel.add(weightField);
		rcmPanel.add(submit);

		if (hasItemAlready) {
			JButton checkout = new JButton("Stop Adding Items");
			checkout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					checkoutScreen();
				}
			});
			rcmPanel.add(checkout);
		}
		rcmPanel.revalidate();
		rcmPanel.repaint();
		rcmPanel.setVisible(true);
	}

	private void addItemHandler() {

		try {
			RCM1.addRecycleItem(itemTypeDropDown.getSelectedItem().toString(),
					Double.parseDouble(weightField.getText()));
			int option = JOptionPane
					.showConfirmDialog(
							CustomerInterface.this,
							"Success! \nRecyclable item is accepted\n Do you want to drop next item?",
							null, JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION)
				addRecycleItemScreen(true);
			else
				checkoutScreen();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null,
					"Invalid weight: Need a positive number", "Error",
					JOptionPane.ERROR_MESSAGE);

		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);

		} catch (IllegalStateException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void checkoutScreen() {
		clearPanels();
		resultPanel.setVisible(false);
		JButton AddMoreItemsButton = new JButton("Add More Items");
		AddMoreItemsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				addRecycleItemScreen(true);
			}
		});
 
		String weight = showlb? " kg" : " lbs";
		
		JButton toggleWeightButton = new JButton("Show Itemtype, Weight (in "
				+ weight + ") and price due to you");
				
		toggleWeightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				showlb = !showlb;
				checkoutScreen();
			} 
		});

		JPanel grid = new JPanel(new GridLayout(0, 3));
		grid.setBackground(new Color(250, 250, 210));
		grid = showItemListPanel();

		// Get reward button.
		JButton getReward = new JButton("Get reward");

		getReward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getReward();
			} 
		});

		rcmPanel.add(AddMoreItemsButton);
		rcmPanel.add(toggleWeightButton);
		rcmPanel.add(getReward);
		resultPanel.add(grid);

		rcmPanel.revalidate();
		rcmPanel.repaint();

		resultPanel.revalidate();
		resultPanel.repaint(); 
	}

	protected void getReward() { 
		if (RCM1.valueForUser() <= RCM1.getCurrMoney()) {
			int option = JOptionPane.showConfirmDialog(
                                	CustomerInterface.this,
                                	"Would you like to get money? If not, a coupon with the same value will be given.",
                                	null, JOptionPane.YES_NO_OPTION);

			if (option == JOptionPane.YES_OPTION)
				JOptionPane.showMessageDialog(null,
					"Please collect your money at the receptacle.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
			else
				JOptionPane.showMessageDialog(null,
					"Please collect your coupon at the receptacle.",
					"Information", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(
				null,
				"There is not enough money in the machine. Please collect your coupon at the receptacle",
				"Information", JOptionPane.INFORMATION_MESSAGE); 
		}
		RCM1.getReward();
		this.RMOS1.statusUpdated();
		Serialize_Deserialize.serializeObject(RMOS1);
		mainMenu();
		return;
	}

	private JPanel showItemListPanel() {
		clearPanels();
		ArrayList<RecycleItem> list = RCM1.getUserItemList();
		if (list.size() == 0) {
			resultPanel.removeAll();
			resultPanel.setVisible(false);
			JOptionPane.showConfirmDialog(null,
					"You have not recycled any items"); 
			return null;
		}

		JPanel grid = new JPanel(new GridLayout(0, 3));
		grid.setBackground(new Color(250, 250, 210));
		grid.setPreferredSize(new Dimension(300, 100));
		Font textFont = new Font("Serif", Font.PLAIN, 18);
		String[] headers = { "Itemtype", "Item Weight", "Price" };

		for (int i = 0; i < headers.length; i++) {
			JLabel label = new JLabel(headers[i]);
			label.setFont(textFont);
			label.setForeground(Color.BLUE);
			grid.add(label);
		}

		for (int i = 0; i < list.size(); i++) {
			RecycleItem item = list.get(i);
			JLabel typeName = new JLabel(item.getItemType().getType());
			typeName.setFont(textFont);
			JLabel weight = new JLabel();
			if (showlb)
				weight.setText(df.format(item.getItemWeightInlb()) + " lbs");
			else
				weight.setText(df.format(item.getItemWeightInkg()) + " kg");
			weight.setFont(textFont);
			JLabel price = new JLabel("$" + df.format(item.getItemPrice()));
			price.setFont(textFont);
			grid.add(typeName);
			grid.add(weight);
			grid.add(price); 
		}
		JLabel totalLabel = new JLabel("Total value:");
		totalLabel.setFont(textFont);
		grid.add(totalLabel);

		JLabel totalPrice = new JLabel("$" + df.format(RCM1.valueForUser()));
		totalPrice.setFont(textFont);
		grid.add(totalPrice);

		resultPanel.setVisible(true);
		return grid; 
	}

	private JLabel generateFormattedLabel(String text) {
		JLabel label = new JLabel(text);
		label.setForeground(Color.MAGENTA);
		label.setFont(new Font("Verdana", Font.PLAIN, 22));
		return label;
	}

	private void clearPanels() {
		rcmPanel.removeAll();
		rcmPanel.revalidate();
		rcmPanel.repaint();
		resultPanel.removeAll();
		resultPanel.revalidate();
		resultPanel.repaint();
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof RMOS) {
			RMOS rmos = (RMOS) o;
			updateRCMMenu(rmos);
		}
		if (showingItemTypes)
			this.showTypeList();
	} 
}
