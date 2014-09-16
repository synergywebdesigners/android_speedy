
package com.speedy.main;

import static com.speedy.main.CommonUtilities.DISPLAY_MESSAGE_ACTION;

import static com.speedy.main.CommonUtilities.EXTRA_MESSAGE;
import static com.speedy.main.CommonUtilities.SENDER_ID;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.adapter.NavigationAdapter;
import br.liveo.fragments.CustomerMapView;
import br.liveo.fragments.DriveRidesDetailsFragment;
import br.liveo.fragments.DriverMapView;
import br.liveo.fragments.ProfileFragment;
import br.liveo.fragments.ViewPagerFragment;
import br.liveo.utils.Constant;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;
import br.liveo.utils.Utils;

import com.speedy.main.AlertDialogManager;
import com.speedy.main.ServerUtilities;
import com.speedy.main.WakeLocker;
import com.google.android.gcm.GCMRegistrar;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class NavigationMain extends ActionBarActivity{
		
    private int lastPosition = 0;
	private ListView listDrawer;    
		
	private int counterItemDownloads;
	
	private DrawerLayout layoutDrawer;		
	private LinearLayout linearDrawer;
	private RelativeLayout userDrawer;

	private NavigationAdapter navigationAdapter;
	private ActionBarDrawerToggleCompat drawerToggle;	
		
	public static FragmentManager fragmentManager;
	
	static boolean flagFragment = false;
	
	private String contactID = null;
	SessionPrefs  session = null;
	Intent bundleIntent = null;
	private TextView userName;
	private TextView emailID;
	private ImageView profileImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		
		setContentView(R.layout.navigation_main);		
		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);		
        
        listDrawer = (ListView) findViewById(R.id.listDrawer);        
		linearDrawer = (LinearLayout) findViewById(R.id.linearDrawer);		
		layoutDrawer = (DrawerLayout) findViewById(R.id.layoutDrawer);
		userName = (TextView)findViewById(R.id.tituloDrawer);
		emailID = (TextView)findViewById(R.id.subTituloDrawer);
		profileImg = (ImageView)findViewById(R.id.ImgDrawer);
		
		userDrawer = (RelativeLayout) findViewById(R.id.userDrawer);
		userDrawer.setOnClickListener(userOnClick);
		
		if (listDrawer != null) {
			navigationAdapter = NavigationList.getNavigationAdapter(this);
		}
		
		listDrawer.setAdapter(navigationAdapter);
		listDrawer.setOnItemClickListener(new DrawerItemClickListener());

		drawerToggle = new ActionBarDrawerToggleCompat(this, layoutDrawer);		
		layoutDrawer.setDrawerListener(drawerToggle);
       		
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		if (savedInstanceState != null) { 			
			setLastPosition(savedInstanceState.getInt(Constant.LAST_POSITION)); 				
			
			if (lastPosition < 5){
				navigationAdapter.resetarCheck();			
				navigationAdapter.setChecked(lastPosition, true);
			}    	
			
	    }else{
	    	setLastPosition(lastPosition); 
	    	setFragmentList(lastPosition);	    	
	    }			
		
		session = new SessionPrefs(getApplicationContext());
		
		bundleIntent = getIntent();
		
		userName.setText(session.getPreference("firstName")+" "+session.getPreference("lastName"));
		emailID.setText(session.getPreference("emailId"));
		setBase64Image();
	}
	
	public void setBase64Image() {
		try{
			String imgURL = session.getPreference("profileImage");
			setImage(imgURL);
		}catch(Exception e){
			e.printStackTrace();			
		}	
	}
	
	
	public void setImage(String url) {
	
		UrlImageViewHelper.setUrlDrawable(profileImg,url, R.drawable.ic_user1, new UrlImageViewCallback() {
            @Override
            public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url, boolean loadedFromCache) {
                if (!loadedFromCache) {
                    ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
                    scale.setDuration(300);
                    scale.setInterpolator(new OvershootInterpolator());
                    imageView.startAnimation(scale);
                }
            }
        });
	   		
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {	
		super.onSaveInstanceState(outState);		
		outState.putInt(Constant.LAST_POSITION, lastPosition);					
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {  

        if (drawerToggle.onOptionsItemSelected(item)) {
              return true;
        }		
        
		switch (item.getItemId()) {		
		case Menus.HOME:
			if (layoutDrawer.isDrawerOpen(linearDrawer)) {
				layoutDrawer.closeDrawer(linearDrawer);
			} else {
				layoutDrawer.openDrawer(linearDrawer);
			}
			return true;			
		default:
			return super.onOptionsItemSelected(item);			
		}		             
    }
		
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	//hideMenus(menu, lastPosition);
        return super.onPrepareOptionsMenu(menu);  
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);        		
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);	     
	    drawerToggle.syncState();	
	 }	
	
	public void setTitleActionBar(CharSequence informacao) {    	
    	getSupportActionBar().setTitle(informacao);
    }	
	
	public void setSubtitleActionBar(CharSequence informacao) {    	
    	getSupportActionBar().setSubtitle(informacao);
    }	

	public void setIconActionBar(int icon) {    	
    	getSupportActionBar().setIcon(icon);
    }	
	
	public void setLastPosition(int posicao){		
		this.lastPosition = posicao;
	}	
		
	private class ActionBarDrawerToggleCompat extends ActionBarDrawerToggle {

		public ActionBarDrawerToggleCompat(Activity mActivity, DrawerLayout mDrawerLayout){
			super(
			    mActivity,
			    mDrawerLayout, 
  			    R.drawable.ic_action_navigation_drawer, 
				R.string.drawer_open,
				R.string.drawer_close);
		}
		
		@Override
		public void onDrawerClosed(View view) {			
			supportInvalidateOptionsMenu();				
		}

		@Override
		public void onDrawerOpened(View drawerView) {	
			navigationAdapter.notifyDataSetChanged();			
			supportInvalidateOptionsMenu();			
		}		
	}
		  
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);		
	}
	
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int posicao, long id) {          	        	
	    	setLastPosition(posicao);        	
	    	setFragmentList(lastPosition);	    	
	    	layoutDrawer.closeDrawer(linearDrawer);	    	
        }
    }	
    
	private OnClickListener userOnClick = new OnClickListener() {		
		@Override
		public void onClick(View v) {			
			layoutDrawer.closeDrawer(linearDrawer);
		}
	};	
	
	private void setFragmentList(int position){
		
		 fragmentManager = getSupportFragmentManager();							
		
		switch (position) {
		case 0:			
			
			session = new SessionPrefs(getApplicationContext());
			
			String userTypeTemp = session.getPreference("userType");
			if("customer".equals(userTypeTemp)){							
				
				String id = session.getPreference("userID");		
				if(id!=null ||!("".equals(id))){			
					userID = id;
					userType ="customer";
					registerDevice();
				}else{
					userID = session.getPreference("userID");
				}
								
				fragmentManager.beginTransaction().replace(R.id.content_frame,new CustomerMapView()).commit();
								
			}else{				
										
				//getting driverID after driver done his registartion...			
				
				String id = session.getPreference("userID");		
				if(id!=null ||!("".equals(id))){	
					Log.e("calling register method..","userid=");
					userID = id;
					userType = "driver";				
					registerDevice();
				}else{
					userID = session.getPreference("userID");
				}
				
				// use this to start and trigger a service
				Intent i= new Intent(this, LocationService.class);
				i.putExtra("driverId", userID);
				this.startService(i); 

				fragmentManager.beginTransaction().replace(R.id.content_frame,new DriverMapView()).commit();
			}
			
			break;	
			
		case 1:
			fragmentManager.beginTransaction().replace(R.id.content_frame,new ProfileFragment()).commit();
			 
		break;
		
		case 2:
			
			String userType = session.getPreference("userType");
			
			if("customer".equals(userType)){							
				fragmentManager.beginTransaction().replace(R.id.content_frame,new ViewPagerFragment()).commit();
			}else{					
				fragmentManager.beginTransaction().replace(R.id.content_frame,new DriveRidesDetailsFragment()).commit();
			}
			 
		break;
			
		case 4:
				session.clearAllPreferences();
				 Intent logout = new Intent(NavigationMain.this,AskViewActivity.class);
				 startActivity(logout);
				 NavigationMain.this.finish();
				 
			break;
			
		default:
			Toast.makeText(getApplicationContext(), "implement other fragments here", Toast.LENGTH_SHORT).show();
			break;	
		}			
	
		if (position < 5){
			navigationAdapter.resetarCheck();			
			navigationAdapter.setChecked(position, true);
		}
	}

   /* private void hideMenus(Menu menu, int posicao) {
    	    	
        boolean drawerOpen = layoutDrawer.isDrawerOpen(linearDrawer);    	
    	
        switch (posicao) {
		case 1:        	             	        	       
	        menu.findItem(Menus.SEARCH).setVisible(!drawerOpen);        
			break;
			
		case 2:	        	        	       
	        menu.findItem(Menus.SEARCH).setVisible(!drawerOpen);        			
			break;				
			//implement other fragments here			
		}          
    }	*/

	public void setTitleFragments(int position){	
		setIconActionBar(Utils.iconNavigation[position]);
		setSubtitleActionBar(Utils.getTitleItem(NavigationMain.this, position));				
	}

	public int getCounterItemDownloads() {
		return counterItemDownloads;
	}

	public void setCounterItemDownloads(int value) {
		this.counterItemDownloads = value;
	}
	
	
	
	/*-----------------------  push notification functionality --------------------------------------*/
	 
	
	// Asyntask
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	// Alert dialog manager
	private AlertDialogManager alert = new AlertDialogManager();
	// Alert dialog manager
	private AlertDialogManager lblMessage = new AlertDialogManager();
	
	// Connection detector
	private ConnectionDetector cd;
	public static String userID;
	public static String userType;
	
	public void registerDevice(){
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(NavigationMain.this,
					"Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Getting name, email from intent
		Intent i = getIntent();		
		
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
					
		registerReceiver(mHandleMessageReceiver, new IntentFilter(
				DISPLAY_MESSAGE_ACTION));
		
		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(NavigationMain.this);

//		Toast.makeText(getApplicationContext(), "register with GCM=:"+SENDER_ID+"  regID=:"+regId, Toast.LENGTH_LONG).show();
		Log.i("register with GCM--",SENDER_ID);
		
		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM			
			GCMRegistrar.register(this, SENDER_ID);
			
		} else {
			// Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.				
//				Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
				Log.i("register with GCM--", "Already registered with GCM");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						// Register on our server
						// On server creates a new user
						ServerUtilities.register(context, userID, userType, regId);
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
		
	}


	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
				// Waking up mobile if it is sleeping
				WakeLocker.acquire(getApplicationContext());
				
				/**
				 * Take appropriate action on this message
				 * depending upon your app requirement
				 * For now i am just displaying it on the screen
				 * */
				
				// Showing received message
				//lblMessage.append(newMessage + "\n");			
				Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
				
				// Releasing wake lock
				WakeLocker.release();
			}catch(Exception e){
				e.printStackTrace();
			}	
		}
	};
	
//	 push notification completed here ------------------------------------------------------------
	
	
	@Override
	protected void onDestroy() {
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		try {
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		} catch (Exception e) {
			Log.e("UnRegister Receiver Error", "> " + e.getMessage());
		}
		super.onDestroy();
	}
}
