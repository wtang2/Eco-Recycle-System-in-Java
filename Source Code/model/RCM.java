package model;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.io.*;
import java.util.GregorianCalendar;

/**
 * This class represents a RCM (Recycling Machine) that will be used by
 * customers or be monitored by RMOS. 
 * 
 * It has some responsibilities: 
 * 	Knows its ID and location, operational status and capacity (in weight) 
 * 	Knows a list of items that it can accept and price paid for each item 
 * 	Calculates total weight of items and amount of money currently in the machine 
 * 	Knows last time the machine is emptied of items
 * 	Knows the statistical information about usage of itself
 *  
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014
 * 
 */
public class RCM extends Observable implements Serializable {
	/**  System generate a serial Version ID; */
	private static final long serialVersionUID = -4700886223615700982L;
	
	// data member
	private String RCMID;
	private String RCMLocation;
	private double RCMMaxWeight;
	private double RCMInitMoney;
	private double RCMCurrWeight;
	private double RCMCurrMoney;
	private RCMStatus status;
	public transient Reward reward;
	public ArrayList<ItemType> itemTypeList;
	public transient ArrayList<RecycleItem> recycleItemList;
	public ArrayList<RecycleItem> totalItemList;
	public ArrayList<RecycleItem> currentItemList; 
	public ArrayList<GregorianCalendar> emptyTimeList;
	public ArrayList<GregorianCalendar> useTimeList;
	
	// final array. used to get the month in the method "get number of items in a month"
	public static final String [] Month = {"JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY","JUNE",
		"JULY", "AUGUST", "SEPTEMBER", "OCTOBER","NOVEMBER", "DECEMBER"};

	// constructor
	public RCM(String RCMID, String RCMLocation, double RCMMaxWeight, double RCMInitMoney) {
		this.RCMID = RCMID;
		this.RCMLocation = RCMLocation;
		this.RCMMaxWeight = RCMMaxWeight;
		this.RCMInitMoney = RCMInitMoney;
		RCMCurrMoney = RCMInitMoney;
		RCMCurrWeight = 0;
		status = RCMStatus.OPERATIONAL;
		itemTypeList = new ArrayList<ItemType>();
		recycleItemList = new ArrayList<RecycleItem>();
		currentItemList = new ArrayList<RecycleItem>();
		totalItemList = new ArrayList<RecycleItem>();
		emptyTimeList = new ArrayList<GregorianCalendar>();
		useTimeList = new ArrayList<GregorianCalendar>();
	}
	
	//default no-argument constructor for serialize
	public RCM() {}
	
	// methods
	public String getRCMID() {
		return RCMID;
	}

	public String getRCMLocation() {
		return RCMLocation;
	}

	public double getMaxWeight() {
		return RCMMaxWeight;
	}

	public double getRCMInitMoney() {
		return RCMInitMoney;
	}

	public RCMStatus getRCMStatus() {
		return status;
	}

	public void setRCMstatus(RCMStatus status) {
		this.status = status;
	}

	public boolean aboveZero(double num) {
		if (num <= 0)
			return false;
		return true;
	}

	public void addItemType(String typeName,
			double itemPrice, double weightForPrice) throws IllegalArgumentException, NumberFormatException {

		if ((!aboveZero(itemPrice)) || (!aboveZero(weightForPrice))) {
			throw new NumberFormatException("itemType " + typeName + ": "
				+ "itemPrice and weight must be greater than zero.");
		}

		int i = indexOfItemTypeByName(typeName);
		
		if (i != -1)	{
			throw new IllegalArgumentException("Itemtype "	+ typeName
				+ " exists in the ItemTypeList. You can click Change button to change its price by weight.");					
		}	
		
		ItemType itemType = new ItemType(typeName, itemPrice, weightForPrice);
		itemTypeList.add(itemType);
		setChanged();
		notifyObservers();
	}
	
	public void changeItemType(int index, String typeName,
		double itemPrice, double weightForPrice) throws IllegalArgumentException, NumberFormatException {
			if ((!aboveZero(itemPrice)) || (!aboveZero(weightForPrice))) {
				throw new NumberFormatException("itemType " + typeName + ": "
					+ "itemPrice and weight must be greater than zero.");
		}
		
		int i = indexOfItemTypeByName(typeName);
		
		if (i != -1 && i != index) {
			throw new IllegalArgumentException("Itemtype " + typeName
				+ " exists in the ItemTypeList. You can click Change button to change its price by weight.");			
		}	
		
		ItemType itemType = itemTypeList.get(index);
		itemType.setType(typeName);
		itemType.setPrice(itemPrice);
		itemType.setWeight(weightForPrice);
		
		setChanged();
		notifyObservers();
	}

	private int indexOfItemTypeByName(String typeName) {
		int i = -1;
		for (int j = 0; j < itemTypeList.size(); j++) {
			if (typeName.equalsIgnoreCase(itemTypeList.get(j).getType())) {
				i = j;
				break;
			}
		}
		return i;
	} 

	/**
	 * Display the list of recyclable item types and the price paid for each of
	 * the item types by weight.
	 */
	public String showTypeListWeightInlb() {
		String typeList = "";
		if (itemTypeList.size() == 0) {
			System.out.println("There are no defined itemtypes");
			return "There are no defined itemtypes";
		}

		int i;
		System.out.println("List of itemtypes and price by weight in lb:");
		System.out.println("\t" + "typeName" + "\t" + "price " + "for "
				+ "weight(lb)");

		for (i = 0; i < itemTypeList.size(); i++) {
			typeList += itemTypeList.get(i).showTypeStatusWeightInlb();
		}
		return typeList;
	}

	public String showTypeListWeightInkg() {
		String typeList = "";
		if (itemTypeList.size() == 0) {
			System.out.println("There are no defined itemtypes");
			return "There are no defined itemtypes";
		}

		int i;
		System.out.println("List of itemtypes and price by weight in kg:");
		System.out.println("\t" + "typeName" + "\t" + "price " + "for "
				+ "weight(kg)");

		for (i = 0; i < itemTypeList.size(); i++) {
			typeList += itemTypeList.get(i).showTypeStatusWeightInkg();
		}
		return typeList;
	}

	/**
	 * This method is used to get one type in the exist itemTypeList and change it.
	 * @return itemTypeList
	 */
	public ArrayList<ItemType> getTypeList() {
		
		return itemTypeList;
	}
	
	public ArrayList<RecycleItem> getRCMItemList() {
		return totalItemList;
	}

	public String getTypeName (ArrayList<ItemType> itemTypeList) {
		int i; 
		String typeName = "";
		for (i = 0; i < itemTypeList.size(); i++) {
			typeName += itemTypeList.get(i).getType() + ":";
		}
		return typeName + "All";
	}
	
	public ItemType getItemType(String typeName) {
		int i;
		for (i = 0; i < itemTypeList.size(); i++) {
			if (typeName.equalsIgnoreCase(itemTypeList.get(i).getType()))
				return itemTypeList.get(i);
		}
		return null;
	}

	public boolean RCMisFull(double itemWeight) {
		if (RCMCurrWeight + itemWeight > RCMMaxWeight)
			return true;
		else
			return false;
	}
	
	public double RCMAvailableSpace() {
		return RCMMaxWeight - RCMCurrWeight;
	}

	/**
	 * 
	 * If user drops an item successfully, add this item to recycleItemList for get reward;
	 * Meanwhile, add this item to totalItemList for calculating total # of items in terms of itemtype.
	 * 
	 * @param typeName
	 * @param itemWeight
	 */
	public void addRecycleItem(String typeName, double itemWeight) throws IllegalArgumentException, NumberFormatException, IllegalStateException {

		ItemType itemType = getItemType(typeName);

		if (!aboveZero(itemWeight)) {
			throw new NumberFormatException("Item weight must be greater than zero");
		}

		// Does not allow an item that is not permitted and shown as a recyclable item
		int index = this.indexOfItemTypeByName(typeName);
		if (index == -1) {
			throw new IllegalArgumentException("Item type " + typeName + " is not acceptable");			
		}
		
		// Does not allow an item if RCM is full
		else if (RCMisFull(itemWeight)) {	
			DecimalFormat df = new DecimalFormat("#.##");
			throw new IllegalStateException("RCM has only " + df.format(RCMAvailableSpace()) + " lbs of space left. Please insert the item into other RCM");
		}
		
		RecycleItem recycleItem = new RecycleItem(itemType, itemWeight);
		recycleItem.setAddItemTime(new GregorianCalendar());
		recycleItemList.add(recycleItem);
		currentItemList.add(recycleItem);
		totalItemList.add(recycleItem);
		RCMCurrWeight += recycleItem.getItemWeightInlb();		
	}

	public String showItemListWeightInlb() {
		int i;
		if (recycleItemList.size() == 0) {
			System.out.println("There are no items in the list");
			return "There are no items in the list";
		}
		String itemList = "";
		System.out.println("List of recycleItem's type, weight(lb) and price due to the customer:");
		System.out.println("\t" + "itemType" + "\t" + "itemWeight " + "\t"
				+ "itemPrice");
		for (i = 0; i < recycleItemList.size(); i++) {
			itemList += recycleItemList.get(i).showItemStatusWeightInlb();
		}
		
		System.out.println("The total value due to the customer is: $"
				+ valueForUser());
		return itemList;
	}

	public String showItemListWeightInkg() {
		int i;
		if (recycleItemList.size() == 0) {
			System.out.println("There are no items in the list");
			return "There are no items in the list";
		}
		String itemList = "";
		System.out.println("List of recycleItem's type, weight(kg) and price due to the customer:");
		System.out.println("\t" + "itemType" + "\t" + "itemWeight " + "\t"
				+ "itemPrice");
		for (i = 0; i < recycleItemList.size(); i++) {
			itemList += recycleItemList.get(i).showItemStatusWeightInkg();
		}
		System.out.println("The total value due to the customer is: $"
				+ valueForUser());
		return itemList;
	}

	public ArrayList<RecycleItem> getUserItemList() {
		return recycleItemList;
	}
	
	public ArrayList<RecycleItem> getTotalItemList() {
		return totalItemList;
	}

	public double valueForUser() {
		int i;
		double valueForUser = 0;
		for (i = 0; i < recycleItemList.size(); i++) {
			valueForUser += recycleItemList.get(i).getItemPrice();
		}
		return valueForUser;
	}

	public double getCurrMoney() {
		return RCMCurrMoney;
	}

	public double getCurrWeightInlb() {
		return RCMCurrWeight;
	}

	public double getCurrWeightInkg() {
		return RCMCurrWeight * 0.4536;
	}

	public Reward getReward() {
		if (valueForUser() <= RCMCurrMoney) {
			RCMCurrMoney -= valueForUser();
			GregorianCalendar useTime = new GregorianCalendar();
			useTimeList.add(useTime);
			return new Money(valueForUser());
		} else {
			GregorianCalendar useTime = new GregorianCalendar();
			useTimeList.add(useTime);
			System.out.println("There is not enough money in the RCM");
			return new Coupon(valueForUser());
		}
	}

	/**
	 * After a customer using RCM, recycleItemList needs to be set to null.
	 */
	public void clearRecycleItemList() {
		if (recycleItemList == null)
			recycleItemList = new ArrayList<RecycleItem>();
		else
			recycleItemList.clear();
	}

	public void emptyRCM() {
		GregorianCalendar emptyTime = new GregorianCalendar();
		System.out.println("The emptyTime is: " + emptyTime.getTime());
		emptyTimeList.add(emptyTime);
		currentItemList.clear();
		RCMCurrWeight = 0;
	}

	public int getNoOfItemInNDays(int n) {
		GregorianCalendar lastNDays = new GregorianCalendar();
		lastNDays.add(GregorianCalendar.DATE, -n);
		int counter = 0;
		
		for(int i = 0; i < this.totalItemList.size(); i++) {
			boolean isAfter = totalItemList.get(i).getItemAddTime().getTime().after(lastNDays.getTime());
			if (isAfter) {
				counter++;
			}
			System.out.println("itemaddtime: " + (totalItemList.get(i).getItemAddTime().getTime()).toString());
			System.out.println("1 day before:" + (lastNDays.getTime()).toString());
			System.out.println(isAfter);
		}
		return counter;
	}

	
	public Date getLastEmptyTime() {
		int last = this.emptyTimeList.size();
		if (last == 0) 
			return null;
		return emptyTimeList.get(last - 1).getTime();
	}
	
	public int getNoOfEmptyTimeInLastNHours(int n) {
		GregorianCalendar lastNHour = new GregorianCalendar();
		lastNHour.add(GregorianCalendar.HOUR, -n);
		int counter = 0;
		
		for(int i = 0; i < this.emptyTimeList.size(); i++) {
			if (emptyTimeList.get(i).getTime().after(lastNHour.getTime())) {
				counter++;
			}
		}
		System.out.println("The RCM: " + this.getRCMID() + "  was emptied " + counter + " times in the last " + n + " days.");
		return counter;
	}
	
	public int getUseTimesInLastNDay(int n) {
		GregorianCalendar lastNDay = new GregorianCalendar();
		lastNDay.add(GregorianCalendar.DATE, -n);
		int counter = 0;
		
		for(int i = 0; i < this.useTimeList.size(); i++) {
			if (useTimeList.get(i).getTime().after(lastNDay.getTime())) {
				counter++;
			}
		}
		return counter;
	}
	
	public int getNoOfItemByType(String typeName) {
		int counter = 0;
		for (int i = 0; i < this.getTotalItemList().size(); i++){
			if (typeName.equalsIgnoreCase(this.getTotalItemList().get(i).getItemType().getType())) {
				counter++;
			}			
		}
		return counter;
	}
	
	public ArrayList<Double> getTotalWeightAndValuePerDay() {
		ArrayList<Double> list = new ArrayList<Double>();
		double totalWeight = 0;
		double totalValue = 0;
		int diffDays = 1;
		
		if (totalItemList.size() == 0) {
			System.out.println("The RCM: " + this.getRCMID() + " has not recycled items.");
			list.add(0.0);
			list.add(0.0);
			return list;
		}
		
		for (int i = 0; i < this.totalItemList.size(); i++) {
			totalWeight += totalItemList.get(i).getItemWeightInlb();
			totalValue += totalItemList.get(i).getItemPrice();
		}
		
		GregorianCalendar startDate = totalItemList.get(0).getItemAddTime();
		GregorianCalendar endDate = totalItemList.get(totalItemList.size() - 1).getItemAddTime();
		GregorianCalendar tempStart = (GregorianCalendar)startDate.clone();
		GregorianCalendar tempEnd = (GregorianCalendar)endDate.clone();
		
		while (tempStart.get(GregorianCalendar.YEAR) != tempEnd.get(GregorianCalendar.YEAR) || tempStart.get(GregorianCalendar.DAY_OF_YEAR) != tempEnd.get(GregorianCalendar.DAY_OF_YEAR)) {
			diffDays++;
			tempStart.add(GregorianCalendar.DATE, 1);
		}
		
		list.add(totalWeight/diffDays);
		list.add(totalValue/diffDays);
		return list;
	}
	
	public ArrayList<Double> getTotalWeightAndValuePerWeek() {
		ArrayList<Double> list = new ArrayList<Double>();
		double totalWeight = 0;
		double totalValue = 0;
		int diffDays = 1;
		
		if (totalItemList.size() == 0) {
			System.out.println("The RCM: " + this.getRCMID() + " has not recycled items.");
			return null;
		}
		
		for (int i = 0; i < this.totalItemList.size(); i++) {
			totalWeight += totalItemList.get(i).getItemWeightInlb();
			totalValue += totalItemList.get(i).getItemPrice();
		}
		
		GregorianCalendar startDate = totalItemList.get(0).getItemAddTime();
		GregorianCalendar endDate = totalItemList.get(totalItemList.size() - 1).getItemAddTime();
		GregorianCalendar tempStart = (GregorianCalendar)startDate.clone();
		GregorianCalendar tempEnd = (GregorianCalendar)endDate.clone();
		
		while (tempStart.get(GregorianCalendar.YEAR) != tempEnd.get(GregorianCalendar.YEAR) || tempStart.get(GregorianCalendar.DAY_OF_YEAR) != tempEnd.get(GregorianCalendar.DAY_OF_YEAR)) {
			diffDays++;
			tempStart.add(GregorianCalendar.DATE, 1);
		}
	
		// In order to get ceiling, integer / integer must be avoided. That is, dividend or  divisor must be double type. 
		int week = (int)Math.ceil(diffDays / 7.0);
		list.add(totalWeight/week);
		list.add(totalValue/week);
		return list;	
	}
	
	public ArrayList<Double> getTotalWeightAndValuePerMonth() {
		ArrayList<Double> list = new ArrayList<Double>();
		double totalWeight = 0;
		double totalValue = 0;
		int diffDays = 1;
		
		if (totalItemList.size() == 0) {
			System.out.println("The RCM: " + this.getRCMID() + " has not recycled items.");
			return null;
		}
		
		for (int i = 0; i < this.totalItemList.size(); i++) {
			totalWeight += totalItemList.get(i).getItemWeightInlb();
			totalValue += totalItemList.get(i).getItemPrice();
		}
		
		GregorianCalendar startDate = totalItemList.get(0).getItemAddTime();
		GregorianCalendar endDate = totalItemList.get(totalItemList.size() - 1).getItemAddTime();
		GregorianCalendar tempStart = (GregorianCalendar)startDate.clone();
		GregorianCalendar tempEnd = (GregorianCalendar)endDate.clone();
		
		while (tempStart.get(GregorianCalendar.YEAR) != tempEnd.get(GregorianCalendar.YEAR) || tempStart.get(GregorianCalendar.DAY_OF_YEAR) != tempEnd.get(GregorianCalendar.DAY_OF_YEAR)) {
			diffDays++;
			tempStart.add(GregorianCalendar.DATE, 1);
		}
	
		//In order to get ceiling, integer / integer must be avoided. That is, dividend or  divisor must be double type. 
		 int month = (int)Math.ceil(diffDays / 30.0);
		 list.add(totalWeight / month);
		 list.add(totalValue / month);
		 return list;		
	}
	
	public ArrayList<Double> getTotalWeightAndValuePerYear() {
		ArrayList<Double> list = new ArrayList<Double>();
		double totalWeight = 0;
		double totalValue = 0;
		int diffDays = 1;
		
		if (totalItemList.size() == 0) {
			System.out.println("The RCM: " + this.getRCMID() + " has not recycled items.");
			return null;
		}
		
		for (int i = 0; i < this.totalItemList.size(); i++) {
			totalWeight += totalItemList.get(i).getItemWeightInlb();
			totalValue += totalItemList.get(i).getItemPrice();
		}
		
		GregorianCalendar startDate = totalItemList.get(0).getItemAddTime();
		GregorianCalendar endDate = totalItemList.get(totalItemList.size() - 1).getItemAddTime();
		GregorianCalendar tempStart = (GregorianCalendar)startDate.clone();
		GregorianCalendar tempEnd = (GregorianCalendar)endDate.clone();
		
		while (tempStart.get(GregorianCalendar.YEAR) != tempEnd.get(GregorianCalendar.YEAR) || tempStart.get(GregorianCalendar.DAY_OF_YEAR) != tempEnd.get(GregorianCalendar.DAY_OF_YEAR)) {
			diffDays++;
			tempStart.add(GregorianCalendar.DATE, 1);
		}
	
		//In order to get ceiling, integer / integer must be avoided. That is, dividend or  divisor must be double type. 
		 int year = (int)Math.ceil(diffDays / 365.0);
		 list.add(totalWeight / year);	
		 list.add(totalValue / year);	
		 return list;
	}

	public String toString() {
		String str = "RCM (" + this.getRCMID() + "): Location: "
				+ this.getRCMLocation() + "; Status: " + this.getRCMStatus()
				+ "\n\t";
		
		str += "MaxWeight: " + this.getMaxWeight() + "; CurrentWeight: " + this.getCurrWeightInlb()
				+ "; Initialized Money: " + this.getRCMInitMoney()
				+ "; CurrentMoney: " + this.getCurrMoney() + "\n\t";
		
		str += "Acceptable item types are: \n";
		for (int i = 0; i < itemTypeList.size(); i++) {
			str += itemTypeList.get(i).toString();
		}
		
		str += "Items in this RCM (" + this.getRCMID() + ") are: \n";
		for (int i = 0; i < totalItemList.size(); i++) {
			str += totalItemList.get(i).toString();
		}
		
		str += "Number of items returned by this RCM (" + this.getRCMID() + ") in the last n days are: \n";
		str += this.getNoOfItemInNDays(1);
		str += "\n";
		str += "Time that this RCM (" + this.getRCMID() + ") emptied: \n";

		for (int i = 0; i < emptyTimeList.size(); i++) {
			str += emptyTimeList.get(i).getTime();
			str += "\n";
		}
		
		str += "Time that this RCM (" + this.getRCMID() + ") last emptied: \n";
		str += this.getLastEmptyTime();
		str += "\n";
		str += "Time that this RCM (" + this.getRCMID() + ") is used: \n";

		for (int i = 0; i < useTimeList.size(); i++) {
			str += useTimeList.get(i).getTime();
		}
		
		str += "\n";
		return str;
	}
}
