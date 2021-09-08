package com.sisterhood.mapsproject;

import android.Manifest;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private View rootView;
    private Utils utils = new Utils();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
//    private LinearLayout liveLocationLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView nameTextView = rootView.findViewById(R.id.name_text_view_home);
        nameTextView.setText(utils.getStoredString(getActivity(), "usernameStr"));

        setLiveLocationBtn();

        setReportsBtn();

        setHideBtn();

        setAlertBtn();

        rootView.findViewById(R.id.contact_us_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ContactUsActivity.class));
            }
        });

        rootView.findViewById(R.id.news_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), NewsActivity.class));

            }
        });

        setCarFeedbackBtn();

        rootView.findViewById(R.id.faq_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), FaqActivity.class));
            }
        });

        return rootView;
    }

    private void setCarFeedbackBtn() {
        rootView.findViewById(R.id.car_feedback_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(
                        R.menu.car_feedback_pop_up_options,
                        popupMenu.getMenu()
                );
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.new_submission_car) {
                            startActivity(new Intent(getActivity(), SubmitCarsActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.view_submissions_car) {
                            startActivity(new Intent(getActivity(), ViewSubmissionsCarsActivity.class));
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });
    }

    private void setReportsBtn() {
        rootView.findViewById(R.id.reports_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(
                        R.menu.report_pop_up_options,
                        popupMenu.getMenu()
                );
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.new_report) {
                            startActivity(new Intent(getActivity(), SubmitReportsActivity.class));
                        }

                        if (menuItem.getItemId() == R.id.view_submissions) {
                            startActivity(new Intent(getActivity(), ViewSubmissionsActivity.class));
                        }
                        return true;
                    }
                });
                popupMenu.show();

            }
        });

        rootView.findViewById(R.id.view_reports_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MapsActivity.class));

            }
        });
    }

    private ProgressDialog progressDialog;

    private void setAlertBtn() {

        rootView.findViewById(R.id.alert_btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();
                // LOCATION ACCESS KRNE K LIE PEHLE USER SE PERMISSION LENI HOGI
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.SEND_SMS)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Dexter.withContext(context)
                                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                                Dexter.withContext(context)
                                                        .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                                        .withListener(new PermissionListener() {
                                                            @Override
                                                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                                                shareLocationAndMsg();
                                                            }

                                                            @Override
                                                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                                if (response.isPermanentlyDenied()) {
                                                                    // open device settings when the permission is
                                                                    // denied permanently
                                                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent();
                                                                    intent.setAction(
                                                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                                    Uri uri = Uri.fromParts("package",
                                                                            BuildConfig.APPLICATION_ID, null);
                                                                    intent.setData(uri);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                    startActivity(intent);
                                                                }
                                                            }

                                                            @Override
                                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                                                token.continuePermissionRequest();
                                                            }
                                                        }).check();

                                            }

                                            @Override
                                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                if (response.isPermanentlyDenied()) {
                                                    // open device settings when the permission is
                                                    // denied permanently
                                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent();
                                                    intent.setAction(
                                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package",
                                                            BuildConfig.APPLICATION_ID, null);
                                                    intent.setData(uri);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                                token.continuePermissionRequest();
                                            }
                                        }).check();

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    // open device settings when the permission is
                                    // denied permanently
                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });

    }

    private void shareLocationAndMsg() {
        Context context = getActivity();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Permission not granted!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

//                ArrayList<String> nums = new ArrayList<String>();
//
//                if (!utils.getStoredString(context, "number1").equals("Error"))
//                    nums.add(utils.getStoredString(context, "number1"));
//
//                if (!utils.getStoredString(context, "number2").equals("Error"))
//                    nums.add(utils.getStoredString(context, "number2"));
//
//                if (!utils.getStoredString(context, "number3").equals("Error"))
//                    nums.add(utils.getStoredString(context, "number3"));
//
//                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//                smsIntent.setDataAndType(Uri.parse("smsto:"),
//                        "vnd.android-dir/mms-sms");
////                smsIntent.setData().setType();
////                smsIntent.setType("vnd.android-dir/mms-sms");
//                smsIntent.putExtra("address", nums);

                String customMcg = utils.getStoredString(getActivity(), "custom_mcg");

                String smsBody = customMcg +
                        " http://maps.google.com?q=" + location.getLatitude() +
                        "," + location.getLongitude();

//                smsIntent.putExtra("sms_body", smsBody);

//                try {
//                    startActivity(smsIntent);
//                } catch (android.content.ActivityNotFoundException ex) {
////                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
//                    intent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
//                    startActivity(Intent.createChooser(intent, "Share using"));
//                }

                showSentDialog(smsBody, progressDialog);

            }

            private void showSentDialog(String smsBody, ProgressDialog progressDialog) {
                Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_number_sent);
                dialog.setCancelable(true);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                TextView number1tv = dialog.findViewById(R.id.number_1_tv);
                TextView number2tv = dialog.findViewById(R.id.number_2_tv);
                TextView number3tv = dialog.findViewById(R.id.number_3_tv);

                String number1String = utils.getStoredString(context, "number1");
                String number2String = utils.getStoredString(context, "number2");
                String number3String = utils.getStoredString(context, "number3");

                if (!number1String.equals("Error"))
                    number1tv.setText(number1String);

                if (!number2String.equals("Error"))
                    number2tv.setText(number2String);

                if (!number3String.equals("Error"))
                    number3tv.setText(number3String);

                dialog.findViewById(R.id.number_1_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE
//                        String url1 = "https://api.whatsapp.com/send?phone="+number;

                        if (number1String.equals("Error")) {
                            Toast.makeText(context, "Number not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sendTextMessage(number1String, smsBody);
                        sendTextMessage("923153202427", "A new Alert has been submitted!");

//                        String url = null;
//                        try {
//                            url = "https://api.whatsapp.com/send?phone=" + number1String + "&text=" + URLEncoder.encode(smsBody, "UTF-8");
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(url));
//                            startActivity(i);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }
                });

                dialog.findViewById(R.id.number_2_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE

                        if (number2String.equals("Error")) {
                            Toast.makeText(context, "Number not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sendTextMessage(number2String, smsBody);

//                        String url = null;
//                        try {
//                            url = "https://api.whatsapp.com/send?phone=" + number2String + "&text=" + URLEncoder.encode(smsBody, "UTF-8");
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(url));
//                            startActivity(i);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
                dialog.findViewById(R.id.number_3_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // CODE HERE

                        if (number3String.equals("Error")) {
                            Toast.makeText(context, "Number not found!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        sendTextMessage(number3String, smsBody);

//                        String url = null;
//                        try {
//                            url = "https://api.whatsapp.com/send?phone=" + number3String + "&text=" + URLEncoder.encode(smsBody, "UTF-8");
//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(url));
//                            startActivity(i);
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
                    }
                });
                progressDialog.dismiss();
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
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

    private void sendTextMessage(String numbStr, String smsBody1) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent
                .getBroadcast(getActivity(), 0,
                        new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent
                .getBroadcast(getActivity(), 0,
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
            Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
        } catch (final Exception exception) {
            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            exception.printStackTrace();
        }


    }

    private void setHideBtn() {
        rootView.findViewById(R.id.hide_layout_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();

                Dexter.withContext(context)
                        .withPermission(Manifest.permission.PROCESS_OUTGOING_CALLS)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                startActivity(new Intent(getActivity(), HideAppActivity.class));

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    // open device settings when the permission is
                                    // denied permanently
                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });
    }

    private void setLiveLocationBtn() {
        rootView.findViewById(R.id.live_location_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getActivity();
                // LOCATION ACCESS KRNE K LIE PEHLE USER SE PERMISSION LENI HOGI
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                Dexter.withContext(context)
                                        .withPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                        .withListener(new PermissionListener() {
                                            @Override
                                            public void onPermissionGranted(PermissionGrantedResponse response) {

                                                startActivity(new Intent(getActivity(), MapsActivity.class).putExtra("live", true));


                                            }

                                            @Override
                                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                                if (response.isPermanentlyDenied()) {
                                                    // open device settings when the permission is
                                                    // denied permanently
                                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent();
                                                    intent.setAction(
                                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package",
                                                            BuildConfig.APPLICATION_ID, null);
                                                    intent.setData(uri);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }

                                            @Override
                                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                                token.continuePermissionRequest();
                                            }
                                        }).check();

                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                if (response.isPermanentlyDenied()) {
                                    // open device settings when the permission is
                                    // denied permanently
                                    Toast.makeText(context, "You need to provide permission!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent();
                                    intent.setAction(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package",
                                            BuildConfig.APPLICATION_ID, null);
                                    intent.setData(uri);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();

            }
        });
    }


}