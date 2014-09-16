package br.liveo.driver;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONException;
import org.json.JSONObject;

import br.liveo.utils.HttpHandler;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.DriverPersonalModel;
import com.speedy.bo.SignUpModel;
import com.speedy.main.AskViewActivity;
import com.speedy.main.NavigationMain;
import com.speedy.main.R;
import com.speedy.main.RegisterActivity;
import com.speedy.main.VehicleRegisterActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DriverPersonalInfo extends ActionBarActivity {

	private EditText et_fname, et_lname, et_add1, et_add2, et_email, et_city,
			et_state, et_country, et_zipcode, et_age, et_sex, et_dob,
			et_mobile, et_alterMobile, et_mobileCountry, et_Password,
			et_ConformPassowrd, et_language;
	private ImageView userpic;
	public final Pattern EMAIL_ADDRESS_PATTERN = Pattern
			.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
					+ "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
	private Button ok;
	private SessionPrefs setSession = null;
	private AlertDialog alertDialog;
	private DriverPersonalModel signupPJ;
	boolean haveConnectedWifi = false;
	boolean haveConnectedMobile = false;
	private int C_year;
	private int C_month;
	private int C_day;

	private String encodedImage;
	static final int DATE_PICKER_ID = 1111;
	public String path = "";
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	ProgressDialog progressDialog;
	String gender;
	private SessionPrefs session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_register_per);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		init();
		setSession = new SessionPrefs(this);

		InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm1.hideSoftInputFromWindow(et_sex.getWindowToken(), 0);

		userpic = (ImageView) findViewById(R.id.userpicture);

		userpic.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
			}
		});

		et_sex.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				genderPicker();
			}
		});

		// DATE PICKER

		imm1.hideSoftInputFromWindow(et_dob.getWindowToken(), 0);

		et_dob.setFocusableInTouchMode(false);

		final Calendar c = Calendar.getInstance();
		C_year = c.get(Calendar.YEAR);
		C_month = c.get(Calendar.MONTH);
		C_day = c.get(Calendar.DAY_OF_MONTH);

		// Button listener to show date picker dialog

		et_dob.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				// On button click show datepicker dialog
				showDialog(DATE_PICKER_ID);
			}
		});
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

	// Initialize Variables
	@SuppressWarnings("deprecation")
	public void init() {
		userpic = (ImageView) findViewById(R.id.userpicture);
		et_fname = (EditText) findViewById(R.id.dri_fname);
		et_lname = (EditText) findViewById(R.id.dri_lname);
		et_add1 = (EditText) findViewById(R.id.dri_add1);
		et_add2 = (EditText) findViewById(R.id.dri_add2);
		et_email = (EditText) findViewById(R.id.dri_email);
		et_city = (EditText) findViewById(R.id.dri_city);
		et_state = (EditText) findViewById(R.id.dri_state);
		et_country = (EditText) findViewById(R.id.dri_country);
		et_zipcode = (EditText) findViewById(R.id.dri_zipcode);
		et_age = (EditText) findViewById(R.id.dri_age);
		et_sex = (EditText) findViewById(R.id.dri_sex);
		et_dob = (EditText) findViewById(R.id.dri_dob);
		et_mobile = (EditText) findViewById(R.id.dri_mobile);
		et_alterMobile = (EditText) findViewById(R.id.dri_alternateMobile);
		et_language = (EditText) findViewById(R.id.dri_lang);
		et_mobileCountry = (EditText) findViewById(R.id.dri_mobcountry);
		et_Password = (EditText) findViewById(R.id.dri_password);
		et_ConformPassowrd = (EditText) findViewById(R.id.dri_confm_password);

		session = new SessionPrefs(getApplicationContext());
		
		alertDialog = new AlertDialog.Builder(DriverPersonalInfo.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
				// closed
			}
		});
	}

	// Email Verification Method

	private boolean checkEmail(String email) {
		return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
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

	private void testData() {

		alertDialog = new AlertDialog.Builder(DriverPersonalInfo.this).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog
				// closed
			}
		});

		if (et_fname.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter First Name");
			alertDialog.show();
			return;
		} else if (!et_fname.getText().toString().matches("[a-zA-Z ]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid first name");
			alertDialog.show();
			return;
		}
		if (et_lname.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Last Name");
			alertDialog.show();
			return;
		} else if (!et_lname.getText().toString().matches("[a-zA-Z ]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid last name");
			alertDialog.show();
			return;
		}

		if (et_add1.getText().toString().isEmpty()) {

			et_fname.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter Address1");
			alertDialog.show();
			return;
		}

		if (!et_email.getText().toString().isEmpty()
				&& checkEmail(et_email.getText().toString())) {

		} else {

			et_email.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please check your Email id.");
			alertDialog.show();
			return;

		}

		if (et_city.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter City");
			alertDialog.show();
			return;
		} else if (!et_city.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid City");
			alertDialog.show();
			return;
		}

		if (et_state.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter State");
			alertDialog.show();
			return;
		} else if (!et_state.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid State");
			alertDialog.show();
			return;
		}

		if (et_country.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Country");
			alertDialog.show();
			return;
		} else if (!et_country.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid country");
			alertDialog.show();
			return;
		}
		if (et_zipcode.getText().toString().isEmpty()) {

			et_zipcode.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Zipcode");
			alertDialog.show();
			return;
		}

		if (et_age.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter age");
			alertDialog.show();
			return;
		}

		if (et_dob.getText().toString().isEmpty()) {

			et_mobile.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter DOB");
			alertDialog.show();
			return;
		}

		if (et_sex.getText().toString().isEmpty()) {

			et_mobile.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter Sex");
			alertDialog.show();
			return;
		}

		if (et_language.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Language");
			alertDialog.show();
			return;
		} else if (!et_language.getText().toString().matches("[a-zA-Z]+")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid Language");
			alertDialog.show();
			return;
		}

		if (et_mobile.getText().toString().isEmpty()
				|| et_mobile.getText().toString().matches("[a-zA-Z]")) {

			et_mobile.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter number");
			alertDialog.show();
			return;
		}

		if (et_mobileCountry.getText().toString().isEmpty()) {

			et_mobile.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter valid country code");
			alertDialog.show();
			return;
		}

		if (et_Password.getText().toString().equals("")) {
			et_Password.requestFocus();
			alertDialog.setTitle("Oops!");
			alertDialog.setMessage("Password  field is empty");
			alertDialog.show();
			return;
		}

		else if (!et_ConformPassowrd.getText().toString()
				.equals(et_ConformPassowrd.getText().toString())) {
			alertDialog.setTitle("Oops!");
			alertDialog.setMessage("Passwords do not match");
			alertDialog.show();
			return;
		}

		if (et_dob.getText().toString().isEmpty()
				|| et_mobile.getText().toString().matches("[a-zA-Z]")) {

			et_mobile.requestFocus();
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Please Enter valid Date");
			alertDialog.show();
			return;
		}
		if (haveNetworkConnection()) {
			progressDialog = ProgressDialog.show(DriverPersonalInfo.this, "",
					"Submitting...");
			verifyData();
			submitData();
		} else {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("No Internet Connection");
			alertDialog.show();
		}

	}

	public Bitmap getBitmap(Bitmap myBitmap) {
		Bitmap bitmap_obj = null;
		if (myBitmap != null) {
			if (myBitmap.getWidth() >= myBitmap.getHeight()) {

				bitmap_obj = Bitmap.createBitmap(myBitmap, myBitmap.getWidth()
						/ 2 - myBitmap.getHeight() / 2, 0,
						myBitmap.getHeight(), myBitmap.getHeight());

			} else {

				bitmap_obj = Bitmap.createBitmap(myBitmap, 0,
						myBitmap.getHeight() / 2 - myBitmap.getWidth() / 2,
						myBitmap.getWidth(), myBitmap.getWidth());
			}
		}
		return bitmap_obj;
	}

	@Override
	protected void onResume() {
		super.onResume();

		String act = null;
		Bitmap myBitmap = null;

		act = setSession.getPreference("front_imagepath");
		if (act != null && !("".equals(act))) {
			try {
				myBitmap = BitmapFactory.decodeFile(act);
				Bitmap bitmap_obj = null;
				bitmap_obj = getBitmap(myBitmap);
				if (bitmap_obj != null)
					userpic.setImageBitmap(bitmap_obj);

				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm
																			// is
																			// the
																			// bitmap
																			// object
				byte[] byteArrayImage = baos.toByteArray();
				encodedImage = Base64.encodeToString(byteArrayImage,
						Base64.DEFAULT);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Date Picker Dialog
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_PICKER_ID:

			DatePickerDialog dialog = null;

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				dialog = new DatePickerDialog(this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view,
									int selectedYear, int selectedMonth,
									int selectedDate) {

								int age = C_year - selectedYear;
								if (age < 18) {
									alertDialog.setTitle("Alert !");
									alertDialog
											.setMessage("Driver must be at least 18 years of age.");
									alertDialog.show();
								} else {
									et_dob.setText((selectedMonth + 1) + "/"
											+ (selectedDate) + "/"
											+ selectedYear);
								}

							}
						}, C_year - 18, C_month - 1, C_day);
				Calendar currentDate = Calendar.getInstance();
				dialog.getDatePicker()
						.setMaxDate(currentDate.getTimeInMillis());
				// If you need you can set min date too
			} else {
				dialog = new DatePickerDialog(this, mDateSetListener, C_year,
						C_month, C_day);
				dialog.setCanceledOnTouchOutside(false);
			}
			return dialog;
		}
		return null;
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {

			Calendar dob = Calendar.getInstance();
			dob.set(year, monthOfYear, dayOfMonth);
			Date d = new Date();
			Calendar currentDate = Calendar.getInstance();
			currentDate.setTime(d);

			if (dob.before(currentDate)) {
				et_dob.setText("");
				et_dob.requestFocus();
				alertDialog.setTitle("Alert !");
				alertDialog.setMessage("Please select future date");
				alertDialog.show();

			} else {
				et_dob.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/"
						+ year);
			}

		}

	};

	private String imgPath;

	public void genderPicker() {

		final CharSequence[] items = { "Male", "Female" };

		AlertDialog.Builder builder = new AlertDialog.Builder(
				DriverPersonalInfo.this);
		builder.setTitle("Select gender");
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				gender = items[item].toString();
				et_sex.setText(gender);

				dialog.dismiss();

			}
		}).show();
	}

	private void submitData() {

		Log.e("json ", signupPJ.toJSON());

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Driver_Registration.php");
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

		String strEmail, strFirstName, strLastName, strMobNumber, strAdd1, strAdd2, strCity, strState, strCountry, strZipcode, strAge, strSex, strLanguage, strMobCountry, strAlternateMobile, strPassword, strConfirmPassword, strDOB, strImage;
		strEmail = et_email.getText().toString();
		strFirstName = et_fname.getText().toString();
		strLastName = et_lname.getText().toString();
		strAdd1 = et_add1.getText().toString();
		strAdd2 = et_add2.getText().toString();
		strCity = et_city.getText().toString();
		strState = et_state.getText().toString();
		strCountry = et_country.getText().toString();
		strZipcode = et_zipcode.getText().toString();
		strAge = et_age.getText().toString();
		strSex = et_sex.getText().toString();
		strAlternateMobile = et_alterMobile.getText().toString();
		strMobNumber = et_mobile.getText().toString();
		strLanguage = et_language.getText().toString();
		strMobCountry = et_mobileCountry.getText().toString();
		strPassword = et_Password.getText().toString();
		strConfirmPassword = et_ConformPassowrd.getText().toString();
		strDOB = et_dob.getText().toString();
		strImage = encodedImage;
		signupPJ = new DriverPersonalModel(strFirstName, strLastName, strAdd1,
				strAdd2, strEmail, strCity, strState, strCountry, strZipcode,
				strAge, strSex, strDOB, strMobNumber, strAlternateMobile,
				strMobCountry, strPassword, strLanguage, "driver", strImage);
	}

	private void callbackRequestResult(String result) {
		// {"data":{"Error_Code":"1","Error_Msg":"Registration Successfully"}}
		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");

			if ("1".equals(errorCode)) {

				String userId = jsonObj.getString("ID");

				session.setPreference("userID", userId);
				session.setPreference("userType", "driver");

				session.setPreference("userID", userId);
				session.setPreference("userType", "driver");
				session.setPreference("firstName",
						jsonObj.getString("firstName"));
				session.setPreference("lastName", jsonObj.getString("lastName"));
				session.setPreference("emailId", jsonObj.getString("emailId"));
				session.setPreference("mobileNumber",
						jsonObj.getString("mobileNumber"));
				session.setPreference("address",
						jsonObj.getString("addressOne"));
				session.setPreference("profileImage",
						jsonObj.getString("profileImage"));

				Intent PassToHome = new Intent(getApplicationContext(),
						VehicleRegisterActivity.class);
				PassToHome.putExtra("ID", userId);
				DriverPersonalInfo.this.finish();
				startActivity(PassToHome);
			} else {
				String errorMsg = jsonObj.getString("Error_Msg");
				Toast.makeText(getApplicationContext(),
						"ErrorMsg-:" + errorMsg, Toast.LENGTH_LONG).show();
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
				DriverPersonalInfo.this);

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
	protected void onDestroy() {
		super.onDestroy();
		setSession.setPreference("imagepath", "");

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
