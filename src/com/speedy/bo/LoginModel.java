package com.speedy.bo;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import br.liveo.utils.HttpHandler;

public class LoginModel {

	String emailID;
	String password;
	private LoginModel loginPJ;
	
	public LoginModel(String strEmail, String strPWD) {
		
		emailID =strEmail;
		password=strPWD;
	}
	
	public String getEmailID() {
		return emailID;
	}
	public void setEmailID(String emailID) {
		this.emailID = emailID;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public String toJSON(){

	    JSONObject jsonObject= new JSONObject();
	    try {
	    	
		        jsonObject.put("emailId", getEmailID());
		        jsonObject.put("password", getPassword());
		        
	        return jsonObject.toString();
	        
	    } catch (JSONException e) {	        
	        e.printStackTrace();
	        return "";
	    }
	}
	
	

}
