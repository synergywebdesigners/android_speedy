package com.braintreepayments.demo;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.braintreepayments.api.dropin.BraintreePaymentActivity;
import com.braintreepayments.api.dropin.view.SecureLoadingSpinner;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.speedy.main.R;

public class FinishedActivity extends Activity {

    private SecureLoadingSpinner mLoadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_finished);
        mLoadingSpinner = (SecureLoadingSpinner) findViewById(R.id.loading_spinner);

        sendNonceToServer(
                getIntent().getStringExtra(BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE),getIntent().getStringExtra("fare"));
    }

    private void sendNonceToServer(String nonce,String fare) {
        RequestParams params = new RequestParams();
        params.put("nonce", nonce);
        params.put("fare", fare);
        Log.d("Fare is == >> "," "+fare);
        new AsyncHttpClient().post(OptionsActivity.getEnvironmentUrl(this), params,
                new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						// TODO Auto-generated method stub
						try {
							String str = new String(arg2,"UTF-8");
							Log.d("Response ==>> "," ====>>> "+str);
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						 showSuccessView();
					}
                });
    }

    private void showSuccessView() {
        mLoadingSpinner.setVisibility(View.GONE);
        findViewById(R.id.thanks).setVisibility(View.VISIBLE);
    }

}
