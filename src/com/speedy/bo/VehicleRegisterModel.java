package com.speedy.bo;

import org.json.JSONException;
import org.json.JSONObject;

public class VehicleRegisterModel {

	String vehicleimage;
	String insuranceimage;
	String year;
	String status;
	String licenseplate;
	String interiorcolor;
	String exteriorcolor;
	String vehicleclass;
	String driverID;
	double curr_lat;
	double curr_long;
	


	public VehicleRegisterModel(String param1, String param2, String param3,
			String param4, String param5, String param6, String param7,
			String param8,String param9,double param10,double param11) {

		this.vehicleimage = param1;
		this.insuranceimage = param2;
		this.year = param3;
		this.status = param4;
		this.licenseplate = param5;
		this.interiorcolor = param6;
		this.exteriorcolor = param7;
		this.vehicleclass = param8;
		this.driverID = param9;
		this.curr_lat = param10;
		this.curr_long = param11;
		
		

	}

	



	public double getCurr_lat() {
		return curr_lat;
	}





	public void setCurr_lat(double curr_lat) {
		this.curr_lat = curr_lat;
	}





	public double getCurr_long() {
		return curr_long;
	}





	public void setCurr_long(double curr_long) {
		this.curr_long = curr_long;
	}





	public String getDriverID() {
		return driverID;
	}



	public void setDriverID(String driverID) {
		this.driverID = driverID;
	}



	public String getVehicleimage() {
		return vehicleimage;
	}



	public void setVehicleimage(String vehicleimage) {
		this.vehicleimage = vehicleimage;
	}



	public String getInsuranceimage() {
		return insuranceimage;
	}



	public void setInsuranceimage(String insuranceimage) {
		this.insuranceimage = insuranceimage;
	}



	public String getYear() {
		return year;
	}



	public void setYear(String year) {
		this.year = year;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getLicenseplate() {
		return licenseplate;
	}



	public void setLicenseplate(String licenseplate) {
		this.licenseplate = licenseplate;
	}



	public String getInteriorcolor() {
		return interiorcolor;
	}



	public void setInteriorcolor(String interiorcolor) {
		this.interiorcolor = interiorcolor;
	}



	public String getExteriorcolor() {
		return exteriorcolor;
	}



	public void setExteriorcolor(String exteriorcolor) {
		this.exteriorcolor = exteriorcolor;
	}



	public String getVehicleclass() {
		return vehicleclass;
	}



	public void setVehicleclass(String vehicleclass) {
		this.vehicleclass = vehicleclass;
	}



	public String toJSON() {

		JSONObject jsonObject = new JSONObject();
		try {


			jsonObject.put("vehicle_image", getVehicleimage());
			jsonObject.put("insurance_image", getInsuranceimage());
			jsonObject.put("year",getYear());
			jsonObject.put("status",getStatus());
			jsonObject.put("license_num", getLicenseplate());
			jsonObject.put("interiorCol",getInteriorcolor());
			jsonObject.put("exteriorCol",getExteriorcolor());
			jsonObject.put("vehicleClass",getVehicleclass());
			jsonObject.put("driverID",getDriverID());
			jsonObject.put("driver_currentLat",getCurr_lat());
			jsonObject.put("driver_currentLong",getCurr_long());
			
			
			
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();
			return "";
		}
	}

}
