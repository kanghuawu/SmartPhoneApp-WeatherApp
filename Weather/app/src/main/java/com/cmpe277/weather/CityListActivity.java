package com.cmpe277.weather;

import android.Manifest;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

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

    private boolean isEditing = false;

    final String[] KEY_ITEMS = new String[] {KEY_CITY, KEY_DATE, KEY_TEMPERATURE};
    final int[] KEY_ITEMS_LAYOUT = new int[] {R.id.textCity, R.id.textDate, R.id.textDegree};

    SimpleAdapter adapter;
    SimpleAdapter ordinaryAdaptor;
    SimpleAdapter editAdapter;
    List<Map<String, Object>> dataList;
    List<String> cityList;
    Button settingButton;
    Button editButton;
    Button addHereButton;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;
    final int REQUEST_CODE = 123;
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Calling onCreate");
        setContentView(R.layout.city_list_layout);

        if (savedInstanceState != null) {
            Log.i(TAG,"savedInstanceState is not null");
            dataList = (List<Map<String, Object>>) savedInstanceState.getSerializable("dataList");
            cityList = (List<String>) savedInstanceState.getStringArrayList("cityList");
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
                } else {
                    Log.i(TAG, "Start editing");
                    editButton.setText("Done");
                }
                switchAdaptor();
                isEditing = !isEditing;
            }
        });

        addHereButton = (Button) findViewById(R.id.addHereButton);
        addHereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCurrentLocation();
            }
        });
    }

    private void addCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Weather App", "onLocationChanged(): callback received!");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("Weather App", "longitude is " + longitude);
                Log.d("Weather App", "latitude is " + latitude);

//                addCityByLocation(latitude, longitude);
//                new CityModel(this, latitude, longitude);

//                RequestParams params = new RequestParams();
//                params.put("lat", latitude);
//                params.put("lon", longitude);
//                params.put("appid", WeatherUpdater.APP_ID);
//                WeatherUpdater.updateCurrentWeather(WeatherController.this, params);
//                WeatherUpdater.updateHourlyForecast(WeatherController.this, params);
//                WeatherUpdater.updateDailyForecast(WeatherController.this, params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("Weather App", "onStatusChanged(): callback received!");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("Weather App", "onProviderEnabled(): callback received!");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Weather App", "onProviderDisabled(): callback received!");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    private void restoreCityList() {
        for (String city : Setting.getCityList(CityListActivity.this)) {
            addCity(city);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Calling onResume");
        updateCityData();
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

    private void addCity(String city) {
        cityList.add(city);
        Map<String, Object> mCity = new HashMap<>();
        mCity.put(KEY_CITY, city);
        mCity.put(KEY_DATE, "--:--");
        mCity.put(KEY_TEMPERATURE, "--");
        dataList.add(mCity);
        adapter.notifyDataSetChanged();
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
            Setting.addCityList(CityListActivity.this, cityList);
            startActivity(swipeIntent);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        Log.i(TAG, "Calling onRestoreInstanceState");
        dataList = (List<Map<String, Object>>) state.getSerializable("dataList");
        cityList = (List<String>) state.getStringArrayList("cityList");
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

    private void updateCityData(int position) {
        final String cityName = dataList.get(position).get(KEY_CITY).toString();
        CityModel cityModel = new CityModel(cityName, position);
        final CityController controller = new CityController(cityModel, this);
        controller.addTask(new UpdateCurrentWeatherTask(controller, TaskType.CITY_LIST));
        controller.addTask(new UpdateLocalizedTimeTask(controller, TaskType.CITY_LIST));
        controller.executeNext();
    }

    public void updateUI(WeatherDataModel weatherData, int position) {
        Map<String, Object> data = dataList.get(position);
        if (data != null) {
            data.put(KEY_TEMPERATURE, weatherData.getCurrentTemperature(Setting.getTemperatureType(this)));
            data.put(KEY_DATE, "-");
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
