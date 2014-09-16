package com.speedy.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.driver.DriverPersonalInfo;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.speedy.bo.SignUpModel;
import com.speedy.bo.VehicleRegisterModel;

public class VehicleRegisterActivity extends ActionBarActivity {

	private Button btn_save;
	private CheckBox checkbox;
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
	private VehicleRegisterModel VehicleRegisterModelPj = null;
	private AlertDialog alertDialog;
	private SessionPrefs session;
	

	private EditText edtVehicleReg, edtInsuranceCertificate, edtYear,
			edtStatus, edtLicensePlate, edtInteriorCol, edtExteriorCol,
			edtVehicleClass;
	private ImageView vehicle, insurance;

	private String vehicle_Image, insurance_Image;

	private ImageView userpic;
	private Uri mImageCaptureUri;
	private ImageView mImageView;

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private SessionPrefs sessionObj = null;

	private ProgressDialog progressDialog;
	private static String profileImgPath = "";
	private boolean vhicleImgFlag = false;
	private boolean insuranceImgFlag = false;
	private String imgPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.vehicle_register);
		getSupportActionBar().setIcon(R.drawable.ic_launcher);

		init();
		sessionObj = new SessionPrefs(this);
		session = new SessionPrefs(getApplicationContext());

		// Showing Alert Message

		vehicle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vhicleImgFlag = true;
				insuranceImgFlag = false;
				selectImage();
			}
		});
		insurance.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vhicleImgFlag = false;
				insuranceImgFlag = true;
				selectImage();
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void init() {

		vehicle = (ImageView) findViewById(R.id.vehicle);
		insurance = (ImageView) findViewById(R.id.insurance);

		edtYear = (EditText) findViewById(R.id.year);
		edtStatus = (EditText) findViewById(R.id.status);
		edtLicensePlate = (EditText) findViewById(R.id.license);
		edtInteriorCol = (EditText) findViewById(R.id.InteriorCol);
		edtExteriorCol = (EditText) findViewById(R.id.ExteriorCol);
		edtVehicleClass = (EditText) findViewById(R.id.VehicleClass);
		checkbox = (CheckBox) findViewById(R.id.check_privracy);

		alertDialog = new AlertDialog.Builder(VehicleRegisterActivity.this)
				.create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
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

		/*
		 * if (profileImgPath.equals("")) {
		 * 
		 * alertDialog.setTitle("Alert !");
		 * alertDialog.setMessage("Select Profile picture"); alertDialog.show();
		 * return; }
		 */

		if (edtYear.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Last Name");
			alertDialog.show();
			return;
		} else if (edtStatus.getText().toString().equals("")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid last name");
			alertDialog.show();
			return;
		}

		if (edtLicensePlate.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter Address1");
			alertDialog.show();
			return;
		}

		if (edtInteriorCol.getText().toString().equals("")) {

			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter City");
			alertDialog.show();
			return;
		}

		if (edtExteriorCol.getText().toString().equals("")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter valid City");
			alertDialog.show();
			return;
		}

		if (edtVehicleClass.getText().toString().equals("")) {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("Enter State");
			alertDialog.show();
			return;
		}

		if (haveNetworkConnection()) {
			progressDialog = ProgressDialog.show(VehicleRegisterActivity.this,
					"", "Submitting...");
			verifyData();
			submitData();
		} else {
			alertDialog.setTitle("Alert !");
			alertDialog.setMessage("No Internet Connection");
			alertDialog.show();
		}
	}

	private void submitData() {

		Log.e("json ", VehicleRegisterModelPj.toJSON());
		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {

				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Add_Driver_Vehicle_Information.php");
			}

			@Override
			public void onResponse(String result) {
				// what to do with result
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				progressDialog.dismiss();
				callbackRequestResult(result);
			}

		}.execute(VehicleRegisterModelPj.toJSON());

	}

	private void verifyData() {

		String strVehicleImage, strInsuranceImage, strYear, strStatus, strLicensePlate, strInteriorCol, strExteriorCol, strVehicleClass;

		strVehicleImage = vehicle_Image;
		strInsuranceImage = insurance_Image;
		strYear = edtYear.getText().toString();
		strStatus = edtStatus.getText().toString();
		strLicensePlate = edtLicensePlate.getText().toString();
		strInteriorCol = edtInteriorCol.getText().toString();
		strExteriorCol = edtExteriorCol.getText().toString();
		strVehicleClass = edtVehicleClass.getText().toString();
		String driverId = session.getPreference("userID");

		Log.e("dataaaaaaaaaaaaaaaaaaaa", "data : " + strYear + "," + strStatus
				+ "," + strLicensePlate + "," + strInteriorCol + ","
				+ strExteriorCol + "," + strVehicleClass);
		VehicleRegisterModelPj = new VehicleRegisterModel(strVehicleImage,
				strInsuranceImage, strYear, strStatus, strLicensePlate,
				strInteriorCol, strExteriorCol, strVehicleClass, driverId,Locations.CURRENT_LAT,Locations.CURRENT_LOGI);
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
				
				session.setPreference("vehicleStatus","DONE");
				Intent PassToHome = new Intent(getApplicationContext(),
						NavigationMain.class);
				startActivity(PassToHome);
				VehicleRegisterActivity.this.finish();
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
				VehicleRegisterActivity.this);

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

		if (vhicleImgFlag) {
			vehicle.setImageBitmap(bitmap);

			bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // compress
																		// to
																		// which
																		// format
																		// you
																		// want.
			byte[] byte_arr = stream.toByteArray();
			vehicle_Image = Base64.encodeToString(byte_arr, Base64.DEFAULT);

		} else if (insuranceImgFlag) {
			insurance.setImageBitmap(bitmap);

			bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream); // compress
																		// to
																		// which
																		// format
																		// you
																		// want.
			byte[] byte_arr = stream.toByteArray();
			insurance_Image = Base64.encodeToString(byte_arr, Base64.DEFAULT);
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

	
}