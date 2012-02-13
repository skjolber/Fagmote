package com.antares.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HelloWorldActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // find the button instance
        Button button = (Button) findViewById(R.id.button);
        
        // add a click listener to button
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
            		// log click
                    Log.d(getClass().getSimpleName(), "Button was clicked!");
                    
                    // launch activity
                    Intent i = new Intent(HelloWorldActivity.this, MyActivity.class);
                    startActivity(i);
            }
        });

    }
}