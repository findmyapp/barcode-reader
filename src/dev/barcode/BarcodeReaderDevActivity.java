package dev.barcode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class BarcodeReaderDevActivity extends Activity {
	private Button scanButton, sendEventButton, downloadAppButton;
	private RestClient restClient;
	private EditText scanResultsEditText;
	private final String NOINFOFOUND = "No info found";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		restClient = new RestClient();

		scanResultsEditText = (EditText) findViewById(R.id.scanResultsEditText);
		
		scanButton = (Button) findViewById(R.id.BarcodeButton);
		scanButton.setOnClickListener(scanButtonListener);
		
		sendEventButton = (Button) findViewById(R.id.sendEventButton);
		sendEventButton.setOnClickListener(sendEventButtonListener);
		
		downloadAppButton = (Button) findViewById(R.id.downloadAppButton);
		downloadAppButton.setOnClickListener(downloadAppListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				scanResultsEditText.setText(contents);

			} else if (resultCode == RESULT_CANCELED) {
				scanResultsEditText.setText(NOINFOFOUND);
			}
		}
	}

	/**
	 * Popup window to display text of your choice
	 * 
	 * @param message
	 */
	private void showDialog(CharSequence message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Info!");
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	/**
	 * OnclickListener for barcode button: Starts the barcode reader via intent
	 */
	private final Button.OnClickListener scanButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			startActivityForResult(intent, 0);
		}
	};

	/**
	 * OnclickListener for send event button: Sends event to server
	 */
	private final Button.OnClickListener sendEventButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String eventIdString = scanResultsEditText.getText().toString();
			if (!eventIdString.startsWith("01492")) {
				showDialog("Ikke gyldig event id");
			} else {
				String dummyId = Integer.toString(1);
				restClient.sendEventToServer(dummyId, eventIdString);
			}
		}
	};
	
	/**
	 * Onclicklistener for download app button
	 */
	private final Button.OnClickListener downloadAppListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String eventIdString = scanResultsEditText.getText().toString();
			if (eventIdString.startsWith("http") || eventIdString.startsWith("market")) {
				downloadApp(eventIdString);
			} else {
				showDialog("Ikke gyldig addresse");
			}
		}
	};
	
	/**
	 * Method to start an intent with an input URL
	 * @param URL to some application
	 */
	public void downloadApp(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(intent);
	}

}