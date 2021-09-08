package com.sisterhood.mapsproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class BottomNavigationActivity extends AppCompatActivity {
    private static final String TAG = "BottomNavigationActivit";

    private ImageView profileTabBtn, homeTabBtn, settingsTabBtn;
    private View homeViewLine, profileViewLine, settingsViewLine;

    private ImageView currentImageView;
    private View currentViewLine;

    private Utils utils = new Utils();
    private Context context = BottomNavigationActivity.this;
//    private HomeFragment homeFragment;
//    private ProfileFragment profileFragment;
//    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        if (firebaseAuth.getCurrentUser() == null) {
//            finish();
//            Intent intent = new Intent(com.sisterhood.tweetytheclone.BottomNavigationActivity.this, SubmitReportsActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            finish();
//            startActivity(intent);
//            return;
//        }

        profileTabBtn = findViewById(R.id.profile_tab_button_bottom_navigation);
        homeTabBtn = findViewById(R.id.home_tab_button_bottom_navigation);
        settingsTabBtn = findViewById(R.id.settings_tab_button_bottom_navigation);
        currentImageView = homeTabBtn;

        homeViewLine = findViewById(R.id.secline);
        profileViewLine = findViewById(R.id.firstline);
        settingsViewLine = findViewById(R.id.thirdline);
        currentViewLine = homeViewLine;

        profileTabBtn.setOnClickListener(profileTabBtnClickListener());
        homeTabBtn.setOnClickListener(homeTabBtnClickListener());
        settingsTabBtn.setOnClickListener(settingsTabBtnClickListener());

//        utils.storeString(BottomNavigationActivity.this, "current_fragment", "home");

        loadFragment(new HomeFragment());

//        if (new Utils().getStoredString(
//                BottomNavigationActivity.this, "usernameStr")
//                .equals("Error")
//                || new Utils().getStoredString(
//                BottomNavigationActivity.this, "profileUrl")
//                .equals("Error")
//
//        ) {
//        getUserdetails();
//        }

//        listenForProfileImageChanges();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("This will enable you to send your GPS information and a pre-programmed message to these contacts in case of emergency.");
        builder1.setCancelable(false);
        builder1.setTitle("Add your emergency contacts!");
        builder1.setPositiveButton(
                "Add",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(context, AddEmergencyActivity.class));
                    }
                });

//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });

        alert11 = builder1.create();
    }

    private AlertDialog alert11;

    @Override
    protected void onResume() {
        super.onResume();

        if (!utils.getStoredBoolean(context, "emer")) {
            alert11.show();
        } else {
            if (alert11.isShowing())
                alert11.dismiss();
        }
    }

    //    private void listenForProfileImageChanges() {
//
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
//                .child("profileUrl").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                String url = snapshot.getValue(String.class);
//                CircleImageView circleImageView = findViewById(R.id.profile_image_view_bottom_navigation);
//                Glide.with(com.sisterhood.tweetytheclone.BottomNavigationActivity.this)
//                        .load(url)
//                        .apply(new RequestOptions()
//                                .placeholder(R.color.grey)
//                                .error(R.color.grey)
//                        )
//                        .into(circleImageView);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d(TAG, "onCancelled: "+error.getMessage());
//            }
//        });
//
//    }

//    private void getUserdetails() {
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("users").child(firebaseAuth.getCurrentUser().getUid())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        if (!snapshot.exists()) {
//                            return;
//                        }
//
//                        new Utils().storeString(com.sisterhood.tweetytheclone.BottomNavigationActivity.this,
//                                "usernameStr", snapshot.child("name").getValue(String.class));
//
//                        new Utils().storeString(com.sisterhood.tweetytheclone.BottomNavigationActivity.this,
//                                "profileUrl", snapshot.child("profileUrl").getValue(String.class));
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Toast.makeText(com.sisterhood.tweetytheclone.BottomNavigationActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//    }

    private View.OnClickListener settingsTabBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
//                String smsBody = "https://play.google.com/store/apps/details?id=com.urbandroid.sleep";
//                intent.putExtra(android.content.Intent.EXTRA_TEXT, smsBody);
//                startActivity(Intent.createChooser(intent, "Share using"));

                currentViewLine.setVisibility(View.INVISIBLE);
                currentViewLine = settingsViewLine;
                currentViewLine.setVisibility(View.VISIBLE);

                homeTabBtn.setImageResource(R.drawable.ic_outline_home_24_unselected);
                profileTabBtn.setImageResource(R.drawable.ic_baseline_person_outline_24_unselected);
                settingsTabBtn.setImageResource(R.drawable.ic_outline_settings_24_selected);

                loadFragment(new SettingsFragment());
            }
        };
    }

    private View.OnClickListener homeTabBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                currentImageView.setBackgroundColor(0);
                currentViewLine.setVisibility(View.INVISIBLE);

//                currentImageView = homeTabBtn;
                currentViewLine = homeViewLine;

//                currentLayout.setBackgroundColor(getResources().getColor(R.color.tabbtnsbg));
                currentViewLine.setVisibility(View.VISIBLE);
                homeTabBtn.setImageResource(R.drawable.ic_outline_home_24_selected);
                profileTabBtn.setImageResource(R.drawable.ic_baseline_person_outline_24_unselected);
                settingsTabBtn.setImageResource(R.drawable.ic_outline_settings_24_unselected);
//                utils.storeString(com.sisterhood.tweetytheclone.BottomNavigationActivity.this, "current_fragment", "home");

                loadFragment(new HomeFragment());
            }
        };
    }

    private View.OnClickListener profileTabBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(BottomNavigationActivity.this, SecondRegistrationActivity.class).putExtra("edit", true));

//                currentViewLine.setVisibility(View.INVISIBLE);
//                currentViewLine = profileViewLine;
//                currentViewLine.setVisibility(View.VISIBLE);
//                homeTabBtn.setImageResource(R.drawable.ic_outline_home_24_unselected);
//                profileTabBtn.setImageResource(R.drawable.ic_person_outline_24_selected);
//                settingsTabBtn.setImageResource(R.drawable.ic_outline_settings_24_unselected);
//                loadFragment(new ProfileFragment());

            }
        };
    }

    private void loadFragment(Fragment fragment) {

        if (fragment != null) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

        } else {
            Toast.makeText(this, "Fragment is null!", Toast.LENGTH_SHORT).show();
        }

    }

}