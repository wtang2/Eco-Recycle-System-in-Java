package Controller;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.RCM;
import model.RMOS;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class StatsPanel extends JPanel implements Observer { 
	private JRadioButton[] radioButton, weekDayButtons; 
	private JPanel optionPanel, chartPanel;
	private JTextField hoursTextField;
	private JButton hoursSetButton;
	private boolean hoursSetPressed = false; 
	private String[] commands = { "Empties", "Weight per day",
			"Items collectedByType", "Value per day" };
	private int currentSelection = -1;
	private RMOS rmos; 
	private String chartTitle, chartCategory, chartValue;

	public StatsPanel(RMOS rmos1) {
		this.setLayout(new BorderLayout());
		this.rmos = rmos1;

		generateRadioPanel();

		hoursTextField = new JTextField(5);
		hoursSetButton = new JButton("Set Hours");
		hoursSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hoursSetPressed = true;
				update(rmos, null);
			}
		});

		optionPanel = new JPanel(new GridLayout(0, 2));
		this.add(optionPanel, BorderLayout.CENTER); 
		optionPanel.setVisible(true);

		chartPanel = new JPanel();
		this.add(chartPanel, BorderLayout.PAGE_END);
		chartPanel.setVisible(false);

		weekDayButtons = new JRadioButton[]{new JRadioButton("Day"), new JRadioButton("Week")};
		weekDayButtons[0].setSelected(true);
		ButtonGroup buttonGroup = new ButtonGroup();
		
		for (int i = 0; i < weekDayButtons.length; i++){
			buttonGroup.add(weekDayButtons[i]); 
			weekDayButtons[i].addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					update(rmos, null);
				}
			});
		} 
		this.setVisible(true);
	}

	private void generateRadioPanel() {
		JPanel radioPanel = new JPanel(new GridLayout(0, 1)); 
		radioButton = new JRadioButton[3];
		radioButton[0] = new JRadioButton("Number of times RCM was emptied");
		radioButton[1] = new JRadioButton(
				"Total weight of recycled items by RCM per day"); 
		radioButton[2] = new JRadioButton(
				"Total value (cash or coupons) issued by RCM per day");

		ButtonGroup buttonGroup = new ButtonGroup();

		for (int i = 0; i < 3; i++) {
			radioButton[i].addActionListener(new ActionListener() { 
				@Override
				public void actionPerformed(ActionEvent e) {
					radioButtonChanged(e.getActionCommand());
				}
			});
			radioButton[i].setActionCommand(commands[i]);
			radioPanel.add(radioButton[i]);
			buttonGroup.add(radioButton[i]);
		} 
		this.add(radioPanel, BorderLayout.PAGE_START);
		radioPanel.setVisible(true);
	}

	private void radioButtonChanged(String actionCommand) {
		optionPanel.removeAll();
		chartPanel.removeAll();

		if (actionCommand.equals(this.commands[0])) { // num empties
			currentSelection = 0;
			// text field for number
			hoursTextField.setText("1");
			optionPanel.add(hoursTextField);
			hoursTextField.setVisible(true);
			// set button
			optionPanel.add(hoursSetButton);
			hoursSetPressed = false;
		} else if (actionCommand.equals(this.commands[1])) { // weight
			currentSelection = 1;
			addWeekDayButtons();
		} else if (actionCommand.equals(this.commands[2])) { // collection type  change to value
			currentSelection = 2;	 
		}  
		update(rmos, null); 
		optionPanel.revalidate();
		optionPanel.repaint();
		this.revalidate();
		this.repaint();
	}

	private void addWeekDayButtons() {
		for (int i = 0; i < weekDayButtons.length; i++)
			optionPanel.add(weekDayButtons[i]);
	}

	public void selectRCM(String rcmName) { 
	}

	private void drawChart(DefaultCategoryDataset data, String title, String category, String value) {
		chartPanel.removeAll();
		JFreeChart chart = ChartFactory.createBarChart3D(this.chartTitle,
				category, this.chartValue, data,
				PlotOrientation.VERTICAL, false, // legend?
				true, // tooltips?
				false // URLs?
				);
		ChartPanel panel = new ChartPanel(chart, 400, 400, 100, 100, 500, 500,
				false, false, false, false, true, false);
		chartPanel.add(panel);
		chartPanel.revalidate();
		chartPanel.repaint();
		chartPanel.setVisible(true);
	}

	private void chartEmptiesData() {
		if (!this.hoursSetPressed)
			return;
		try {
			int numHours = Integer.parseInt(this.hoursTextField.getText());

			DefaultCategoryDataset data = new DefaultCategoryDataset();

			ArrayList<RCM> list = rmos.getRCMList();
			
			for (int i = 0; i < list.size(); i++){
				RCM rcm = list.get(i);
				int numEmpties = rmos.getNoOfEmptyTimeInLastNHours(rcm, numHours);
				data.addValue(numEmpties, rcm.getRCMID(), rcm.getRCMID());
			}
			
			drawChart(data, "# of times emptied in " + numHours + " hours", "RCM", "Times emptied");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Please enter a valid integer",
				"Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void chartWeightValueData(boolean isWeight) {
		String duration = "day";
		if (weekDayButtons[0].isSelected())
			duration = "week";
		String valueWeight = "Value";
		int index = 1;
		if (isWeight) {
			valueWeight = "Weight";
			index = 0;
		}
		
		DefaultCategoryDataset data = new DefaultCategoryDataset();
		ArrayList<RCM> list = rmos.getRCMList();
		for (int i = 0; i < list.size(); i++){
			RCM rcm = list.get(i);
			ArrayList<Double> result = rcm.getTotalWeightAndValuePerDay();
			data.addValue(result.get(index), rcm.getRCMID(), rcm.getRCMID());
		} 
		drawChart(data, valueWeight + " Per " + duration, "RCM", "Times emptied");
	}

	@Override
	public void update(Observable o, Object arg) {
		if (currentSelection == -1)
			return;
		switch (currentSelection) {
			case 0: // empties
				chartEmptiesData();
				break;
			case 1: // weight
				chartWeightValueData(true);
				break; 
			case 2: // value
				chartWeightValueData(false);
				break;
		}
	}
}
