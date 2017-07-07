package com.example.hp.movie;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class GridFilmFragment extends Fragment {


    public String[] imageArray;
    static ArrayList<movie> movieData;
    String[] Array;
    GridView gridview;
    static PreferenceChangeListener listener;
    static SharedPreferences sharedPref;
    static boolean sortByFavorites = false;
    public boolean sortByPopularity = false;
    static ImageAdapter imageAdapter;
    ProgressBar pb;
    Handler mListener;


    public GridFilmFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { //fragment created
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //by adding this line the fragment handles the menu events...
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);

            startActivity(intent);
            return true;
        }


        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        gridview = (GridView) rootView.findViewById(R.id.movie_gridview);
        final String[] images = {};
        gridview.setAdapter(new ImageAdapter(rootView.getContext(), images));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), movieData.get(position).getMovieTitle(), Toast.LENGTH_SHORT).show();
               // movie movieObject = movieData.get(position);
                movieData.get(position).getMovieImage();
                movieData.get(position).getMovieTitle();
                movieData.get(position).getReleaseDate();
                movieData.get(position).getMovieOverView();
                movieData.get(position).getMovieId();
                movieData.get(position).getMovieTopRated();

//                movieObject.getMovieImage();
//                movieObject.getMovieTitle();
//                movieObject.getReleaseDate();
//                movieObject.getMovieOverView();
//                movieObject.getMovieId();
//                movieObject.getMovieTopRated();
                if (mListener != null) {
                    mListener.setSelectedMovie(movieData.get(position));
                }
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    intent.putExtra("MOVIE_TITLE", movieData.get(position).getMovieTitle());
//
//                    intent.putExtra("MOVIE_RATING", movieData.get(position).getMovieTopRated());
//
//                    intent.putExtra("MOVIE_RELEASE_DATE", movieData.get(position).getReleaseDate());
//
//                    intent.putExtra("MOVIE_POSTER", movieData.get(position).getMovieImage());
//
//                    intent.putExtra("MOVIE_ID", movieData.get(position).getMovieId());
//
//                    intent.putExtra("MOVIE_OVERVIEW", movieData.get(position).getMovieOverView());
//
//
//
//                    startActivity(intent);

                //           }

//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                title = movieTitle[position];
//                Intent intent = new Intent(getContext(), DetailActivity.class);
//                //intent.putExtra("Title", movieTitle[position]);
//                startActivity(intent);
//            }

            }
        });

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener =(Handler)context ;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "  must implement Handler");
        }
    }

    public void setMovieListener(Handler movieListener) {
        mListener = movieListener;
    }


    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gridview.setAdapter(null);
            // imageAdapter.clearItems();
            choosePref();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
       choosePref();

    }

    public void choosePref()
    {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortType = sharedPref.getString("SortVia", "rating");
        listener = new PreferenceChangeListener();
        sharedPref.registerOnSharedPreferenceChangeListener(listener);
        if (sortType.equals("popularity")) {

            getActivity().setTitle("Popular Movies");
            sortByPopularity = true;
            Log.v("sort By popularity", String.valueOf(sortByPopularity));
            new FetchMovies().execute();
        } else if (sortType.equals("rating")) {

            getActivity().setTitle("Highest Rated Movies");
            sortByPopularity = false;
            Log.v("sort By Popularity", String.valueOf(sortByPopularity));
            new FetchMovies().execute();

        }
        else if (sortType.equals("favorites"))

        {
            if (sortByFavorites)
            {
                sortByFavorites=false;
                getActivity().setTitle("Highest Rated Movies");
                new FetchMovies().execute();
            }
            else {
                getActivity().setTitle("Favorite Movies");
                sortByPopularity = false;
                sortByFavorites = true;
                Intent intent = new Intent(getContext(), favoritesActivity.class);
                startActivity(intent);
            }

        }
    }

    public class FetchMovies extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchMovies.class.getSimpleName();

        public String[] getMoviesDataFromJson(String movieJasonStr)
                throws JSONException {
            final String POSTER_PATH = "poster_path";
            final String ID = "id";
            final String OVERVIEW = "overview";
            final String RELEASE_DATE = "release_date";
            final String RATING = "vote_average";
            final String ORIGINAL_TITLE = "original_title";
            //final String IMAGE2 = "backdrop_path";
            JSONObject JSONString = new JSONObject(movieJasonStr);
            JSONArray moviesArray = JSONString.getJSONArray("results");

            //to check connection
            movieData = new ArrayList<movie>(); //static movie array
            imageArray = new String[moviesArray.length()];

            for (int i = 0; i < moviesArray.length(); i++) {

                movie myMovie = new movie();
                JSONObject movie = moviesArray.getJSONObject(i);
                String movieOverView = movie.getString(OVERVIEW);
                myMovie.setMovieOverView(movieOverView);
                Log.v("getMoviesoverview", movieOverView);


                String movietit = movie.getString(ORIGINAL_TITLE);
                myMovie.setMovieTitle(movietit);
                Log.v("getMoviestitle", movietit);

                String movieRating = movie.getString(RATING);
                myMovie.setMovieTopRated(movieRating);
                Log.v("getMoviesRating", movieRating);

                String movieDate = movie.getString(RELEASE_DATE);
                myMovie.setReleaseDate(movieDate);
                Log.v("getMoviesDate", movieDate);


                String movieId = movie.getString(ID);
                myMovie.setMovieId(movieId);
                Log.v("getMoviesid", movieId);


                String moviePath = movie.getString(POSTER_PATH);
                myMovie.setMovieImage(("http://image.tmdb.org/t/p/w185/" + moviePath));

                imageArray[i] = "http://image.tmdb.org/t/p/w185/" + moviePath;
                Log.v("getMoviesImages", moviePath);

                movieData.add(i, myMovie);

            }


            return imageArray;
        }



        protected String[] doInBackground(String... params) {
            HttpURLConnection httpURLConnection = null;
            BufferedReader reader = null;
            String movieJasonStr = null;


            try {
                Uri uri;

                if (sortByPopularity) {
                    uri = Uri.parse("https://api.themoviedb.org/3/discover/movie?api_key=92550efceb70312ca5de907f509b5d4f&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=true&page=1").buildUpon().build();
                } else {
                    uri = Uri.parse("http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&&api_key=92550efceb70312ca5de907f509b5d4f&include_adult=false&include_video=true&page=1").buildUpon().build();
                }
                Log.v(LOG_TAG, "uri" + uri);
                URL url = new URL(uri.toString());
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
// Read the input stream into a String
                InputStream inputStream = httpURLConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJasonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie string: " + movieJasonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the Movie data, there's no point in attemping
                // to parse it.

            } finally {
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }

            }

            try {
                Array = getMoviesDataFromJson(movieJasonStr);
                for (int i = 0; i < Array.length; i++) {
                    Log.v("Array of Posters Returned", Array[i]);
                }


            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error return data from Json", e);
                e.printStackTrace();

            }


            return Array;

        }

        @Override
        protected void onPostExecute(String[] result) {
            // super.onPostExecute(res);
            gridview.setAdapter(new ImageAdapter(getContext(), result));
        }


    }
}






