package com.marlonmoorer.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.marlonmoorer.sunshine.data.WeatherContract;

import java.net.URI;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    ShareActionProvider provider;
    TextView dateView,highView,lowView,humidityView,windView,pressureView,descView,friendlyDateView;
    ImageView icon;
    String forecast;
    int LoaderId =1318;
    private Uri _uri;
    static final String DETAIL_URI = "URI";

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    private static int COL_WEATHER_ID=0;
    private static int COL_WEATHER_DATE=1;
    private static int COL_WEATHER_DESC=2;
    private static int COL_WEATHER_MAX_TEMP=3;
    private static int COL_WEATHER_MIN_TEMP=4;
    private static int COL_WEATHER_PRESSURE=5;
    private static int COL_WEATHER_HUMIDITY=6;
    private static int COL_WEATHER_WIND_SPEED=7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;





    public DetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //getLoaderManager().initLoader(LoaderId,null,this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(LoaderId,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle arguments = getArguments();
        if (arguments != null) {
            _uri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }


        View view = inflater.inflate(R.layout.fragment_detail, null);

        dateView=(TextView)view.findViewById(R.id.detail_date_textView);
        highView=(TextView)view.findViewById(R.id.detail_high_textView);
        lowView=(TextView)view.findViewById(R.id.detail_low_textView);
        humidityView=(TextView)view.findViewById(R.id.detail_humidity_textView);
        windView=(TextView)view.findViewById(R.id.detail_wind_textView);
        pressureView=(TextView)view.findViewById(R.id.detail_pressure_textView);
        descView=(TextView)view.findViewById(R.id.detail_desc_textView);
        icon=(ImageView) view.findViewById(R.id.detail_icon);
        friendlyDateView = (TextView)view.findViewById(R.id.detail_day_textView);


        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment,menu);
        MenuItem item= menu.findItem(R.id.action_share);
        provider= (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent intent=this.CreateShareIntent();

        if(forecast!=null)
        {
            provider.setShareIntent(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId()==R.id.action_settings){

            Intent intent= new Intent(getActivity(),SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent CreateShareIntent() {
        Intent intent= new Intent(Intent.ACTION_SEND);
        String forecast= "#SunshineApp";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.putExtra(Intent.EXTRA_TEXT,forecast);
        intent.setType("text/plain");
        return  intent;
    }


    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = _uri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            _uri = updatedUri;
            getLoaderManager().restartLoader(LoaderId, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if ( null != _uri ) {
                       // Now create and return a CursorLoader that will take care of
                               // creating a Cursor for the data being displayed.
            return new CursorLoader(
               getActivity(),
               _uri,
               FORECAST_COLUMNS,
               null,
               null,
               null
            );
        }
        return  null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {
            data.moveToFirst();
            boolean isMetric = Utility.isMetric(getActivity());
            long date=data.getLong(COL_WEATHER_DATE);
            String desc = data.getString(COL_WEATHER_DESC);
            String dateText = Utility.formatDate(date);
            String min = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);
            String max = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
            String wind = data.getString(COL_WEATHER_WIND_SPEED);
            String humidity = data.getString(COL_WEATHER_HUMIDITY);
            String pressure = data.getString(COL_WEATHER_PRESSURE);
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            String day = Utility.getDayName(getActivity(),date);


            dateView.setText(dateText);
            highView.setText(max);
            lowView.setText(min);
            pressureView.setText("Pressure: " + pressure);
            windView.setText("WIND: " + wind);
            humidityView.setText("Humidity:" + humidity);
            descView.setText(desc);
            icon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            friendlyDateView.setText(day);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
