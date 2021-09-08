package com.sisterhood.mapsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddEmergencyActivity extends AppCompatActivity {
    private static final String TAG = "AddEmergencyActivity";
    private Context context = AddEmergencyActivity.this;
    private Utils utils = new Utils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency);

        EditText number1Et = findViewById(R.id.number1_edittext);
        EditText number2Et = findViewById(R.id.number2_edittext);
        EditText number3Et = findViewById(R.id.number3_edittext);

        if (getIntent().hasExtra("edit")) {
            String number1String = utils.getStoredString(context, "number1");
            String number2String = utils.getStoredString(context, "number2");
            String number3String = utils.getStoredString(context, "number3");

            if (!number1String.equals("Error"))
                number1Et.setText(number1String);

            if (!number2String.equals("Error"))
                number2Et.setText(number2String);

            if (!number3String.equals("Error"))
                number3Et.setText(number3String);
        }

        findViewById(R.id.submitBtn_emergencyNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String number1 = number1Et.getText().toString();
                String number2 = number2Et.getText().toString();
                String number3 = number3Et.getText().toString();

                if (number1.isEmpty() && number2.isEmpty() && number3.isEmpty()) {
                    Toast.makeText(AddEmergencyActivity.this, "Please add at least 1 Emergency number!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!number1.isEmpty()) {
                    if (isValidated(number1)) {
                        utils.storeString(context, "number1", number1);
                    } else if (!isValidated(number1)) {
                        number1Et.setError("Number should start with 923");
                        return;
                    }
                }

                if (!number2.isEmpty()) {
                    if (isValidated(number2)) {
                        utils.storeString(context, "number2", number2);
                    } else {
                        number2Et.setError("Number should start with 923");
                        return;
                    }
                }

                if (!number3.isEmpty()) {
                    if (isValidated(number3)) {
                        utils.storeString(context, "number3", number3);
                    } else if (!isValidated(number3)) {
                        number3Et.setError("Number should start with 923");
                        return;
                    }
                }

                utils.storeBoolean(context, "emer", true);

                finish();

            }

            private boolean isValidated(String numb) {
                return numb.substring(0, Math.min(numb.length(), 3)).equals("923");
            }
        });

    }
}