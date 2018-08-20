package com.orosz.myapp.uberclone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orosz.myapp.uberclone.Model.RequestUber;
import com.orosz.myapp.uberclone.Model.UberDriver;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ViewActivity extends AppCompatActivity implements LocationListener{

    ListView requestsListView;
    ArrayList<String> listViewContent;
    static ArrayList<RequestUber> allRequest;
    ArrayAdapter arrayAdapter;

    String currentUserUID;
    String currentUserOption;

    LocationManager locationManager;
    String longitudeDriver, latitudeDriver;
    //Location location;
    String provider;

    FirebaseDatabase database;
    DatabaseReference table_uberDriver;

    private static DecimalFormat df2 = new DecimalFormat(".##");


    protected void getRiderRequests() {
        //Init firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table_request_uber = database.getReference("RequestUber");

        //Get all users and add them to the arrayList -> Without the current user
        table_request_uber.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                allRequest.clear();
                listViewContent.clear();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    RequestUber requestUber = postSnapshot.getValue(RequestUber.class);


                    if (requestUber.getDriverUID().equals("NoID") && requestUber.getReqStatus().equals("Active")) {

                        //calculate distances between riders and driver
                        double distance = getRiderDriverDistance(Double.parseDouble(requestUber.getRiderLongtitude()),
                                Double.parseDouble(requestUber.getRiderLatitude()),
                                Double.parseDouble(longitudeDriver),
                                Double.parseDouble(latitudeDriver));

                        Log.i("Disntance", String.valueOf(distance));

                        listViewContent.add("Distance: " + df2.format(distance/1000) +" km");
                        allRequest.add(requestUber);
                    }

                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getApplicationContext(), "Cannot reach data", Toast.LENGTH_LONG).show();

            }
        });

    }

    protected static float getRiderDriverDistance(double riderLong, double riderLat, double driverLong, double driverLat) {
        Location rider = new Location("");
        Location driver = new Location("");

        rider.setLongitude(riderLong);
        rider.setLatitude(riderLat);
        driver.setLongitude(driverLong);
        driver.setLatitude(driverLat);

        return driver.distanceTo(rider);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //get annonymous user credential
        Intent intent = getIntent();
        currentUserUID = intent.getStringExtra("UserUID");
        currentUserOption = intent.getStringExtra("UserOption");

        //Init firebase
        database = FirebaseDatabase.getInstance();
        table_uberDriver = database.getReference("UberDriver");

        //location manager stuff
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        locationManager.requestLocationUpdates(provider, 400, 1, this);

        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {

            //update location
            longitudeDriver = String.valueOf(location.getLongitude());
            latitudeDriver = String.valueOf(location.getLatitude());


            requestsListView = (ListView) findViewById(R.id.viewRequestsListView);
            listViewContent = new ArrayList<>();
            allRequest = new ArrayList<>();

            getRiderRequests();

            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listViewContent);
            requestsListView.setAdapter(arrayAdapter);

        }

        requestsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent mapIntent = new Intent(getApplicationContext(), ViewRiderLocation.class);
                mapIntent.putExtra("riderUID", allRequest.get(i).getRiderUID());
                mapIntent.putExtra("driverUID", currentUserUID);
                mapIntent.putExtra("riderLong", allRequest.get(i).getRiderLongtitude());
                mapIntent.putExtra("riderLat", allRequest.get(i).getRiderLatitude());
                mapIntent.putExtra("driverLong", longitudeDriver);
                mapIntent.putExtra("driverLat", latitudeDriver);
                startActivity(mapIntent);

            }
        });


    }

    @Override
    public void onLocationChanged(Location location) {

        longitudeDriver = String.valueOf(location.getLongitude());
        latitudeDriver = String.valueOf(location.getLatitude());

        table_uberDriver.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UberDriver uberDriver = new UberDriver(currentUserUID, longitudeDriver, latitudeDriver, "enRoute" );
                table_uberDriver.child(currentUserUID).setValue(uberDriver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

}
