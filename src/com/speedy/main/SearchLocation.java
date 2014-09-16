package com.speedy.main;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import br.liveo.fragments.AsynchCallBack;
import br.liveo.utils.AsynchronoucsCall;
import br.liveo.utils.Locations;
import br.liveo.utils.SessionPrefs;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SearchLocation extends Activity implements OnQueryTextListener {

	private AsynchronoucsCall asynchSearch;
	private RelativeLayout homedialogview = null;
	private JSONArray contacts;
	private static final String TAG_RESULT = "predictions";
	ArrayList<String> names;
	private ListView listView;
	SessionPrefs session = null;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.searchlocationview);

		session = new SessionPrefs(this);
		homedialogview = (RelativeLayout) findViewById(R.id.homedialogview);
		// Attach the adapter to a ListView
		listView = (ListView) findViewById(R.id.searchListView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterView, View arg1,
					int position, long arg3) {

				String locationStr = (String) adapterView
						.getItemAtPosition(position);

				progressDialog = ProgressDialog.show(SearchLocation.this, "",
						"Getting Location Wait...");

				asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {

					@Override
					public void onTaskDone(String result) {

						session.setPreference("searchAddress", result);
						progressDialog.dismiss();
						finish();
					}
				});

				locationStr = locationStr.replaceAll(" ", "%20");
				String url = "http://maps.google.com/maps/api/geocode/json?address="
						+ locationStr + "&sensor=false";

				asynchSearch.execute(url);

			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.searchmenu, menu);
		MenuItem searchMenuItem = (MenuItem) menu.findItem(R.id.menu_search);
		SearchView searchView = (SearchView) searchMenuItem.getActionView();
		searchMenuItem.expandActionView();
		searchView.setQueryHint("Search Location");
		searchView.setOnQueryTextListener(this);

		return true;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

	@Override
	public boolean onQueryTextChange(String txtsearch) {
		// Toast.makeText(this, "Selected Item: " + txtsearch,
		// Toast.LENGTH_SHORT).show();
		/*
		 * if(asynchSearch!=null){ asynchSearch.cancel(true); }
		 */

		asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {

			@Override
			public void onTaskDone(String result) {

				onSearchResultGot(result);
			}
		});

		// url="https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+search_text[0]+"&components=country:in&radius=500&sensor=true&key=AIzaSyC1CHljfuBrSeTgXmm_NuJ1HZC6moNRRPE";
		txtsearch = txtsearch.replaceAll(" ", "%20");
		names = new ArrayList<String>();
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
				+ txtsearch
				+ "&location="
				+ Locations.CURRENT_LAT
				+ ","
				+ Locations.CURRENT_LOGI
				+ "&radius=500&sensor=true&key=AIzaSyC1CHljfuBrSeTgXmm_NuJ1HZC6moNRRPE";

		homedialogview.setVisibility(View.VISIBLE);
		asynchSearch.execute(url);

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String txtsearch) {
		// Toast.makeText(this, "Selected Item: " + txtsearch,
		// Toast.LENGTH_SHORT).show();

		if (asynchSearch != null) {
			asynchSearch.cancel(true);
		}

		asynchSearch = new AsynchronoucsCall(new AsynchCallBack() {

			@Override
			public void onTaskDone(String result) {

				onSearchResultGot(result);
			}
		});

		txtsearch = txtsearch.replaceAll(" ", "%20");
		names = new ArrayList<String>();
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
				+ txtsearch
				+ "&location="
				+ Locations.SOURCE_LAT
				+ ","
				+ Locations.SOURCE_LOGI
				+ "&radius=500&sensor=true&key=AIzaSyC1CHljfuBrSeTgXmm_NuJ1HZC6moNRRPE";

		homedialogview.setVisibility(View.VISIBLE);
		asynchSearch.execute(url);

		return true;
	}

	public void onSearchResultGot(String result) {

		homedialogview.setVisibility(View.INVISIBLE);
		Log.e("result of search address", result);
		if (result != null) {
			try {
				// Getting Array of Contacts\
				JSONObject json = new JSONObject(result);
				contacts = json.getJSONArray(TAG_RESULT);

				for (int i = 0; i < contacts.length(); i++) {
					JSONObject c = contacts.getJSONObject(i);
					String description = c.getString("description");
					Log.d("description", description);
					names.add(description);
				}

				ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(
						this, android.R.layout.simple_list_item_1, names);
				listView.setAdapter(itemsAdapter);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

}
