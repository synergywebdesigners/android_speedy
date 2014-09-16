package br.liveo.fragments;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.speedy.bo.SignUpModel;
import com.speedy.main.ChangePassoword;
import com.speedy.main.LoginActivity;
import com.speedy.main.NavigationMain;
import com.speedy.main.R;
import com.speedy.main.RegisterActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

public class ProfileFragment extends Fragment {

	private TextView txtFragmentDownload;
	private boolean searchCheck;
	private EditText emailadd;
	private EditText fname;
	private EditText mnumber;
	private EditText address;
	SessionPrefs session = null;
	ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private EditText profile_password;
	private EditText lname;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.profile, container, false);

		emailadd = (EditText) rootView.findViewById(R.id.emailadd);
		fname = (EditText) rootView.findViewById(R.id.profile_fname);
		mnumber = (EditText) rootView.findViewById(R.id.profile_mobnumber);
		address = (EditText) rootView.findViewById(R.id.profile_add);
		profile_password = (EditText)rootView.findViewById(R.id.profile_password);
		lname = (EditText)rootView.findViewById(R.id.profile_lname);

	
		rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
		session = new SessionPrefs(getActivity());
	
		emailadd.setText(session.getPreference("emailId"));
		fname.setText(session.getPreference("firstName"));
		mnumber.setText(session.getPreference("mobileNumber"));
		address.setText(session.getPreference("address"));
		lname.setText(session.getPreference("lastName"));
		
		profile_password.setFocusable(false);
		
		profile_password.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent newIntent = new Intent(getActivity(),ChangePassoword.class);
				startActivity(newIntent);
				
			}
		});
	}

	private void setUpdateddata() {

		session.setPreference("firstName", fname.getText().toString());
		session.setPreference("emailId", emailadd.getText().toString());
		session.setPreference("mobileNumber", mnumber.getText().toString());
		session.setPreference("address", address.getText().toString());
	}

	@SuppressWarnings("deprecation")
	private void callbackRequestResult(String result) {
		// {"data":{"Error_Code":"1","Error_Msg":"Registration Successfully"}}
		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
			String errorMsg = jsonObj.getString("Error_Msg");

			if ("1".equals(errorCode)) {
				
				setUpdateddata();
				alertDialog = new AlertDialog.Builder(getActivity()).create();

				// Setting OK Button
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Write your code here to execute after dialog closed
					}
				});
				
				alertDialog.setMessage(errorMsg);
				alertDialog.show();
				
				
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu, menu);
		
		menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(true).setTitle("UPDATE");
		menu.findItem(Menus.CANCEL).setVisible(false);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case Menus.PROCEED:
			SignUpModel signupPJ = new SignUpModel(fname.getText()
					.toString(),lname.getText().toString(), address
					.getText().toString(), "", emailadd.getText()
					.toString(), "", "", "", "", mnumber.getText()
					.toString(), "", "", "", "customer", "");

			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(signupPJ.toJSON());
				jsonObj.put("flag", "update");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.e("stringg", "" + jsonObj.toString());
			progressDialog = ProgressDialog.show(getActivity(), "",
					"Saving...");

			new HttpHandler() {
				@Override
				public HttpUriRequest getHttpRequestMethod() {

					return new HttpPost(
							"http://phbjharkhand.in/speedyTaxi/User_Registration.php");
				}

				@Override
				public void onResponse(String result) {
					// what to do with result
					// e.g. display it on edit text
					// etResponse.setText(result);
					Log.e("Response data by registration=:", result);

					progressDialog.dismiss();
				
					callbackRequestResult(result);
				}

			}.execute(jsonObj.toString());

			break;
		}
		return true;
	}

}
