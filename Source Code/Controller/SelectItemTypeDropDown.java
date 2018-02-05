package Controller;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import model.ItemType;
import model.RCM;

public class SelectItemTypeDropDown extends JComboBox<String> implements Observer { 
	public SelectItemTypeDropDown(RCM rcm) {
		update(rcm, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		RCM rcm = (RCM) o;

		int index = this.getSelectedIndex();

		ArrayList<ItemType> list = rcm.getTypeList();

		boolean changed = false;

		if (list.size() != this.getComponentCount())
			changed = true;
		else {
			for (int i = 0; i < list.size(); i++) {
				if (!list.get(i).getType().equals(this.getItemAt(i))) {
					changed = true;
					break;
				}
			}
		}

		if (changed) {
			removeAllItems();
			for (int i = 0; i < list.size(); i++) {
				addItem(list.get(i).getType());
			}
			addItem("Others");
			
			if (index >= 0)
				this.setSelectedIndex(index);
		}	

	}
}
