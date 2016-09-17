package com.marlonmoorer.sunshine;

import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;



        import android.content.Context;
        import android.database.Cursor;
        import android.support.v4.widget.CursorAdapter;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.marlonmoorer.sunshine.*;
import com.marlonmoorer.sunshine.data.WeatherContract;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    private final  int VIEW_TYPE_TODAY=0;
    private final  int VIEW_TYPE_FUTURE_DAY=1;
    private  boolean UseTodayLayout=false;
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext,high, isMetric) + "/" + Utility.formatTemperature(mContext,low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
//        int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
//        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
//        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
//        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType= getItemViewType(cursor.getPosition());
        int layoutId= -1;
        if(viewType==VIEW_TYPE_TODAY){
            layoutId=R.layout.list_item_forecast_today;
        }else{
            layoutId=R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder holder= new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
     public int getViewTypeCount() {
        return 2;
    }

    public void setUseTodayLayout(boolean useTodayLayout){
        this.UseTodayLayout=useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return position==0&&UseTodayLayout?VIEW_TYPE_TODAY:VIEW_TYPE_FUTURE_DAY;
    }

    /*
                This is where we fill-in the views with the contents of the cursor.
             */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.
        boolean isMetric=Utility.isMetric(context);

        ViewHolder holder= (ViewHolder)view.getTag();
        int viewType= getItemViewType(cursor.getPosition());

        String dateText= Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE));
        String highText= Utility.formatTemperature(mContext,cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),isMetric);
        String lowText=Utility.formatTemperature(mContext,cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP),isMetric);
        String forecastText= cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        int  weatherId= cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        holder.icon.setImageResource(R.mipmap.ic_launcher);
        holder.high.setText(highText);
        holder.low.setText(lowText);
        holder.forecast.setText(forecastText);
        holder.date.setText(dateText);
        if(viewType==VIEW_TYPE_FUTURE_DAY){

            holder.icon.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
        }else{
            holder.icon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        }


       // TextView tv = (TextView)view;
       // tv.setText(convertCursorRowToUXFormat(cursor));
    }



    public static class ViewHolder {

      public final  TextView high, low,date ,forecast;
      public final   ImageView icon;

        public ViewHolder(View view){
            icon=(ImageView)view.findViewById(R.id.list_item_icon);
            high= (TextView)view.findViewById(R.id.list_item_high_textview);
            low =(TextView)view.findViewById(R.id.list_item_low_textview);
            date= (TextView)view.findViewById(R.id.list_item_date_textview);
            forecast= (TextView)view.findViewById(R.id.list_item_forecast_textview);
        }

    }

}