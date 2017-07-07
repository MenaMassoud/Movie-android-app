package com.example.hp.movie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class favoritesActivity extends AppCompatActivity {
    GridView gridViewFavorites;
    Handler mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        final ArrayList<movie> retFavMov = databaseAdapter.selectAllMovies();
        final String[] favGrid = new String[retFavMov.size()];
        for (int i = 0; i < retFavMov.size(); i++) {
            favGrid[i] = retFavMov.get(i).getMovieImage();
            Log.v("Image Posters in my favorite ", favGrid[i]);

        }

        gridViewFavorites = (GridView) findViewById(R.id.movie_gridview_favorites);
        gridViewFavorites.setAdapter(new ImageAdapter(this, favGrid));

        gridViewFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                retFavMov.get(position).getMovieImage();
                Log.v("Image",retFavMov.get(position).getMovieImage());
                retFavMov.get(position).getMovieTitle();
                retFavMov.get(position).getReleaseDate();
                retFavMov.get(position).getMovieOverView();
                retFavMov.get(position).getMovieId();
                retFavMov.get(position).getMovieTopRated();
                if (mListener != null) {
                    mListener.setSelectedMovie(retFavMov.get(position));
                }

            }
        });

    }

}




