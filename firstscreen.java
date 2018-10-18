package com.example.noshin.cyride;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class firstscreen extends Activity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstscreen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();

                if (user != null) {
                    /// already logged in
                    /// for bypassing
                    startActivity(new Intent(firstscreen.this, SourceDestination.class));
                } else {
                startActivity(new Intent(firstscreen.this, MainActivity.class));
                finish();
                }
                Log.w("Splash", "run: ", null);
            }
        }, 5000);   //5 seconds
    }
}
