package com.marlonmoorer.sunshine;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.marlonmoorer.sunshine.data.WeatherContract;

import java.net.URI;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    String location;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean _2painz;
    public  static String USE_TODAY="today";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        location= Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            _2painz = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
           _2painz= false;
            getSupportActionBar().setElevation(0f);
        }

        ForecastFragment ff= (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!_2painz);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String _location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        if (_location != null && !_location.equals(location)){

            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
            if (null != ff) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
            if (null != df) {
                df.onLocationChanged(location);
            }
            location =_location;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.action_map) {

            openPreferedLocationInMap();

        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferedLocationInMap(){

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String location= preferences.getString(getString(R.string.pref_location_key),
                                                getString(R.string.pref_location_default));
        Uri geoLocation= Uri.parse("geo:0,0?").buildUpon().appendQueryParameter("q",location).build();


        Intent intent= new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if(intent.resolveActivity(getPackageManager())!=null) {
            startActivity(intent);

        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (_2painz){

            DetailFragment fragment= new DetailFragment();
            Bundle bundle=new Bundle();
            bundle.putParcelable(DetailFragment.DETAIL_URI,dateUri);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.weather_detail_container,fragment,DETAILFRAGMENT_TAG)
                    .commit();

        }else{

            Intent intent = new Intent(this, DetailActivity.class).setData(dateUri);

            startActivity(intent);


        }
    }
}
