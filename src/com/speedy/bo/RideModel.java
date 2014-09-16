package com.speedy.bo;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Parcel;
import android.os.Parcelable;

public class RideModel implements Parcelable {
	private String Ride_Time = "";
	private String Ride_Source = "";
	private String Ride_Destination = "";
	private String Ride_Date = "";
	private String SLocationLat;
	private String SLocationLongi;
	private String DLocationLat;
	private String DLocationLongi;
	private String userName;
	private String emailId;
	private String mobileNumber;
	private String tripStatus;
	private String paymentMode;
	private String profileImage;
	private String tripId;
	private String paymentStatus;
	

	public RideModel(Parcel in) {

		String[] data = new String[15];

		in.readStringArray(data);
		this.Ride_Source = data[0];
		this.Ride_Destination = data[1];
		this.Ride_Date = data[2];
		this.Ride_Time = data[3];
		this.SLocationLat = data[4];
		this.SLocationLongi = data[5];
		this.DLocationLat = data[6];
		this.DLocationLongi = data[7];
		this.userName = data[8];
		this.emailId = data[9];
		this.mobileNumber = data[10];
		this.tripStatus = data[11];
		this.paymentMode = data[12];
		this.tripId = data[13];
		this.paymentStatus = data[14];
		

	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getTripId() {
		return tripId;
	}

	public void setTripId(String tripId) {
		this.tripId = tripId;
	}

	public RideModel() {

	}

	public String getRide_Time() {
		return Ride_Time;
	}

	public void setRide_Time(String ride_Time) {
		Ride_Time = ride_Time;
	}

	public String getRide_Source() {
		return Ride_Source;
	}

	public void setRide_Source(String ride_Source) {
		Ride_Source = ride_Source;
	}

	public String getRide_Destination() {
		return Ride_Destination;
	}

	public void setRide_Destination(String ride_Destination) {
		Ride_Destination = ride_Destination;
	}

	public String getRide_Date() {
		return Ride_Date;
	}

	public void setRide_Date(String ride_Date) {
		Ride_Date = ride_Date;
	}

	public String getSLocationLat() {
		return SLocationLat;
	}

	public void setSLocationLat(String sLocationLat) {
		SLocationLat = sLocationLat;
	}

	public String getSLocationLongi() {
		return SLocationLongi;
	}

	public void setSLocationLongi(String sLocationLongi) {
		SLocationLongi = sLocationLongi;
	}

	public String getDLocationLat() {
		return DLocationLat;
	}

	public void setDLocationLat(String dLocationLat) {
		DLocationLat = dLocationLat;
	}

	public String getDLocationLongi() {
		return DLocationLongi;
	}

	public void setDLocationLongi(String dLocationLongi) {
		DLocationLongi = dLocationLongi;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getTripStatus() {
		return tripStatus;
	}

	public void setTripStatus(String tripStatus) {
		this.tripStatus = tripStatus;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("ride_time", getRide_Time());
			jsonObject.put("ride_date", getRide_Date());
			jsonObject.put("ride_source", getRide_Source());
			jsonObject.put("ride_destination", getRide_Destination());

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

	public static Parcelable.Creator getCreator() {
		return CREATOR;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

		dest.writeStringArray(new String[] { this.Ride_Source,
				this.Ride_Destination, this.Ride_Date, this.Ride_Time,
				this.SLocationLat, this.SLocationLongi, this.DLocationLat,
				this.DLocationLongi, this.userName, this.emailId,
				this.mobileNumber, this.tripStatus, this.paymentMode,
				this.tripId,this.paymentStatus

		});
	}

	@SuppressWarnings("rawtypes")
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public RideModel createFromParcel(Parcel in) {
			return new RideModel(in);
		}

		public RideModel[] newArray(int size) {
			return new RideModel[size];
		}
	};

}
