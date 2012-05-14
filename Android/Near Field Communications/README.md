Near Field Communications overview
==================================
* close-range radio communication - 4 cm effective range
* effective for small bursts of data
* comes in 'active' and 'passive' forms, typically as in an active mobile device and a passive (sticker) tag  
* expected smart-phone penetration of 50% by 2013

### Android NFC development
* Android SDK 4.0 or higher with an NFC device (NFC is not supported by emulator yet) 

Installation
============
1. Install Eclipse
2. Install ADT plugin from update site https://dl-ssl.google.com/android/eclipse/
3. An Android wizard should appear automatically, run through it. Otherwise install latest Android SDK runtime 4.0.3, platform tools and USB drivers via Eclipse menu Window->Android SDK Manager.
4. Open Window->Android SDK Manager and install the default selected items.
5. Install NDEF plugin from update site http://nfc-eclipse-plugin.googlecode.com/git/nfc-eclipse-plugin-feature/update-site/ 
6. Install Android application [NFC Developer](https://play.google.com/store/apps/details?id=com.antares.nfc) from Android Play.

Task 1 - Hello NFC tag
======================

### a. Create new project and launch application from tag
1. Create new Android project called 'HelloWorld NFC' and use package name com.helloworld.nfc.
2. Connect Android device and launch application via project 'run as Android application'.
3. Close the application, scan the tag provided by Antares to launch the application via an [Android Application Record](http://developer.android.com/guide/topics/nfc/nfc.html#aar).

### b. Change Hello World text by scanning a tag
Add NFC support for Hello World.

1. Add NFC [permissions](http://developer.android.com/guide/topics/nfc/nfc.html#manifest) to AndroidMainfest.xml:

        <uses-permission android:name="android.permission.NFC" />
	    <uses-feature android:name="android.hardware.nfc" android:required="true" />

2. Initialize NFC [foreground mode](http://developer.android.com/guide/topics/nfc/advanced-nfc.html#foreground-dispatch) in the Hello World activity:

    	protected NfcAdapter nfcAdapter;
        protected PendingIntent nfcPendingIntent;
        
		@Override
    	public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
        	setContentView(R.layout.main);
            
        	nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        	nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    	}
	
* Enable and disable foreground mode in onResume() and onPause():

		public void enableForegroundMode() {
        	IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        	IntentFilter[] writeTagFilters = new IntentFilter[] {ndefDetected};
        	nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
    	}
	
		public void disableForegroundMode() {
			nfcAdapter.disableForegroundDispatch(this);
		}

* Change text from 'Hello world' to 'Hello NFC tag' when a tag is scanned:

    	@Override
    	public void onNewIntent(Intent intent) {
			// check for NFC related actions
        	if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
        		TextView textView = (TextView) findViewById(R.id.title);
        		textView.setText("Hello NFC tag");
        	} else {
        		// ignore
        	}
    	}
	
### c. Read payload of the scanned tag
Check for [NDEF](http://developer.android.com/guide/topics/nfc/nfc.html) messages using 

		Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (messages != null) {
			NdefMessage[] ndefMessages = new NdefMessage[messages.length];
		   	for (int i = 0; i < messages.length; i++) {
		   	    ndefMessages[i] = (NdefMessage) messages[i];
		   	}
		
			// found messages
		}


### d. Parse payload of the scanned tag using [nfctools](https://github.com/grundid/nfctools/tree/master/nfctools-ndef/src/main/java/org/nfctools/ndef)
Create directory 'libs' and add [nfctools.jar](http://nfc-eclipse-plugin.googlecode.com/git/Android%20NFC/libs/nfctools.jar) to your classpath.
Parse an NDEF message into records using

		NdefMessageDecoder ndefMessageDecoder = NdefContext.getNdefMessageDecoder();
		// parse to records - byte to POJO
		List<Record> records = ndefMessageDecoder.decodeToRecords(ndefMessages[i].toByteArray());

### e. Determine which NDEF record types are present on the tag.
Iterate of the parsed records and investigate their type and contents.

Hint: You should be able to two types of records.

Task 2 - create new NDEF tag
============================
Create a new file using New -> Other -> Near Field Communications -> NDEF File.

### a. Create an Android Application Record
Create an Android Application Record with package name 'no.java.schedule' using the editor.

### b. Write the Android Application Record to a tag.
Use the 'NFC Developer' application to scan the QR code and then write hold a tag to the back of the device.

### c. Scan the newly created tag

Hint: If you do not already have an application 'no.java.schedule' installed, Android will open google market and look for it there.

Task 3 - device to device communication: Android Beam
=====================================================
Use [Android Beam](http://developer.android.com/guide/topics/nfc/nfc.html#p2p) to exchange information between two devices.

### a. Register push message callback interface 
Implement the CreateNdefMessageCallback interface and register callback using 

        // Register callback
        nfcAdapter.setNdefPushMessageCallback(this, this);

### b. Write push NdefMessage  
Compose an NdefMessage in the createNdefMessage(..) method:

        TextRecord record = new TextRecord();
		// set text
		// ...
				
		// encode one or more record	
        NdefMessageEncoder ndefMessageEncoder = NdefContext.getNdefMessageEncoder();
		return new NdefMessage(ndefMessageEncoder.encodeSingle(record));

### c. Read push NdefMessage
Change text from 'Hello world' to 'Hello NFC device' when a is message pushed from another NFC device.

Hint: Extent intent filter from task 1 to include NfcAdapter.ACTION_NDEF_DISCOVERED.