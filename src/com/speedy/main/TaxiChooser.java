package com.speedy.main;

import java.math.BigDecimal;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import br.liveo.driver.DriverPersonalInfo;
import br.liveo.fragments.AsynchCallBack;
import br.liveo.fragments.CustomerMapView;
import br.liveo.utils.AsynchronoucsCall;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.TripCustomerDetails;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class TaxiChooser extends ActionBarActivity implements OnClickListener {

	int radiovalidation;
	private RadioGroup taxiRadioGroup, rideRadioGroup;
	private TripCustomerDetails tripObj = null;
	private TextView choosedTaxi, choosePayment;
	private Button btnDate, btnTimePicker;
	// Variable for storing current date and time
	private int mYear, mMonth, mDay, mHour, mMinute;
	public static double fare;
	private SessionPrefs sessionObj = null;
	private AlertDialog alertDialog;
	private TaxiReceiver TaxiReceiver;
	private AsynchronoucsCall asynchSearch;
	private String location_string;
	private String temp_skip;
	public static final String ACTION_CLOSE = "com.speedy.main.ACTION_CLOSE";
	private ProgressDialog progressDialog;
	private RadioButton selectedRadio,selectedRadioRide;
	private Dialog taxiChooseDialog;
	private TextView go;
	private String method;
	private LinearLayout datetimePanel;
	private boolean dateStatus = false;
	private LayoutInflater inflater;
	private DatePickerDialog dpd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vechicle_type);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		init();
		currenttime();
		dateStatus = true;
		setCurrentDate();
		setCurrentTime();

		IntentFilter filter = new IntentFilter(ACTION_CLOSE);
		TaxiReceiver = new TaxiReceiver();
		registerReceiver(TaxiReceiver, filter);

		taxiRadioGroup
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// checkedId is the RadioButton selected

						int selectedId = taxiRadioGroup
								.getCheckedRadioButtonId();

						// find the radiobutton by returned id
						selectedRadio = (RadioButton) taxiChooseDialog
								.findViewById(selectedId);
						choosedTaxi.setText(""
								+ selectedRadio.getText().toString());
						taxiChooseDialog.dismiss();
					}
				});
		
		rideRadioGroup
		.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected

				int selectedId1 = rideRadioGroup
						.getCheckedRadioButtonId();

				selectedRadioRide = (RadioButton)findViewById(selectedId1);
			if("Ride Later".equals(selectedRadioRide.getText().toString()))
			{
						datetimePanel.setVisibility(View.FOCUSABLES_TOUCH_MODE);
				
			}
			
			if("Ride Now".equals(selectedRadioRide.getText().toString())){
						
				dateStatus = true;
				datetimePanel.setVisibility(View.INVISIBLE);
						setCurrentDate();
						setCurrentTime();

						Toast.makeText(getApplicationContext(),
								"Set To Current Date & Time", Toast.LENGTH_LONG).show();
					}
			}
		});
		
		

		choosedTaxi.setFocusableInTouchMode(false);

		choosedTaxi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				taxiChooseDialog.show();
			}
		});

		choosePayment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				genderPicker();

			}
		});

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);// Menu Resource, Menu

		menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(true).setTitle("DONE");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menus.PROCEED:

			String userID = sessionObj.getPreference("userID");
			String dateTime = btnDate.getText().toString() + " "
					+ btnTimePicker.getText().toString();
			String paymentMode = choosePayment.getText().toString();
			String taxiChoose = choosedTaxi.getText().toString();
			String resourceStr = getResources().getString(
					R.string.paymentoptions).toString();
			String resourceStr2 = getResources().getString(R.string.choosetaxi)
					.toString();

			if (resourceStr.equals(paymentMode)) {
				alertDialog.setTitle("Error !");
				alertDialog.setMessage("Please Select Payment Mode.");
				alertDialog.show();
			} else if (resourceStr2.equals(taxiChoose)) {
				alertDialog.setTitle("Error !");
				alertDialog.setMessage("Please Select Vehicle Type.");
				alertDialog.show();
			} else if (!dateStatus) {
				alertDialog.setTitle("Error !");
				alertDialog.setMessage("Please Select Ride Date & Time.");
				alertDialog.show();
			} else {

				tripObj = new TripCustomerDetails(userID, selectedRadio
						.getTag().toString(), selectedRadio.getText()
						.toString(), "" + "", "", "" + "", ""
						+ Locations.SOURCE_LAT, "" + Locations.SOURCE_LOGI, ""
						+ Locations.DESTINATION_LAT, ""
						+ Locations.DESTINATION_LOGI, paymentMode, dateTime);

				submitTripDetails();
			}

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void genderPicker() {

		final CharSequence[] items = { "CASH", "CREDIT CARD" };

		AlertDialog.Builder builder = new AlertDialog.Builder(TaxiChooser.this);
		builder.setTitle("Select Payment Method");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {

				method = items[item].toString();
				choosePayment.setText(method);

				dialog.dismiss();

			}
		}).show();

	}

	@SuppressWarnings("deprecation")
	private void init() {
		// set the custom dialog components - text, image and button

		taxiChooseDialog = new Dialog(this);
		taxiChooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		taxiChooseDialog.setContentView(R.layout.taxi_choosedialog);
		taxiChooseDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		taxiChooseDialog.setCanceledOnTouchOutside(false);

		taxiRadioGroup = (RadioGroup) taxiChooseDialog
				.findViewById(R.id.radioTaxi);
		radiovalidation = taxiRadioGroup.getCheckedRadioButtonId();
		rideRadioGroup = (RadioGroup)findViewById(R.id.rideRadio);
		btnDate = (Button) findViewById(R.id.date);
		btnTimePicker = (Button) findViewById(R.id.time);
		choosedTaxi = (TextView) findViewById(R.id.chooseTaxi);
		choosePayment = (TextView) findViewById(R.id.PaymentMethod);
		datetimePanel = (LinearLayout) findViewById(R.id.datetimePanel);
		inflater = getLayoutInflater();

		btnDate.setOnClickListener(this);
		btnTimePicker.setOnClickListener(this);
		sessionObj = new SessionPrefs(this);
		alertDialog = new AlertDialog.Builder(TaxiChooser.this).create();

		Intent i = getIntent();
		temp_skip = i.getStringExtra("Skip");

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

		setCurrentDate();
		setCurrentTime();

	}

	public void setCurrentDate() {

		// Process to get Current Date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		btnDate.setText(mDay + "-" + (mMonth+1) + "-" + mYear);

	}

	public void setCurrentTime() {
		// Process to get Current Time
		final Calendar c = Calendar.getInstance();
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		btnTimePicker.setText(mHour + ":" + mMinute);
	}

	@Override
	public void onClick(View v) {

		if (v == btnDate) {

			// Launch Date Picker Dialog
			dpd = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							// Display Selected date in textbox
							btnDate.setText(dayOfMonth + "-"
									+ (monthOfYear + 1) + "-" + year);
							
							dateStatus = true;

							
							Calendar cal = Calendar.getInstance();
							cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
							cal.set(Calendar.MONTH, monthOfYear);
							cal.set(Calendar.YEAR, year);
							
							long yourDateMillis = System.currentTimeMillis() + (21 * 24 * 60 * 60 * 1000);
							long selectedDateMills = cal.getTimeInMillis();
							if(selectedDateMills>yourDateMillis){
								alertDialog.setTitle("Alert !");
								alertDialog.setMessage("Date range will be 3 weeks only.");
								alertDialog.show();
								setCurrentDate();
							
							}else {
								
							}
							
							if (cal.after(Calendar.getInstance())){
								
							}else {
								alertDialog.setTitle("Alert !");
								alertDialog.setMessage("Select Future date");
								alertDialog.show();
								
							}

						}
					}, mYear, mMonth, mDay);
			dpd.show();
		}
		if (v == btnTimePicker) {

			// Launch Time Picker Dialog
			TimePickerDialog tpd = new TimePickerDialog(this,
					new TimePickerDialog.OnTimeSetListener() {

						@Override
						public void onTimeSet(TimePicker view, int hourOfDay,
								int minute) {
							// Display Selected time in textbox
							btnTimePicker.setText(hourOfDay + ":" + minute);
							dateStatus = true;
						}
					}, mHour, mMinute, false);
			tpd.show();
		}

	}

	private void currenttime() {
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// set current date into textview
		btnDate.setText(new StringBuilder()
				// Month is 0 based, just add 1
				.append(mDay).append("-").append(mMonth + 1).append("-")
				.append(mYear));

		int minutes = c.get(Calendar.MINUTE);

		if (DateFormat.is24HourFormat(this)) {
			int hours = c.get(Calendar.HOUR_OF_DAY);
			btnTimePicker.setText((hours < 10 ? "0" + hours : hours) + ":"
					+ (minutes < 10 ? "0" + minutes : minutes));
		} else {
			int hours = c.get(Calendar.HOUR);
			btnTimePicker.setText(hours
					+ ":"
					+ (minutes < 10 ? "0" + minutes : minutes)
					+ " "
					+ new DateFormatSymbols().getAmPmStrings()[c
							.get(Calendar.AM_PM)]);
		}

	}

	private void submitTripDetails() {
		if (temp_skip.equals("skip")) {

			Log.e("trip details json paramerts data", tripObj.toJSON()
					.toString());
			progressDialog = ProgressDialog.show(TaxiChooser.this, "",
					"Submitting...");

			new HttpHandler() {
				@Override
				public HttpUriRequest getHttpRequestMethod() {
					return new HttpPost(
							"http://phbjharkhand.in/speedyTaxi/Add_Trip_Details.php");
				}

				@Override
				public void onResponse(String result) { // what to do with
					// e.g. display it on edit text etResponse.setText(result);
					Log.e("Response data by registration=:", result);
					callbackRequestResult(result);
				}

			}.execute(tripObj.toJSON());

		} else {
			Intent tripdata = new Intent(getApplicationContext(),
					CustomerTripDetails.class);
			tripdata.putExtra("customer_trip_details", tripObj);
			tripdata.putExtra("radio_value", selectedRadio.getText());
			startActivity(tripdata);

			Log.e("trip details json paramerts data", tripObj.toJSON()
					.toString());
		}

	}

	private void callbackRequestResult(String result) {
		Log.e("response result-:", result);

		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");

			if ("1".equals(errorCode)) {
				String tripId = jsonObj.getString("tripID");

				JSONObject tripIdJson = new JSONObject();
				tripIdJson.put("tripID", tripId);

				new HttpHandler() {
					@Override
					public HttpUriRequest getHttpRequestMethod() {
						return new HttpPost(
								"http://phbjharkhand.in/speedyTaxi/Get_Driver_Allocation.php");
					}

					@Override
					public void onResponse(String result) { // what to do with
						// e.g. display it on edit text
						// etResponse.setText(result);
						Log.e("Response data by registration=:", result);
						callbackRequestResultDemo(result);
					}

				}.execute(tripIdJson.toString());

			} else {

				progressDialog.dismiss();

				String errorMsg = jsonObj.getString("Error_Msg");
				alertDialog.setTitle("Error !");
				alertDialog.setMessage(errorMsg);
				alertDialog.show();
			}

		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
			progressDialog.dismiss();

		}

	}

	public void callbackRequestResultDemo(String result) {

		Log.e("response result-:", result);
		progressDialog.dismiss();
		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");

			if ("1".equals(errorCode)) {

				Intent myIntent1 = new Intent(CustomerMapView.ACTION_CLEAR_MAP_REQUEST);
				sendBroadcast(myIntent1);

				this.finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
			progressDialog.dismiss();
		}
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	private static BigDecimal truncateDecimal(double x, int numberofDecimals) {
		if (x > 0) {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
					BigDecimal.ROUND_FLOOR);
		} else {
			return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals,
					BigDecimal.ROUND_CEILING);
		}
	}

	class TaxiReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("FirstReceiver", "FirstReceiver");
			if (intent.getAction().equals(ACTION_CLOSE)) {
				TaxiChooser.this.finish();
			}
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(TaxiReceiver);
		super.onDestroy();
	}

}
