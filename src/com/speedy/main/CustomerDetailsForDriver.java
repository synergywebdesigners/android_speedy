package com.speedy.main;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class CustomerDetailsForDriver extends Activity {

	private AlertDialog alertDialog;
	private String customerJson = null;
	String test;
	private JSONObject jsonobje;
	TextView custName = null, custMoblie = null, custDatetime = null,
			custPickLocation = null, custDropLocation = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customershortinfo);

		custName = (TextView) findViewById(R.id.username);
		custMoblie = (TextView) findViewById(R.id.mobileno);
		custDatetime = (TextView) findViewById(R.id.timedate);
		custPickLocation = (TextView) findViewById(R.id.PickupLoc);
		custDropLocation = (TextView) findViewById(R.id.DestLoc);

		customerJson = getIntent().getStringExtra("customerJsonData");

		try {
			jsonobje = new JSONObject(customerJson);
			custName.setText(jsonobje.getString("customerName"));
			custMoblie.setText(jsonobje.getString("mobileNumber"));
			custDatetime.setText(jsonobje.getString("tripDateTime"));

			double sLat = Double.parseDouble(jsonobje
					.getString("sourceLatitude"));
			double sLongi = Double.parseDouble(jsonobje
					.getString("sourceLongitude"));
			double dLat = Double.parseDouble(jsonobje
					.getString("destinationLatitude"));
			double dLongi = Double.parseDouble(jsonobje
					.getString("destinationLongitude"));

			LatLng StartPoint = new LatLng(sLat, sLongi);
			LatLng EndPoint = new LatLng(dLat, dLongi);

			test = ConvertPointToLocation(StartPoint);
			custPickLocation.setText("" + test);
			custDropLocation.setText(ConvertPointToLocation(EndPoint));

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public String ConvertPointToLocation(LatLng point) {
		Log.e("", point.latitude + "" + point.longitude);
		String address1 = "";
		Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

		try {
			List<Address> addresses = geoCoder.getFromLocation(point.latitude,
					point.longitude, 1);

			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0)
						.getMaxAddressLineIndex(); index++)
					address1 += addresses.get(0).getAddressLine(index) + " ";
			}
		} catch (IOException e) {
			Log.v("Geocoder Exception::", e.toString());
		}

		return address1;
	}


	/*
	 * public void submitData() {
	 * 
	 * JSONObject paramsJson = new JSONObject(); paramsJson.put("", value); new
	 * HttpHandler() {
	 * 
	 * @Override public HttpUriRequest getHttpRequestMethod() {
	 * 
	 * return new HttpPost( "http://phbjharkhand.in/speedyTaxi/User_Login.php");
	 * }
	 * 
	 * @Override public void onResponse(String result) { // what to do with
	 * result // e.g. display it on edit text etResponse.setText(result);
	 * Log.e("Response data by registration=:", result);
	 * callbackRequestResult(result); }
	 * 
	 * }.execute(paramsJson.toString());
	 * 
	 * }
	 * 
	 * protected void callbackRequestResult(String result) {
	 * 
	 * Log.e("resullttttttt", "" + result); JSONObject resultJson = null; try {
	 * resultJson = new JSONObject(result); JSONObject jsonObj =
	 * resultJson.getJSONObject("data"); String errorCode =
	 * jsonObj.getString("Error_Code"); String errorMsg =
	 * jsonObj.getString("Error_Msg");
	 * 
	 * //
	 * {"data":{"Error_Code":"1","Error_Msg":"Login Success","result":{"userID"
	 * :"1","userType":"customer"}}}
	 * 
	 * if ("1".equals(errorCode)) {
	 * 
	 * 
	 * } else { //Toast.makeText(getApplicationContext(), // "ErrorMsg-:" +
	 * errorMsg, Toast.LENGTH_LONG).show();
	 * 
	 * alertDialog.setTitle("Error !"); alertDialog.setMessage(errorMsg);
	 * alertDialog.show(); }
	 * 
	 * } catch (JSONException e) { e.printStackTrace(); resultJson = null; }
	 * 
	 * }
	 */
}
