package com.grayraven.movietest;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.grayraven.MovieService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.model.MovieDb;


public class MainActivity extends ActionBarActivity {

    private final String TAG = "MovieMain";
    //private final AccountID APITESTS_ACCOUNT = new AccountID(6065849);
    //private final SessionToken APITESTS_TOKEN = new SessionToken("76c5c544e9c1f51d7569989d95a8d10cfb5164e5");
    private List<MovieDb> mMovies = new ArrayList<MovieDb>();
    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    private Intent mMovieService;
    private Context mContext;
    private Menu mOptions;
    private String mSortPreference = MovieService.SORT_BY_POPULARITY;
    private ProgressDialog mLoadProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        Logger logger = LoggerFactory.getLogger(MainActivity.class);
        logger.info("Hello MainActivity from SLF4j");

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(MovieService.MOVIE_SERVICE_INTENT));

        mGridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, mMovies);
        mGridView = (GridView) findViewById(R.id.gridView);
        mGridView.setAdapter(mGridAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                MovieDb item = mGridAdapter.getMovie(position);

                //Pass the image title and url to DetailsActivity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("title", item.getOriginalTitle());
                intent.putExtra("imageUrl", MovieService.getPosterUrl(item.getPosterPath()));

                //Start details activity
                startActivity(intent);
            }
        });


        StartMovieService();

    }

    private void StartMovieService() {
        if(isNetworkConnected()) {
            Intent service = new Intent(getApplicationContext(), MovieService.class);
            service.putExtra(MovieService.SORT_PREFERENCE, mSortPreference);
            service.setPackage("com.grayraven.movietest");
            Log.i(TAG, "starting service");
            ShowLoadingProgress();
            startService(service); } else
        {
            Toast.makeText(getApplicationContext(), "No internet connection!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.menuPopularity).setChecked(true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        mOptions = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        mOptions.findItem(R.id.menuPopularity).setChecked(false);
        mOptions.findItem(R.id.menuRated).setChecked(false);
        switch(id) {
            case R.id.action_settings:
                Log.i(TAG,"action settings");
                break;
            case R.id.menuPopularity:
                Log.i(TAG, "popularity");
                mSortPreference = MovieService.SORT_BY_POPULARITY;
                item.setChecked(true);
                StartMovieService();
                break;
            case R.id.menuRated:
                Log.i(TAG, "rated");
                mSortPreference = MovieService.SORT_BY_USER_RATING;
                item.setChecked(true);
                StartMovieService();
                break;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action != MovieService.MOVIE_SERVICE_INTENT) {
                Log.d(TAG, "Unknown intent: " + action);
                return;
            }
            mLoadProgress.cancel();
            int result =  intent.getExtras().getInt(MovieService.RESULT_STATUS);
            String json = intent.getStringExtra(MovieService.MOVIE_LIST_JSON);
            if(result == MovieService.STATUS_FINISHED) {
                mMovies = (List<MovieDb>) new Gson().fromJson(json, new TypeToken<List<MovieDb>>(){}.getType());
                for(MovieDb db : mMovies) {
                    Log.i(TAG, "Title: " + db.getOriginalTitle());
                    Log.i(TAG, "rating : " + db.getVoteAverage());
                    Log.i(TAG, "popularity: " + db.getPopularity());

                  /*  Log.i(TAG, "thumb: " + db.getPosterPath());
                    Log.i(TAG, "plot : " + db.getOverview() );
                    Log.i(TAG, "rating : " + db.getVoteAverage());
                    Log.i(TAG, "popularity: " + db.getPopularity());
                    Log.i(TAG, "release date: " + db.getReleaseDate());*/
                }

                mGridAdapter.setGridData(mMovies);
            }
        }

    };

    /** Check the active network to see if it is connected or connecting */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }



    private void ShowLoadingProgress() {
        mLoadProgress = new ProgressDialog(this);
        mLoadProgress.setCancelable(false);
        mLoadProgress.setTitle(getString(R.string.loading_movies));
        mLoadProgress.setMessage(getString((R.string.loading_msg)));
        mLoadProgress.show();
    }
}


