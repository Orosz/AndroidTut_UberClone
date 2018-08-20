package com.orosz.myapp.uberclone;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orosz.myapp.uberclone.Model.RequestUber;

import java.util.ArrayList;

public class ViewRiderLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng riderCoord;
    LatLng driverCoord;
    Button acceptReq;
    Intent intent;

    boolean acceptedRequest = false;

    public void acceptRequest(View view) {
        acceptedRequest = true;

        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_request_uber = database.getReference("RequestUber");

        //Get all users and add them to the arrayList -> Without the current user
        table_request_uber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Check if user does exist in the data base
                if (dataSnapshot.child(intent.getStringExtra("riderUID")).exists()) {


                    //Get User information
                    RequestUber requestUber = dataSnapshot.child(intent.getStringExtra("riderUID")).getValue(RequestUber.class);

                    requestUber.setDriverUID(intent.getStringExtra("driverUID"));
                    requestUber.setReqStatus("accepted");
                    requestUber.setDriverLatitude(intent.getStringExtra("driverLat"));
                    requestUber.setDriverLongtitude(intent.getStringExtra("driverLong"));

                    //update request with accept driver
                    table_request_uber.child(intent.getStringExtra("riderUID")).setValue(requestUber);

                    //start navigation
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/daddr="
                                    + riderCoord.latitude +","
                                    + riderCoord.longitude +""));
                    startActivity(intent);

                    } else {

                        Toast.makeText(ViewRiderLocation.this, "Cannont accept request !", Toast.LENGTH_LONG).show();

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Cannot reach data", Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_rider_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        intent = getIntent();
        riderCoord = new LatLng(Double.parseDouble(intent.getStringExtra("riderLat")),
                Double.parseDouble(intent.getStringExtra("riderLong")));

        driverCoord = new LatLng(Double.parseDouble(intent.getStringExtra("driverLat")),
                Double.parseDouble(intent.getStringExtra("driverLong")));

        RelativeLayout mapLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                // Add a marker in Sydney and move the camera

                ArrayList<Marker> markers = new ArrayList<>();

                markers.add(mMap.addMarker(new MarkerOptions().position(riderCoord).title("Rider Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                markers.add(mMap.addMarker(new MarkerOptions().position(driverCoord).title("Your Location")));

                for (Marker marker : markers) {
                    builder.include(marker.getPosition());
                }

                LatLngBounds bounds = builder.build();

                int padding = 100;
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cameraUpdate);
            }
        });

        acceptReq = (Button)findViewById(R.id.acceptRequest);
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

//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(riderCoord).title("Rider Location"));
//        mMap.addMarker(new MarkerOptions().position(driverCoord).title("Your Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(driverCoord));


    }

    public void back(View view) {

        finish();

    }
}
