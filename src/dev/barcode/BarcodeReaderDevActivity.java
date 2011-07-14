package dev.barcode;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

public class BarcodeReaderDevActivity extends Activity {
	private Button barcodeButton;
	private Button sendEventButton;
	private Button downloadAppButton;
	private RestClient restClient;
	private EditText eventFoundEditText;
	private String eventId;
	private final String NOINFOFOUND = "No info found";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		restClient = new RestClient();

		eventFoundEditText = (EditText) findViewById(R.id.eventFoundEditText);
		
		barcodeButton = (Button) findViewById(R.id.BarcodeButton);
		barcodeButton.setOnClickListener(barCodeButtonListener);
		
		sendEventButton = (Button) findViewById(R.id.sendEventButton);
		sendEventButton.setOnClickListener(sendEventButtonListener);
		
		downloadAppButton = (Button) findViewById(R.id.downloadAppButton);
		downloadAppButton.setOnClickListener(downloadAppListener);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				eventFoundEditText.setText(contents);
				eventId = contents;

			} else if (resultCode == RESULT_CANCELED) {
				eventFoundEditText.setText(NOINFOFOUND);
				eventId = "No info found";
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
		builder.setTitle("Barcode info");
		builder.setMessage(message);
		builder.setPositiveButton("OK", null);
		builder.show();
	}

	/**
	 * OnclickListener for barcode button: Starts the barcode reader via intent
	 */
	public final Button.OnClickListener barCodeButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			startActivityForResult(intent, 0);
		}
	};

	/**
	 * OnclickListener for send event button: Sends event to server
	 */
	public final Button.OnClickListener sendEventButtonListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String eventIdString = eventFoundEditText.getText().toString();
			if (!eventIdString.startsWith("01492")) {
				showDialog("Ikke gyldig event id");
			} else {
				String dummyId = Integer.toString(1);
				restClient.sendEventToServer(dummyId, eventIdString);
			}
		}
	};
	
	public final Button.OnClickListener downloadAppListener = new Button.OnClickListener() {
		public void onClick(View v) {
			String eventIdString = eventFoundEditText.getText().toString();
			if (eventIdString.startsWith("http") || eventIdString.startsWith("market")) {
				downloadApp(eventIdString);
			} else {
				showDialog("Ikke gyldig addresse");
			}
		}
	};
	
	public void downloadApp(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(intent);
	}

}