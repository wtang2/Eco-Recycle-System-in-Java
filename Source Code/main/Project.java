package main;

import Controller.AdminLogin;
import Controller.CustomerInterface;
import model.RMOS;
import model.Serialize_Deserialize;

public class Project {
	public static void main(String[] args) {
		RMOS RMOS1 = Serialize_Deserialize.deserializeRMOS();
		if (RMOS1 == null)
			RMOS1 = new RMOS();
	    	AdminLogin si = new AdminLogin(RMOS1);
	    	si.login();
	    	new CustomerInterface(RMOS1);
    	}
}
