package org.secuso.privacyfriendlywerwolf.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Splash Activity which is shown while starting the application
 * Tutorial will be started while loading.
 *
 *
 * @author Florian Staubach <florian.staubach@stud.tu-darmstadt.de>
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }
}
