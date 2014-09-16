package com.speedy.main;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import br.liveo.fragments.AsynchCallBack;
import br.liveo.fragments.CustomerMapView;
import br.liveo.utils.AsynchronoucsCall;
import br.liveo.utils.FaresDetails;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.TripCustomerDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerTripDetails extends ActionBarActivity {

	private TextView sourceaddress, destination_address, total_distance,
			time_duration, car_type, expected_fair;
	private TripCustomerDetails trip_details;
	private TextView submitcustomerDetails,cancel;
	ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private AsynchronoucsCall asynchSearch;
	private String temp_radioValue;
	private SessionPrefs session;
	public static double fare;	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customer_trip_details);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		
		init();
		Bundle data = getIntent().getExtras();
		  trip_details = data.getParcelable("customer_trip_details");
		  
		  
		  distance(Locations.SOURCE_LAT,
					Locations.SOURCE_LOGI, Locations.DESTINATION_LAT,
					Locations.DESTINATION_LOGI, 'K');
			// system.println(distance(32.9697, -96.80322, 29.46786,
			// -98.53506, "N") + " Nautical Miles\n");

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);// Menu Resource, Menu

		 menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(true).setTitle("SUBMIT");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menus.PROCEED:
			
			Log.e("trip details json paramerts data", trip_details.toJSON().toString());
			progressDialog = ProgressDialog.show(CustomerTripDetails.this, "", "Submitting...");
			
				new HttpHandler() {				  
				  @Override 
				  public HttpUriRequest getHttpRequestMethod() {					 
					  return new HttpPost( "http://phbjharkhand.in/speedyTaxi/Add_Trip_Details.php"); 
				  }
				  
				  @Override 
				  public void onResponse(String result) { // what to do with
					  // e.g. display it on edit text etResponse.setText(result);
					  Log.e("Response data by registration=:", result);
					  callbackRequestResult(result); }
				  
				}.execute(trip_details.toJSON());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void init() {

		total_distance = (TextView) findViewById(R.id.totalDistance);
		time_duration = (TextView) findViewById(R.id.duration);
		expected_fair = (TextView) findViewById(R.id.expectedFaire);
		submitcustomerDetails = (TextView)findViewById(R.id.submit);
		cancel= (TextView)findViewById(R.id.cancel);
		
		Intent i=getIntent();
		temp_radioValue= i.getStringExtra("radio_value");
		
		alertDialog = new AlertDialog.Builder(CustomerTripDetails.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

		session = new SessionPrefs(this);
	}
	
	private void callbackRequestResult(String result) {
		Log.e("response result-:", result);

		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
		
			if ("1".equals(errorCode)) {
				String tripId = jsonObj.getString("tripID");
				session.setPreference("tripID", tripId);
				JSONObject tripIdJson = new JSONObject();
				tripIdJson.put("tripID", tripId);
				
				progressDialog.dismiss();				
				Intent myIntent = new Intent(TaxiChooser.ACTION_CLOSE);
		        sendBroadcast(myIntent);
				
				Intent myIntent1 = new Intent(CustomerMapView.ACTION_CLEAR_MAP_REQUEST);
		        sendBroadcast(myIntent1);
		        
		        this.finish();
				
				/*new HttpHandler() {				  
					  @Override 
					  public HttpUriRequest getHttpRequestMethod() {					 
						  return new HttpPost( "http://phbjharkhand.in/speedyTaxi/Get_Driver_Allocation.php"); 
					  }
					  
					  @Override 
					  public void onResponse(String result) { // what to do with
						  // e.g. display it on edit text etResponse.setText(result);
						  Log.e("Response data by registration=:", result);
						  callbackRequestResultDemo(result); }
					  
					}.execute(tripIdJson.toString());*/

			} else {
				
				progressDialog.dismiss();
				
				String errorMsg = jsonObj.getString("Error_Msg");
				alertDialog.setTitle("Error !");
				alertDialog.setMessage(errorMsg);
				alertDialog.show();

			}

		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
		}
		
	}
	
	/*public void callbackRequestResultDemo(String result) {
		
		Log.e("response result-:", result);

		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
		

			if ("1".equals(errorCode)) {
				progressDialog.dismiss();
				Intent PassToHome = new Intent(getApplicationContext(),
						NavigationMain.class);
				PassToHome.putExtra("driverStatus", "true");
				startActivity(PassToHome);
				
				Intent myIntent = new Intent(TaxiChooser.ACTION_CLOSE);
		        sendBroadcast(myIntent);
				
				Intent myIntent1 = new Intent(CustomerMapView.ACTION_CLEAR_MAP);
		        sendBroadcast(myIntent1);
		        
		        this.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
		}
	}*/
	
	
	private void distance(double lat1, double lon1, double lat2, double lon2,
			char unit) {
		
		String distanceResult;
		asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {
			@Override
			public void onTaskDone(String result) {

				onLocationChnageResultGot(result);
			}
		});

		String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="
				+ lat1
				+ ","
				+ lon1
				+ "&destinations="
				+ lat2
				+ ","
				+ lon2
				+ "&mode=driving&language=en-EN&sensor=false&units=imperial";

		progressDialog = ProgressDialog.show(CustomerTripDetails.this, "",
				"Wait Calculating distance & time...");
		
		asynchSearch.execute(url);
		System.out.println("the url is : " + url);

	}

	protected void onLocationChnageResultGot(String result) {

		progressDialog.dismiss();
		Log.e("Location details ", result);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			JSONObject location, km, time;

			String addre = null;
			String distance ,durattion,faretemp,timeInSecondStr;
			double distanceInMiles;
			try {
				location = jsonObject.getJSONArray("rows").getJSONObject(0);
				JSONObject location_string = location.getJSONArray("elements")
						.getJSONObject(0);
				Log.d("test", "formattted address:" + location_string);

				km = location_string.getJSONObject("distance");
				time = location_string.getJSONObject("duration");

				 distance = km.getString("text");
				 durattion = time.getString("text");
				 timeInSecondStr = time.getString("value");
				 
				  total_distance.setText(distance);
				  time_duration.setText(durattion);
				  String dis[] = distance.split(" ");
				
				  distanceInMiles=Double.parseDouble(dis[0]);
				  double timeInSecond = Double.parseDouble(timeInSecondStr);
				  
				 String farePay=FaresDetails.getFareAmount(temp_radioValue,timeInSecond,distanceInMiles);
				
				  
				  faretemp="$"+farePay;
				  expected_fair.setText(faretemp);
				  
				  trip_details.setFair(faretemp);
				  trip_details.setDistance(distance);
				  trip_details.setExpectedTime(durattion);
				  
				  
			} catch (JSONException e1) {
				e1.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}
}
