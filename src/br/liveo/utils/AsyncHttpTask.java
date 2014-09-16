package br.liveo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncHttpTask extends AsyncTask<String, Void, String>{

    private HttpHandler httpHandler;
    public AsyncHttpTask(HttpHandler httpHandler){
        this.httpHandler = httpHandler;
    }

    @Override
    protected String doInBackground(String... params) {
        
    	InputStream inputStream = null;
        String result = "";
       
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
           
            //method name
            HttpPost httpost = (HttpPost)httpHandler.getHttpRequestMethod();
            httpost.setHeader("Content-type", "application/x-www-form-urlencoded");
//            httpost.setHeader("json",params[0]);
            httpost.setEntity(new StringEntity("json="+params[0]));
            
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
    @Override
    protected void onPostExecute(String result) {
        httpHandler.onResponse(result);
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