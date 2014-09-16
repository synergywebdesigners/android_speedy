package com.speedy.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import br.liveo.utils.HttpHandler;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.LoginModel;
import com.speedy.main.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	TextView btn_login, btn_Registration;
	Button login;
	EditText edtEmail, edtPassword;
	String name, age, phone, speed;
	// DBAdapter db;
	public boolean emailcheck = true;
	String str_sessionvalue;
	AlertDialog.Builder alertdialog;
	public static String android_id;
	boolean haveConnectedWifi = false;
	boolean haveConnectedMobile = false;
	private LoginModel loginPJ;
	private SessionPrefs sessionObj = null;
	// private PrefSingleton mMyPreferences;
	private static boolean flag = true;
	private AlertDialog alertDialog;
	ProgressDialog progressDialog;
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");

	private SessionPrefs session = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		inti();

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				

				if (!edtEmail.getText().toString().isEmpty()
						&& checkEmail(edtEmail.getText().toString())) {

				} else {

					edtEmail.requestFocus();
					alertDialog.setTitle("Oops!");
					alertDialog.setMessage("Enter Valid Email Id");
					alertDialog.show();
					return;

				}

				if (edtPassword.getText().toString().equals("")) {
					edtPassword.requestFocus();
					alertDialog.setTitle("Oops!");
					alertDialog.setMessage("Password  field is empty");
					alertDialog.show();
					return;
				}

				
				verifyData();
				submitData();
			}
		});

	}// oncreate ends

	@SuppressWarnings("deprecation")
	private void inti() {

		login = (Button) findViewById(R.id.btnSingIn);
		edtEmail = (EditText) findViewById(R.id.tx_email);
		edtPassword = (EditText) findViewById(R.id.tx_password);
		sessionObj = new SessionPrefs(this);
		alertDialog = new AlertDialog.Builder(LoginActivity.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

		session = new SessionPrefs(this);	
		
	}

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
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

	public void verifyData() {

		String strEmail, strPWD;

		strEmail = edtEmail.getText().toString();
		strPWD = edtPassword.getText().toString();
		loginPJ = new LoginModel(strEmail, strPWD);
	}

	public void submitData() {

		Log.e("json paramerts data", loginPJ.toJSON().toString());
		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/User_Login.php");
			}

			@Override
			public void onResponse(String result) {
				// what to do with result
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				callbackRequestResult(result);
			}

		}.execute(loginPJ.toJSON());
		
		progressDialog = ProgressDialog.show(LoginActivity.this, "",
				"Logging in...");
	}

	protected void callbackRequestResult(String result) {

		Log.e("resullttttttt", "" + result);
		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
			String errorMsg = jsonObj.getString("Error_Msg");

			// {"data":{"Error_Code":"1","Error_Msg":"Login Success","result":{"userID":"1","userType":"customer"}}}
			progressDialog.dismiss();
			
			if ("1".equals(errorCode)) {

//				session.setPreference("userID", jsonObj.getString("userID"));
//				session.setPreference("userType", jsonObj.getString("userType"));
				
				sessionObj.setPreference("userID",jsonObj.getString("userID"));
				sessionObj.setPreference("userType",jsonObj.getString("userType"));				
				sessionObj.setPreference("firstName", jsonObj.getString("firstName"));
				sessionObj.setPreference("lastName",jsonObj.getString("lastName"));
				sessionObj.setPreference("emailId", jsonObj.getString("emailId"));
				sessionObj.setPreference("mobileNumber", jsonObj.getString("mobileNumber"));
				sessionObj.setPreference("address",jsonObj.getString("addressOne"));
				sessionObj.setPreference("profileImage", jsonObj.getString("profileImage"));
				sessionObj.setPreference("vehicleStatus","DONE");
				
				Intent PassToHome = new Intent(getApplicationContext(),
						NavigationMain.class);
				PassToHome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(PassToHome);
				this.finish();
				
			} else {
				
				alertDialog.setTitle("Error !");
				alertDialog.setMessage(errorMsg);
				alertDialog.show();
			}
	
		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
		}

	}
	
	@Override
	public void onBackPressed() {
		

		Intent PassToHome = new Intent(getApplicationContext(),
				AskViewActivity.class);
		startActivity(PassToHome);
		overridePendingTransition(R.anim.lefttoright,
				R.anim.righttoleft);
		super.onBackPressed();
	}

}