package br.liveo.utils;

import java.text.DecimalFormat;

import com.speedy.main.R;

public class FaresDetails {
	
	// TAXI SERVICES
	    
	public static final double TIME_SPEEDY_SEDAN_TAXI =0.40;
	public static final double TIME_REGULAR_SEDAN_TAXI =0.40;
	public static final double TIME_SPEEDY_VAN_TAXI=0.40;
	public static final double TIME_SPEEDY_LUXURY_TAXI=0.40;
	
	public static final double DISTANCE_SPEEDY_SEDAN_TAXI=1.85;
	public static final double DISTANCE_REGULAR_SEDAN_TAXI=1.95; 
	public static final double DISTANCE_SPEEDY_VAN_TAXI= 2.50;
	public static final double DISTANCE_SPEEDY_LUXURY_TAXI=3.50;
	
	// TOWING SERVICES
	public static final double TIME_TOWING_HOOK_AND_CHAIN=0.40;
	public static final double TIME_TOWING_WHEEL_LIFT=0.50;
	public static final double TIME_TOWING_FLAT_BED =0.60;
	
	public static final double DISTANCE_TOWING_HOOK_AND_CHAIN= 3.00;
	public static final double DISTANCE_TOWING_WHEEL_LIFT=3.50;
	public static final double DISTANCE_TOWING_FLAT_BED =4.00;
	
	//MEDICAL TRASPORTATION
	public static final double DISTANCE_AMBULATORY =2.25;
	public static final double DISTANCE_WHEELCHAIR =2.50;
	
	public static final double TIME_AMBULATORY =0.40;	
	public static final double TIME_WHEELCHAIR =0.50;
	
	//BASE FEES
	public static final double BASE_FEE_TOWING =30.00;
	public static final double BASE_FEE_AMBULATORY =2.50;
	public static final double BASE_FEE_WHEELCHAIR =15.00;

	private final static String SPEEDY_SEDAN= "Speedy Sedan";
	private final static String REGULAR_SEDAN= "Regular Sedan Taxi";
	private final static String SPEEDY_VAN= "Speedy Van";
	private final static String SPEEDY_SUV= "Speedy SUV";
	private final static String HOOK_CHAIN= "Hook and Chain";
	private final static String WHEEL_LIFT= "Wheel Lift";
	private final static String FLAT_BED= "Flat Bed";
	private final static String AMBULATORY= "Ambulatory";
	private final static String WHEEL_CHAIR= "Wheel chair services";
	
    
    
    
	
	
	public static String getFareAmount(String vehicleType,double timeInSecond,double distanceInMiles){
		
		String temp_radioValue=vehicleType;
		double fare = 0;
		String totalFare;
		 DecimalFormat df = new DecimalFormat("#.##");
		if(temp_radioValue.equals(SPEEDY_SEDAN)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_SPEEDY_SEDAN_TAXI;
			  resultWithDistance = distanceInMiles * DISTANCE_SPEEDY_SEDAN_TAXI;
			  fare=(resultWithTime + resultWithDistance);
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(REGULAR_SEDAN)){					  
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_REGULAR_SEDAN_TAXI;
			  resultWithDistance = distanceInMiles * DISTANCE_REGULAR_SEDAN_TAXI;
			  fare=resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(SPEEDY_VAN)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_SPEEDY_VAN_TAXI;
			  resultWithDistance = distanceInMiles * DISTANCE_SPEEDY_VAN_TAXI;
			  fare=resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(SPEEDY_SUV)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_SPEEDY_LUXURY_TAXI;
			  resultWithDistance = distanceInMiles * DISTANCE_SPEEDY_LUXURY_TAXI;
			  fare=resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(HOOK_CHAIN)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_TOWING_HOOK_AND_CHAIN;
			  resultWithDistance = distanceInMiles * DISTANCE_TOWING_HOOK_AND_CHAIN;
			  fare=BASE_FEE_TOWING+resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(WHEEL_LIFT)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_TOWING_WHEEL_LIFT;
			  resultWithDistance = distanceInMiles * DISTANCE_TOWING_WHEEL_LIFT;
			  fare=BASE_FEE_TOWING+resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(FLAT_BED)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_TOWING_FLAT_BED;
			  resultWithDistance = distanceInMiles * DISTANCE_TOWING_FLAT_BED;
			  fare=BASE_FEE_TOWING+resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else  if(temp_radioValue.equals(AMBULATORY)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_AMBULATORY;
			  resultWithDistance = distanceInMiles * DISTANCE_AMBULATORY;
			  fare=BASE_FEE_AMBULATORY+resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }else if(temp_radioValue.equals(WHEEL_CHAIR)){
			  double resultWithTime,resultWithDistance;					 
			  resultWithTime = (timeInSecond/60)*TIME_WHEELCHAIR;
			  resultWithDistance = distanceInMiles * DISTANCE_WHEELCHAIR;
			  fare=BASE_FEE_WHEELCHAIR+resultWithTime + resultWithDistance;
			  fare = Double.valueOf(df.format(fare));
		  }
		  
		
		totalFare=""+fare;
		return totalFare;
	}
	

}
