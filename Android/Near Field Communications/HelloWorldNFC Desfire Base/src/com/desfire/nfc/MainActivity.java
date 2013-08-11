package com.desfire.nfc;

import java.io.IOException;

import org.ndeftools.util.activity.NfcDetectorActivity;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends NfcDetectorActivity {

	private static final String TAG = MainActivity.class.getName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setDetecting(true);
	}

	/**
	 * 
	 * NFC feature was found and is currently enabled
	 * 
	 */

	@Override
	protected void onNfcStateEnabled() {
		toast(getString(R.string.nfcAvailableEnabled));
	}

	/**
	 * 
	 * NFC feature was found but is currently disabled
	 * 
	 */

	@Override
	protected void onNfcStateDisabled() {
		toast(getString(R.string.nfcAvailableDisabled));
	}

	/**
	 * 
	 * NFC setting changed since last check. For example, the user enabled NFC in the wireless settings.
	 * 
	 */

	@Override
	protected void onNfcStateChange(boolean enabled) {
		if(enabled) {
			toast(getString(R.string.nfcAvailableEnabled));
		} else {
			toast(getString(R.string.nfcAvailableDisabled));
		}
	}

	/**
	 * 
	 * This device does not have NFC hardware
	 * 
	 */

	@Override
	protected void onNfcFeatureNotFound() {
		toast(getString(R.string.noNfcMessage));
	}

	@Override
	protected void nfcIntentDetected(Intent intent, String action) {
		
	}

	public void toast(String message) {
		Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
		toast.show();
	}

	/**
	 * Converts the byte array to HEX string.
	 * 
	 * @param buffer
	 *            the buffer.
	 * @return the HEX string.
	 */
	public String toHexString(byte[] buffer) {
		StringBuilder sb = new StringBuilder();
		for(byte b: buffer)
			sb.append(String.format("%02x ", b&0xff));
		return sb.toString().toUpperCase();
	}
	
	
}
