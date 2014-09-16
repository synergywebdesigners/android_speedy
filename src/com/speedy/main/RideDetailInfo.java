package com.speedy.main;

import java.util.List;

import br.liveo.adapter.RidesAdapter;
import br.liveo.utils.Locations;
import br.liveo.utils.SessionPrefs;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.speedy.bo.RideModel;
import com.speedy.main.CustomerTripDetails;
import com.speedy.main.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.SupportMapFragment;
import com.kaplandroid.shortestpathdirection.googlemaps.GMapV2Direction;
import com.kaplandroid.shortestpathdirection.googlemaps.GetRotueListTask;
import com.kaplandroid.shortestpathdirection.googlemaps.GMapV2Direction.DirecitonReceivedListener;

public class RideDetailInfo extends FragmentActivity implements
DirecitonReceivedListener {

	private RideModel ride_detail=null;
	private TextView address,dattime;
	private AlertDialog alertDialog;
	private Dialog paymentDialog;
	private GoogleMap mMap;
	private LatLng startPoint;
	private LatLng endPoint;
	private TextView navigationMap,paymentDeatails;
	private SessionPrefs session;
	private String userType;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ride_detailinfo);		
		 
			init();
		
			mMap = ((SupportMapFragment) getSupportFragmentManager()
	                .findFragmentById(R.id.map)).getMap();
		 
	        if (mMap == null) {
	            Toast.makeText(this, "Google Maps not available", 
	                Toast.LENGTH_LONG).show();
	        }else{
	        	
	        	
	        	
	        	try{
	    	    	double lat = Double.parseDouble(ride_detail.getSLocationLat());
	    		    double longi = Double.parseDouble(ride_detail.getSLocationLongi());
	    		    startPoint = new LatLng(lat, longi);
	    	    }catch(NumberFormatException e){
	    	    	e.printStackTrace();
	    	    }
	    	   
	    	   try{
	    	    	double lat = Double.parseDouble(ride_detail.getDLocationLat());
	    		    double longi = Double.parseDouble(ride_detail.getDLocationLongi());
	    		    endPoint = new LatLng(lat, longi);
	    	    }catch(NumberFormatException e){
	    	    	e.printStackTrace();
	    	    }
	    	   
	    	   
	    	   mMap.addMarker(new MarkerOptions()
								.position(startPoint)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.marker_customer_center_bottom_blue)));

				 mMap.addMarker(new MarkerOptions()
								.position(endPoint)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.marker_customer_center_bottom_blue)));

				
	    	   new GetRotueListTask(this, startPoint, endPoint,
						GMapV2Direction.MODE_DRIVING, this).execute();
	    	   
	    	   setUpMapSettings(mMap);
	        }	        
	        
	    	
			if(ride_detail!=null){
				address.setText(ride_detail.getRide_Source()+"\n To \n"+ride_detail.getRide_Destination());
				dattime.setText(ride_detail.getRide_Time()+" "+ride_detail.getRide_Date());			
			}
			
			navigationMap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if (startPoint != null && endPoint != null) {

						Intent intent = new Intent(
								android.content.Intent.ACTION_VIEW, Uri
										.parse("http://maps.google.com/maps?saddr="
												+ "" + startPoint.latitude + ","
												+ "" + startPoint.longitude
												+ "&daddr=" + ""
												+ endPoint.latitude + "," + ""
												+ endPoint.longitude));
						intent.setComponent(new ComponentName(
								"com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity"));
						startActivity(intent);

					}
				}
			});
			
			paymentDeatails.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					
					if("customer".equals(userType)){
						Intent intent = new Intent(RideDetailInfo.this,CustomerDNPDetails.class);
						intent.putExtra("ride_details", ride_detail);
						startActivity(intent);
					}else{
						Intent intent = new Intent(RideDetailInfo.this,DriverRidePayment.class);
						intent.putExtra("ride_details", ride_detail);
						startActivity(intent);
					}
					
					
				}
			});
			
	}
	
	
	
	@SuppressWarnings("deprecation")
	private void init() {

		address=(TextView)findViewById(R.id.address);
    	dattime=(TextView)findViewById(R.id.time_date);	
    	navigationMap = (TextView)findViewById(R.id.navigationView);
    	paymentDeatails = (TextView)findViewById(R.id.paymentDetails);
    	
		
		Bundle data = getIntent().getExtras();
		ride_detail = data.getParcelable("ride_details");
		
		alertDialog = new AlertDialog.Builder(RideDetailInfo.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});
		
		session = new SessionPrefs(this);
		userType = session.getPreference("userType");
		if("customer".equals(userType)){
			paymentDeatails.setText("Driver/Payment");
		}else{
			paymentDeatails.setText("Customer/Payment");
		}
		
	
	}
	
	
	private void setUpMapSettings(GoogleMap mMap2) {
		UiSettings mUiSettings;
		mUiSettings = mMap2.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setCompassEnabled(false);
		mUiSettings.setMyLocationButtonEnabled(false);
		mUiSettings.setScrollGesturesEnabled(true);
		mUiSettings.setZoomGesturesEnabled(false);
		mUiSettings.setTiltGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(true);
	}



	@Override
	public void OnDirectionListReceived(List<LatLng> mPointList) {
		if (mPointList != null) {
			PolylineOptions rectLine = new PolylineOptions().width(10).color(
					Color.BLUE);
			rectLine.width(3);
			for (int i = 0; i < mPointList.size(); i++) {
				rectLine.add(mPointList.get(i));
			}
			mMap.addPolyline(rectLine);

			CameraPosition mCPFrom = new CameraPosition.Builder()
					.target(startPoint).zoom(11.5f).bearing(0).tilt(25).build();
			final CameraPosition mCPTo = new CameraPosition.Builder()
					.target(endPoint).zoom(11.5f).bearing(0).tilt(50).build();

			changeCamera(CameraUpdateFactory.newCameraPosition(mCPFrom),
					new CancelableCallback() {
						@Override
						public void onFinish() {
							changeCamera(CameraUpdateFactory
									.newCameraPosition(mCPTo),
									new CancelableCallback() {

										@Override
										public void onFinish() {

											LatLngBounds bounds = new LatLngBounds.Builder()
													.include(startPoint)
													.include(endPoint).build();
											changeCamera(
													CameraUpdateFactory
															.newLatLngBounds(
																	bounds, 50),
													null, false);
										}

										@Override
										public void onCancel() {
										}
									}, false);

							mMap.moveCamera(CameraUpdateFactory
									.newLatLng(new LatLng(
											Locations.CURRENT_LAT,
											Locations.CURRENT_LOGI)));
							// addressTitle.setText("Current Location");
						}

						@Override
						public void onCancel() {
						}
					}, true);
		}
	}

	
	/**
	 * Change the camera position by moving or animating the camera depending on
	 * input parameter.
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback,
			boolean instant) {

		if (instant) {
			mMap.animateCamera(update, 1, callback);
		} else {
			mMap.animateCamera(update, 4000, callback);
		}
	}
	
}
