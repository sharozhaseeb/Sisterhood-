package com.sisterhood.mapsproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SecondRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "SecondRegistrationActiv";
    private Context context = SecondRegistrationActivity.this;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_registration);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        EditText nameEditText = findViewById(R.id.name_edittext_sec);
        EditText numberEditText = findViewById(R.id.number_edittext_sec);
        EditText addressEditText = findViewById(R.id.address_edittext_sec);

        Utils utils = new Utils();

        if (getIntent().hasExtra("edit")) {

            String name = utils.getStoredString(context, "usernameStr");
            String nmbr = utils.getStoredString(context, "numberStr");
            String address = utils.getStoredString(context, "addressStr");

            nameEditText.setText(name);
            numberEditText.setText(nmbr);
            addressEditText.setText(address);

        }

        findViewById(R.id.submitBtn_sec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (nameEditText.getText().toString().isEmpty() || numberEditText.getText().toString().isEmpty() || addressEditText.getText().toString().isEmpty()) {

                    Toast.makeText(context, "Please fill out all the information!", Toast.LENGTH_SHORT).show();
                    return;

                }

                progressDialog.show();

                //name, number, address, email;
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("name", nameEditText.getText().toString());
                hashMap.put("number", numberEditText.getText().toString());
                hashMap.put("address", addressEditText.getText().toString());

                databaseReference.child("users").child(mAuth.getCurrentUser().getUid())
                        .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            utils.storeString(context,
                                    "usernameStr", nameEditText.getText().toString());
                            utils.storeString(context,
                                    "numberStr", numberEditText.getText().toString());
                            utils.storeString(context,
                                    "addressStr", addressEditText.getText().toString());

                            progressDialog.dismiss();

                            finish();
                            Intent intent = new Intent(context, BottomNavigationActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            Toast.makeText(context, "You are signed up!", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: " + task.getException().getMessage());
                        }

                    }
                });

            }
        });

    }
}