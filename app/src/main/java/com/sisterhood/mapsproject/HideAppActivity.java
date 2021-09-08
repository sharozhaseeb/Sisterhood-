package com.sisterhood.mapsproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class HideAppActivity extends AppCompatActivity {

    SwitchMaterial switchMaterial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_app);

        switchMaterial = findViewById(R.id.hide_app_switch);
        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                PackageManager p = getPackageManager();
                ComponentName componentName = new ComponentName(HideAppActivity.this,
                        SplashActivity.class);
                if (b) {
                    // HIDE APP
                    p.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);

                } else {
                    // SHOW APP
                    p.setComponentEnabledSetting(componentName,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);

                }

                Toast.makeText(HideAppActivity.this, "Done", Toast.LENGTH_SHORT).show();
            }
        });

    }
}