package com.speedy.main;

import static com.speedy.main.CommonUtilities.SENDER_ID;
import static com.speedy.main.CommonUtilities.displayMessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, "Your device registred with GCM");
        Log.d("NAME", NavigationMain.userID);
        ServerUtilities.register(context, NavigationMain.userID, NavigationMain.userType, registrationId);
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        ServerUtilities.unregister(context, registrationId);
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("Message");
     
        if(message==null||"".equals(message)){
        	
        }else{
        	Log.e("notiMSG",message);
            
            JSONObject jsonObj;
            JSONObject josnString;
            String josnMsg =null;
            String josnDriverId=null; 
            String tripID = null;
            String userType = null;
    		try {
    			
    			jsonObj = new JSONObject(message);		
    	        josnString = jsonObj.getJSONObject("data");
    	        userType = josnString.getString("userType");
    	        josnMsg = josnString.getString("Message") ;	        
    	        josnDriverId = josnString.getString("driverID");
    	        tripID = josnString.getString("tripID");
    	        
    		} catch (JSONException e) {			
    			e.printStackTrace();
    			josnMsg = null;
    		}catch(Exception e){
    			e.printStackTrace();
    			josnMsg = null;
    		}		
            	
            if(josnMsg==null){
            	
            }else{
    	        displayMessage(context, josnMsg);
    	        // notifies user
    	        generateNotification(context, josnMsg,josnDriverId,tripID,userType);
            }    
        }
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        
        Log.e("notiMSG",message);
        
        JSONObject jsonObj;
        JSONObject josnString;
        String josnMsg =null;
        String josnDriverId=null; 
        String tripID = null;
        String userType = null;
		try {
			
			jsonObj = new JSONObject(message);		
	        josnString = jsonObj.getJSONObject("data");
	        userType = josnString.getString("userType");
	        josnMsg = josnString.getString("Message") ;	        
	        josnDriverId = josnString.getString("driverID");
	        tripID = josnString.getString("tripID");
	        
		} catch (JSONException e) {			
			e.printStackTrace();
			josnMsg = null;
		}catch(Exception e){
			e.printStackTrace();
			josnMsg = null;
		}		
        	
        if(josnMsg==null){
        	
        }else{
	        displayMessage(context, josnMsg);
	        // notifies user
	        generateNotification(context, josnMsg,josnDriverId,tripID,userType);
        }    
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message, String driverId,String tripID,String userType) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        
        String title = context.getString(R.string.app_name);
        Intent notificationIntent= null;
        Bundle bundle = null;
        
        if("Customer".equals(userType)){
        	notificationIntent = new Intent(context, NavigationMain.class);
        	bundle = new Bundle();
            bundle.putString("msg", message);
            bundle.putString("userType", userType);
            bundle.putString("driverId", driverId);
            bundle.putString("tripID", tripID);
        }else if("driver".equals(userType)){
        	notificationIntent = new Intent(context, NoficationDemoActivity.class);
        	bundle = new Bundle();
        	bundle.putString("userType", userType);
            bundle.putString("msg", message);
            bundle.putString("driverId", driverId);
            bundle.putString("tripID", tripID);
        }else{
        	
        }
       
        notificationIntent.putExtras(bundle);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        // Play default notification sound
        notification.defaults |= Notification.DEFAULT_SOUND;
        
        //notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "your_sound_file_name.mp3");
        
        // Vibrate if vibrate is enabled
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, notification);      

    }

}
