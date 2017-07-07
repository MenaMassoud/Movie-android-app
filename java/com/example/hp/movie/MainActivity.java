package com.example.hp.movie;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity implements Handler {

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout flPanel2 = (FrameLayout) findViewById(R.id.movie_detail_container2pane);
        if (null == flPanel2) {
            mTwoPane = false;

        } else {
            mTwoPane = true;
        }

        if (savedInstanceState == null) {
            GridFilmFragment gridFilmFragment = new GridFilmFragment();
            gridFilmFragment.setMovieListener(this);//this object of name listener.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movie, gridFilmFragment)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);//
        return true;
    }

    @Override
    public void setSelectedMovie(movie movieObject) {

        if(mTwoPane) {
            // two pane
            DetailActivity.DetailFragment detailFragment = new DetailActivity.DetailFragment();
            Bundle extras = new Bundle();
            extras.putString("MOVIE_POSTER",movieObject.getMovieImage());
            extras.putString("MOVIE_TITLE", movieObject.getMovieTitle());
            extras.putString("MOVIE_RELEASE_DATE", movieObject.getReleaseDate());
            extras.putString("MOVIE_OVERVIEW", movieObject.getMovieOverView());
            extras.putString("MOVIE_ID", movieObject.getMovieId());
            extras.putString("MOVIE_RATING", movieObject.getMovieTopRated());
            detailFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container2pane,detailFragment).commit();
        }
        else{
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("MOVIE_POSTER", movieObject.getMovieImage());
            intent.putExtra("MOVIE_TITLE", movieObject.getMovieTitle());
            intent.putExtra("MOVIE_RELEASE_DATE", movieObject.getReleaseDate());
            intent.putExtra("MOVIE_OVERVIEW", movieObject.getMovieOverView());
            intent.putExtra("MOVIE_ID", movieObject.getMovieId());
            intent.putExtra("MOVIE_RATING", movieObject.getMovieTopRated());
            startActivity(intent);
            }


        }

    }

