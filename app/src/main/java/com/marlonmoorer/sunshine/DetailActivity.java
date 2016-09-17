package com.marlonmoorer.sunshine;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

         if (savedInstanceState == null) {

             Bundle arguments = new Bundle();
             arguments.putParcelable(DetailFragment.DETAIL_URI,getIntent().getData());
             DetailFragment fragment = new DetailFragment();
             fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container,fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.detail,menu);


        return true;
    }

}
