package com.sisterhood.mapsproject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MapsActivity";
    private Context context = MapsActivity.this;

    private static final String DOMESTIC_VIOLENCE = "Domestic Violence";
    private static final String HARASSMENT = "Harassment";
    private static final String SEXUAL_ASSAULT = "Sexual Assault";
    private static final String STALKING = "Stalking";

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private GoogleMap mMap;

    private Button shareLocationBtn;

    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

//    LatLng warongCheLatLng;
//    MarkerOptions warongCheMarkerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        shareLocationBtn = findViewById(R.id.share_location_btn_live);
        shareLocationBtn.setOnClickListener(shareLocationBtnCLickListener());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

    }

    private View.OnClickListener shareLocationBtnCLickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Permission not granted!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        sendLocation(location);
                    }
                });

                locationTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
    }

    private void sendLocation(Location currentLocation) {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        String smsBody = "http://maps.google.com?q=" +
                currentLocation.getLatitude() +
                "," +
                currentLocation.getLongitude();
        intent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
        progressDialog.dismiss();
        startActivity(Intent.createChooser(intent, "Share using"));

    }

    private ProgressDialog progressDialog;
    LatLng latLng;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
//            zoomToUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);

        }

        if (getIntent().hasExtra("live")) {

            shareLocationBtn.setVisibility(View.VISIBLE);
            zoomToUserLocation();

            return;
        }

        progressDialog.show();

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.strokeWidth(4);

        // SARI LOCATIONS RETRIEVE HONGI ONLINE TAKAY MAPS PAR MARKERS ADD KIE JAA SKEN
        databaseReference.child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    LocationModel model = dataSnapshot.getValue(LocationModel.class);

                    latLng = new LatLng(model.getLatitude(), model.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(latLng)
                            .title(model.getName())
                            .snippet(model.getDateTime());

                    mMap.addMarker(markerOptions).showInfoWindow();

                    String categoryValue = null;
                    if (dataSnapshot.child("category").exists())
                        categoryValue = dataSnapshot.child("category").getValue(String.class);

                    // NULL VALUES
                    if (categoryValue == null)
                        setCircleVal(circleOptions, 123, 131, 169, 50, latLng);
                    else {
                        if (categoryValue.equals(DOMESTIC_VIOLENCE))
                            setCircleVal(circleOptions, 242, 121, 58, 100, latLng);

                        if (categoryValue.equals(HARASSMENT))
                            setCircleVal(circleOptions, 255, 0, 0, 500, latLng);

                        if (categoryValue.equals(SEXUAL_ASSAULT))
                            setCircleVal(circleOptions, 128, 0, 0, 5000, latLng);

                        if (categoryValue.equals(STALKING))
                            setCircleVal(circleOptions, 246, 173, 33, 200, latLng);

                    }

                    mMap.addCircle(circleOptions);

                }

                if (getIntent().hasExtra("lat")) {

                    LatLng latLng1 = new LatLng(
                            getIntent().getDoubleExtra("lat", 0.0),
                            getIntent().getDoubleExtra("long", 0.0));

                    CameraUpdate cameraUpdate1 = CameraUpdateFactory.newLatLngZoom(latLng1, 12);//25
                    mMap.animateCamera(cameraUpdate1);

                    progressDialog.dismiss();

                    return;
                }

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12);//25
                mMap.animateCamera(cameraUpdate);

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(context, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setCircleVal(CircleOptions circleOptions, int i, int i2, int i3, int i4, LatLng latLng1) {
        // Sexual Assault
        circleOptions.strokeColor(Color.argb(255, i, i2, i3));
        circleOptions.fillColor(Color.argb(12, i, i2, i3));
        circleOptions.radius(i4);
        circleOptions.center(latLng1);
    }

    //    String TAG = "MapsActivity";
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Log.d(TAG, "onLocationResult: " + locationResult.getLastLocation());
            if (mMap != null) {
//                setUserLocationMarker(locationResult.getLastLocation());
            }
        }
    };

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
        }
//        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
//        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private static class LocationModel {

        private String name, dateTime, cityName;
        private double latitude, longitude;

        public LocationModel(String name, String dateTime, String cityName, double latitude, double longitude) {
            this.name = name;
            this.dateTime = dateTime;
            this.cityName = cityName;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        LocationModel() {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
            stopLocationUpdates();
        } else {
            // you need to request permissions...
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
//                Toast.makeText(MapsActivity.this, "onSuccess", Toast.LENGTH_SHORT).show();
//                mMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}