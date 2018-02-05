package model;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.io.*;
import model.RCMStatus;

/**
 * This class represents a RMOS system (Recycling Machine) that will be used by administrator to monitor RCM or itemType. 
 * It has some responsibilities:
 * 		Activate/ Remove a RCM
 * 		Keeps track of the status of each individual RCM
 * 		Updates the capabilities of the RCMs
 * 		Collects statistical information about usage of the RCMs it monitors
 * 
 * @author  Woon Jeen Tang 
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014 
 */
 
public class RMOS extends Observable implements Serializable{

	/** System generate a serial Version ID; */
	private static final long serialVersionUID = -3044191174489783065L;
	
	//data member
	public ArrayList<RCM> RCMList;
	private static RCMStatus OPERATIONAL = RCMStatus.OPERATIONAL;
	private static RCMStatus DOWN = RCMStatus.DOWN;
	
	//constructor
	public RMOS() {
		RCMList = new ArrayList<RCM>();
	}
	
	public boolean aboveZero(double num) {
		if (num <= 0)
			return false;
		return true;
	}
	
	//methods
	public Boolean addRCM(String RCMID, String RCMLocation, double RCMMaxWeight, double RCMInitMoney) {
		int i;
		if ((!aboveZero(RCMMaxWeight)) || (!aboveZero(RCMInitMoney))) {
			throw new NumberFormatException("Weight and money must be greater than zero.");
		}
		
		for (i = 0; i < RCMList.size(); i++) {
			if (RCMID.equalsIgnoreCase(RCMList.get(i).getRCMID()) ) {
				System.out.println("The RCM has been monitor by RMOS");
				return false;
			}
		}
		
		RCMList.add(new RCM(RCMID, RCMLocation, RCMMaxWeight, RCMInitMoney));
		setChanged();
		notifyObservers();
		return true;
	}
	
	public void reactivateRCM(String RCMID) {
		RCM RCM = this.getRCMByID(RCMID);
		RCM.setRCMstatus(OPERATIONAL);
		RCMStatus status = RCM.getRCMStatus();
		System.out.println(status.toString());
		setChanged();
		notifyObservers();
	}
	
	public void reactivateAllRCM() {
		for (int i = 0; i < this.getRCMList().size(); i++) {
			this.reactivateRCM(getRCMList().get(i).getRCMID());
		}
		setChanged();
		notifyObservers();
	}
	
	public void deactivateRCM(String RCMID) {
		RCM RCM = this.getRCMByID(RCMID);
		RCM.setRCMstatus(DOWN);
		RCMStatus status = RCM.getRCMStatus();
		System.out.println(status.toString());
		setChanged();
		notifyObservers();
	}
	
	public void deactivateAllRCM() {
		for (int i = 0; i < this.getRCMList().size(); i++) {
			this.deactivateRCM(getRCMList().get(i).getRCMID());
		}
		setChanged();
		notifyObservers();
	}

	public void removeRCM(String RCMID) {
		int i;
		for (i = 0; i < RCMList.size(); i++) {
			if (RCMID.equalsIgnoreCase(RCMList.get(i).getRCMID())) 
				RCMList.remove(i);
		}
		setChanged();
		notifyObservers();
	}
	
	public void removeAllRCM() {
		RCMList.clear();
		setChanged();
		notifyObservers();
	}
	
	public void emptyRCM(String RCMID) {
		RCM RCM = this.getRCMByID(RCMID);
		RCM.emptyRCM();
	}
	
	public void emptyAllRCM() {
		for (int i = 0; i < this.getRCMList().size(); i++) {
			getRCMList().get(i).emptyRCM();
		}
	}
	
	public boolean RCMReachMaximum() {
		if (RCMList.size() > 10) {
			System.out.println("Reach maximum number of RCMs that a RMOS can monitor.");
			return true;
		}
		return false;
	}
	
	public String getRCMID (ArrayList<RCM> RCMList) {
		int i; 
		String RCMID = "";
		if (RCMList.size() == 0)
			return RCMID;
		for (i = 0; i < RCMList.size()-1; i++) {
			RCMID += RCMList.get(i).getRCMID() + ":";
		}
		RCMID += RCMList.get(RCMList.size()-1).getRCMID();
		return RCMID;
	}
	
	public String getRCMStatus (RCM RCM) {
		System.out.println("RCM( " + RCM.getRCMID() + ", "+ RCM.getRCMLocation() + ") Status: " + RCM.getRCMStatus());
		return "RCM( " + RCM.getRCMID() + ", "+ RCM.getRCMLocation() + ") Status: " + RCM.getRCMStatus() + "\n";
	}
	
	public void setRCMStatus(RCM RCM, RCMStatus status) {
		RCM.setRCMstatus(status);
		setChanged();
		notifyObservers();
	}
	
	public ArrayList<RCM> getRCMList() {
		return RCMList;
	}
	
	public RCM getRCMByID(String id) {
		RCM result = null;
		for (int i = 0; i < RCMList.size(); i++) {
			if (RCMList.get(i).getRCMID().equals(id)) {
				result = RCMList.get(i);
				break;
			}
		}
		return result;
	}
	
	public String getAllRCMStatus (ArrayList<RCM> RCMList) {
		int i;
		String RCMStatus = "";
		for (i = 0; i < RCMList.size(); i++) {
			RCMStatus += getRCMStatus(RCMList.get(i)); 
		}
		return RCMStatus;
	}
	
	public String showRCMByStatus (RCMStatus status) {
		int i;
		int counter = 0;
		String RCMStatus = "";
		for (i = 0; i < RCMList.size(); i++) {
			if (RCMList.get(i).getRCMStatus() == status) {
				RCMStatus += getRCMStatus(RCMList.get(i)); 
			counter++;
			}
		}
		
		if (counter == 0) {
			System.out.println("There are no RCM that are" + status);
			return "There are no RCM that are" + status + "\n";
		}
		return RCMStatus;
	}
	
	
	public void addNewItemType (RCM RCM, String typeName, double itemPrice, double weightForPrice) {		
		RCM.addItemType(typeName, itemPrice, weightForPrice);		
	}
	
	public void changeItemType (RCM RCM, int index, String typeName, double itemPrice, double weightForPrice) {
		RCM.changeItemType(index, typeName, itemPrice, weightForPrice);		
	}

	public double getRCMMoney(RCM RCM) {
		System.out.println("Amount of money in RCM( " + RCM.getRCMID() + ", "+ RCM.getRCMLocation() + ") : $" + RCM.getCurrMoney());
		return RCM.getCurrMoney();
	}
	
	public double getRCMWeightInlb (RCM RCM) {
		System.out.println("RCM( " + RCM.getRCMID() + ", "+ RCM.getRCMLocation() + ") currentWeight: " + RCM.getCurrWeightInlb() + "lb(s);\n\t\t\t availableWeight " + (RCM.getMaxWeight() - RCM.getCurrWeightInlb()) + "lb(s)");
		return RCM.getCurrWeightInlb();
	}	

	public int getNoOfItemInNDays(RCM RCM, int n) {
		for (int i = 0; i < RCMList.size(); i++) {
			if (RCMList.get(i).getRCMID().equalsIgnoreCase(RCM.getRCMID())) {
				return RCM.getNoOfItemInNDays(n);
			}
		}
		return 0;
	}
	
	public int getNoOfItemByType(RCM RCM, String typeName) {
		return RCM.getNoOfItemByType(typeName);
	}
	
	public Date showLastEmptyTime(RCM RCM) {
		return RCM.getLastEmptyTime();
	}
	
	public int getNoOfEmptyTimeInLastNHours(RCM RCM, int n){
		return RCM.getNoOfEmptyTimeInLastNHours(n);
	}
	
	public ArrayList<RCM> getUsedMostRCMInNDays(int n) {
		ArrayList<RCM> useMostRCMList = new ArrayList<RCM>();
		
		if (RCMList.size() == 0) {
			System.out.println("This RMOS does not monitor any RCMs.");
			return null;
		}
		
		useMostRCMList.add(RCMList.get(0));
		int maxUseTimes = RCMList.get(0).getUseTimesInLastNDay(n);

		for (int i = 1; i < RCMList.size(); i++) {
			if (maxUseTimes == RCMList.get(i).getUseTimesInLastNDay(n)) {
				useMostRCMList.add(RCMList.get(i));
			}
			else if (maxUseTimes < RCMList.get(i).getUseTimesInLastNDay(n)) {
				useMostRCMList.clear();
				useMostRCMList.add(RCMList.get(i));
				maxUseTimes = RCMList.get(i).getUseTimesInLastNDay(n);
			}
		}
		
		if (maxUseTimes == 0) {
			useMostRCMList = null;
			System.out.println("All the RCMs are not used in the last " + n + " days");
			return useMostRCMList;
		}
		
		if (useMostRCMList.size() >1 && useMostRCMList.size() == RCMList.size()) {
			System.out.println("All the RCMs are used " + useMostRCMList.get(0).getUseTimesInLastNDay(n) + " times in the last " + n + " days\n");
			System.out.println("RCM ID and Locations are: \n");
			
			for (int i = 0; i < useMostRCMList.size(); i++) {
				System.out.println("ID: " + useMostRCMList.get(i).getRCMID() + "; Location: " + useMostRCMList.get(i).getRCMLocation() + "\n");							
			}
			return useMostRCMList;	
		} else if (useMostRCMList.size() >1) {
			System.out.println("There are " + useMostRCMList.size() + " RCMs that are used most in the last " + n + " days\n");
			System.out.println("Use times is(are): " + useMostRCMList.get(0).getUseTimesInLastNDay(n));
			System.out.println("RCM ID and Locations are: \n");
			for (int i = 0; i < useMostRCMList.size(); i++) {
				System.out.println("ID: " + useMostRCMList.get(i).getRCMID() + "; Location: " + useMostRCMList.get(i).getRCMLocation() + "\n");							
			}
			return useMostRCMList;
		}
		
		String RCMIDLocation = "The ID and Location of RCM that is used most in the last " + n + " days are: \n";
		RCMIDLocation += "ID: " + useMostRCMList.get(0).getRCMID() + "; Location: " + useMostRCMList.get(0).getRCMLocation()+ "\n";
		RCMIDLocation += "Use times is(are): " + useMostRCMList.get(0).getUseTimesInLastNDay(n);
		System.out.println(RCMIDLocation);
		return useMostRCMList;
	}
	
	public void statusUpdated(){
		this.setChanged();
		this.notifyObservers();
	}
	
	public String toString() {
		String str = "This RMOS monitors these RCMs:\n";
		for (int i = 0; i < RCMList.size(); i++) {
			str += RCMList.get(i).toString();
		}
		str += "\n";
		return str;
	}
}
