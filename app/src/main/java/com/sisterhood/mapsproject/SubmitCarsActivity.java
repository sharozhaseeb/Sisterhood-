package com.sisterhood.mapsproject;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sisterhood.mapsproject.models.CarSubmissionModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubmitCarsActivity extends AppCompatActivity {
    private static final String TAG = "SubmitReportsActivity";
    private Context context = SubmitCarsActivity.this;
//    private Geocoder geocoder;

    private void sendTextMessage1(String numbStr, String smsBody1) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent
                .getBroadcast(SubmitCarsActivity.this, 0,
                        new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent
                .getBroadcast(SubmitCarsActivity.this, 0,
                        new Intent(DELIVERED), 0);

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(smsBody1);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);
        try {
            sms.sendMultipartTextMessage(numbStr, null, parts, sendList, deliverList);
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage(numbStr, null, smsBody1, null, null);
            Toast.makeText(SubmitCarsActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
        } catch (final Exception exception) {
            Toast.makeText(SubmitCarsActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            exception.printStackTrace();
        }


    }

    private static final String DOMESTIC_VIOLENCE = "Domestic Violence";
    private static final String HARASSMENT = "Harassment";
    private static final String SEXUAL_ASSAULT = "Sexual Assault";
    private static final String STALKING = "Stalking";

    // YE LOCATION ACCESS KRNE KA OBJECT HE
//    private FusedLocationProviderClient fusedLocationProviderClient;

    AppCompatButton submitButton;
    TextInputEditText driverNameEditText, carNmbrEditText, incidentReportEditText;
    //    RelativeLayout locationLayout;
//    TextView locationTextView;
    String driverNameString, carNmbrString, incidentReportString;
    //    double latitude, longitude;
    private ProgressDialog progressDialog;

    private String categoryString = null;

    // YE ONLINE DATABASE KA LINK HE YAHAN PAR SARA DATA STORE HOGA JO KAY BAD MEN MAPS PAR SHOW KIA JAEGA
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    boolean isLocationget = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_car);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        submitButton = findViewById(R.id.submitBtnCar);
//        viewButton = findViewById(R.id.viewSubmissionsBtn);
        driverNameEditText = findViewById(R.id.name_driver_edittext);
        carNmbrEditText = findViewById(R.id.car_number_edittext);
        incidentReportEditText = findViewById(R.id.incident_report_edittext);
//        locationLayout = findViewById(R.id.location_layout);
//        locationTextView = findViewById(R.id.location_textview);

//        geocoder = new Geocoder(this, Locale.getDefault());

//        carNmbrEditText.setText(getTime());

        locationLayoutListener();

        submitButtonListener();

//        viewButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//        viewButton.setVisibility(View.GONE);

//        TextView textView = findViewById(R.id.categories_layout_textview);

//        findViewById(R.id.categories_layout_textview).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                PopupMenu popupMenu = new PopupMenu(SubmitCarsActivity.this, view);
//                popupMenu.getMenuInflater().inflate(
//                        R.menu.popup_menu_categories,
//                        popupMenu.getMenu()
//                );
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        if (menuItem.getItemId() == R.id.DOMESTIC_VIOLENCE) {
//                            categoryString = DOMESTIC_VIOLENCE;
//                            textView.setText(DOMESTIC_VIOLENCE);
//                        }
//                        if (menuItem.getItemId() == R.id.Harassment) {
//                            categoryString = HARASSMENT;
//                            textView.setText(HARASSMENT);
//                        }
//                        if (menuItem.getItemId() == R.id.SexualAssault) {
//                            categoryString = SEXUAL_ASSAULT;
//                            textView.setText(SEXUAL_ASSAULT);
//                        }
//                        if (menuItem.getItemId() == R.id.Stalking) {
//                            categoryString = STALKING;
//                            textView.setText(STALKING);
//                        }
//                        return true;
//                    }
//                });
//                popupMenu.show();
//
//            }
//        });

    }

    private String cityName = "null";

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(SubmitCarsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SubmitCarsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permissions are not granted!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(SubmitCarsActivity.this);

//        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

//        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    //We have a location
////                    Log.d(TAG, "onSuccess: " + location.toString());
//                    Log.d(TAG, "onSuccess: startLocation: " + location.getLatitude());
//                    Log.d(TAG, "onSuccess: startLocation: " + location.getLongitude());
//
//                    latitude = location.getLatitude();
//                    longitude = location.getLongitude();
//
//                    List<Address> addresses = null;
//
//                    try {
//                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
//                        cityName = addresses.get(0).getLocality();
////                        cityName = addresses.get(0).getAddressLine(0);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    locationTextView.setText(String.valueOf(
//                            latitude + "," + longitude
//                                    + "(" + cityName + ")"
//                    ));
//
//                    isLocationget = true;
////                    startLocationChecker();
//                } else {
//                    Toast.makeText(SubmitCarsActivity.this, "Location is null", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "onSuccess: Location was null...");
//                }
//                progressDialog.dismiss();
//            }
//        });
//        locationTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "onFailure: " + e.getLocalizedMessage());
//                progressDialog.dismiss();
//                Toast.makeText(SubmitCarsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//
//            }
//        });


    }

    private void locationLayoutListener() {
//        locationLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // LOCATION ACCESS KRNE K LIE PEHLE USER SE PERMISSION LENI HOGI
//                Dexter.withContext(context)
//                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
//                        .withListener(new PermissionListener() {
//                            @Override
//                            public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                                Dexter.withContext(context)
//                                        .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
//                                        .withListener(new PermissionListener() {
//                                            @Override
//                                            public void onPermissionGranted(PermissionGrantedResponse response) {
//
//                                                // PERMISSION MILNE PAR LOCATION GET HOGI
//                                                getLastLocation();
//
//                                            }
//
//                                            @Override
//                                            public void onPermissionDenied(PermissionDeniedResponse response) {
//                                                if (response.isPermanentlyDenied()) {
//                                                    // open device settings when the permission is
//                                                    // denied permanently
//                                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();
//
//                                                    Intent intent = new Intent();
//                                                    intent.setAction(
//                                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                                    Uri uri = Uri.fromParts("package",
//                                                            BuildConfig.APPLICATION_ID, null);
//                                                    intent.setData(uri);
//                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                                    startActivity(intent);
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                                                token.continuePermissionRequest();
//                                            }
//                                        }).check();
//
//                            }
//
//                            @Override
//                            public void onPermissionDenied(PermissionDeniedResponse response) {
//                                if (response.isPermanentlyDenied()) {
//                                    // open device settings when the permission is
//                                    // denied permanently
//                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();
//
//                                    Intent intent = new Intent();
//                                    intent.setAction(
//                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    Uri uri = Uri.fromParts("package",
//                                            BuildConfig.APPLICATION_ID, null);
//                                    intent.setData(uri);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//                                token.continuePermissionRequest();
//                            }
//                        }).check();
//
//            }
//        });
    }

    private void submitButtonListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                driverNameString = driverNameEditText.getText().toString();
                carNmbrString = carNmbrEditText.getText().toString();
                incidentReportString = incidentReportEditText.getText().toString();

                if (driverNameString.isEmpty() || driverNameString == null) {
                    Toast.makeText(context, "Please enter a name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (carNmbrString.isEmpty() || carNmbrString == null) {
                    Toast.makeText(context, "Please enter car Number!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (!isLocationget) {
//                    Toast.makeText(context, "Please enter your location!", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                if (incidentReportString.isEmpty() || incidentReportString == null) {
                    Toast.makeText(context, "Please enter incident!", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (categoryString.isEmpty() || categoryString == null) {
//                    Toast.makeText(context, "Please select a category!", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                Dialog dialog = new Dialog(SubmitCarsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_rating);
                dialog.setCancelable(true);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);

                dialog.findViewById(R.id.submitBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE
                        progressDialog.show();

                        CarSubmissionModel model = new CarSubmissionModel(driverNameString, carNmbrString, String.valueOf(ratingBar.getRating()), incidentReportString);

//                        LocationModel model = new LocationModel(driverNameString, carNmbrString, cityName, latitude, longitude);

//                        if (categoryString.equals(SEXUAL_ASSAULT))
//                            sendTextMessage("923012345678", "A new report of Sexual Assault has been submitted!");

                        String key = databaseReference.child("cars").push().getKey();

                        databaseReference.child("cars").child(key)
                                .setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
//                                    databaseReference.child("cars").child(key)
//                                            .child("rating")
//                                            .setValue(String.valueOf(ratingBar.getRating()))
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//
//                                                    databaseReference.child("cars").child(key)
//                                                            .child("desc")
//                                                            .setValue(incidentReportString);

//                                                    databaseReference.child("cars").child(key)
//                                                            .child("category")
//                                                            .setValue(categoryString);
                                    progressDialog.dismiss();
                                    dialog.dismiss();

                                    if (task.isSuccessful()) {
                                        driverNameEditText.setText("");
                                        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
//                                            });
//                                } else {
//                                    progressDialog.dismiss();
//                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
                            }
                        });

                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
            }
        });
    }

    public String getTime() {

        try {

            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return sdf.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error";

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

}