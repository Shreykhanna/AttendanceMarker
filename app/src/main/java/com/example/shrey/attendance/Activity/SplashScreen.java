package com.example.shrey.attendance.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.shrey.attendance.R;

/**
 * Created by Shrey on 6/13/2018.
 */

public class SplashScreen extends StartPageActivity {
    private static int SPLASH_TIME_OUT = 3000;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splashscreen);
        new Handler().postDelayed(new Runnable(){
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if(loginDetails.getString("emailid","").length()>0)
                {
                    Intent homepageIntent = new Intent(SplashScreen.this, HomePageActivity.class);
                    homepageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homepageIntent);
                }else {
                    Intent startpageIntent = new Intent(SplashScreen.this, StartPageActivity.class);
                    startpageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startpageIntent);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}
