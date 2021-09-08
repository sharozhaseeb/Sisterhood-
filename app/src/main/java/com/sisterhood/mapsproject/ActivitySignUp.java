package com.sisterhood.mapsproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ActivitySignUp extends AppCompatActivity {
    private static final String TAG = "ActivitySignUp";

    private EditText userNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, numberEditText, addressEditText;
    private Button signUpBtn;

    private ProgressDialog mDialog;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUsers;

    private String userNameStr;

    private String emailStr;

    private String passwordStr;

    private String confirmedPasswordStr, numberStr, addressStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
//        if (mAuth.getCurrentUser() != null) {
//            finish();
//            Intent intent = new Intent(ActivitySignUp.this, BottomNavigationActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//            return;
//        }

        findViewById(R.id.loginBtn_signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivitySignUp.this, ActivityLogin.class));
            }
        });

        findViewById(R.id.backbtn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mDatabaseUsers = FirebaseDatabase.getInstance().getReference();
        mDatabaseUsers.keepSynced(true);

        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setMessage("Signing you in...");

        initViews();

    }

    private View.OnClickListener signUpBtnListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                checkStatusOfEditTexts();
            }
        };
    }

    private void checkStatusOfEditTexts() {

        // Getting strings from edit texts
        userNameStr = userNameEditText.getText().toString();
        emailStr = emailEditText.getText().toString();
        passwordStr = passwordEditText.getText().toString();
        confirmedPasswordStr = confirmPasswordEditText.getText().toString();
        numberStr = numberEditText.getText().toString();
        addressStr = addressEditText.getText().toString();

        if (TextUtils.isEmpty(addressStr)) {
            mDialog.dismiss();
            addressEditText.setError("Address is empty");
            addressEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(numberStr)) {
            mDialog.dismiss();
            numberEditText.setError("Number is empty");
            numberEditText.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(userNameStr)) {
            mDialog.dismiss();
            userNameEditText.setError("Username is empty");
            userNameEditText.requestFocus();
            return;
        }

        // Checking if Fields are empty or not
        if (!TextUtils.isEmpty(emailStr) && !TextUtils.isEmpty(passwordStr) && !TextUtils.isEmpty(confirmedPasswordStr)) {

            // Checking if passwordStr is equal to confirmed Password
            if (passwordStr.equals(confirmedPasswordStr)) {

                // Signing up user
                signUpUserWithNameAndPassword();

            } else {

                mDialog.dismiss();
                confirmPasswordEditText.setError("Password does not match!");
                confirmPasswordEditText.requestFocus();

            }

            // User Name is Empty
        } else if (TextUtils.isEmpty(emailStr)) {

            mDialog.dismiss();
            emailEditText.setError("Please provide a emailStr");
            emailEditText.requestFocus();


            // Password is Empty
        } else if (TextUtils.isEmpty(passwordStr)) {

            mDialog.dismiss();
            passwordEditText.setError("Please provide a passwordStr");
            passwordEditText.requestFocus();


            // Confirm Password is Empty
        } else if (TextUtils.isEmpty(confirmedPasswordStr)) {

            mDialog.dismiss();
            confirmPasswordEditText.setError("Please confirm your passwordStr");
            confirmPasswordEditText.requestFocus();


        }

    }

    private void signUpUserWithNameAndPassword() {

        if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            //if Email Address is Invalid..

            mDialog.dismiss();
            emailEditText.setError("Please enter a valid email with no spaces and special characters included");
            emailEditText.requestFocus();
        } else {

            mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        addUserDetailsToDatabase();

                    } else {

                        mDialog.dismiss();
                        Toast.makeText(ActivitySignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
//        } else {
//
//            mDialog.dismiss();
//            Toast.makeText(this, "You are not online", Toast.LENGTH_SHORT).show();
//        }
    }

    private static class UserDetails {

        private String name, number, address, email;

        public UserDetails(String name, String number, String address, String email) {
            this.name = name;
            this.number = number;
            this.address = address;
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        UserDetails() {
        }
    }

    private void addUserDetailsToDatabase() {

        UserDetails userDetails = new UserDetails();
        userDetails.setEmail(emailStr);
        userDetails.setName(userNameStr);
        userDetails.setNumber(numberStr);
        userDetails.setAddress(addressStr);

        mDatabaseUsers.child("users").child(mAuth.getCurrentUser().getUid())
                .setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Utils utils = new Utils();

                    utils.storeString(ActivitySignUp.this,
                            "usernameStr", userNameStr);
                    utils.storeString(ActivitySignUp.this,
                            "numberStr", numberStr);
                    utils.storeString(ActivitySignUp.this,
                            "emailStr", emailStr);
                    utils.storeString(ActivitySignUp.this,
                            "addressStr", addressStr);

                    mDialog.dismiss();

                    finish();
                    Intent intent = new Intent(ActivitySignUp.this, BottomNavigationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                    Toast.makeText(ActivitySignUp.this, "You are signed up!", Toast.LENGTH_SHORT).show();

                } else {
                    mDialog.dismiss();
                    Toast.makeText(ActivitySignUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onComplete: " + task.getException().getMessage());
                }

            }
        });

    }

    private void initViews() {

        userNameEditText = findViewById(R.id.user_name_edittext_activity_sign_up);

        emailEditText = findViewById(R.id.email_edittext_activity_sign_up);
        passwordEditText = findViewById(R.id.password_edittext_activity_sign_up);
        confirmPasswordEditText = findViewById(R.id.confirm_password_edittext_activity_sign_up);
        numberEditText = findViewById(R.id.number_edittext_activity_sign_up);
        addressEditText = findViewById(R.id.address_edittext_activity_sign_up);

        signUpBtn = findViewById(R.id.create_btn_activity_sign_up);
        signUpBtn.setOnClickListener(signUpBtnListener());
    }

}