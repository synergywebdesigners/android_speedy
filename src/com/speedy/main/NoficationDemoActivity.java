package com.speedy.main;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import br.liveo.utils.HttpHandler;
import br.liveo.utils.SessionPrefs;
import android.app.Activity;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NoficationDemoActivity extends Activity {

	private Button acceptBtn = null, cancelBtn=null;
	private TextView msgTxt=null;
	private String msg ;
	private String driverID;
	private String tripID = null;
	private ProgressDialog progressDialog;
	private boolean requestFlag = false;
	SessionPrefs sessionObj = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.notificationhandleview);
		
		Intent intent = getIntent();
		msg = intent.getStringExtra("msg");
		driverID = intent.getStringExtra("driverId");
		tripID= intent.getStringExtra("tripID");
		
		acceptBtn = (Button)findViewById(R.id.accept);
		cancelBtn = (Button)findViewById(R.id.cancel);
		msgTxt = (TextView)findViewById(R.id.txtMsg);
		sessionObj = new SessionPrefs(getApplicationContext());
		
		acceptBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				progressDialog = ProgressDialog.show(NoficationDemoActivity.this, "",
						"Submitting...");
				
				JSONObject tripIdJson = new JSONObject();
				try {
					tripIdJson.put("driverID", driverID);
					tripIdJson.put("status", "Accept");
					tripIdJson.put("tripID", tripID);
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
				Log.e("json data =:",tripIdJson.toString());
				
				new HttpHandler() {				  
					  @Override 
					  public HttpUriRequest getHttpRequestMethod() {	
						  requestFlag = true;
						  return new HttpPost( "http://phbjharkhand.in/speedyTaxi/Get_Driver_Allocation.php"); 
					  }
					  
					  @Override 
					  public void onResponse(String result) { // what to do with
						  // e.g. display it on edit text etResponse.setText(result);
						  Log.e("Response data by registration=:", result);
						  callbackRequestResult(result); }
					  
					}.execute(tripIdJson.toString());
			}
		});
		
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				progressDialog = ProgressDialog.show(NoficationDemoActivity.this, "",
						"Submitting...");
				
				JSONObject tripIdJson = new JSONObject();
				try {
					tripIdJson.put("driverID", driverID);
					tripIdJson.put("status", "cancel");
					tripIdJson.put("tripID", tripID);
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
				Log.e("json data =:",tripIdJson.toString());
				
				new HttpHandler() {				  
					  @Override 
					  public HttpUriRequest getHttpRequestMethod() {
						  requestFlag = false;
						  return new HttpPost( "http://phbjharkhand.in/speedyTaxi/Get_Driver_Allocation.php"); 
					  }
					  
					  @Override 
					  public void onResponse(String result) { // what to do with
						  // e.g. display it on edit text etResponse.setText(result);
						  Log.e("Response data by registration=:", result);
						  callbackRequestResult(result); }
					  
					}.execute(tripIdJson.toString());
			}
		});
		
	}
	
	
	private void callbackRequestResult(String result){
		
		progressDialog.dismiss();
		Log.e("response of driver action", result);
		
		JSONObject resultJson = null;String errorCode=null;JSONObject jsonObj=null;
		Intent intent = null;
		try {
			resultJson = new JSONObject(result);
			jsonObj = resultJson.getJSONObject("data");
			errorCode = jsonObj.getString("Error_Code");
			
		}catch(JSONException w){
			w.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}	
		
		if(requestFlag && "1".equals(errorCode)){
			intent = new Intent(NoficationDemoActivity.this,NavigationMain.class);
			sessionObj.setPreference("notificationFlag", "RideAccepted_"+tripID);
		}else{
			intent = new Intent(NoficationDemoActivity.this,NavigationMain.class);
			sessionObj.setPreference("notificationFlag", "RideCancelled_"+tripID);
		}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(intent);
		  
		this.finish();
	}
	
}
