package dev.barcode;

import java.io.IOException;
import java.io.InputStream;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import android.util.Log;


/**
 * Klient for å kommunisere med serveren
 * 
 * @author audun.sorheim
 * 
 */

public class RestClient { // emulator
	private String TAG = "RESTCLIENT";
	private boolean sent;


	public RestClient() {
		sent = false;
	}

	/**
	 * Tar inn et event nummer og sender det til eventStream
	 * 
	 * @param eventNumber
	 * @return true if event was successfully sent, false if not
	 */
	public boolean sendEventToServer(String userId, String eventNumber) {
		String URL = "http://findmyapp.net/findmyapp/user/" + userId
				+ "/event/" + eventNumber;
		Log.e(TAG, URL);
		postEventStream(URL);
		return sent;
	}

	/**
	 * Stream used for posting events
	 */
	private InputStream postEventStream(String URL) {
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpPost request = new HttpPost(URL);
		request.setHeader("Accept", "application/json");
		request.setHeader("Content-type", "application/json");
		
		try {

			HttpResponse getResponse = client.execute(request);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.e(TAG, "Error " + statusCode + " for URL " + URL);
				sent = false;
				return null;
			} else {
				Log.e(TAG, "Http Response " + statusCode + " for URL " + URL);
				sent = true;
			}
			HttpEntity getResponseEntity = getResponse.getEntity();
			return getResponseEntity.getContent();
		} catch (IOException e) {
			request.abort();
			Log.e(TAG, "Error for URL " + URL, e);
		}
		return null;
	}
}
