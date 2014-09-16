package com.speedy.main;

import com.speedy.main.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CreditCardActivity extends Activity {
	
	Button btn_save;
	EditText et_name,et_age,et_phone,et_speed;
	String name,age,phone,speed;
	//DBAdapter db;
	public boolean emailcheck=true;
	String str_sessionvalue;
	AlertDialog.Builder alertdialog; 
	public static String android_id;
	boolean haveConnectedWifi = false;
    boolean haveConnectedMobile = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);
	
	}
}		