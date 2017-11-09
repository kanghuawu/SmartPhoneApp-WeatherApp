package com.cmpe277.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CitySwipeViewActivity extends FragmentActivity {
    public static final String TAG = "Weather";
    static List<String> cityList;
    static int cityListSize;

    CitySwipePagerAdapter citySwipePagerAdapter;
    ViewPager mViewPager;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.city_swipe_layout);
        citySwipePagerAdapter = new CitySwipePagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(citySwipePagerAdapter);

        cityList = Setting.getCityList(CitySwipeViewActivity.this);
        cityListSize = cityList.size();
        citySwipePagerAdapter.notifyDataSetChanged();

        Intent cityListIntent = getIntent();
        List<Map<String, String>> dataList = (ArrayList<Map<String, String>>) cityListIntent.getSerializableExtra("dataList");
        Log.i(TAG, "City swip activity receive " + dataList);
        mViewPager.setCurrentItem(cityListIntent.getIntExtra(CityListActivity.KEY_POSITION, 0));
    }

    public static class CitySwipePagerAdapter extends FragmentStatePagerAdapter {
        public CitySwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Log.i(TAG, "Swiping getting item " + i);
            Fragment fragment = new SingleCityFragment();
            Bundle args = new Bundle();
            args.putInt(CityListActivity.KEY_POSITION, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return cityListSize;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "City " + (position + 1);
        }
    }

    public static class SingleCityFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.single_city_swipe_layout, container, false);
            Bundle args = getArguments();
            int position = args.getInt(CityListActivity.KEY_POSITION);
            ((TextView) rootView.findViewById(R.id.cityName)).setText(cityList.get(position));

            return rootView;
        }
    }

    @Override
    public boolean onNavigateUp() {
        onBackPressed();
        return true;
    }
}
