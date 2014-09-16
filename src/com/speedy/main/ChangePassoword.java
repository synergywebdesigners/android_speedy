package com.speedy.main;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.speedy.bo.TripCustomerDetails;

import br.liveo.adapter.RidesAdapter;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePassoword extends ActionBarActivity {

	EditText currentPassword, tx_password, tx_retypepassowrd;
	private AlertDialog alertDialog;
	private SessionPrefs session;
	private ProgressDialog progressDialog;
	private boolean flagPassowrdChange = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.change_password);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		alertDialog = new AlertDialog.Builder(ChangePassoword.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
				if (flagPassowrdChange) {
					ChangePassoword.this.finish();
				}
			}
		});

		session = new SessionPrefs(this);
		currentPassword = (EditText) findViewById(R.id.currentPassword);
		tx_password = (EditText) findViewById(R.id.tx_password);
		tx_retypepassowrd = (EditText) findViewById(R.id.tx_retypepassowrd);

	}

	protected void updatePassword(String oldPass, String newPass) {

		JSONObject requestParams = new JSONObject();
		try {
			requestParams.put("userID", session.getPreference("userID"));
			requestParams.put("userType", session.getPreference("userType"));
			requestParams.put("oldPassword", oldPass);
			requestParams.put("newPassword", newPass);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		Log.e("result", requestParams.toString());

		progressDialog = ProgressDialog.show(this, "", "Saving...");

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Change_Password.php");
			}

			@Override
			public void onResponse(String result) {
				Log.e("Response data by registration=:", result);

				callbackRequestResult(result);
			}

		}.execute(requestParams.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);// Menu Resource, Menu

		 menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(true).setTitle("UPDATE");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menus.PROCEED:
			String txPassword,
			txRePassword,
			currentPass;
			txPassword = tx_password.getText().toString();
			txRePassword = tx_retypepassowrd.getText().toString();
			currentPass = currentPassword.getText().toString();

			if (!("".equals(txPassword)) && txPassword != null
					&& !("".equals(txRePassword)) && txRePassword != null
					&& !("".equals(currentPass)) && currentPass != null) {

				if (txPassword.equals(txRePassword)) {

					updatePassword(currentPass, txPassword);

				} else {
					tx_password.setText("");
					tx_retypepassowrd.setText("");

					showMessage("Passwords do not match");
				}

			} else {

				showMessage("Password  field is empty");
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("deprecation")
	protected void callbackRequestResult(String result) {

		progressDialog.dismiss();

		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
			String errorMsg = jsonObj.getString("Error_Msg");

			if ("1".equals(errorCode)) {
				showMessage(errorMsg);
				flagPassowrdChange = true;
			} else {
				showMessage(errorMsg);
				flagPassowrdChange = false;
			}

		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
		}

	}

	public void showMessage(String msg) {
		alertDialog.setTitle("Alert!");
		alertDialog.setMessage(msg);
		alertDialog.show();
	}

}
