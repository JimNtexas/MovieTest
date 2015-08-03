package com.grayraven.movietest;


import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.Discover;
import info.movito.themoviedbapi.model.MovieDb;

/**
 * Created by jhoward on 7/24/2015.
 */
public class Mdb  extends AsyncTask<String, String, String> {
    final private String TAG = "MovieMDBtask";

    private List<MovieDb> mResult ;

    @Override
    protected String doInBackground(String... strings) {
        TmdbApi tmdb = new TmdbApi(ApiKey.API_KEY);
        Discover discover = new Discover();
        discover.page(1);
        discover.sortBy("popularity.desc"); // vote_average.desc
        mResult = tmdb.getDiscover().getDiscover(discover).getResults();

        for(MovieDb db : mResult) {
            Log.i(TAG, "Title: " + db.getOriginalTitle());
            Log.i(TAG, "thumb: " + db.getPosterPath());
            Log.i(TAG, "plot : " + db.getOverview() );
            Log.i(TAG, "rating : " + db.getVoteAverage());
            Log.i(TAG, "popularity: " + db.getPopularity());
            Log.i(TAG, "release date: " + db.getReleaseDate());
        }


        return null;
    }
}
