Android overview
=================
* Linux-based platform which runs on mobile phones and pads. 
* Has a considerable market share

### Android development
* Java SDK with emulators 
    * Eclipse plugin
* Native SDK available for special cases

### Android devices
* Hundreds of devices, mostly touch screen
* [Most devices](http://developer.android.com/resources/dashboard/platform-versions.html) are at runtime 2.1 or later, latest and greatest is 4.1. 
* Multiple resolutions and aspect ratios, keyboard configurations.

Workshop targets
=========================
* Create a clickable button
* Create a new activity (screen)
* Do some on-device debugging
* Handle multiple screen resolutions

Requirements - bring this
=========================
* A computer (PC, Mac or Linux)
* An NFC-enabled device (Android 4.0 or higher)
* An USB cable to connect computer and device
* Eclipe with Android SDK installed, see below.

Installation - do this before you arrive
========================================
1. Install Eclipse 3.7.2 (Indigo) or later and launch it
2. Install ADT plugin from update site https://dl-ssl.google.com/android/eclipse/
3. An Android wizard should appear automatically, run through it. Otherwise install latest Android SDK runtime 4.0.x, platform tools and USB drivers via Eclipse menu Window->Android SDK Manager.
4. Open Window->Android SDK Manager and install the default selected items.
5. Install [NFC plugin](http://nfc-eclipse-plugin.googlecode.com) from update site http://nfc-eclipse-plugin.googlecode.com/git/nfc-eclipse-plugin-feature/update-site/ 
6. Install Android application [NFC Developer](https://play.google.com/store/apps/details?id=com.antares.nfc) from Android Play.
7. Check out this (https://github.com/skjolber/Fagmote.git) Git repository.

Task 1 - Hello world
====================

### a. Create new project and launch emulator
* Create new Android project called 'HelloWorld'
* Launch emulator via project 'run as Android application'. 
    * Create a new virtual device

### b. Change hello world text
Change text from 'Hello world' to 'Hello Antares'

Hint: Check the files in the 'res' directory

### c. Add button in XML
Add a button to GUI by modifying 'main.xml' in 'res/layout'. Add the XML

    <Button android:id="@+id/button"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Hello, I am a Button" 
    />

### d. Find XML button view at runtime
Access XML-generated button instance from within the HelloWorldActivity.onCreate(..) method.

Hint: Use the method findViewById(..) method together with auto-generated class R.id.xxx.

### e. Add button click listener
Connect a click listener to the button. Log text to console on click:

            Log.d(getClass.getSimpleName(), "Button was clicked!");

Hint: Use method Button.setOnClickListener(..).

Hint: Show view 'LogCat' in Eclipse

Task 2 - create new Activity
============================
Check out the topmost figure in [Android documentation](http://developer.android.com/reference/android/app/Activity.html).

### a. Create new Activity class
Create a new class 'MyActivity' with extends the Activity class. Copy the 'onCreate' method from HelloWorldActivity.

### b. Create a new layout file for MyActivity
Create a new layout xml file in 'res/layout'. Change the MyActivity.onCreate class so that it loads the new layout file.

### c. Start 'MyActivity'
Start the activity from the button click listener in task 1. 

Launch the new activity like this: 

    Intent i = new Intent(this, MyActivity.class);
    startActivity(i);

Hint: Add activity to AndroidManifest.xml

Task 3 - on-device debugging
============================
Connect Android device using USB cable and enable developer mode in settings:
* USB debugging
* Remain awake when charging

Launch hello world application via right-clicking on project and 'Run As -> Android application'. Show view 'LogCat' in Eclipse and verify that messages appear.

Task 4 - multiple screen resolutions
=====================================
## a. Resource folders
[Determine](http://developer.android.com/guide/practices/screens_support.html) what the resource folders

* drawable-ldpi
* drawable-mdpi
* drawable-hdpi 

are used for. 

## b. Image view
Create directory 'res/drawable' and copy file [android.png](http://www.mediafire.com/imgbnc.php/1ba890f73cfdc925e08b13fe34d5141e6g.jpg) into the directory. Then add the following to main.xml:

    <ImageView 
      android:id="@+id/android_image"
      android:src="@drawable/android"
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
    />

Use AVD Manager to create multiple virtual (emulator) devices to see how different devices see the same image. Then add scaled versions to ldpi, mdpi and hdpi and try again.
