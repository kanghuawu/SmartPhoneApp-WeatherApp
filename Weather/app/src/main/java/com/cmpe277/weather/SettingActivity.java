package com.cmpe277.weather;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class SettingActivity extends ListActivity {
    final String TAG = "Weather";
    ToggleButton unitToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        unitToggle = (ToggleButton) findViewById(R.id.toggleButton);

        unitToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i(TAG, "Unit set to Fahrenheit");
                } else {
                    Log.i(TAG, "Unit set to Celsius");
                }
            }
        });
    }
}
