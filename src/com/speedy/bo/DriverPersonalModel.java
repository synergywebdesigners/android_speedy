package com.speedy.bo;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverPersonalModel {

	String firstName;
	String lastName;
	String address1;
	String address2;
	String email_id;
	String city;
	String state;
	String country;
	String zipcode;
	String age;
	String sex;
	String dob;
	String password;
	String mobileNumber;
	String alternateNumber;
	String mobileCountry;
	String language;
	String userType;
	String userImage;

	public DriverPersonalModel(String param1, String param2, String param3,
			String param4, String param5, String param6, String param7,
			String param8, String param9, String param10, String param11,
			String param12, String param13, String param14, String param15,
			String param16, String param17, String param18, String param19) {

		this.firstName = param1;
		this.lastName = param2;
		this.address1 = param3;
		this.address2 = param4;
		this.email_id = param5;
		this.city = param6;
		this.state = param7;
		this.country = param8;
		this.zipcode = param9;
		this.age = param10;
		this.sex = param11;
		this.dob = param12;
		this.mobileNumber = param13;
		this.alternateNumber = param14;
		this.mobileCountry = param15;
		this.password = param16;
		this.language = param17;
		this.userType = param18;
		this.userImage = param19;

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

	public String getEmail_id() {
		return email_id;
	}

	public void setEmail_id(String email_id) {
		this.email_id = email_id;
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

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getAlternateNumber() {
		return alternateNumber;
	}

	public void setAlternateNumber(String alternateNumber) {
		this.alternateNumber = alternateNumber;
	}

	public String getMobileCountry() {
		return mobileCountry;
	}

	public void setMobileCountry(String mobileCountry) {
		this.mobileCountry = mobileCountry;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserImage() {
		return userImage;
	}

	public void setUserImage(String userImage) {
		this.userImage = userImage;
	}

	public String toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {


			jsonObject.put("firstName", getFirstName());
			jsonObject.put("lastName", getLastName());
			jsonObject.put("addressLine1",getAddress1());
			jsonObject.put("addressLine2",getAddress2());
			jsonObject.put("emailId", getEmail_id());
			jsonObject.put("city",getCity());
			jsonObject.put("state",getState());
			jsonObject.put("country",getCountry());
			jsonObject.put("zipCode",getZipcode());
			jsonObject.put("age",getAge());
			jsonObject.put("sex",getSex());
			jsonObject.put("dob",getDob());
			jsonObject.put("mobileNumber", getMobileNumber());
			jsonObject.put("alternateMobileNumber",getAlternateNumber());
			jsonObject.put("mobileCountry", getMobileCountry());
			jsonObject.put("password", getPassword());
			jsonObject.put("language",getLanguage());
			jsonObject.put("registrationType", getUserType());
			jsonObject.put("image", getUserImage());

			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
