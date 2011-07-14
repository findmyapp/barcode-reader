package dev.barcode;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Klient for å kommunisere med serveren En del kodeduplisering, bør
 * refaktoreres
 * 
 * @author audun.sorheim
 * 
 */

public class RestClient { // emulator
	private String TAG = "RESTCLIENT";
	private boolean sent;
	private Gson gson;

	public RestClient() {
		sent = false;
	}

	/**
	 * Tar inn et event nummer og sender det til eventStream
	 * 
	 * @param eventNumber
	 * @return
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

		// StringEntity entity;
		// entity = new StringEntity(json);
		// request.setEntity(entity);
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
