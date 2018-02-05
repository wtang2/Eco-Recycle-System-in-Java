package model;
import java.io.*;

/**
 * This class represents item types that are acceptable to RCM. It holds information 
 * about the itemType itself(typeName, price by weight)
 
 */
 
public class ItemType implements Serializable {
	/* System generate a serial Version ID; */
	private static final long serialVersionUID = 7652100000729626969L;
	
	//data member
	private String typeName;
	private double typePrice;
	private double weightForPrice;
	
	//constructor with parameters
	public ItemType (String typeName, double typePrice, double weightForPrice) {
		this.typeName = typeName;
		this.typePrice = typePrice;
		this.weightForPrice = weightForPrice;
	}
	
	//default constructor
	public ItemType() {}
	
	//methods
	public void setType(String typeName) {
		this.typeName = typeName;
	}
	
	public void setPrice(double typePrice) {
		this.typePrice = typePrice;
	}
	
	public void setWeight(double weightForPrice) {
		this.weightForPrice = weightForPrice;
	} 
	
	public String getType() {
		return typeName;
	}
	
	public double getPrice() {
		return typePrice;
	}
	
	public double getWeightInlb() {
		return weightForPrice;
	}
	
	public double getWeightInkg() {
		return getWeightInlb() * 0.4536;
	}
	
	public String showTypeStatusWeightInlb() {
		System.out.println("\t"+ getType() + " :" + "\t"+ "$" + getPrice() +" for "+ getWeightInlb() + "lb(s)");
		return "\t"+ getType() + " :" + "\t"+ "$" + getPrice() +" for "+ getWeightInlb() + "lb(s)\n";
	}
	
	public String showTypeStatusWeightInkg() {
		System.out.println("\t"+ getType() + " :" + "\t"+ "$" + getPrice() +" for "+ getWeightInkg() + "kg(s)");
		return "\t"+ getType() + " :" + "\t"+ "$" + getPrice() +" for "+ getWeightInkg() + "kg(s)\n";
	}
	
  	public String toString(){
  		String str = "TypeName: "+ getType() + "\tPrice: " + this.getPrice() + " for " + this.getWeightInlb() + "\n";
  		
  		return str;
  	} 
}
