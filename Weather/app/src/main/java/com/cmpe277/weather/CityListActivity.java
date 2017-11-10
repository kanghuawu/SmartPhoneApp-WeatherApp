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
    public static final String KEY_CITY_DUMMY = "Searching";
    public static final String KEY_POSITION = "position";

    final String[] KEY_ITEMS = new String[] {KEY_CITY, KEY_DATE, KEY_TEMPERATURE};
    final int[] KEY_ITEMS_LAYOUT = new int[] {R.id.textCity, R.id.textDate, R.id.textTemperature};

    SimpleAdapter adapter;
    SimpleAdapter ordinaryAdaptor;
    SimpleAdapter editAdapter;
    List<Map<String, Object>> dataList;
    List<String> cityList;
    Button settingButton;
    Button editButton;
    Button addHereButton;
    Button refreshButton;
    final int REQUEST_CODE = 123;
    boolean isEditing = false;
    BroadcastReceiver mBroadcastReceiver;
    String currentCity = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Calling onCreate");
        setContentView(R.layout.city_list_layout);

        if (savedInstanceState != null) {
            Log.i(TAG,"savedInstanceState is not null");
            dataList = (List<Map<String, Object>>) savedInstanceState.getSerializable("dataList");
            cityList = savedInstanceState.getStringArrayList("cityList");
        } else {
            dataList = new ArrayList<>();
            cityList = new ArrayList<>();
        }

        setUpAdaptors();
        adapter = ordinaryAdaptor;
        setListAdapter(adapter);
        restoreCityList();
        updateCityData();

        // auto complete
        createAutocomplete();
        runtimePermissions();
        setupButtons();
    }

    private boolean runtimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return true;
        }
        return false;
    }

    private void setupButtons() {

        // setting button
        settingButton = (Button) findViewById(R.id.setting);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Setting button clicked");
                Intent cityIntent = new Intent(CityListActivity.this, SettingActivity.class);
                startActivity(cityIntent);
            }
        });

        // edit button
        editButton = (Button) findViewById(R.id.edit);
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
                addCity(currentCity);
            }
        });

        refreshButton = (Button) findViewById(R.id.refreshButton);
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
                addCity(place.getName().toString());
                Log.i(TAG, String.valueOf(place.getName().toString()));
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == 100){
//            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
//                return;
//            }else {
//                runtimePermissions();
//            }
//        }
//    }

    private void addCurrentLocation(String city) {
        Log.i(TAG, "Current city is " + city);
        if (city == null) {
            return;
        }
        Log.i(TAG, "Adding city " + city);
    }

    private void restoreCityList() {
        Setting.removeCityList(CityListActivity.this);
        for (String city : Setting.getCityList(CityListActivity.this)) {
//            addCity(city);
            Log.i(TAG, "Restoring " + city);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Calling onResume");
        Intent localServiceIntent = new Intent(getApplicationContext(), LocalService.class);
        startService(localServiceIntent);
        updateCityData();
        if(mBroadcastReceiver == null){
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    currentCity = intent.getExtras().getString("currentCity");
                    Log.i(TAG, currentCity);
                }
            };
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter("locationUpdate"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBroadcastReceiver != null){
            unregisterReceiver(mBroadcastReceiver);
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

    private void addCity(String city) {
        Log.i(TAG, "Adding city " + city);
        if (city == null) {
            return;
        }
        if (cityList.contains(city)) {
            showCityAdded(city);
            return;
        }
        cityList.add(city);
        Map<String, Object> mCity = new HashMap<>();
        mCity.put(KEY_CITY, city);
        mCity.put(KEY_DATE, "--:--");
        mCity.put(KEY_TEMPERATURE, "--");
        dataList.add(mCity);
        adapter.notifyDataSetChanged();
        updateCityData();
        return;
    }

    private void removeCity(int position) {
        cityList.remove(position);
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (!isEditing) {
            super.onListItemClick(l, v, position, id);
            Log.i(TAG, "Item " + position + " clicked");
            Intent swipeIntent = new Intent(CityListActivity.this, CitySwipeViewActivity.class);
            swipeIntent.putExtra(KEY_POSITION, position);
            swipeIntent.putExtra("dataList", (Serializable) dataList);
            Setting.addCityList(CityListActivity.this, cityList);
            startActivity(swipeIntent);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(TAG, "Calling onRestoreInstanceState");
        dataList = (List<Map<String, Object>>) state.getSerializable("dataList");
        cityList = state.getStringArrayList("cityList");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "Calling onSaveInstanceState");
        outState.putSerializable("dataList", (Serializable) dataList);
        outState.putStringArrayList("cityList", (ArrayList<String>) cityList);
    }

    private void updateCityData() {
        for(int position=0; position<dataList.size(); position++) {
            updateCityData(position);
        }
    }

//    private void updateCityData(int position, String lon, String lat) {
//        RequestParams params = new RequestParams();
//        params.put("lon", lon);
//        params.put("lat", lat);
//        CityModel cityModel = new CityModel(cityName, position);
//        final CityController controller = new CityController(cityModel, this);
//        WeatherUpdater.updateCurrentWeatherForCityList(controller, CityListActivity.this, position, params);
//    }

    private void updateCityData(int position) {
        final String cityName = dataList.get(position).get(KEY_CITY).toString();
        CityModel cityModel = new CityModel(cityName, position);
        final CityController controller = new CityController(cityModel, this);
        controller.addTask(new UpdateCurrentWeatherTask(controller, TaskType.CITY_LIST));
        controller.addTask(new UpdateLocalizedTimeTask(controller, TaskType.CITY_LIST));
        controller.executeNext();
    }

    public void updateUI(WeatherDataModel weatherData, int position) {
        String city = weatherData.getCity();
        Log.i(TAG, "Updating UI " + city + " " + position);
        Map<String, Object> data = dataList.get(position);
        if (data.get(KEY_CITY).equals(KEY_CITY_DUMMY)) {
            if (cityList.contains(city)) {
                showCityAdded(city);
                cityList.remove(position);
                dataList.remove(position);
                adapter.notifyDataSetChanged();
                return;
            }
            cityList.set(position, city);
            data.put(KEY_CITY, city);
        }
        if (data != null) {
            data.put(KEY_TEMPERATURE, weatherData.getCurrentTemperature(Setting.getTemperatureType(this)));
            data.put(KEY_DATE, weatherData.getDate());
            data.put(KEY_DATE, "-");
            adapter.notifyDataSetChanged();
        }
        adapter.notifyDataSetChanged();
    }

    public void updateUITime(final CityModel cityModel) {
        Map<String, Object> data = dataList.get(cityModel.getPosition());
        if (data != null) {
            data.put(KEY_DATE, cityModel.getFormattedShortDate());
            adapter.notifyDataSetChanged();
        }
    }

}
