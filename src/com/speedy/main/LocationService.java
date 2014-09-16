package com.speedy.main;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import br.liveo.utils.HelperApi;
import br.liveo.utils.SessionPrefs;

public class LocationService extends Service {

	public static final String BROADCAST_ACTION = "Hello World";
	private static final int TWO_MINUTES = 1000 * 60 * 1;
	public LocationManager locationManager;
	public android.location.LocationListener listener;
	public Location previousBestLocation = null;
	SessionPrefs sessionPrefs  =null;


	Intent intent;
	int counter = 0;
	String driverId;


	@Override
	public void onCreate() {
	    super.onCreate();
	    intent = new Intent(BROADCAST_ACTION); 
	    sessionPrefs = new SessionPrefs(getApplicationContext());
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int startId) {      
		
		driverId = sessionPrefs.getPreference("userID");
		Log.e("services is started -:","services is satarted by activity");
	    
	    
	    listener = new android.location.LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {				
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
			//	 Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void onLocationChanged(Location loc) {
				 Log.i("**************************************", "Location changed");
			        if(isBetterLocation(loc, previousBestLocation)) {
			            loc.getLatitude();
			            loc.getLongitude();
			            			
			            Log.e("location updatessssss -:","lat-:"+loc.getLatitude()+" logi-:"+loc.getLongitude());
			            sessionPrefs.setPreference("currentLat", ""+loc.getLatitude());
			            sessionPrefs.setPreference("currentLongi", ""+loc.getLongitude());
			            
//			            intent.putExtra("Latitude", loc.getLatitude());
//			            intent.putExtra("Longitude", loc.getLongitude());     
//			            intent.putExtra("Provider", loc.getProvider());                 
			           // sendBroadcast(intent);          
			         //   Toast.makeText( getApplicationContext(), "location updated "+"lat-:"+loc.getLatitude()+" logi-:"+loc.getLongitude(), Toast.LENGTH_SHORT).show();
			        }                
				
			}
		};     
		
	    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
	    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	    
	    Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
           @Override
           public void run() {

        	  String latitude =  sessionPrefs.getPreference("currentLat");
	          String longitude = sessionPrefs.getPreference("currentLongi");
	          JSONObject jsonStr = new JSONObject();
	          try {
	        	  
				  jsonStr.put("driverID", driverId);
				  jsonStr.put("Latitude", latitude);
		          jsonStr.put("Longitude", longitude);
	          }catch (JSONException e) {				
				e.printStackTrace();
	          }
	         
	          Log.e("location updates -:",jsonStr.toString());
	          
	          HelperApi.httpCallToServer("http://phbjharkhand.in/speedyTaxi/Driver_Update_Location.php", jsonStr.toString());  
           }
        }, 0, (60000*1));
	}

	@Override
	public IBinder onBind(Intent intent) {
	    return null;
	}

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        // A new location is always better than no location
	        return true;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
	    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {       
	   // handler.removeCallbacks(sendUpdatesToUI);     
	    super.onDestroy();
	    Log.v("STOP_SERVICE", "DONE");
	    locationManager.removeUpdates(listener);        
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
	    final Thread t = new Thread() {
	        @Override
	        public void run() {
	            try {
	                runnable.run();
	            } finally {
	            	
	            }
	        }
	    };
	    t.start();
	    return t;
	}
}
