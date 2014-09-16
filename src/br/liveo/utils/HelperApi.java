package br.liveo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.format.DateUtils;
import android.util.Log;

public class HelperApi {

	
	public static String getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

        HttpPost httppost = new HttpPost(address);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

       /* JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            
            e.printStackTrace();
        }*/

        return stringBuilder.toString();
    }
	
	
public static String  httpCallToServer(String url1 , String jsonStr){
		
		String url =url1;
		
		InputStream inputStream = null;
        String result = "";
       
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
           
            //method name
            HttpPost httpost = new HttpPost(url);
            httpost.setHeader("Content-type", "application/x-www-form-urlencoded");
//            httpost.setHeader("json",params[0]);
            httpost.setEntity(new StringEntity("json="+jsonStr));
            
            // make the http request
            HttpResponse httpResponse = httpclient.execute(httpost);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
	}
	
	
	//--------------------------------------------------------------------------------------------
	private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	       BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	       String line = "";
	       String result = "";
	       while((line = bufferedReader.readLine()) != null)
	           result += line;
	
	       inputStream.close();
	       return result;   
	   }
	
}
