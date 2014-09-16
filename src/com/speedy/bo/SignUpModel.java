package com.speedy.bo;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpModel {

	String firstName;
	String lastName;
	String address1;
	String address2;
	String email_id;
	String city;
	String state;
	String country;
	String zipcode;
	String password;
	String mobileNumber;
	String alternateNumber;
	String mobileCountry;
	String userType;
	String userImage;
	

	public SignUpModel(String param1, String param2, String param3,
			String param4, String param5, String param6, String param7,
			String param8, String param9, String param10, String param11,
			String param12, String param13, String param14,String param15) {

		this.firstName = param1;
		this.lastName = param2;
		this.address1 = param3;
		this.address2 = param4;
		this.email_id = param5;
		this.city = param6;
		this.state = param7;
		this.country = param8;
		this.zipcode = param9;
		this.mobileNumber = param10;
		this.alternateNumber = param11;
		this.mobileCountry = param12;
		this.password = param13;
		this.userType = param14;
		this.userImage = param15;
			
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String getEmail_id() {
		return email_id;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getAlternateNumber() {
		return alternateNumber;
	}

	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getMobileCountry() {
		return mobileCountry;
	}

	public void setMobileCountry(String mobileCountry) {
		this.mobileCountry = mobileCountry;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("firstName", getFirstName());
			jsonObject.put("lastName", getLastName());
			jsonObject.put("addressOne",getAddress1());
			jsonObject.put("addressTwo",getAddress2());
			jsonObject.put("emailId", getEmail_id());
			jsonObject.put("city",getCity());
			jsonObject.put("state",getState());
			jsonObject.put("country",getCountry());
			jsonObject.put("zipCode",getZipcode());
			jsonObject.put("mobileNumber", getMobileNumber());
			jsonObject.put("alternateMobileNo",getAlternateNumber());
			jsonObject.put("mobileCountry", getMobileCountry());
			jsonObject.put("password", getPassword());
			jsonObject.put("userType", getUserType());
			jsonObject.put("image", getUserImage());
			

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
