package com.speedy.bo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class TripCustomerDetails implements Parcelable {

	String vehicleType;
	String vehicleSubType;
	String expectedTime;
	String fair;
	String distance;
	String sourceLatitude;
	String sourceLongitude;
	String destinationLatitude;
	String destinationLongitude;
	String paymentMode;
	String userId;
	String tripDate;
	
	
	public TripCustomerDetails(String userId, String vechicleType,String vehicleSubType,String expectedTime,String fair,String distance,String sourceLatitude,String sourceLongitude,
			String destinationLatitude,String destinationLongitude,String paymentMode,String tripDate) {
	
		this.userId = userId;
		this.vehicleType = vechicleType;
		this.vehicleSubType = vehicleSubType;
		this.expectedTime = expectedTime;
		this.fair = fair;
		this.distance = distance;
		this.sourceLatitude = sourceLatitude;
		this.sourceLongitude = sourceLongitude;
		this.destinationLatitude = destinationLatitude;
		this.destinationLongitude = destinationLongitude;
		this.paymentMode = paymentMode;
		this.tripDate = tripDate;
	}
	
	public TripCustomerDetails(Parcel in) {
		
		String[] data = new String[12];

        in.readStringArray(data);
        this.userId = data[0];
		this.vehicleType =data[1];
		this.vehicleSubType = data[2];
		this.expectedTime = data[3];
		this.fair = data[4];
		this.distance =data[5];
		this.sourceLatitude = data[6];
		this.sourceLongitude = data[7];
		this.destinationLatitude = data[8];
		this.destinationLongitude = data[9];
		this.paymentMode = data[10]; 
		this.tripDate = data[11]; 
		
	}

	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	public String getVehicleSubType() {
		return vehicleSubType;
	}
	public void setVehicleSubType(String vehicleSubType) {
		this.vehicleSubType = vehicleSubType;
	}
	public String getExpectedTime() {
		return expectedTime;
	}
	public void setExpectedTime(String expectedTime) {
		this.expectedTime = expectedTime;
	}
	public String getFair() {
		return fair;
	}
	public void setFair(String fair) {
		this.fair = fair;
	}
	public String getDistance() {
		return distance;
	}
	public void setDistance(String distance) {
		this.distance = distance;
	}
	public String getSourceLatitude() {
		return sourceLatitude;
	}
	public void setSourceLatitude(String sourceLatitude) {
		this.sourceLatitude = sourceLatitude;
	}
	public String getSourceLongitude() {
		return sourceLongitude;
	}
	public void setSourceLongitude(String sourceLongitude) {
		this.sourceLongitude = sourceLongitude;
	}
	public String getDestinationLatitude() {
		return destinationLatitude;
	}
	public void setDestinationLatitude(String destinationLatitude) {
		this.destinationLatitude = destinationLatitude;
	}
	public String getDestinationLongitude() {
		return destinationLongitude;
	}
	public void setDestinationLongitude(String destinationLongitude) {
		this.destinationLongitude = destinationLongitude;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getTripDate() {
		return tripDate;
	}

	public void setTripDate(String tripDate) {
		this.tripDate = tripDate;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String toJSON(){

	    JSONObject jsonObject= new JSONObject();
	    try {
	    	
	    	 	jsonObject.put("userId", getUserId());
		        jsonObject.put("vehicleType", getVehicleType());
		        jsonObject.put("vehicleSubType", getVehicleSubType());
		        jsonObject.put("expectedTime", getExpectedTime());
		        jsonObject.put("fair", getFair());
		        jsonObject.put("distance", getDistance());
		        jsonObject.put("sourceLatitude", getSourceLatitude());
		        jsonObject.put("sourceLongitude", getSourceLongitude());
		        jsonObject.put("destinationLatitude", getDestinationLatitude());
		        jsonObject.put("destinationLongitude", getDestinationLongitude());
		        jsonObject.put("paymentMode", getPaymentMode());
		        jsonObject.put("tripDate",getTripDate());
		        		     
		        
	        return jsonObject.toString();
	        
	    } catch (JSONException e) {	        
	        e.printStackTrace();
	        return "";
	    }
	}

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		
		 dest.writeStringArray(new String[] { this.userId, 
					this.vehicleType, 
					this.vehicleSubType, 
					this.expectedTime ,
					this.fair,
					this.distance,
					this.sourceLatitude ,
					this.sourceLongitude, 
					this.destinationLatitude ,
					this.destinationLongitude, 
					this.paymentMode,
					this.tripDate
		 });	
		
	}
	
	 @SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
         public TripCustomerDetails createFromParcel(Parcel in) {
             return new TripCustomerDetails(in); 
         }

         public TripCustomerDetails[] newArray(int size) {
             return new TripCustomerDetails[size];
         }
     };
}
