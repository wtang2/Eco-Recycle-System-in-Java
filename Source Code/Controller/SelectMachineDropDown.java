package Controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.ItemSelectable;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.ItemType;
import model.RCM;
import model.RMOS;

public class SelectMachineDropDown extends JComboBox<String> implements Observer {
	private boolean hasAll; 
	public SelectMachineDropDown(RMOS RMOS1) { 
		hasAll = true; 
		update(RMOS1, null); 
		this.setEditable(false); 
	}
	 
	public void toggleHasAll(boolean on) {
		if (hasAll && !on) {
			int count = this.getItemCount();
			if (count > 0)
			{
				this.removeItemAt(count - 1);
			}
		} else if (!hasAll && on) {
			this.addItem("All");
		} 
		hasAll = on; 	
	}
	
	public void update(Observable o1, Object object) {
		int index = this.getSelectedIndex();
		RMOS RMOS1 =(RMOS)o1;
		ArrayList<RCM> list = RMOS1.getRCMList();
				
		removeAllItems();
				
		for (int i = 0; i < list.size(); i++) {
			addItem(list.get(i).getRCMID());
		}

		if (list.size() != 0 && hasAll)
			 addItem("All");	

		if (index >= 0)
			this.setSelectedIndex(index);
	}	
	
}
