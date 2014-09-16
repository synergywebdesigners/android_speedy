package br.liveo.utils;

import org.apache.http.client.methods.HttpUriRequest;
import br.liveo.utils.AsyncHttpTask;

public abstract class HttpHandler {
	
		public abstract HttpUriRequest getHttpRequestMethod();

	    public abstract void onResponse(String result);

	    public void execute(String... params){
	        new AsyncHttpTask(this).execute(params);
	    } 
}
