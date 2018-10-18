package com.example.noshin.cyride;

import android.app.Service;
import android.content.Intent;
import android.drm.DrmStore;
import android.graphics.PixelFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class FloatingViewService extends Service implements Runnable {


    LocationListener locationListener;
    LocationManager locationManager;

    public FloatingViewService() {
    }

    Thread t;

    private WindowManager mWindowManager;
    private View mFloatingView;


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility() == View.VISIBLE;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    private Chronometer ll;
    private ImageView cross;
    private String slat, slong, dlat, dlong;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        slat = (String) intent.getStringExtra("slat");
        slong = (String) intent.getStringExtra("slong");
        dlat = (String) intent.getStringExtra("dlat");
        dlong = (String) intent.getStringExtra("dlong");
        started = true;
        return super.onStartCommand(intent, flags, startId);
    }


    LatLng curr = null;

    @Override
    public void onCreate() {
        super.onCreate();


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Toast.makeText(getApplicationContext(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                curr = new LatLng(location.getLatitude(), location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, locationListener);


        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);

        //The root element of the collapsed view layout
        final View collapsedView = mFloatingView.findViewById(R.id.collapse_view);

        ll = mFloatingView.findViewById(R.id.chronometer);
        ll.start();

        cross = mFloatingView.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "alal", Toast.LENGTH_LONG).show();
                ll.stop();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent in = new Intent(getApplicationContext(), MapsActivity.class);
                        in.putExtra("latlng", "");
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(in);
                    }
                }, 3000);
                stopSelf();
            }
        });

        //Drag and move floating view using user's touch action.
        mFloatingView.findViewById(R.id.root_container).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {

                            }
                        }
//                        stopSelf();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });

//        started = true;
//        this.run();
        t = new Thread(this);
        t.start();

    }

    private boolean started = false;
    int count = 0;

    @Override
    public void run() {
        while (started) {
            if (curr != null) {
//                Toast.makeText(getApplicationContext(), "dhukse", Toast.LENGTH_SHORT).show();
                try {
                    if (Math.abs(curr.latitude - Integer.valueOf(dlat)) <= 20 && Math.abs(curr.longitude - Integer.valueOf(dlong)) <= 5) {
                        cross.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getApplicationContext(), Math.abs(curr.latitude - Integer.valueOf(dlat)) + "", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(250);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
