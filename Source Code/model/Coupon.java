package model;

/**
 * This class represents one reward (Coupon) that will be given to customer in the case that
 * there is not enough money in the RCM. 
 * @Section: Tuesday and Thursday 
 * @CourseNumber: COEN 275
 * @AssignmentNumber: Final Project
 * @DateOfSubmission: 3/2014
 *
 */
public class Coupon extends Reward {
	
	//constructor
	public Coupon(double totalValue) {
		super.totalValue = totalValue;
	}
	
}
