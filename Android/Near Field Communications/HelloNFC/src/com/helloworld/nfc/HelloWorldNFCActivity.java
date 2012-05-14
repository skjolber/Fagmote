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
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;

public class HelloWorldNFCActivity extends Activity implements CreateNdefMessageCallback {
	
    private static final String TAG = HelloWorldNFCActivity.class.getSimpleName();

	protected NfcAdapter nfcAdapter;
	protected PendingIntent nfcPendingIntent;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        
        // Register Android Beam callback
        nfcAdapter.setNdefPushMessageCallback(this, this);
    }
    
	public void enableForegroundMode() {
    	Log.d(TAG, "enableForegroundMode");

        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for tags
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED); // filter for Android Beam
        IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected, ndefDetected};
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
        	textView.setText("Hello NFC");
        	
    		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
    		if (messages != null) {
    			NdefMessage[] ndefMessages = new NdefMessage[messages.length];
    		    for (int i = 0; i < messages.length; i++) {
    		        ndefMessages[i] = (NdefMessage) messages[i];
    		    }
    		    
    		    Log.d(TAG, "Found " + messages.length + " messages");
    		    
    			NdefMessageDecoder ndefMessageDecoder = NdefContext.getNdefMessageDecoder();
    			// parse to records - byte to POJO
    		    for (int i = 0; i < messages.length; i++) {
    		    	List<Record> records = ndefMessageDecoder.decodeToRecords(ndefMessages[i].toByteArray());
    		    	
        		    Log.d(TAG, "Found " + records.size() + " records in message " + i);
    		    }
    		}
    	} else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        	TextView textView = (TextView) findViewById(R.id.title);
        	textView.setText("Hello Android Beam");
    		
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
    	
    	// create text record
        TextRecord record = new TextRecord();
        record.setText("Android Bean");
        record.setLocale(Locale.ENGLISH);
        record.setEncoding(Charset.forName("UTF-8"));
        
        // encode one or more records to NdefMessage	
        NdefMessageEncoder ndefMessageEncoder = NdefContext.getNdefMessageEncoder();
		try {
			return new NdefMessage(ndefMessageEncoder.encodeSingle(record));
		} catch (FormatException e) {
			throw new RuntimeException("Problem encoding record", e);
		}
    }

	
}