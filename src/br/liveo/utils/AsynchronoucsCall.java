package br.liveo.utils;

import br.liveo.fragments.AsynchCallBack;
import android.os.AsyncTask;


public class AsynchronoucsCall extends AsyncTask<String , Void, String>{
	
	AsynchCallBack callBackResult = null;
	public AsynchronoucsCall(AsynchCallBack callBackObj) {
		callBackResult = callBackObj;
	}
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... arg0) {
		
		String result = HelperApi.getLocationInfo(arg0[0]);
		
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {			
		super.onPostExecute(result);
		callBackResult.onTaskDone(result);		
	}
	
	
}
