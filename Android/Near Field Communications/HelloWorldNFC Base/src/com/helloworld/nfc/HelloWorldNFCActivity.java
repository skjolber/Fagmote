package com.helloworld.nfc;

import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.wellknown.TextRecord;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class HelloWorldNFCActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {
    /** Called when the activity is first created. */
	
	private static String TAG = HelloWorldNFCActivity.class.getSimpleName();
	
	protected NfcAdapter nfcAdapter;
    protected PendingIntent nfcPendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	Log.d(TAG, "onResume");

        setContentView(R.layout.main);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    	Log.d(TAG, "onResume");
    	
    	// enableForegroundMode(); // task 2
    }
    
    @Override
    protected void onPause() { 
    	super.onResume();

    	Log.d(TAG, "onPause");

    	// disableForegroundMode(); // task 2
    }

	@Override
	public void onNewIntent(Intent intent) { // task 2 and 3
		Log.d(TAG, "onNewIntent");

		
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) { 	

		Log.d(TAG, "createNdefMessage");
		
		throw new IllegalArgumentException("Not implemented"); // task 3
	}

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		Log.d(TAG, "onNdefPushComplete");
		
		
		throw new IllegalArgumentException("Not implemented"); // task 3
	}

	/** 
	 * Activate device vibrator for 500 ms 
	 * */
	
	private void vibrate() {
		Log.d(TAG, "vibrate");

		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);
	}


}