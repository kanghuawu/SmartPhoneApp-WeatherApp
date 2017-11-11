package com.cmpe277.weather;

import android.Manifest;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.cmpe277.weather.task.TaskType;
import com.cmpe277.weather.task.UpdateCurrentWeatherTask;
import com.cmpe277.weather.task.UpdateLocalizedTimeTask;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityListActivity extends ListActivity {
    public static final String TAG = "Weather";
    public static final String KEY_CITY = "city";
    public static final String KEY_DATE = "date";
    public static final String KEY_TEMPERATURE = "temperature";
    public static final String KEY_POSITION = "position";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LON = "lon";

    final String[] KEY_ITEMS = new String[]{KEY_CITY, KEY_DATE, KEY_TEMPERATURE};
    final int[] KEY_ITEMS_LAYOUT = new int[]{R.id.textCity, R.id.textDate, R.id.textTemperature};
    final int REQUEST_CODE = 123;
    SimpleAdapter adapter;
    SimpleAdapter ordinaryAdaptor;
    SimpleAdapter editAdapter;
    List<Map<String, Object>> dataList;
    Button settingButton, editButton, addHereButton, refreshButton;
    boolean isEditing = false;
    BroadcastReceiver mBroadcastReceiver;
    static CityModel currentCity = null;
    Intent localServiceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Calling onCreate");
        setContentView(R.layout.city_list_layout);

        if (savedInstanceState != null) {
            Log.i(TAG, "savedInstanceState is not null");
            dataList = (List<Map<String, Object>>) savedInstanceState.getSerializable("dataList");
        } else {
            Log.i(TAG, "Creating new dataList");
            dataList = new ArrayList<>();
        }

        setUpAdaptors();
        adapter = ordinaryAdaptor;
        setListAdapter(adapter);
        restoreCityList();
        updateCityData();

        // auto complete
        createAutocomplete();
        setupButtons();
        if (!runtimePermissions()) {
            localServiceIntent = new Intent(getApplicationContext(), LocalService.class);
            startService(localServiceIntent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Calling onResume");

        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    currentCity = (CityModel) intent.getExtras().getSerializable("currentCity");
                    Log.i(TAG, String.valueOf(currentCity));
                }
            };
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter("locationUpdate"));
        updateCityData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "CityListActivity calling onDestroy");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        Setting.removeCityList(CityListActivity.this);
        List<String> cityList = new ArrayList<>();
        for (Map<String, Object> city : dataList) {
            String cityNameLatLon = city.get(KEY_CITY).toString() + ","
                    + city.get(KEY_LAT).toString() + ","
                    + city.get(KEY_LON).toString();
            cityList.add(cityNameLatLon);
        }
        Setting.addCityList(CityListActivity.this, cityList);
        stopService(localServiceIntent);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!isEditing) {
            super.onListItemClick(l, v, position, id);
            Log.i(TAG, "Item " + position + " clicked");
            Intent swipeIntent = new Intent(CityListActivity.this, CitySwipeViewActivity.class);
            swipeIntent.putExtra(KEY_POSITION, position);
            swipeIntent.putExtra("dataList", (Serializable) dataList);
            swipeIntent.putExtra("currentCity", (Serializable) currentCity);
            startActivity(swipeIntent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "Calling onSaveInstanceState");
        outState.putSerializable("dataList", (Serializable) dataList);
        super.onSaveInstanceState(outState);
    }

    private boolean runtimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return true;
        }
        return false;
    }

    private void setupButtons() {

        // setting button
        settingButton = findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Setting button clicked");
                Intent cityIntent = new Intent(CityListActivity.this, SettingActivity.class);
                startActivity(cityIntent);
            }
        });

        // edit button
        editButton = findViewById(R.id.edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEditing) {
                    Log.i(TAG, "Done editing");
                    editButton.setText("Edit");
                    refreshButton.setEnabled(true);
                    addHereButton.setEnabled(true);
                    settingButton.setEnabled(true);
                } else {
                    Log.i(TAG, "Start editing");
                    editButton.setText("Done");
                    refreshButton.setEnabled(false);
                    addHereButton.setEnabled(false);
                    settingButton.setEnabled(false);
                }
                switchAdaptor();
                isEditing = !isEditing;
            }
        });

        addHereButton = (Button) findViewById(R.id.addHereButton);
        addHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (runtimePermissions()) {
                    return;
                }
                if (localServiceIntent == null) {
                    localServiceIntent = new Intent(getApplicationContext(), LocalService.class);
                    startService(localServiceIntent);
                }
                if (currentCity == null) {
                    Log.i(TAG, "Current city is null");
                    return;
                }
                Log.i(TAG, String.valueOf(currentCity));
                addCity(currentCity.getCityName(),
                        currentCity.getLatitude(),
                        currentCity.getLongitude());
            }
        });

        refreshButton = findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCityData();
            }
        });
    }

    private void createAutocomplete() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                addCity(place.getName().toString(),
                        String.valueOf(place.getLatLng().latitude),
                        String.valueOf(place.getLatLng().longitude));
                Log.i(TAG, String.valueOf(place.getName().toString()));
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void restoreCityList() {
        if (dataList == null || dataList.size() == 0) {
            for (String city : Setting.getCityList(CityListActivity.this)) {
                String[] cityInfo =  city.split(",");
                addCity(cityInfo[0], cityInfo[1], cityInfo[2]);
            }
        }
    }

    private void setUpAdaptors() {
        ordinaryAdaptor = new SimpleAdapter(CityListActivity.this, dataList, R.layout.city_item_layout,
                KEY_ITEMS,
                KEY_ITEMS_LAYOUT);

        editAdapter = new SimpleAdapter(CityListActivity.this, dataList, R.layout.city_item_del_layout,
                KEY_ITEMS,
                KEY_ITEMS_LAYOUT) {
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewGroup layout = (ViewGroup) super.getView(position, convertView, parent);
                Button delete = layout.findViewById(R.id.delButton);
                delete.setTag(position);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeCity(position);
                    }
                });
                return layout;
            }
        };
    }

    private void showCityAdded(String city) {
        Toast.makeText(CityListActivity.this, city + " already added!", Toast.LENGTH_SHORT).show();
    }

    private void addCity(String city, String lat, String lon) {
        Log.i(TAG, "Adding city " + city + ", lat: " + lat + ", lon: " + lon);
        if (city == null) {
            return;
        }
        for (Map<String, Object> m : dataList) {
            if (m.get(KEY_CITY).equals(city)) {
                showCityAdded(city);
                return;
            }
        }
        Map<String, Object> mCity = new HashMap<>();
        mCity.put(KEY_CITY, city);
        mCity.put(KEY_LAT, lat);
        mCity.put(KEY_LON, lon);
        mCity.put(KEY_DATE, "--:--");
        mCity.put(KEY_TEMPERATURE, "--");
        dataList.add(mCity);
        adapter.notifyDataSetChanged();
        updateCityData();
        return;
    }

    private void removeCity(int position) {
        dataList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void switchAdaptor() {
        if (adapter.equals(ordinaryAdaptor)) {
            adapter = editAdapter;
        } else {
            adapter = ordinaryAdaptor;
        }
        setListAdapter(adapter);
    }

    private void updateCityData() {
        for (int position = 0; position < dataList.size(); position++) {
            updateCityData(position);
        }
    }

    private void updateCityData(int position) {
        CityModel cityModel = new CityModel(dataList.get(position).get(KEY_CITY).toString(),
                dataList.get(position).get(KEY_LAT).toString(),
                dataList.get(position).get(KEY_LON).toString(),
                position);
        final CityController controller = new CityController(cityModel, this);
        controller.addTask(new UpdateCurrentWeatherTask(controller, TaskType.CITY_LIST));
        controller.addTask(new UpdateLocalizedTimeTask(controller, TaskType.CITY_LIST));
        controller.executeNext();
    }

    public void updateUI(WeatherDataModel weatherData, int position) {
        String city = weatherData.getCity();
        Log.i(TAG, "Updating UI " + city + " " + position);
        Map<String, Object> data = dataList.get(position);
        if (data != null) {
            data.put(KEY_TEMPERATURE, weatherData.getCurrentTemperature(Setting.getTemperatureType(this)));
            data.put(KEY_DATE, weatherData.getDate());
            data.put(KEY_DATE, "-");
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
        }
    }

    public void updateUITime(final CityModel cityModel) {
        Map<String, Object> data = dataList.get(cityModel.getPosition());
        if (data != null) {
            data.put(KEY_DATE, cityModel.getFormattedShortDate());
            adapter.notifyDataSetChanged();
        }
    }

}
