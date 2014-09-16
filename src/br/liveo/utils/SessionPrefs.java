package br.liveo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SessionPrefs {

	Context myContext;

	public SessionPrefs(Context ctx) {
	  myContext = ctx;
	}

	/*
	 * Allows you to save and retrieve persistent key-value pairs of primitive data types.This data will persist across user sessions 
	 */
	public void setPreference(String key, String value) {
	  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myContext);
	      
	  SharedPreferences.Editor editor = prefs.edit();
	  editor.putString(key, value);
	  editor.commit();  // important!  Don't forget!
	}

	public void clearAllPreferences() {
	  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myContext);
	  
	  SharedPreferences.Editor editor = prefs.edit();
	  editor.clear();
	  editor.commit();  // important!  Don't forget!
	}

	public String getPreference(String key) {
	  String val = "";
	  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(myContext);
	  
	  val = prefs.getString(key, "");
	  return val;
	}
}