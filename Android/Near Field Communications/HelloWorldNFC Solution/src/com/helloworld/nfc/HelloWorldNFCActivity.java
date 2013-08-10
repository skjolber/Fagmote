package com.helloworld.nfc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.ndeftools.Message;
import org.ndeftools.Record;
import org.ndeftools.externaltype.AndroidApplicationRecord;
import org.ndeftools.externaltype.ExternalTypeRecord;
import org.ndeftools.externaltype.GenericExternalTypeRecord;
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
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

public class HelloWorldNFCActivity extends Activity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback {

	private static final int MESSAGE_SENT = 1;

	private static final String TAG = HelloWorldNFCActivity.class.getName();

	protected NfcAdapter nfcAdapter;
	protected PendingIntent nfcPendingIntent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	   	Log.d(TAG, "onCreate");

		setContentView(R.layout.main);

		// initialize NFC
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Register Android Beam callback
		nfcAdapter.setNdefPushMessageCallback(this, this);
		// Register callback to listen for message-sent success
		nfcAdapter.setOnNdefPushCompleteCallback(this, this);
		
		if(getIntent().hasExtra(NfcAdapter.EXTRA_TAG)) {
		    TextView textView = (TextView) findViewById(R.id.title);
		    textView.setText("Hello NFC tag from home screen!");
		};
		
		printTagId(getIntent());
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
		setIntent(intent);
		
	    if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
	        Log.d(TAG, "A tag was scanned!");
	        TextView textView = (TextView) findViewById(R.id.title);
	        textView.setText("Hello NFC tag!");
	        
	        vibrate(); // signal found messages :-)
	        
			printTagId(getIntent());
	    }
	    
	    Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
	    if (messages != null) { 
	        Log.d(TAG, "Found " + messages.length + " NDEF messages");
	        
	        NdefMessage ndefMessage = (NdefMessage)messages[0];
	        
	        // parse to high-level records
            try {
                List<Record> records = new Message(ndefMessage);

                Log.d(TAG, "Found " + records.size() + " records in message");

                for(int k = 0; k < records.size(); k++) {
                	Record record = records.get(k);
                	
                    Log.d(TAG, " Record #" + k + " is of class " + record.getClass().getName());
                    
                    // TODO: add breakpoint or log statement and inspect record. 
                }
                
                // filter out text record, increment and write back
                // subtask 4c
                for(int k = 0; k < records.size(); k++) {
                	Record record = records.get(k);
                	if(record instanceof TextRecord) {
                		TextRecord textRecord = (TextRecord)record;

                		// parse
                		int count = Integer.parseInt(textRecord.getText());
                		// increment
                		count++;
                		
                		// compose new message
                        Message composedMessage = composeMessage(Integer.toString(count));
                        NdefMessage composedMessageNdefMessage = composedMessage.getNdefMessage();

                        // write to tag
                        if(write(composedMessageNdefMessage, intent)) {
                        	Log.d(TAG, "Write success: Incremented count to " + count);
                        	
                	        TextView textView = (TextView) findViewById(R.id.title);
                	        textView.setText("Write success!");
                        } else {
                        	Log.d(TAG, "Write failure!");
                        	
                	        TextView textView = (TextView) findViewById(R.id.title);
                	        textView.setText("Write failure!");
                        }

                		return;
                	}
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Problem parsing message", e);
            }
            
            Message composedMessage = composeMessage("1");
            NdefMessage composedMessageNdefMessage = composedMessage.getNdefMessage();
            
            if(write(composedMessageNdefMessage, intent)) {
            	Log.d(TAG, "Write success!");
            	
    	        TextView textView = (TextView) findViewById(R.id.title);
    	        textView.setText("Write success!");
            } else {
            	Log.d(TAG, "Write failure!");
            	
    	        TextView textView = (TextView) findViewById(R.id.title);
    	        textView.setText("Write failure!");
            }
	    }
	}
	
	public boolean write(NdefMessage rawMessage, Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
        Ndef ndef = Ndef.get(tag);
        if(ndef != null) {
        	try {
        		Log.d(TAG, "Write formatted tag");

        		ndef.connect();
        		if (!ndef.isWritable()) {
        			Log.d(TAG, "Tag is not writeable");
                    
        		    return false;
        		}
        		
        		if (ndef.getMaxSize() < rawMessage.toByteArray().length) {
        			Log.d(TAG, "Tag size is too small, have " + ndef.getMaxSize() + ", need " + rawMessage.toByteArray().length);

        		    return false;
        		}
        		ndef.writeNdefMessage(rawMessage);
        		
        		return true;
        	} catch (Exception e) {
        		Log.d(TAG, "Problem writing to tag", e);
            } finally {
            	try {
					ndef.close();
				} catch (IOException e) {
					// ignore
				}
            }
        } else {
			Log.d(TAG, "Write to an unformatted tag not implemented");
		}

	    return false;
	}

	
	protected void printTagId(Intent intent) {
		if(intent.hasExtra(NfcAdapter.EXTRA_ID)) {
			byte[] byteArrayExtra = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			
			Log.d(TAG, "Tag id is " + toHexString(byteArrayExtra));
		}
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

	public Message composeMessage(String text) {
		Log.d(TAG, "createMessage");

		Message message = new Message(); // ndeftools ndef message
		
		// add an android application record
		AndroidApplicationRecord aar = new AndroidApplicationRecord("com.helloworld.nfc");
		message.add(aar);
		
		// add a text record
		TextRecord record = new TextRecord(text);
		message.add(record);

		return message;
	}
	
	@Override
	public NdefMessage createNdefMessage(NfcEvent event) {
		Log.d(TAG, "createNdefMessage");

		Message message = new Message(); // ndeftools ndef message
		
		// add an android application record
		AndroidApplicationRecord aar = new AndroidApplicationRecord("com.helloworld.nfc");
		message.add(aar);

		// create external type record to be pushed
		ExternalTypeRecord record = new GenericExternalTypeRecord("com.my.data", "myDataType", "This is my magic payload".getBytes(Charset.forName("UTF-8")));
		message.add(record);
		
		// encode one or more record to NdefMessage
		return message.getNdefMessage();
	}

	@Override
	public void onNdefPushComplete(NfcEvent arg0) {
		Log.d(TAG, "onNdefPushComplete");
		
		runOnUiThread(new Runnable() {
			public void run() {
    	        TextView textView = (TextView) findViewById(R.id.title);
    	        textView.setText("Message beamed!");
			}	
		});
	}
	
	private void vibrate() {
		Log.d(TAG, "vibrate");
		
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
		vibe.vibrate(500);
	}


}