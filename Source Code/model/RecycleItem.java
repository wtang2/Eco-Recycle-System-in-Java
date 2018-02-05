package model;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * This class represents items the customer recycles.  It holds the items' information: itemType and weight.
 * 
 * @author  Woon Jeen Tang 	
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014
 */
 
public class RecycleItem implements Serializable{
	/**  System generate a serial Version ID; */
	private static final long serialVersionUID = -216726458316195860L;
	
	//data member
	private ItemType itemType;
	private double itemWeight;
	private GregorianCalendar addItemTime;

	
	//constructor with parameters
	public RecycleItem(ItemType itemType, double itemWeight) {		
		this.itemType = itemType;
		this.itemWeight = itemWeight;
		addItemTime = new GregorianCalendar();		
	}
	 	
	//methods
	public ItemType getItemType() {
		return itemType;
	}
	
	public double getItemWeightInlb() {
		return itemWeight;
	}
	
	public double getItemWeightInkg () {
		return itemWeight * 0.4536;
	}
	
	public double getItemPrice() { 
		return itemType.getPrice() / itemType.getWeightInlb() * getItemWeightInlb();
	}
	
	public void setAddItemTime (GregorianCalendar addItemTime) {
		this.addItemTime = addItemTime;
	}
	
	public GregorianCalendar getItemAddTime() {
		return addItemTime;
	}
	
	public String showItemStatusWeightInlb() {
		System.out.println("\t"+ itemType.getType() + "\t"+ getItemWeightInlb() + "lb" + "\t" + "$" + getItemPrice());
		return "\t"+ itemType.getType() + "\t"+ getItemWeightInlb() + "lb" + "\t" + "$" + getItemPrice() + "\n";
	}
	
	public String showItemStatusWeightInkg() {
		System.out.println("\t"+ itemType.getType() + "\t"+ getItemWeightInlb() + "kg" + "\t" + "$" + getItemPrice());
		return "\t"+ itemType.getType() + "\t"+ getItemWeightInlb() + "kg" + "\t" + "$" + getItemPrice() + "\n";
	}
	
	public String toString() {
		String str = "The type of this item is: " + itemType.getType();
		str += "\tItem Weight: " + this.getItemWeightInlb();
		str += "\t Add Time: " + this.getItemAddTime().getTime();
		str += "\n";
		return str;
	} 
}
