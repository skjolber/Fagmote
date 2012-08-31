package com.helloworld.nfc;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import org.nfctools.ndef.NdefContext;
import org.nfctools.ndef.NdefMessageDecoder;
import org.nfctools.ndef.NdefMessageEncoder;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.records.TextRecord;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class HelloWorldNFCActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private static final int MESSAGE_SENT = 1;

	private static final String TAG = HelloWorldNFCActivity.class.getSimpleName();

	protected NfcAdapter nfcAdapter;
	protected PendingIntent nfcPendingIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// initialize NFC
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Register Android Beam callback
		nfcAdapter.setNdefPushMessageCallback(this, this);
		// Register callback to listen for message-sent success
		nfcAdapter.setOnNdefPushCompleteCallback(this, this);
	}

	public void enableForegroundMode() {
		Log.d(TAG, "enableForegroundMode");

		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
		IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
	}

	public void disableForegroundMode() {
		Log.d(TAG, "disableForegroundMode");

		nfcAdapter.disableForegroundDispatch(this);
	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {			
			TextView textView = (TextView) findViewById(R.id.title);

			// task 2
			textView.setText("Hello NFC!");

			Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			if (messages != null) {

				Log.d(TAG, "Found " + messages.length + " NDEF messages");

				vibrate(); // signal found messages :-)

				NdefMessageDecoder ndefMessageDecoder = NdefContext.getNdefMessageDecoder();
				// parse to records - byte to POJO
				for (int i = 0; i < messages.length; i++) {
					List<Record> records = ndefMessageDecoder.decodeToRecords(((NdefMessage)messages[i]).toByteArray());

					Log.d(TAG, "Found " + records.size() + " records in message " + i);
					
					for(int k = 0; k < records.size(); k++) {
						Log.d(TAG, " Record #" + k + " is of class " + records.get(k).getClass().getSimpleName());
					}
 				}
			}
		} else {
			// ignore
		}
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");

		super.onResume();

		enableForegroundMode();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");

		super.onPause();

		disableForegroundMode();
	}

	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(TAG, "createNdefMessage");

		// create record to be pushed
		TextRecord record = new TextRecord("This is my text record");

		// encode one or more record to NdefMessage
		NdefMessageEncoder ndefMessageEncoder = NdefContext.getNdefMessageEncoder();
		try {
			return new NdefMessage(ndefMessageEncoder.encodeSingle(record));
		} catch (FormatException e) {
			throw new RuntimeException("Problem encoding record", e);
		}
	}

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		Log.d(TAG, "onNdefPushComplete");
		
		// A handler is needed to send messages to the activity when this
		// callback occurs, because it happens from a binder thread
		mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
	}

	/** This handler receives a message from onNdefPushComplete */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_SENT:
				Toast.makeText(getApplicationContext(), "Message beamed!", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};	
	
	private void vibrate() {
		Log.d(TAG, "vibrate");
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);
	}


}