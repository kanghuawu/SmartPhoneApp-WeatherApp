package com.cmpe277.weather;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ToggleButton;

public class SettingActivity extends Activity {
    final String TAG = "Weather";
    RadioGroup tempRadioGroup;
    Button rtnHomeButton;
    WeatherDataModel.TemperatureType selectedType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

//        rtnHomeButton = (Button) findViewById(R.id.returnButton);
//        rtnHomeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.i(TAG, "Return home from setting");
//                finish();
//            }
//        });

        tempRadioGroup = (RadioGroup) findViewById(R.id.tempGroup);
        selectedType = Setting.getTemperatureType(this);
        if (selectedType.equals(WeatherDataModel.TemperatureType.CELSIUS)) {
            tempRadioGroup.check(R.id.radioCelsius);
        } else {
            tempRadioGroup.check(R.id.radioFahrenheit);
        }

        tempRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton fRadioButton = (RadioButton) findViewById(R.id.radioFahrenheit);
                boolean isChecked = fRadioButton.isChecked();
                if (isChecked) {
                    Log.i(TAG, "Temperature type changed to Fahrenheit");
                    Setting.changeTemperatureType(SettingActivity.this, WeatherDataModel.TemperatureType.FAHRENHEIT);
                } else {
                    Log.i(TAG, "Temperature type changed to Celius");
                    Setting.changeTemperatureType(SettingActivity.this, WeatherDataModel.TemperatureType.CELSIUS);
                }

            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}
