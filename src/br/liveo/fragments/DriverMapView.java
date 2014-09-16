package br.liveo.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import br.liveo.utils.AsynchronoucsCall;
import br.liveo.utils.Constant;
import br.liveo.utils.HttpHandler;
import br.liveo.utils.Locations;
import br.liveo.utils.Menus;
import br.liveo.utils.SessionPrefs;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.kaplandroid.shortestpathdirection.googlemaps.GMapV2Direction;
import com.kaplandroid.shortestpathdirection.googlemaps.GetRotueListTask;
import com.kaplandroid.shortestpathdirection.googlemaps.GMapV2Direction.DirecitonReceivedListener;
import com.speedy.bo.TripCustomerDetails;
import com.speedy.main.CustomerDetailsForDriver;
import com.speedy.main.LocationService;
import com.speedy.main.NavigationMain;
import com.speedy.main.NoficationDemoActivity;
import com.speedy.main.R;
import com.speedy.main.TaxiChooser;

public class DriverMapView extends Fragment implements OnCameraChangeListener,
		DirecitonReceivedListener {

	private static View view;
	/**
	 * Note that this may be null if the Google Play services APK is not
	 * available.
	 */

	private GoogleMap mMap;
	TextView address;
	// Button address;

	MarkerOptions marker;
	double currentLongitude = 0;
	double currentLatitude = 0;
	LatLng position = null;
	LocationManager locManager;
	String provider;
	Location location;
	Marker now;
	VisibleRegion visibleRegion;
	Point centerPoint;
	boolean flag;
	private boolean searchCheck;
	private SearchView searchView;
	private AsynchronoucsCall asynchSearch;
	private MenuItem searchViewitem;
	private Button startRide;
	protected RadioGroup taxiRadioGroup;
	static boolean startFlag = true, endFlag = false;
	TripCustomerDetails tripObj = null;
	public LocationManager locationManager;

	SessionPrefs sessionObj = null;
	private ProgressDialog progressDialog;
	private TextView customerDetailsTap;
	private MenuItem cancelViewitem;
	private MenuItem proceedViewitem;
	private JSONObject resultJson;
	private JSONObject tripDetailsJson;
	private ImageView navigationview, currentLocation;
	private LatLng startPoint, endPoint;
	private String tripID = null;
	private AlertDialog alertDialog_startRide, alertDialog_StopRide;

	private static int START_RIDE = 1;
	private static int STOP_RIDE = 2;
	private static int TRIP_DETAILS = 3;
	private static boolean NAVIGATION_MAP_FLAG = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.driver_map_view, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
			e.printStackTrace();
		}

		setUpMapIfNeeded();
		dialogbox();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// start service for locatin updates
		// use this to start and trigger a service
		 Intent i= new Intent(getActivity(), LocationService.class);
		 getActivity().startService(i);

		sessionObj = new SessionPrefs(getActivity());
		address = (TextView) getActivity().findViewById(
				R.id.txtInfoWindowEventType);
		startRide = (Button) getActivity().findViewById(R.id.startRide);
		navigationview = (ImageView) getActivity().findViewById(
				R.id.navigationview);
		currentLocation = (ImageView) getActivity().findViewById(
				R.id.currentlocation);

		address.setSelected(true);
		address.setEllipsize(TruncateAt.MARQUEE);

		setHasOptionsMenu(true);

		startRide.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View arg0) {

				String rideString = startRide.getText().toString();

				if ("Start Ride".equals(rideString)) {

					if (tripID == null || "".equals(tripID)) {
						Toast.makeText(getActivity(), "Start Customer Ride",
								Toast.LENGTH_LONG).show();
					} else {

						alertDialog_startRide.setButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										JSONObject tripJson = new JSONObject();
										try {
											tripJson.put("driverID", sessionObj
													.getPreference("userID"));
											tripJson.put("tripID", tripID);
											tripJson.put("status", "Start");
											tripJson.put(
													"actualSourceLatitude",
													"" + Locations.CURRENT_LAT);
											tripJson.put(
													"actualSourceLongitude",
													"" + Locations.CURRENT_LOGI);

											DateFormat dateFormat = new SimpleDateFormat(
													"yyyy/MM/dd HH:mm:ss");
											Date date = new Date();
											tripJson.put("StartTime",
													dateFormat.format(date));

										} catch (JSONException e) {
											e.printStackTrace();
										}

										Log.e("response json",
												tripJson.toString());

										new HttpHandler() {
											@Override
											public HttpUriRequest getHttpRequestMethod() {
												return new HttpPost(
														"http://phbjharkhand.in/speedyTaxi/Update_Travel_Time.php");
											}

											@Override
											public void onResponse(String result) { // what
																					// to
																					// do
																					// with
												// e.g. display it on edit text
												// etResponse.setText(result);

												callbackRequestResult(result,
														START_RIDE);
											}
										}.execute(tripJson.toString());
									}
								});

						alertDialog_startRide.setButton2("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										// Write your code here to execute after
										// dialog
										// closed
									}
								});
						alertDialog_startRide.setTitle("Warning!");
						alertDialog_startRide
								.setMessage("Do you want to Start Ride ?");
						alertDialog_startRide.show();

					}
				} else if ("Stop Ride".equals(rideString)) {
					if (tripID == null || "".equals(tripID)) {

					} else {

						alertDialog_StopRide.setButton("YES",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										JSONObject tripJson = new JSONObject();
										try {

											tripJson.put("driverID", sessionObj
													.getPreference("userID"));
											tripJson.put("tripID", tripID);
											tripJson.put("status", "Completed");
											tripJson.put(
													"actualDestinationLatitude",
													"" + Locations.CURRENT_LAT);
											tripJson.put(
													"actualDestinationLongitude",
													"" + Locations.CURRENT_LOGI);
											DateFormat dateFormat = new SimpleDateFormat(
													"yyyy/MM/dd HH:mm:ss");
											Date date = new Date();
											tripJson.put("EndTime",
													dateFormat.format(date));

										} catch (JSONException e) {
											e.printStackTrace();
										}

										Log.e("response json",
												tripJson.toString());

										new HttpHandler() {
											@Override
											public HttpUriRequest getHttpRequestMethod() {
												return new HttpPost(
														"http://phbjharkhand.in/speedyTaxi/Update_Travel_Time.php");
											}

											@Override
											public void onResponse(String result) { // what
																					// to
																					// do
																					// with
												// e.g. display it on edit text
												// etResponse.setText(result);

												callbackRequestResult(result,
														STOP_RIDE);
											}
										}.execute(tripJson.toString());

									}
								});

						alertDialog_StopRide.setButton2("NO",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										
									}
								});
						alertDialog_StopRide.setTitle("Warning!");
						alertDialog_StopRide
								.setMessage("Do you want to Stop Ride ?");
						alertDialog_StopRide.show();

					}
				}
			}
		});

		String notificationFlag = sessionObj.getPreference("notificationFlag");
		if (!("".equals(notificationFlag))) {
			String tempArray[] = notificationFlag.split("_");

			if ("RideAccepted".equals(tempArray[0])) {

				navigationview.setVisibility(View.VISIBLE);
				customerDetailsTap = (TextView) getActivity().findViewById(
						R.id.titletext);
				customerDetailsTap.setVisibility(View.VISIBLE);
				customerDetailsTap.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {

						Intent newIntent = new Intent(getActivity(),
								CustomerDetailsForDriver.class);
						if (tripDetailsJson != null) {
							newIntent.putExtra("customerJsonData",
									tripDetailsJson.toString());
						}

						startActivity(newIntent);

					}
				});

				tripID = tempArray[1];
				getAcceptedTripDetails(tempArray[1]);
			} else if ("RideCancelled".equals(tempArray[0])) {
				Toast.makeText(getActivity(), "Trip is Cancelled",
						Toast.LENGTH_LONG).show();
			}
		}

		navigationview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (StartPoint != null && EndPoint != null) {

					if (!NAVIGATION_MAP_FLAG) {
						Intent intent = new Intent(
								android.content.Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.com/maps?saddr="
										+ "" + StartPoint.latitude + "," + ""
										+ StartPoint.longitude + "&daddr=" + ""
										+ EndPoint.latitude + "," + ""
										+ EndPoint.longitude));
						intent.setComponent(new ComponentName(
								"com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity"));
						startActivity(intent);
					} else {
						Intent intent = new Intent(
								android.content.Intent.ACTION_VIEW,
								Uri.parse("http://maps.google.com/maps?saddr="
										+ "" + StartPoint.latitude + "," + ""
										+ StartPoint.longitude + "&daddr=" + ""
										+ EndPoint.latitude + "," + ""
										+ EndPoint.longitude));
						intent.setComponent(new ComponentName(
								"com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity"));
						startActivity(intent);
					}

				}

			}
		});

		currentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mMap != null) {
					// mMap.moveCamera(CameraUpdateFactory.newLatLng(new
					// LatLng(Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));
					try {
						mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
								new LatLng(Locations.CURRENT_LAT,
										Locations.CURRENT_LOGI), 10.0f));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void dialogbox() {
		alertDialog_startRide = new AlertDialog.Builder(getActivity()).create();
		alertDialog_StopRide = new AlertDialog.Builder(getActivity()).create();

	}

	private void getAcceptedTripDetails(String tripID) {

		progressDialog = ProgressDialog
				.show(getActivity(), "", "Submitting...");

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
						"http://phbjharkhand.in/speedyTaxi/Get_Trip_Details.php");
			}

			@Override
			public void onResponse(String result) { // what to do with
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				callbackRequestResult(result, TRIP_DETAILS);
			}
		}.execute(tripIdJson.toString());
	}

	private void callbackRequestResult(String result, int flagNumber) {

		JSONObject resultJson;
		String errorCode = null;
		JSONObject jsonObj = null;
		try {

			resultJson = new JSONObject(result);
			jsonObj = resultJson.getJSONObject("data");
			errorCode = jsonObj.getString("Error_Code");

		} catch (JSONException e) {
			e.printStackTrace();
			result = null;
		} catch (Exception e) {
			e.printStackTrace();
			result = null;
		}

		if (progressDialog != null)
			progressDialog.dismiss();

		if (TRIP_DETAILS == flagNumber && result != null) {

			Log.e("response of driver action", result);

			if ("1".equals(errorCode)) {

				try {

					JSONArray jsonResult = new JSONArray(
							jsonObj.getString("result"));
					tripDetailsJson = jsonResult.getJSONObject(0);
					double sLat = Double.parseDouble(tripDetailsJson
							.getString("sourceLatitude"));
					double sLongi = Double.parseDouble(tripDetailsJson
							.getString("sourceLongitude"));
					double dLat = Double.parseDouble(tripDetailsJson
							.getString("destinationLatitude"));
					double dLongi = Double.parseDouble(tripDetailsJson
							.getString("destinationLongitude"));

					StartPoint = new LatLng(sLat, sLongi);
					EndPoint = new LatLng(dLat, dLongi);
					showLocation();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

			}
		} else if (START_RIDE == flagNumber && result != null) {
			Log.e("Response :", result);

			if ("1".equals(errorCode)) {
				NAVIGATION_MAP_FLAG = true;
				startRide.setText("Stop Ride");
			}
		} else if (STOP_RIDE == flagNumber && result != null) {
			Log.e("Response :", result);

			if ("1".equals(errorCode)) {
				NAVIGATION_MAP_FLAG = true;
				startRide.setText("Start Ride");
			}
		}

	}

	/***** Sets up the map if it is possible to do so *****/
	public void setUpMapIfNeeded() {

		if (mMap == null) {
			mMap = ((SupportMapFragment) NavigationMain.fragmentManager
					.findFragmentById(R.id.map)).getMap();

			if (mMap != null) {
				setUpMapSettings(mMap);

				if (Constant.isPositionAllowed) {
					mMap.setMyLocationEnabled(true);
					mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {

						@Override
						public void onMyLocationChange(Location arg0) {

							if (now != null) {
								now.remove();
							}

							Locations.CURRENT_LAT = arg0.getLatitude();
							Locations.CURRENT_LOGI = arg0.getLongitude();

							if (!flag) {
								mMap.animateCamera(CameraUpdateFactory
										.newLatLngZoom(new LatLng(
												Locations.CURRENT_LAT,
												Locations.CURRENT_LOGI), 10.0f));

								flag = true;
							}

						}
					});

					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
						public void onCameraChange(CameraPosition arg0) {
							position = arg0.target;
							if (position != null && position.latitude != 0
									&& position.longitude != 0) {

								if (asynchSearch != null)
									asynchSearch.cancel(true);

								address.setText(getResources().getString(
										R.string.updating_Location));

								getCentrePoint();

							}
						}
					});

				} else {
					mMap.setMyLocationEnabled(false);
					mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
						public void onCameraChange(CameraPosition arg0) {
							position = arg0.target;
							if (position != null && position.latitude != 0
									&& position.longitude != 0) {

								if (asynchSearch != null)
									asynchSearch.cancel(true);

								address.setText(getResources().getString(
										R.string.updating_Location));
								getCentrePoint();
							}
						}
					});
				}
			}
		}

		if (mMap != null)
			mMap.setOnMarkerClickListener(null);

	}

	public double[] getLocation() {

		LocationManager lm = locationManager;
		List<String> providers = lm.getProviders(true);

		Location l = null;
		for (int i = 0; i < providers.size(); i++) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}
		double[] gps = new double[2];

		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		return gps;
	}

	private void setUpMapSettings(GoogleMap mMap2) {
		UiSettings mUiSettings;
		mUiSettings = mMap2.getUiSettings();
		mUiSettings.setZoomControlsEnabled(false);
		mUiSettings.setCompassEnabled(true);
		mUiSettings.setMyLocationButtonEnabled(false);
		mUiSettings.setScrollGesturesEnabled(true);
		mUiSettings.setZoomGesturesEnabled(true);
		mUiSettings.setTiltGesturesEnabled(true);
		mUiSettings.setRotateGesturesEnabled(false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		setUpMapIfNeeded();

	}

	/****
	 * The mapfragment's id must be removed from the FragmentManager or else if
	 * the same it is passed on the next time then app will crash
	 ****/
	@Override
	public void onDestroyView() {
		super.onDestroyView();

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu, menu);

		menu.findItem(Menus.SEARCH).setVisible(false);
		;
		menu.findItem(Menus.CANCEL).setVisible(false);
		;
		menu.findItem(Menus.PROCEED).setVisible(false);
		;

	}

	/*
	 * Function sets the center point of map
	 */

	private void getCentrePoint() {

		visibleRegion = mMap.getProjection().getVisibleRegion();

		Point x = mMap.getProjection().toScreenLocation(visibleRegion.farRight);

		Point y = mMap.getProjection().toScreenLocation(visibleRegion.nearLeft);

		centerPoint = new Point(x.x / 2, y.y / 2);

		LatLng centerFromPoint = mMap.getProjection().fromScreenLocation(
				centerPoint);

		String addressStr = null;// ConvertPointToLocation(new
									// LatLng(centerFromPoint.latitude,
									// centerFromPoint.longitude));
		if (addressStr == null) {
			if (asynchSearch != null)
				asynchSearch.cancel(true);

			asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {
				@Override
				public void onTaskDone(String result) {

					onLocationChnageResultGot(result);
				}
			});

			String url = "http://maps.google.com/maps/api/geocode/json?latlng="
					+ centerFromPoint.latitude + ","
					+ centerFromPoint.longitude + "&sensor=true";

			asynchSearch.execute(url);
		}/*
		 * else{ address.setText(addressStr); }
		 */

	}

	public String ConvertPointToLocation(LatLng point) {
		Log.e("", point.latitude + "" + point.longitude);
		String address1 = "";
		Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault());

		try {
			List<Address> addresses = geoCoder.getFromLocation(point.latitude,
					point.longitude, 1);

			if (addresses.size() > 0) {
				for (int index = 0; index < addresses.get(0)
						.getMaxAddressLineIndex(); index++)
					address1 += addresses.get(0).getAddressLine(index) + " ";
			}
		} catch (IOException e) {
			Log.v("Geocoder Exception::", e.toString());
			address1 = null;
		}

		return address1;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case Menus.CANCEL:
			sessionObj.setPreference("notificationFlag", "");

			if (customerDetailsTap != null)
				customerDetailsTap.setVisibility(View.INVISIBLE);

			if (mMap != null)
				mMap.clear();

			StartPoint = null;
			EndPoint = null;
			navigationview.setVisibility(View.INVISIBLE);
			NAVIGATION_MAP_FLAG = false;

			break;
		}
		return true;
	}

	private OnQueryTextListener OnQuerySearchView = new OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String txtsearch) {

			searchView.clearFocus();
			searchViewitem.collapseActionView();

			asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {

				@Override
				public void onTaskDone(String result) {

					onSearchResultGot(result);
				}
			});

			Toast.makeText(getActivity(), "Searched Location -:" + txtsearch,
					Toast.LENGTH_LONG).show();
			txtsearch = txtsearch.replaceAll(" ", "%20");
			String url = "http://maps.google.com/maps/api/geocode/json?address="
					+ txtsearch + "&sensor=false";

			asynchSearch.execute(url);

			return false;
		}

		@Override
		public boolean onQueryTextChange(String arg0) {

			if (searchCheck) {
				// implement your search here

			}
			return false;
		}
	};
	private LatLng StartPoint;
	private LatLng EndPoint;

	public void onSearchResultGot(String result) {

		Log.e("result of search address", result);

		try {

			JSONObject jsonObj = new JSONObject(result);
			JSONArray jsonArray = jsonObj.getJSONArray("results");
			JSONObject geoPoints = (jsonArray.getJSONObject(0))
					.getJSONObject("geometry");
			JSONObject jsonPoints = geoPoints.getJSONObject("location");
			double lat = jsonPoints.getDouble("lat");
			double lng = jsonPoints.getDouble("lng");
			JSONObject viewPortJson = geoPoints.getJSONObject("viewport");

			Log.e("lat", "" + lat);
			Log.e("lng", "" + lng);
			Log.e("viewpost data=:", viewPortJson.toString());

			/*
			 * if (startFlag) { startFlag = false; //
			 * mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new //
			 * LatLng(lat, lng),10.0f)); Locations.SOURCE_LAT = lat;
			 * Locations.SOURCE_LOGI = lng; mMap.addMarker(new
			 * MarkerOptions().position( new LatLng(lat, lng)).icon(
			 * BitmapDescriptorFactory.fromResource(R.drawable.pin))); }
			 * 
			 * if (endFlag) { endFlag = false; Locations.DESTINATION_LAT = lat;
			 * Locations.DESTINATION_LOGI = lng; mMap.addMarker(new
			 * MarkerOptions().position( new LatLng(lat, lng)).icon(
			 * BitmapDescriptorFactory.fromResource(R.drawable.pin)));
			 * startRide.setText("NEXT"); }
			 */

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	protected void onLocationChnageResultGot(String result) {

		Log.e("Location details ", result);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			JSONObject location;
			String location_string;
			String addre = null;
			try {
				location = jsonObject.getJSONArray("results").getJSONObject(0);
				location_string = location.getString("formatted_address");
				Log.d("test", "formattted address:" + location_string);

				if (location_string != null)
					address.setText(location_string);

			} catch (JSONException e1) {
				e1.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onCameraChange(CameraPosition position) {
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(position.target).title(
				position.toString()));

		address.setText(getResources().getString(R.string.updating_Location));
		getCentrePoint();

	}

	protected void showLocation() {

		if (StartPoint != null && EndPoint != null) {
			mMap.clear();

			Marker endLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(EndPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			Marker startLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(StartPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			new GetRotueListTask(getActivity(), StartPoint, EndPoint,
					GMapV2Direction.MODE_DRIVING, this).execute();

			mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
					Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));

		} else {

		}
	}

	@Override
	public void OnDirectionListReceived(List<LatLng> mPointList) {

		if (mPointList != null) {
			PolylineOptions rectLine = new PolylineOptions().width(10).color(
					Color.BLUE);
			rectLine.width(3);
			for (int i = 0; i < mPointList.size(); i++) {
				rectLine.add(mPointList.get(i));
			}
			mMap.addPolyline(rectLine);

			CameraPosition mCPFrom = new CameraPosition.Builder()
					.target(StartPoint).zoom(10.5f).bearing(0).tilt(25).build();
			final CameraPosition mCPTo = new CameraPosition.Builder()
					.target(EndPoint).zoom(10.5f).bearing(0).tilt(50).build();

			changeCamera(CameraUpdateFactory.newCameraPosition(mCPFrom),
					new CancelableCallback() {
						@Override
						public void onFinish() {
							changeCamera(CameraUpdateFactory
									.newCameraPosition(mCPTo),
									new CancelableCallback() {

										@Override
										public void onFinish() {

											LatLngBounds bounds = new LatLngBounds.Builder()
													.include(StartPoint)
													.include(EndPoint).build();
											changeCamera(
													CameraUpdateFactory
															.newLatLngBounds(
																	bounds, 50),
													null, false);
										}

										@Override
										public void onCancel() {
										}
									}, false);

							mMap.moveCamera(CameraUpdateFactory
									.newLatLng(new LatLng(
											Locations.CURRENT_LAT,
											Locations.CURRENT_LOGI)));
							// addressTitle.setText("Current Location");
						}

						@Override
						public void onCancel() {
						}
					}, true);
		}
	}

	/**
	 * Change the camera position by moving or animating the camera depending on
	 * input parameter.
	 */
	private void changeCamera(CameraUpdate update, CancelableCallback callback,
			boolean instant) {

		if (instant) {
			mMap.animateCamera(update, 1, callback);
		} else {
			mMap.animateCamera(update, 4000, callback);
		}
	}

}
