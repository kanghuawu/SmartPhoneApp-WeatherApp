package com.cmpe277.weather;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListActivity extends ListActivity {
    final String TAG = "Weather";
    public static final String KEY_CITY = "city";
    public static final String KEY_TIME = "time";
    public static final String KEY_TEMPERATURE = "temperature";

    SimpleAdapter adapter;
    ArrayList<HashMap<String, String>> dataList;
    Button settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_list_layout);

        if (savedInstanceState != null) {
            dataList = (ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("cities");
        } else {
            dataList = new ArrayList<>();
        }

        adapter = new SimpleAdapter(this, dataList, R.layout.city_item_layout,
                new String[] {KEY_CITY, KEY_TIME, KEY_TEMPERATURE},
                new int[] {R.id.textCity, R.id.textTime, R.id.textDegree});
        setListAdapter(adapter);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                HashMap<String, String> city = new HashMap<>();
                city.put(KEY_CITY, place.getName().toString());
                city.put(KEY_TIME, "11:30");
                city.put(KEY_TEMPERATURE, "13°C");
                dataList.add(city);
                Log.i(TAG, String.valueOf(place.getName().toString()));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        settingButton = (Button) findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Setting button clicked");
                Intent cityIntent = new Intent(CityListActivity.this, SettingActivity.class);
                startActivity(cityIntent);
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.i(TAG, "Item" + position + "clicked");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("cities", dataList);
        super.onSaveInstanceState(outState);
    }
}
