package Controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import model.ItemType;
import model.RMOS;
import model.RCM;
import model.Serialize_Deserialize;

@SuppressWarnings("serial")
public class CapabilitiesPanel extends JPanel implements ActionListener {
	private RMOS rmos;
	private ArrayList<ArrayList<JTextField>> rows;
	private RCM selectedRCM;
	private int indexCounter;
	
	public CapabilitiesPanel(RMOS rmos) {
		setLayout(new GridLayout(0,5));
		this.setPreferredSize(new Dimension(600, 300));
		this.rmos = rmos;
		rows = new ArrayList<ArrayList<JTextField>>();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String str = (String) ((JComboBox)e.getSource()).getSelectedItem();
		if (str == null || str.equalsIgnoreCase("All")) 
			return;
		RCM selectedRCM = rmos.getRCMByID(str);		  
		updatePanel(selectedRCM);
	}

	public void updatePanel(RCM selectedRCM){
		if (selectedRCM == this.selectedRCM)
			return;
		this.removeAll();
		rows.clear();
		this.selectedRCM = selectedRCM;
		indexCounter = 0;
		
		JLabel t1 = new JLabel("Item type");
		t1.setForeground(Color.BLUE); 
		
		JLabel t2 = new JLabel("Price for");  
		t2.setForeground(Color.BLUE); 
		
		JLabel t3 = new JLabel("weight (lbs)");  
		t3.setForeground(Color.BLUE); 
		     
		JButton newItem = new JButton("Add item type");
		newItem.enableInputMethods(true);
		newItem.addActionListener(new NewTypeListener());
		
		this.add(t1);
		this.add(t2); 
		this.add(t3); 
		this.add(new JLabel("")); 
		this.add(newItem);
		    
		ArrayList<ItemType> list = selectedRCM.getTypeList();
		
		for (int i = 0; i < list.size(); i++) {
			addRow(list.get(i));
		}
		
		this.revalidate();
		this.repaint();
	}

	private void addRow(ItemType itemType) {
		   JTextField nameField = new  JTextField(1);
		   String name = "";
		   double price = 0;
		   double weight = 0;
		   boolean isNew = itemType == null;
		   
		   if (!isNew) {
			   name = itemType.getType();
			   price = itemType.getPrice();
			   weight = itemType.getWeightInlb();
		   }
		   
		   nameField.setText("" + name);
		   nameField.setHorizontalAlignment(SwingConstants.CENTER);
		   nameField.setEditable(isNew);
		   
		   JTextField priceField = new  JTextField(1); 
		   
		   priceField.setText("" + price); 
		   priceField.setHorizontalAlignment(SwingConstants.CENTER);
		   priceField.setEditable(isNew);
		     
		   JTextField weightField = new  JTextField(1);
  		   
		   weightField.setText("" + weight); 
		   weightField.setHorizontalAlignment(SwingConstants.CENTER);
		   weightField.setEditable(isNew);		   
		   
		   ArrayList<JTextField> list = new ArrayList<JTextField>();
		   
		   rows.add(list);
		   list.add(nameField);
		   list.add(priceField);
		   list.add(weightField);
		   
		   JButton change = new JButton("Change"); 		   
		   change.addActionListener(new ChangeListener(list));
		   JButton save = new JButton("Save"); 		   
		   save.addActionListener(new SaveListener(indexCounter, isNew));
		  		   		   
		   this.add(nameField); 
		   this.add(priceField);  
		   this.add(weightField);
		   this.add(change);
		   this.add(save);		   
		   
		   this.revalidate();
		   this.repaint();
		   setVisible(true); 
		   indexCounter++;
	}
	
	private class ChangeListener implements ActionListener {
		private ArrayList<JTextField> list;
		ChangeListener(ArrayList<JTextField> list) {
			this.list = list;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < list.size(); i++) {
				list.get(i).setEditable(true);
			}
		}			
	}
	
	private class NewTypeListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			CapabilitiesPanel.this.addRow(null);
			
		}			
	}
	
	private class SaveListener implements ActionListener {
		private int index;
		private boolean isNew; 
		
		SaveListener(int index, boolean isNew) {
			this.index = index;
			this.isNew = isNew;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<JTextField> list = rows.get(index);
			try {
				String name = list.get(0).getText();

				if (name.isEmpty())
					throw new NullPointerException();
				
				Double price = Double.parseDouble(list.get(1).getText());
				Double weight = Double.parseDouble(list.get(2).getText());			

				if (isNew) {
					rmos.addNewItemType(selectedRCM, name, price.doubleValue(), weight.doubleValue());
					isNew = false;  // change this to a change listener
				} else {
					rmos.changeItemType(selectedRCM, index, name, price.doubleValue(), weight.doubleValue());
				}
				
				JOptionPane.showMessageDialog(null,
						" \nSuccess! Item Type was successfully saved.", null, JOptionPane.INFORMATION_MESSAGE);
				Serialize_Deserialize.serializeObject(rmos);
				
			} catch (NullPointerException exception) {
				exception.printStackTrace();
				JOptionPane
				.showMessageDialog(
						null,
						"Invalid input! Empty field",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (NumberFormatException exception) {
				JOptionPane
				.showMessageDialog(
						null,
						"Invalid input! Please enter a positive number",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			} catch (IllegalArgumentException exception) {
				JOptionPane
				.showMessageDialog(
						null,
						"Invalid input! Item with duplicated item type name",
						"Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}			
	}
}
