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
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if(tag != null) {

			IsoDep isoDep = null;
			
			String[] techList = tag.getTechList();
			for (String tech : techList) {
				Log.d(TAG, "Tech " + tech);

				if (tech.equals(android.nfc.tech.IsoDep.class.getName())) {
					isoDep = android.nfc.tech.IsoDep.get(tag);
				}
			}
			
			if(isoDep != null) {
				Log.d(TAG, "Detected Desfire tag");
				
		        TextView textView = (TextView) findViewById(R.id.title);
		        textView.setText("Desfire tag!");

				try {
					isoDep.connect();

					DesfireReader reader = new DesfireReader(isoDep);

					VersionInfo versionInfo = reader.getVersionInfo();

					Log.d(TAG, "UID " + toHexString(versionInfo.getUid()));

					Log.d(TAG, "Hardware version " + versionInfo.getHardwareVersion() + ", software version " + versionInfo.getSoftwareVersion());
					Log.d(TAG, "Hardware capacity " + versionInfo.getHardwareStorageSize() + ", software capacity " + versionInfo.getSoftwareStorageSize());

					int[] applicationDirectory = reader.getApplicationDirectory();

					Log.d(TAG, "Read " + applicationDirectory.length + " apps");

					for (int i = 0; i < applicationDirectory.length; i++) {
						reader.selectApplication(applicationDirectory[i]);

						int[] fileList = reader.getFiles();

						Log.d(TAG, "App " + Integer.toHexString(applicationDirectory[i]) + " at index " + i + " has " + fileList.length + " files");


						for (int k = 0; k < fileList.length; k++) {
							DesfireFileSettings fileSettings = reader.getFileSettings(fileList[k]);

							Log.d(TAG, "Read file " + k + " settings " + fileSettings.getClass().getSimpleName() + " 0x" + Integer.toHexString(fileList[k]) + " " + fileSettings.getFileTypeName());

							// print file access
							if(fileSettings.freeReadAccess()) {
								Log.d(TAG, "File " + k + " is readable");
							}
							if(fileSettings.freeWriteAccess()) {
								Log.d(TAG, "File " + k + " is writable");
							}

							byte[] fileContents = reader.readFile(fileList[k]);

							Log.d(TAG, "Read " + fileContents.length + " bytes for file "+ k);
						}
					}

				} catch (Exception e) {
					Log.d(TAG, "Problem accessing Desfire tag", e);
				} finally {
					try {
						isoDep.close();
					} catch (IOException e) {
						// ignore
					}
				}					
			} else {
				Log.d(TAG, "Did not detect a Desfire tag");
				
		        TextView textView = (TextView) findViewById(R.id.title);
		        textView.setText("Unknown tag!");
			}			
		} else {
			Log.d(TAG, "No tag");
		}

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
