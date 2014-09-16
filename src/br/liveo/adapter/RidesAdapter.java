package br.liveo.adapter;

import java.util.ArrayList;

import com.speedy.bo.RideModel;
import com.speedy.main.CustomerTripDetails;
import com.speedy.main.R;
import com.speedy.main.RideDetailInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.sax.StartElementListener;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class RidesAdapter extends BaseAdapter implements OnClickListener {

	/*********** Declare Used Variables *********/
	private Activity activity;
	private ArrayList data;
	private static LayoutInflater inflater = null;
	
	RideModel tempValues = null;
	int i = 0;

	/************* CustomAdapter Constructor *****************/
	public RidesAdapter(Activity a, ArrayList d) {

		/********** Take passed values **********/
		activity = a;
		data = d;

		/*********** Layout inflator to call external xml layout () **********************/
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/******** What is the size of Passed Arraylist Size ************/
	public int getCount() {

		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder to contain inflated xml file elements ***********/
	public static class ViewHolder {

		public TextView ride_time, ride_source, ride_dest, ride_date;
		public ImageView image;
		public TextView ride_status;

	}

	/*********** Depends upon data size called for each row , Create each ListView row ***********/
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			/********** Inflate tabitem.xml file for each row ( Defined below ) ************/
			vi = inflater.inflate(R.layout.summerylistrow, null);

			/******** View Holder Object to contain tabitem.xml file elements ************/
			holder = new ViewHolder();
			holder.ride_time = (TextView) vi.findViewById(R.id.ride_summary);
			holder.ride_time.setEllipsize(TruncateAt.END);
			holder.ride_date = (TextView) vi.findViewById(R.id.ride_datetime);
			holder.ride_status = (TextView)vi.findViewById(R.id.ride_status);

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else {
			holder = (ViewHolder) vi.getTag();
		}

		if (data.size() <= 0) {

			holder.ride_time.setText("No Data");
			// holder.ride_source.setText("No Data");
			// holder.ride_dest.setText("No Data");
			holder.ride_date.setText("No Data");

		} else {

			/***** Get each Model object from Arraylist ********/
			tempValues = null;
			tempValues = (RideModel) data.get(position);

			/************ Set Model values in Holder elements ***********/
			holder.ride_time.setText("FROM- " + tempValues.getRide_Source()
					+ " TO " + tempValues.getRide_Destination());

			// holder.ride_source.setText();
			// holder.ride_dest.setText();
			holder.ride_date.setText(tempValues.getRide_Date() + ""
					+ tempValues.getRide_Time());
			holder.ride_status.setText(tempValues.getTripStatus());
			
			/******** Set Item Click Listner for LayoutInflater for each row ***********/
			vi.setOnClickListener(new OnItemClickListener(position));

		}

		return vi;
	}

	@Override
	public void onClick(View v) {
		Log.v("CustomAdapter", "=====Row button clicked");
	}

	/********* Called when Item click in ListView ************/
	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;

		}

		@Override
		public void onClick(View arg0) {
			/*
			 * CustomListViewAndroidExample sct =
			 * (CustomListViewAndroidExample)activity;
			 * sct.onItemClick(mPosition);
			 */

			RideModel tempValues = (RideModel) data.get(mPosition);
			Intent tripdata = new Intent(activity,
					RideDetailInfo.class);
			tripdata.putExtra("ride_details", tempValues);
			activity.startActivity(tripdata);
		}
	}
}