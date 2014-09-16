package br.liveo.fragments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.speedy.bo.RideModel;
import com.speedy.main.R;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import br.liveo.adapter.RidesAdapter;
import br.liveo.utils.HelperApi;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

public class CurrentRidesFragment extends Fragment{

	RelativeLayout progressView = null; 
	ArrayList<RideModel> ridesList = null;
	private SessionPrefs session;
	private ListView listCurrentRides;
	private LatLng startPoint;
	private LatLng endPoint;
	private TextView ridesMsg;
	
    public static CurrentRidesFragment newInstance() {
        CurrentRidesFragment fragment = new CurrentRidesFragment();
        return fragment;
    }	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
			
		View rootView = inflater.inflate(R.layout.summerycurrent, container, false);

		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
		
		progressView = (RelativeLayout)rootView.findViewById(R.id.progressdialogview);
		listCurrentRides = (ListView)rootView.findViewById(R.id.currentlist);
		
		ridesMsg = (TextView)rootView.findViewById(R.id.ridesMsg);
		
		return rootView;
	}
			
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(false);
		
		session = new SessionPrefs(getActivity());
		loadData();
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);		
		inflater.inflate(R.menu.menu, menu);
		menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(false);
	
	}	
	
	
	@Override
	public void onStart() {
	
		super.onStart();
	}
	
	
	private void loadData() {
		
		final JSONObject requestParams = new JSONObject();
		try {
			requestParams.put("userID",session.getPreference("userID"));
			requestParams.put("userType",session.getPreference("userType"));
			requestParams.put("typeData","current");
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		
		Log.e("result",requestParams.toString());
		
		final Handler h = new Handler(){
		    @Override
		    public void handleMessage(Message msg){
		    	 String aResponse = msg.getData().getString("message");
		    	 callbackRequestResult(aResponse);
		    }
		};
		
		
		Thread t = new Thread() {
		    @Override
		    public void run(){
		    	
		    	String result = HelperApi.httpCallToServer("http://phbjharkhand.in/speedyTaxi/Get_User_Driver_Allocated_Details.php", requestParams.toString());
				 
		    	Message msgObj = h.obtainMessage();
                Bundle b = new Bundle();
                b.putString("message", result);
                msgObj.setData(b);
                h.sendMessage(msgObj);            
		    }   
		};
		t.start();
		
	}
	

	private void callbackRequestResult(String result) {
		
		Log.e("result",result);
		progressView.setVisibility(View.INVISIBLE);
		
		try {

				JSONObject resultJson = new JSONObject(result);
				JSONObject jsonObj = resultJson.getJSONObject("data");
				String errorCode = jsonObj.getString("Error_Code");
				
				if("1".equals(errorCode)){
							
				JSONArray jsonArray = jsonObj.getJSONArray("result");
				ridesList = new ArrayList<RideModel>();
				
				for(int i=0;i<jsonArray.length();i++){
						
					   JSONObject ridesJsonObject = jsonArray.getJSONObject(i);
					   RideModel ridesModel = new RideModel();
					   try{
						   ridesModel.setSLocationLat(ridesJsonObject.getString("sourceLatitude"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   try{
						   ridesModel.setSLocationLongi(ridesJsonObject.getString("sourceLongitude"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   try{
						   ridesModel.setDLocationLat(ridesJsonObject.getString("destinationLatitude"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setDLocationLongi(ridesJsonObject.getString("destinationLongitude"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setRide_Destination(ridesJsonObject.getString("destinationAddress"));	
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setRide_Source(ridesJsonObject.getString("sourceAddress"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setRide_Date(ridesJsonObject.getString("tripDateTime"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setTripStatus(ridesJsonObject.getString("tripStatus"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setUserName(ridesJsonObject.getString("userName"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setMobileNumber(ridesJsonObject.getString("mobileNumber"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setEmailId(ridesJsonObject.getString("emailID"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setPaymentMode(ridesJsonObject.getString("paymentMode"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					   
					   try{
						   ridesModel.setProfileImage(ridesJsonObject.getString("profileImage"));
					   }catch(JSONException e){
						   e.printStackTrace();
					   }
					 					  			
					   ridesList.add(ridesModel);
									
				}
			
				if(ridesList!=null && (!ridesList.isEmpty())){
					RidesAdapter adapterCurrentRides = new RidesAdapter(getActivity(), ridesList);
					listCurrentRides.setAdapter(adapterCurrentRides);
				}
			
			}else if("2".equals(errorCode)){
				
				ridesMsg.setVisibility(View.VISIBLE);
			}
			
		}catch(JSONException e){
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String ConvertPointToLocation(LatLng point) {
		
		Log.e("", point.latitude + "" + point.longitude);
		String address1 = "";
		Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());

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
			address1="";
		}

		return address1;
	}
}

