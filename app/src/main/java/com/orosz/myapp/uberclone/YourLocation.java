package com.orosz.myapp.uberclone;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orosz.myapp.uberclone.Model.RequestUber;
import com.orosz.myapp.uberclone.Model.UberDriver;

public class YourLocation extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    FirebaseDatabase database;
    DatabaseReference table_request_uber;
    DatabaseReference table_uberDriver;

    String currentUserUID;
    String currentUserOption;
    TextView infoRequestUber;
    String driverUID;
    Button requestUberButton;
    boolean requestUberActive = false;

    private GoogleMap mMap;

    LocationManager locationManager;
    String longitudeReq, latitudeReq;
    //Location location;
    String provider;

    public void requestUber(View view) {

        if (requestUberActive == false) {


            RequestUber requestUber = new RequestUber(currentUserUID, "NoID", longitudeReq, latitudeReq,"Null", "Null", "Active");
            table_request_uber.child(currentUserUID).setValue(requestUber);

            infoRequestUber.setText("Finding Uber driver...");
            requestUberButton.setText("Cancel Uber");
            requestUberActive = true;


        } else {


            table_request_uber.child(currentUserUID).removeValue();
            infoRequestUber.setText("Request Cancelled");
            requestUberButton.setText("Request Uber");
            requestUberActive = false;
        }

}








    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //location manager stuff
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);

        Location location = locationManager.getLastKnownLocation(provider);

//        if (location != null) {
//
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),10 ));
//            mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your location"));
//
//        }



        //get annonymous user credential

        Intent intent = getIntent();
        currentUserUID = intent.getStringExtra("UserUID");
        currentUserOption = intent.getStringExtra("UserOption");

        //Init firebase
        database = FirebaseDatabase.getInstance();
        table_request_uber = database.getReference("RequestUber");
        table_uberDriver = database.getReference("UberDriver");

        infoRequestUber = (TextView) findViewById(R.id.infoTextView);
        requestUberButton = (Button) findViewById(R.id.requestUberButton);

        if (ViewActivity.driverAcceptedRequest) {

            table_request_uber.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    RequestUber requestUber = dataSnapshot.child(currentUserUID).getValue(RequestUber.class);
                    driverUID = requestUber.getDriverUID();

                    if (requestUber.getReqStatus().equals("accepted")) {
                        infoRequestUber.setText("Your request has been accepted");
                        requestUberButton.setVisibility(View.INVISIBLE);

                        table_uberDriver.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                UberDriver uberDriver = dataSnapshot.child(driverUID).getValue(UberDriver.class);
                                mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(uberDriver.getLatitude()), Double.parseDouble(uberDriver.getLongitude()))).title("Driver location"));

                                double distance = ViewActivity.getRiderDriverDistance(Double.parseDouble(longitudeReq),
                                        Double.parseDouble(latitudeReq),
                                        Double.parseDouble(uberDriver.getLongitude()),
                                        Double.parseDouble(uberDriver.getLatitude()));

                                infoRequestUber.setText("Your UberDriver is " + ViewActivity.df2.format(distance) + "km away");
                                requestUberButton.setVisibility(View.INVISIBLE);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onLocationChanged(Location location) {

        //remove old markers
        mMap.clear();

        longitudeReq = String.valueOf(location.getLongitude());
        latitudeReq = String.valueOf(location.getLatitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),10 ));
        mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Your location"));

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

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();

        //permission request surpassed
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
    }
}
