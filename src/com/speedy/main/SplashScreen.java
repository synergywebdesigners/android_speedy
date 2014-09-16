package com.speedy.main;

import br.liveo.utils.SessionPrefs;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity{
	
	// Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;
	private SessionPrefs sessionObj;
	private String userType;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
                
        sessionObj = new SessionPrefs(this);		
		userType = sessionObj.getPreference("userType");
 
        new Handler().postDelayed(new Runnable() {
 
 
            @Override
            public void run() {
            	
            	
            	if("customer".equals(userType)){
        			
        			Intent PassToHome = new Intent(getApplicationContext(),
        					NavigationMain.class);
        			startActivity(PassToHome);
        			overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);        			
        			
        		}else if("driver".equals(userType)){
        			String driverStatus = sessionObj.getPreference("vehicleStatus");
        			if("DONE".equals(driverStatus)){
	        			Intent PassToHome = new Intent(getApplicationContext(),
	        					NavigationMain.class);
	        			startActivity(PassToHome);
	        			overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        			}else{
        				Intent PassToHome = new Intent(getApplicationContext(),
	        					VehicleRegisterActivity.class);
	        			startActivity(PassToHome);
	        			overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        			}
        		
        		}else{
		                // This method will be executed once the timer is over
		                // Start your app main activity
		                Intent i = new Intent(SplashScreen.this, AskViewActivity.class);
		                startActivity(i);
		        		overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
        		}
        		// close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 

}
