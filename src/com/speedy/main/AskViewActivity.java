package com.speedy.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import br.liveo.driver.DriverPersonalInfo;
import br.liveo.utils.SessionPrefs;

public class AskViewActivity extends ActionBarActivity {

	private TextView btn_login, user_reg, driver_add, register;
	private EditText et_name, et_age, et_phone, et_speed;
	private String name, age, phone, speed;
	// DBAdapter db;
	public boolean emailcheck = true;
	private String str_sessionvalue;
	private AlertDialog.Builder alertdialog;
	public static String android_id;
	boolean haveConnectedWifi = false;
	boolean haveConnectedMobile = false;
	// private PrefSingleton mMyPreferences;
	private static boolean flag = true;
	private SessionPrefs sessionObj = null;
	private Dialog reg_dialog;
	private String userType;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);	
		setContentView(R.layout.askview);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);
		sessionObj = new SessionPrefs(this);
		
		userType = sessionObj.getPreference("userType");
		
			init();
			
			register.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View arg0) {
					reg_dialog.show();
				}
			});
	
			btn_login.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
	
					Intent PassToHome = new Intent(getApplicationContext(),
							LoginActivity.class);
					startActivity(PassToHome);
					overridePendingTransition(R.anim.lefttoright,
							R.anim.righttoleft);
					AskViewActivity.this.finish();
				}
			});
	
			user_reg.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
	
					Intent PassToHome = new Intent(getApplicationContext(),
							RegisterActivity.class);
					startActivity(PassToHome);
					overridePendingTransition(R.anim.lefttoright,
							R.anim.righttoleft);
					reg_dialog.dismiss();
				}
			});
			driver_add.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
	
					startActivity(new Intent(getApplicationContext(),
							DriverPersonalInfo.class));
					overridePendingTransition(R.anim.lefttoright,
							R.anim.righttoleft);
					reg_dialog.dismiss();
				}
			});
		
	

	}// oncreate ends

	
	private void init() {
		
		btn_login = (TextView) findViewById(R.id.signin);
		register = (TextView) findViewById(R.id.signup);

		reg_dialog = new Dialog(this);
		reg_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		reg_dialog.setContentView(R.layout.dailog_dri_cust);
		// dialog.setTitle("Title...");
		reg_dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		reg_dialog.setCanceledOnTouchOutside(false);

		driver_add = (TextView) reg_dialog.findViewById(R.id.driver);
		user_reg = (TextView) reg_dialog.findViewById(R.id.user);
		
	}

	
	private boolean haveNetworkConnection() {

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return (haveConnectedWifi || haveConnectedMobile);

	}

	public void checkemail(String email) {
		Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+");
		Matcher matcher = pattern.matcher(email);
		emailcheck = matcher.matches();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		overridePendingTransition(R.anim.lefttoright,
				R.anim.righttoleft);
	}
}