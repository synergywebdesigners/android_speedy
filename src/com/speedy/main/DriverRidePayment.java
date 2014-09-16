package com.speedy.main;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.FaresDetails;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.SessionPrefs;

import com.braintreepayments.demo.MainActivity;
import com.google.android.gms.wallet.Cart;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.speedy.bo.RideModel;

public class DriverRidePayment extends Activity {

	private AlertDialog alertDialog;
	private SessionPrefs session;
	private ProgressDialog progressDialog;

	private ImageView user;
	private EditText driver_fname, driver_lname;
	private TextView total_distance, total_time, total_fare;
	private Dialog paymentDialog;
	private TextView callUser;
	private RideModel ride_detail;
	private TextView paymentMode;
	private TextView makePayment;
	private String tripID, PaymentStatus;
	private RelativeLayout payment_progressdialogview;
	private Dialog dialogResume;
	private TextView cashPaymentBtn;
	private TextView driverName, CustName, totalDistance, totalTime, totalFare;
	private String distance, timeInSecondStr, vechileType, vehicleSubType,
			faretemp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.driver_payment_view);
		init();

		if ("Completed".equals(ride_detail.getTripStatus())
				&& "Done".equals(PaymentStatus)) {
			makePayment.setText("Payment Done");
			makePayment.setEnabled(false);
		} else if ("Completed".equals(ride_detail.getTripStatus())
				&& !("Done".equals(PaymentStatus))) {
			makePayment.setText("Make Payment");
			makePayment.setEnabled(true);
		} else {
			makePayment.setText("Ride Pending");
			makePayment.setEnabled(false);
		}

		callUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String number = ride_detail.getMobileNumber();
				if (!("".equals(number))) {
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:" + number));
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							"Number not Available for call", Toast.LENGTH_LONG)
							.show();
				}

			}
		});

		makePayment.setOnClickListener(new OnClickListener() {

			private ConnectionDetector cd;

			@Override
			public void onClick(View arg0) {

				cd = new ConnectionDetector(getApplicationContext());

				// Check if Internet present
				if (!cd.isConnectingToInternet()) {
					// Internet Connection is not present

					alertDialog.setTitle("Internet Connection Error");
					alertDialog
							.setMessage("Please connect to working Internet connection");
					alertDialog.show();
				} else {

					if ("CASH".equals(ride_detail.getPaymentMode())) {
						dialogResume.show();
					} else {

						Intent intent = new Intent(getApplicationContext(),
								MainActivity.class);
						intent.putExtra("fare", faretemp);

						intent.putExtra("trip_id", ride_detail.getTripId());
						intent.putExtra("trip_time", ride_detail.getRide_Time());
						intent.putExtra("trip_date", ride_detail.getRide_Date());

						startActivity(intent);

					}
				}
			}
		});

	}

	@SuppressWarnings("deprecation")
	public void init() {

		session = new SessionPrefs(this);

		user = (ImageView) findViewById(R.id.userpicture);
		driver_fname = (EditText) findViewById(R.id.dri_fname);
		driver_lname = (EditText) findViewById(R.id.dri_lname);
		total_distance = (TextView) findViewById(R.id.total_distance);
		total_time = (TextView) findViewById(R.id.total_time);
		total_fare = (TextView) findViewById(R.id.total_fare);
		callUser = (TextView) findViewById(R.id.callUser);
		paymentMode = (TextView) findViewById(R.id.driverpaymentmode);
		makePayment = (TextView) findViewById(R.id.makePayment);
		payment_progressdialogview = (RelativeLayout) findViewById(R.id.payment_progressdialogview);

		driver_fname.setFocusable(false);
		driver_lname.setFocusable(false);
		Bundle data = getIntent().getExtras();
		ride_detail = data.getParcelable("ride_details");

		if (ride_detail != null) {
			tripID = ride_detail.getTripId();
			PaymentStatus = ride_detail.getPaymentStatus();

			try {
				String names[] = ride_detail.getUserName().split(" ");
				driver_fname.setText(names[0]);
				driver_lname.setText(names[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				driver_fname.setText(ride_detail.getUserName());
			}
			try {
				paymentMode.setText(ride_detail.getPaymentMode());
				setImage(ride_detail.getProfileImage());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		alertDialog = new AlertDialog.Builder(DriverRidePayment.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

		paymentDialog = new Dialog(this);
		paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		paymentDialog.setContentView(R.layout.paymentdailog);
		paymentDialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		paymentDialog.setCanceledOnTouchOutside(false);

		getPaymentDetails();

	}

	public void setImage(String url) {

		UrlImageViewHelper.setUrlDrawable(user, url, R.drawable.ic_user1,
				new UrlImageViewCallback() {
					@Override
					public void onLoaded(ImageView imageView,
							Bitmap loadedBitmap, String url,
							boolean loadedFromCache) {
						if (!loadedFromCache) {
							ScaleAnimation scale = new ScaleAnimation(0, 1, 0,
									1, ScaleAnimation.RELATIVE_TO_SELF, .5f,
									ScaleAnimation.RELATIVE_TO_SELF, .5f);
							scale.setDuration(300);
							scale.setInterpolator(new OvershootInterpolator());
							imageView.startAnimation(scale);
						}
					}
				});
	}

	private void CashPaymentDialog() {
		dialogResume = new Dialog(this);
		dialogResume.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogResume.setContentView(R.layout.cash_payment_receipt);
		// dialog.setTitle("Title...");
		dialogResume.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogResume.setCanceledOnTouchOutside(false);

		cashPaymentBtn = (TextView) dialogResume.findViewById(R.id.cashPayment);
		driverName = (TextView) dialogResume.findViewById(R.id.driverName);
		CustName = (TextView) dialogResume.findViewById(R.id.custName);
		totalDistance = (TextView) dialogResume
				.findViewById(R.id.total_distance);
		totalTime = (TextView) dialogResume.findViewById(R.id.total_time);
		totalFare = (TextView) dialogResume.findViewById(R.id.total_fare);

		CustName.setText(ride_detail.getUserName());
		driverName.setText(session.getPreference("firstName"));
		totalDistance.setText(distance);
		totalTime.setText(timeInSecondStr);
		totalFare.setText(faretemp);
		cashPaymentBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				submitInvoiceDetails();
				dialogResume.dismiss();
				progressDialog = ProgressDialog.show(DriverRidePayment.this,
						"", "Processing...");
			}
		});

	}

	private void getPaymentDetails() {

		JSONObject tripIdJson = new JSONObject();
		try {
			tripIdJson.put("tripID", tripID);
		} catch (JSONException e) {

			e.printStackTrace();
		}

		Log.e("json data =:", tripIdJson.toString());

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {
				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Ride_Payment_Details.php");
			}

			@Override
			public void onResponse(String result) { // what to do with
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				callbackRequestResult(result);
			}
		}.execute(tripIdJson.toString());

	}

	protected void callbackRequestResult(String result) {

		Log.e("result", result);

		payment_progressdialogview.setVisibility(View.VISIBLE);
		try {

			JSONObject resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");

			if ("1".equals(errorCode)) {
				payment_progressdialogview.setVisibility(View.INVISIBLE);
				JSONArray jsonArray = jsonObj.getJSONArray("result");
				JSONObject json = jsonArray.getJSONObject(0);

				distance = json.getString("actualDistance");
				timeInSecondStr = json.getString("actualTime");
				vechileType = json.getString("vehicleType");
				vehicleSubType = json.getString("vehicleSubType");
				double distanceInMiles = 0;
				String dis[] = distance.split(" ");
				if ("ft".equals(dis[1])) {
					double distanceInFT = Double.parseDouble(dis[0]);
					distanceInMiles = distanceInFT * 0.000189394;
				} else if ("mi".equals(dis[1])) {
					distanceInMiles = Double.parseDouble(dis[0]);
				}

				double timeInSecond = Double.parseDouble(timeInSecondStr);

				String farePay = FaresDetails.getFareAmount(vehicleSubType,
						timeInSecond, distanceInMiles);
				faretemp = "$" + farePay;

				total_distance.setText(distance);
				total_fare.setText(faretemp);
				total_time.setText(timeInSecondStr);
				CashPaymentDialog();

				// 0.000189394

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void submitInvoiceDetails() {

		JSONObject tripIdJson = new JSONObject();
		try {
			tripIdJson.put("tripID", tripID);
			tripIdJson.put("fare", faretemp);

		} catch (JSONException e) {

			e.printStackTrace();
		}

		Log.e("json data =:", tripIdJson.toString());

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {
				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Add_Invoice_Details.php");
			}

			@Override
			public void onResponse(String result) { // what to do with
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				callbackRequestResult1(result);
			}
		}.execute(tripIdJson.toString());

	}

	protected void callbackRequestResult1(String result) {

		Log.e("result", result);

		try {

			JSONObject resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
			String errorMsg = jsonObj.getString("Error_Msg");

			if ("1".equals(errorCode)) {
				progressDialog.dismiss();
				alertDialog.setTitle("Alert !");
				alertDialog.setMessage(errorMsg);
				alertDialog.show();
			} else {
				progressDialog.dismiss();
				alertDialog.setTitle("Error !");
				alertDialog.setMessage(errorMsg);
				alertDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
