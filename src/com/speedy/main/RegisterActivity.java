package com.speedy.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.SignUpModel;
import com.speedy.bo.TripCustomerDetails;

public class RegisterActivity extends ActionBarActivity {

	Button btn_save;
	private CheckBox checkbox;
	EditText et_name, et_age, et_phone, et_speed;
	String name, age, phone, speed;
	// DBAdapter db;
	public boolean emailcheck = true;
	String str_sessionvalue;
	AlertDialog.Builder alertdialog;
	public static String android_id;
	boolean haveConnectedWifi = false;
	boolean haveConnectedMobile = false;
	// private PrefSingleton mMyPreferences;
	private static boolean flag = true;
	private SignUpModel signupPJ = null;
	private AlertDialog alertDialog;

	private EditText edtFirstName, edtLastName, edtAdd1, edtAdd2, edtEmail,
			edtCity, edtState, edtCountry, edtZipcode, edtMobNumber,
			edtAlternateMobile, edtMobCountry, edtPWD, edtConformPWD;

	String encodedImage;

	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	private ImageView userpic;
	private SessionPrefs sessionObj = null;
	ProgressDialog progressDialog;
	private String imgPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		init();

		sessionObj = new SessionPrefs(this);

	}

	public void testData() {

		if (edtFirstName.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter First Name");
			alertDialog.show();
			return;
		} else if (!edtFirstName.getText().toString().matches("[a-zA-Z ]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid first name");
			alertDialog.show();
			return;
		}
		if (edtLastName.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Last Name");
			alertDialog.show();
			return;
		} else if (!edtLastName.getText().toString().matches("[a-zA-Z ]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid last name");
			alertDialog.show();
			return;
		}

		if (edtAdd1.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Address1");
			alertDialog.show();
			return;
		}

		if (!edtEmail.getText().toString().isEmpty()
				&& checkEmail(edtEmail.getText().toString())) {

		} else {

			edtEmail.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Valid Email id");
			alertDialog.show();
			return;

		}

		if (edtCity.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter City");
			alertDialog.show();
			return;
		} else if (!edtCity.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid City");
			alertDialog.show();
			return;
		}

		if (edtState.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter State");
			alertDialog.show();
			return;
		} else if (!edtState.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid State");
			alertDialog.show();
			return;
		}

		if (edtCountry.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Country");
			alertDialog.show();
			return;
		} else if (!edtCountry.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid Country");
			alertDialog.show();
			return;
		}

		if (edtZipcode.getText().toString().isEmpty()) {

			edtMobNumber.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Mobile Number");
			alertDialog.show();
			return;
		}

		if (edtMobNumber.getText().toString().isEmpty()) {

			edtMobNumber.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Mobile Number");
			alertDialog.show();
			return;
		}

		if (edtMobCountry.getText().toString().isEmpty()) {

			edtMobNumber.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Country code");
			alertDialog.show();
			return;
		}

		if (edtPWD.getText().toString().equals("")) {
			edtPWD.requestFocus();
			alertDialog.setTitle("Oops!");
			alertDialog.setMessage("Password  field is empty");
			alertDialog.show();
			return;
		}

		else if (!edtConformPWD.getText().toString()
				.equals(edtPWD.getText().toString())) {
			alertDialog.setTitle("Oops!");
			alertDialog.setMessage("Passwords do not match");
			alertDialog.show();
			return;
		}

		if (!checkbox.isChecked()) {
			alertDialog.setTitle("Alert !");
			alertDialog
					.setMessage("Please accept policy by clicking on checkbox.");
			alertDialog.show();
			return;
		}

		if (haveNetworkConnection()) {
			progressDialog = ProgressDialog.show(RegisterActivity.this, "",
					"Submitting...");
			verifyData();
			submitData();
		} else {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("No Internet Connection");
			alertDialog.show();
		}

	}

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	@SuppressWarnings("deprecation")
	private void init() {

		edtFirstName = (EditText) findViewById(R.id.tx_fname);
		edtLastName = (EditText) findViewById(R.id.tx_lname);
		edtAdd1 = (EditText) findViewById(R.id.add1);
		edtAdd2 = (EditText) findViewById(R.id.add2);
		edtCity = (EditText) findViewById(R.id.city);
		edtState = (EditText) findViewById(R.id.state);
		edtCountry = (EditText) findViewById(R.id.country);
		edtZipcode = (EditText) findViewById(R.id.zipcode);
		edtEmail = (EditText) findViewById(R.id.emailadd);
		edtMobNumber = (EditText) findViewById(R.id.mobnumber);
		edtAlternateMobile = (EditText) findViewById(R.id.alternateMobile);
		edtMobCountry = (EditText) findViewById(R.id.mobcountry);
		edtPWD = (EditText) findViewById(R.id.tx_password);
		edtConformPWD = (EditText) findViewById(R.id.tx_repassword);
		checkbox = (CheckBox) findViewById(R.id.check_privracy);

		userpic = (ImageView) findViewById(R.id.userpicture);

		userpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

		alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.menu, menu);// Menu Resource, Menu

		menu.findItem(Menus.SEARCH).setVisible(false);
		menu.findItem(Menus.PROCEED).setVisible(true).setTitle("SUBMIT");
		menu.findItem(Menus.CANCEL).setVisible(false);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menus.PROCEED:
			testData();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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

	private void submitData() {

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/User_Registration.php");
			}

			@Override
			public void onResponse(String result) {
				// what to do with result
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				progressDialog.dismiss();
				callbackRequestResult(result);
			}

		}.execute(signupPJ.toJSON());

	}

	private void verifyData() {

		String strFirstName, strLastName, strAdd1, strAdd2, strCity, strState, strCountry, strZipcode, strEmail, strMobNumber, strAlternatemobile, strMobCountry, strPWD, strImage;

		strFirstName = edtFirstName.getText().toString();
		strLastName = edtLastName.getText().toString();
		strEmail = edtEmail.getText().toString();
		strAdd1 = edtAdd1.getText().toString();
		strAdd2 = edtAdd2.getText().toString();
		strCity = edtCity.getText().toString();
		strState = edtState.getText().toString();
		strCountry = edtCountry.getText().toString();
		strZipcode = edtZipcode.getText().toString();
		strPWD = edtPWD.getText().toString();
		strMobNumber = edtMobNumber.getText().toString();
		strAlternatemobile = edtAlternateMobile.getText().toString();
		strMobCountry = edtMobCountry.getText().toString();
		strImage = encodedImage;

		signupPJ = new SignUpModel(strFirstName, strLastName, strAdd1, strAdd2,
				strEmail, strCity, strState, strCountry, strZipcode,
				strMobNumber, strAlternatemobile, strMobCountry, strPWD,
				"customer", strImage);
	}

	private void callbackRequestResult(String result) {
		// {"data":{"Error_Code":"1","Error_Msg":"Registration Successfully"}}
		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");
			String errorMsg = jsonObj.getString("Error_Msg");

			if ("1".equals(errorCode)) {

				String userType = jsonObj.getString("userType");
				String userId = jsonObj.getString("ID");

				sessionObj.setPreference("userID", userId);
				sessionObj.setPreference("userType", userType);
				sessionObj.setPreference("firstName",
						jsonObj.getString("firstName"));
				sessionObj.setPreference("lastName",
						jsonObj.getString("lastName"));
				sessionObj.setPreference("emailId",
						jsonObj.getString("emailId"));
				sessionObj.setPreference("mobileNumber",
						jsonObj.getString("mobileNumber"));
				sessionObj.setPreference("address",
						jsonObj.getString("addressOne"));
				sessionObj.setPreference("profileImage",
						jsonObj.getString("profileImage"));

				Intent PassToHome = new Intent(getApplicationContext(),
						NavigationMain.class);
				startActivity(PassToHome);
				RegisterActivity.this.finish();
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

	private void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery",
				"Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterActivity.this);

		builder.setTitle("Add Photo!");

		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {

				if (options[item].equals("Take Photo")) {

					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
					// pic = f;

					startActivityForResult(intent, 1);
				} else if (options[item].equals("Choose from Gallery")) {

					Intent intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(intent, 2);
				} else if (options[item].equals("Cancel")) {

					dialog.dismiss();

				}
			}

		});

		builder.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			if (requestCode == 1) {

				rotateImage(getImagePath());

			} else if (requestCode == 2) {
				rotateImage(getAbsolutePath(data.getData()));

			}
		}
	}

	private Uri getImageUri(String path) {
		return Uri.fromFile(new File(path));
	}

	public Uri setImageUri() {

		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/DCIM/", "image" + new Date().getTime() + ".jpg");
			Uri imgUri = Uri.fromFile(file);
			this.imgPath = file.getAbsolutePath();
			return imgUri;
		} else {
			File file = new File(getFilesDir(), "image" + new Date().getTime()
					+ ".jpg");
			Uri imgUri = Uri.fromFile(file);
			this.imgPath = file.getAbsolutePath();
			return imgUri;
		}
	}

	public String getImagePath() {
		return imgPath;
	}

	public void setSelectedImg(Bitmap bitmap) {
		if (bitmap != null) {
			userpic.setImageBitmap(bitmap);
			// encode into base 64

			bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // compress
																		// to
																		// which
																		// format
																		// you
																		// want.
			byte[] byte_arr = stream.toByteArray();
			encodedImage = Base64.encodeToString(byte_arr, Base64.DEFAULT);
		}
	}

	private void rotateImage(final String path) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Bitmap b = decodeFileFromPath(path);
				try {
					ExifInterface ei = new ExifInterface(path);
					int orientation = ei.getAttributeInt(
							ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_NORMAL);
					Matrix matrix = new Matrix();
					switch (orientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						matrix.postRotate(90);
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						matrix.postRotate(180);
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						matrix.postRotate(270);
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
						break;
					default:
						b = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
								b.getHeight(), matrix, true);
						break;
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}

				FileOutputStream out1 = null;
				File file;
				try {
					String state = Environment.getExternalStorageState();
					if (Environment.MEDIA_MOUNTED.equals(state)) {
						file = new File(
								Environment.getExternalStorageDirectory()
										+ "/DCIM/", "image"
										+ new Date().getTime() + ".jpg");
					} else {
						file = new File(getFilesDir(), "image"
								+ new Date().getTime() + ".jpg");
					}
					out1 = new FileOutputStream(file);
					b.compress(Bitmap.CompressFormat.JPEG, 90, out1);
					setSelectedImg(b);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						out1.close();
					} catch (Throwable ignore) {

					}
				}

			}
		});

	}

	private Bitmap decodeFileFromPath(String path) {
		Uri uri = getImageUri(path);
		InputStream in = null;
		try {
			in = getContentResolver().openInputStream(uri);

			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;

			BitmapFactory.decodeStream(in, null, o);
			in.close();

			int scale = 1;
			int inSampleSize = 1024;
			if (o.outHeight > inSampleSize || o.outWidth > inSampleSize) {
				scale = (int) Math.pow(
						2,
						(int) Math.round(Math.log(inSampleSize
								/ (double) Math.max(o.outHeight, o.outWidth))
								/ Math.log(0.5)));
			}

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			in = getContentResolver().openInputStream(uri);
			Bitmap b = BitmapFactory.decodeStream(in, null, o2);

			in.close();

			return b;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAbsolutePath(Uri uri) {
		if (Build.VERSION.SDK_INT >= 19) {
			String id = uri.getLastPathSegment().split(":")[1];
			final String[] imageColumns = { MediaStore.Images.Media.DATA };
			final String imageOrderBy = null;
			Uri tempUri = getUri();
			Cursor imageCursor = getContentResolver().query(tempUri,
					imageColumns, MediaStore.Images.Media._ID + "=" + id, null,
					imageOrderBy);
			if (imageCursor.moveToFirst()) {
				return imageCursor.getString(imageCursor
						.getColumnIndex(MediaStore.Images.Media.DATA));
			} else {
				return null;
			}
		} else {
			String[] projection = { MediaStore.MediaColumns.DATA };
			Cursor cursor = getContentResolver().query(uri, projection, null,
					null, null);
			if (cursor != null) {
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
				cursor.moveToFirst();
				return cursor.getString(column_index);
			} else
				return null;
		}

	}

	private Uri getUri() {
		String state = Environment.getExternalStorageState();
		if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

		return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	}

	@Override
	public void onBackPressed() {

		Intent PassToHome = new Intent(getApplicationContext(),
				AskViewActivity.class);
		startActivity(PassToHome);
		overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft);
		super.onBackPressed();
	}

}