package br.liveo.fragments;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.kaplandroid.shortestpathdirection.googlemaps.GMapV2Direction.DirecitonReceivedListener;
import com.kaplandroid.shortestpathdirection.googlemaps.GetRotueListTask;
import com.speedy.bo.TripCustomerDetails;
import com.speedy.main.NavigationMain;
import com.speedy.main.R;
import com.speedy.main.SearchLocation;
import com.speedy.main.TaxiChooser;

public class CustomerMapView extends Fragment implements
		DirecitonReceivedListener {

	public static final String ACTION_CLEAR_MAP_REQUEST = "com.speedy.main.ACTION_CLEAR_MAP";

	private static View view;

	private GoogleMap mMap;
	private TextView address;
	private MarkerOptions marker;
	private LatLng position = null;
	private LocationManager locManager;
	private String provider;
	private Location location;
	private Marker now;
	private VisibleRegion visibleRegion;
	private Point centerPoint;
	boolean flag;
	private boolean searchCheck;
	private SearchView searchView;
	private AsynchronoucsCall asynchSearch;
	private MenuItem proceedViewitem;
	private Button dropBtn;
	protected RadioGroup taxiRadioGroup;
	static boolean startLocationFlag = false, endLocationFlag = false;
	private TripCustomerDetails tripObj = null;
	private ImageView navigationview, currentLocation;

	private SessionPrefs session = null;
	private Button pickUpBtn;
	private FrameLayout mapFrame;
	public static boolean RIDEFLAG_CANCEL = false;
	private MenuItem cancelViewitem;
	private AlertDialog alertDialog, alertDialog_Dest;
	private MenuItem searchViewitem;
	private AlertDialog LocationDialog;
	private TextView addressTitle;
	private ImageView clearImageView;

	private TripDetailsReceiver TaxiReceiver;

	private TextView userName;

	private TextView emailID;

	private static LatLng StartPoint, EndPoint;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.map_view, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
			e.printStackTrace();
		}
		setUpMapIfNeeded();
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		setUpMapIfNeeded();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		session = new SessionPrefs(getActivity());
		addressTitle = (TextView) getActivity().findViewById(
				R.id.txtInfoWindowTitle);
		address = (TextView) getActivity().findViewById(
				R.id.txtInfoWindowEventType);
		dropBtn = (Button) getActivity().findViewById(R.id.dropbtn);
		pickUpBtn = (Button) getActivity().findViewById(R.id.pickupbtn);
		navigationview = (ImageView) getActivity().findViewById(
				R.id.navigationview);
		currentLocation = (ImageView) getActivity().findViewById(
				R.id.currentlocation);
		clearImageView = (ImageView) getActivity().findViewById(R.id.mapclear);

		address.setSelected(true);
		address.setEllipsize(TruncateAt.MARQUEE);
		setHasOptionsMenu(true);

		navigationview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (StartPoint != null && EndPoint != null) {

					Intent intent = new Intent(
							android.content.Intent.ACTION_VIEW, Uri
									.parse("http://maps.google.com/maps?saddr="
											+ "" + StartPoint.latitude + ","
											+ "" + StartPoint.longitude
											+ "&daddr=" + ""
											+ EndPoint.latitude + "," + ""
											+ EndPoint.longitude));
					intent.setComponent(new ComponentName(
							"com.google.android.apps.maps",
							"com.google.android.maps.MapsActivity"));
					startActivity(intent);

				}

			}
		});

		currentLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mMap != null) {
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

		dropBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				setDropLocation();
			}
		});

		pickUpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				setPickLocation();

			}
		});

		clearImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resetMap();
			}
		});

		alertDialog = new AlertDialog.Builder(getActivity()).create();

		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// Write your code here to execute after dialog closed
			}
		});

		// register broadcast receiver for finishing this activity from another
		// activity.
		IntentFilter filter = new IntentFilter(ACTION_CLEAR_MAP_REQUEST);
		TaxiReceiver = new TripDetailsReceiver();
		getActivity().registerReceiver(TaxiReceiver, filter);

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

								address.setText(getActivity().getResources()
										.getString(R.string.updating_Location));

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

								address.setText(getActivity().getResources()
										.getString(R.string.updating_Location));
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

		Locations.SEARCHED_LAT = centerFromPoint.latitude;
		Locations.SEARCHED_LOGI = centerFromPoint.longitude;
		if (Locations.CURRENT_LAT == centerFromPoint.latitude
				&& Locations.CURRENT_LOGI == centerFromPoint.longitude) {
			addressTitle.setText("Current Location");
		}
		String addressStr = null;// ConvertPointToLocation(new LatLng(
		// centerFromPoint.latitude, centerFromPoint.longitude));
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
		} /*
		 * else { address.setText(addressStr); }
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

	protected void setPickLocation() {

		Locations.SOURCE_LAT = Locations.SEARCHED_LAT;
		Locations.SOURCE_LOGI = Locations.SEARCHED_LOGI;

		StartPoint = new LatLng(Locations.SOURCE_LAT, Locations.SOURCE_LOGI);

		if (StartPoint != null && EndPoint != null) {
			mMap.clear();
			navigationview.setVisibility(View.VISIBLE);
			endLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(EndPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			startLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(StartPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			new GetRotueListTask(getActivity(), StartPoint, EndPoint,
					GMapV2Direction.MODE_DRIVING, this).execute();

			mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
					Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));
			// addressTitle.setText("Current Location");
			Toast.makeText(getActivity(), "Pickup location is set.",
					Toast.LENGTH_SHORT).show();
		} else {

			if (startLocatioMarker != null)
				startLocatioMarker.remove();

			startLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(StartPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));
			Toast.makeText(getActivity(), "Pickup location is set.",
					Toast.LENGTH_SHORT).show();
		}

		// Locations.SEARCHED_LAT = 0;
		// Locations.SEARCHED_LOGI = 0;

	}

	protected void setDropLocation() {

		Locations.DESTINATION_LAT = Locations.SEARCHED_LAT;
		Locations.DESTINATION_LOGI = Locations.SEARCHED_LOGI;

		EndPoint = new LatLng(Locations.DESTINATION_LAT,
				Locations.DESTINATION_LOGI);

		if (StartPoint != null && EndPoint != null) {

			mMap.clear();
			navigationview.setVisibility(View.VISIBLE);

			endLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(EndPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			startLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(StartPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));

			new GetRotueListTask(getActivity(), StartPoint, EndPoint,
					GMapV2Direction.MODE_DRIVING, this).execute();

			mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
					Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));
			addressTitle.setText("Current Location");
			Toast.makeText(getActivity(), "Drop location is set.",
					Toast.LENGTH_SHORT).show();
		} else {

			if (endLocatioMarker != null)
				endLocatioMarker.remove();

			endLocatioMarker = mMap
					.addMarker(new MarkerOptions()
							.position(EndPoint)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.marker_customer_center_bottom_blue)));
			Toast.makeText(getActivity(), "Drop location is set.",
					Toast.LENGTH_SHORT).show();
		}

		// Locations.SEARCHED_LAT = 0;
		// Locations.SEARCHED_LOGI = 0;
	}

	private String location_string;
	private Marker endLocatioMarker;
	private Marker startLocatioMarker;

	public void onSearchResultGot(String result) {

		if ("".equals(result) || result == null) {
			try {

				JSONObject jsonObj = new JSONObject(result);
				JSONArray jsonArray = jsonObj.getJSONArray("results");
				JSONObject geoPoints = (jsonArray.getJSONObject(0))
						.getJSONObject("geometry");
				JSONObject jsonPoints = geoPoints.getJSONObject("location");
				double lat = jsonPoints.getDouble("lat");
				double lng = jsonPoints.getDouble("lng");
				JSONObject viewPortJson = geoPoints.getJSONObject("viewport");
				Log.e("viewpost data=:", viewPortJson.toString());

				Locations.SEARCHED_LAT = lat;
				Locations.SEARCHED_LOGI = lng;

				mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
						Locations.SEARCHED_LAT, Locations.SEARCHED_LOGI)));

			} catch (JSONException e) {
				e.printStackTrace();
			}
			session.setPreference("searchAddress", "");
		}

	}

	protected void onLocationChnageResultGot(String result) {

		Log.e("Location details ", result);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			JSONObject location;

			String addre = null;
			try {
				location = jsonObject.getJSONArray("results").getJSONObject(0);
				location_string = location.getString("formatted_address");
				Log.d("test", "formattted address:" + location_string);

				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						if (location_string != null)
							address.setText(location_string);
					}
				});

			} catch (JSONException e1) {
				e1.printStackTrace();

			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/****
	 * The mapfragment's id must be removed from the FragmentManager or else if
	 * the same it is passed on the next time then app will crash
	 ****/
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		getActivity().unregisterReceiver(TaxiReceiver);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.menu, menu);

		searchViewitem = menu.findItem(Menus.SEARCH).setVisible(true);
		proceedViewitem = menu.findItem(Menus.PROCEED).setVisible(true);
		cancelViewitem = menu.findItem(Menus.CANCEL);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case Menus.PROCEED:

			if ((Locations.DESTINATION_LAT == 0 && Locations.DESTINATION_LOGI == 0)
					&& (Locations.SOURCE_LAT == 0 && Locations.SOURCE_LOGI == 0)) {
				alertDialog.setTitle("Warning!");
				alertDialog.setMessage("Please Set Locations");
				alertDialog.show();

			} else if (Locations.SOURCE_LAT == 0 && Locations.SOURCE_LOGI == 0) {

				alertDialog.setTitle("Warning!");
				alertDialog.setMessage("Please Set Source Location");
				alertDialog.show();
			} else if (Locations.DESTINATION_LAT == 0
					&& Locations.DESTINATION_LOGI == 0) {
				alertDialog_Dest = new AlertDialog.Builder(getActivity())
						.create();

				alertDialog_Dest.setButton("Skip",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								Intent next = new Intent(getActivity(),
										TaxiChooser.class);
								next.putExtra("Skip", "skip");
								startActivity(next);

							}
						});

				alertDialog_Dest.setButton2("NO",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								// Write your code here to execute after dialog
								// closed
							}
						});
				alertDialog_Dest.setTitle("Warning!");
				alertDialog_Dest
						.setMessage("Do you want to skip Drop Location ?");
				alertDialog_Dest.show();

			} else {

				mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
						Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));
				Intent next = new Intent(getActivity(), TaxiChooser.class);
				next.putExtra("Skip", "with_dest");
				startActivity(next);
			}
			break;
		case Menus.CANCEL:

			break;

		case Menus.SEARCH:

			Intent newActivity = new Intent(getActivity(), SearchLocation.class);
			startActivity(newActivity);
			break;

		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		String locationStr = session.getPreference("searchAddress");

		if (locationStr != null || !("".equals(locationStr))) {
			onSearchResultGot(locationStr);
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

	private void resetMap() {

		mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(
				Locations.CURRENT_LAT, Locations.CURRENT_LOGI)));

		Locations.clearAllLocation();
		mMap.clear();

		StartPoint = null;
		EndPoint = null;

		proceedViewitem.setVisible(true);
		cancelViewitem.setVisible(false);

		navigationview.setVisibility(View.INVISIBLE);

		Toast.makeText(getActivity(), "Map Reset", Toast.LENGTH_LONG).show();
	}

	// broadcast whenever we want to finish this activity
	class TripDetailsReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_CLEAR_MAP_REQUEST)) {
				resetMap();
				requestForDrvierAllocation();

			}
		}
	}

	public void requestForDrvierAllocation() {

		String tripID = session.getPreference("tripID");
		JSONObject tripIdJson = new JSONObject();
		try {
			tripIdJson.put("tripID", tripID);
		} catch (JSONException e) {

			e.printStackTrace();
		}

		new HttpHandler() {
			@Override
			public HttpUriRequest getHttpRequestMethod() {
				return new HttpPost(
						"http://phbjharkhand.in/speedyTaxi/Get_Driver_Allocation.php");
			}

			@Override
			public void onResponse(String result) { // what to do with
				// e.g. display it on edit text etResponse.setText(result);
				Log.e("Response data by registration=:", result);
				callbackRequestResultDemo(result);
			}

		}.execute(tripIdJson.toString());
	}

	public void callbackRequestResultDemo(String result) {

		Log.e("response result-:", result);

		JSONObject resultJson = null;
		try {
			resultJson = new JSONObject(result);
			JSONObject jsonObj = resultJson.getJSONObject("data");
			String errorCode = jsonObj.getString("Error_Code");

			if ("1".equals(errorCode)) {

			}
		} catch (JSONException e) {
			e.printStackTrace();
			resultJson = null;
		}
	}
}
