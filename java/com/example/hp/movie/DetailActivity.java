package com.example.hp.movie;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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
import java.util.Arrays;

import static com.example.hp.movie.R.menu.detailfragment;

public class DetailActivity extends AppCompatActivity {
    public static String mMovieId;
    static ListView list2;
    static ListView list;
    private static ArrayAdapter<String> TrailerListAdapter;
    static int trailerNum;
    static int revNum;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
       // Log.v("Detail Activity class Bundle extra ", String.valueOf(extras));

        if (savedInstanceState == null) {
            DetailFragment mDetailFragment = new DetailFragment();
            mDetailFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, mDetailFragment)//decl in activity_detail , fragment detail..
                    .commit();
        }
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    // .add(container, new DetailFragment())
//                    .add(movie_detail_container, new DetailFragment())//problem?????
//                    .commit();
//        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends android.support.v4.app.Fragment {

        private final String LOG_TAG = DetailFragment.class.getSimpleName();

        private static final String MOVIE_SHARE_HASHTAG = " #AmazingMovieAtMovieApp";
        private String mMovieStr;
        private Uri mMovieImage;
        private String mMovieRating;
        private String mMovieDate;
        private String mMovieOverView;
        private RatingBar ratingBar;
        private String[] arrayTrailers;

        review[] array;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            mMovieImage = Uri.parse(getArguments().getString("MOVIE_POSTER"));
            Log.v("mMovieImage in detail", String.valueOf(mMovieImage));


            ImageView IMAGE = ((ImageView) rootView.findViewById(R.id.detail_image_view));
            Picasso.with(IMAGE.getContext())
                    .load(mMovieImage)//load images here
                    .resize(250, 350)
                    .into(IMAGE);

            mMovieStr = getArguments().getString("MOVIE_TITLE");
            ((TextView) rootView.findViewById(R.id.detail_text_view))
                    .setText(mMovieStr);

            mMovieDate = getArguments().getString("MOVIE_RELEASE_DATE");
            ((TextView) rootView.findViewById(R.id.text_view_release_date))
                    .setText(mMovieDate);

            mMovieOverView = getArguments().getString("MOVIE_OVERVIEW");
            ((TextView) rootView.findViewById(R.id.text_view_overview))
                    .setText(mMovieOverView);


            mMovieId = getArguments().getString("MOVIE_ID");
            Log.v("mMovieId ", String.valueOf(mMovieId));

            mMovieRating = getArguments().getString("MOVIE_RATING");
//            ((TextView) rootView.findViewById(R.id.text_view_rating))
//                    .setText(mMovieRating);
            ratingBar = (RatingBar)rootView.findViewById(R.id.rating_bar);
            ratingBar.setRating((6*Float.parseFloat(mMovieRating))/9);



            new FetchMovieTrailer().execute();
            new FetchMovieReview().execute();

            TrailerListAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.trailer_listview_item,
                    R.id.text_view_movie_trailer,
                    new ArrayList<String>());
            list = (ListView) rootView.findViewById(R.id.listview_trailers);

            list.setAdapter(TrailerListAdapter);

            ArrayList<review> arrayRev = new ArrayList<>();
            list2 = (ListView) rootView.findViewById(R.id.listview_reviews);
            list2.setAdapter(new ReviewListAdapter(getContext(), R.layout.review_listview_item,arrayRev));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String Trailer =  arrayTrailers[position];
                    Log.v("Trailer displayed",Trailer);
                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + Trailer));
                    Log.v("Trailer displayed2",Trailer);
                    startActivity(appIntent);

                }
            });




            final Button button = (Button) rootView.findViewById(R.id.add_remove_favorites_button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseAdapter databaseAdapter;
                    databaseAdapter = new DatabaseAdapter(getContext());
                    String movData;
                    Log.v("id in database",mMovieId);

                    movData = databaseAdapter.getData(mMovieId);
                    Log.v("mov data", movData);

                    if (!movData.equals("")) {
                        int n = databaseAdapter.deleteMovie(mMovieId);
                        Log.v("Movie deleted from favorites  ", String.valueOf(n));
                        button.setBackgroundResource(R.color.undoFav);

                    }
                    else {

                        long id = databaseAdapter.Insert(String.valueOf(mMovieImage), mMovieStr, mMovieDate, mMovieOverView, mMovieId,mMovieRating);
                        button.setBackgroundResource(R.color.fav);
                        Log.v("Movie inserted in database as favorite", String.valueOf(id));
                        if (id < 0) {
                            Log.v("Insert method in database ", "error");
                        } else {
                            Log.v("Insert method in database ", "row inserted sucessfully");
                        }

                    }

                }
            });

            return rootView;
        }


        public class FetchMovieTrailer extends AsyncTask<String, Void, String[]> {

            private final String LOG_TAG_DETAIL_TRAILER = DetailActivity.class.getSimpleName();


            public String[] getMovieTrailerDataFromJson(String movieJasonStr)
                    throws JSONException {
                final String KEY = "key";

                JSONObject JSONString = new JSONObject(movieJasonStr);
                JSONArray trailerArray = JSONString.getJSONArray("results");
                trailerNum = trailerArray.length();
                arrayTrailers = new String[trailerNum];

                for (int i = 0; i < trailerArray.length(); i++) {
                    JSONObject movie = trailerArray.getJSONObject(i);
                    String movieKey = movie.getString(KEY);
                    Log.v("Movie trailer ", movieKey);
                    arrayTrailers[i] = movieKey;
                }

                for (String s : arrayTrailers) {
                    Log.v(LOG_TAG_DETAIL_TRAILER, "movie trailers : " + s);
                }
                return arrayTrailers;

            }


            @Override
            protected String[] doInBackground(String... params) {

                HttpURLConnection httpURLConnection = null;
                BufferedReader reader = null;
                String movieJasonTrailerStr = null;
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(mMovieId)
                            .appendPath("videos")
                            .appendQueryParameter("api_key", "92550efceb70312ca5de907f509b5d4f");

                    String myUrl = builder.build().toString();
                    URL url = new URL(myUrl.toString());
                    Log.v(LOG_TAG_DETAIL_TRAILER, myUrl);

                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.connect();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {

                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    movieJasonTrailerStr = buffer.toString();
                    trailerNum = movieJasonTrailerStr.length();
                    Log.v(LOG_TAG_DETAIL_TRAILER, "Movie string: " + movieJasonTrailerStr);

                } catch (IOException e) {
                    Log.e(LOG_TAG_DETAIL_TRAILER, "Error ", e);
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
                            Log.e(LOG_TAG_DETAIL_TRAILER, "Error closing stream", e);
                        }
                    }

                }

                try {
                    return getMovieTrailerDataFromJson(movieJasonTrailerStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String[] strings) {
                if (strings != null) {
                    TrailerListAdapter.clear();
                    for (String MT : strings) {
                        TrailerListAdapter.add("Trailer");
                        getTotalHeightofListView(list);
                        Log.v("movie trailer", MT);

                    }

                }
            }
        }


        public class FetchMovieReview extends AsyncTask<String, Void, review[]> {

            private final String LOG_TAG_DETAIL_REVIEW = DetailActivity.class.getSimpleName();

            public review[] getMovieReviewDataFromJson(String movieJasonStr)
                    throws JSONException {
                final String AUTHOR = "author";
                final String CONTENT = "content";

                JSONObject JSONString = new JSONObject(movieJasonStr);
                JSONArray reviewArray = JSONString.getJSONArray("results");
                revNum = reviewArray.length();
                review[] rev = new review[reviewArray.length()];

                if (reviewArray != null) {
                    for (int i = 0; i < reviewArray.length(); i++) {
                        review revObj = new review();
                        JSONObject movie = reviewArray.getJSONObject(i);
                        String author = movie.getString(AUTHOR);
                        revObj.setAuther(author);
                        Log.v("author", author);
                        String content = movie.getString(CONTENT);
                        revObj.setContent(content);
                        Log.v("content", content);

                        rev[i] = revObj;

                    }
                    return rev;
                } else
                    return null;
            }

            @Override
            protected review[] doInBackground(String... params) {
                HttpURLConnection httpURLConnection = null;
                BufferedReader reader = null;
                String movieJasonReviewStr = null;
                try {
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("https")
                            .authority("api.themoviedb.org")
                            .appendPath("3")
                            .appendPath("movie")
                            .appendPath(mMovieId)
                            .appendPath("reviews")
                            .appendQueryParameter("api_key", "92550efceb70312ca5de907f509b5d4f");

                    String myUrl = builder.build().toString();
                    Log.v(LOG_TAG_DETAIL_REVIEW, myUrl);

                    URL url = new URL(myUrl.toString());
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
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    movieJasonReviewStr = buffer.toString();

                    Log.v(LOG_TAG_DETAIL_REVIEW, "review string: " + movieJasonReviewStr);

                    try {
                        array = getMovieReviewDataFromJson(movieJasonReviewStr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG_DETAIL_REVIEW, "Error ", e);
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
                            Log.e(LOG_TAG_DETAIL_REVIEW, "Error closing stream", e);
                        }
                    }

                }

                try {
                    return getMovieReviewDataFromJson(movieJasonReviewStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG_DETAIL_REVIEW, e.getMessage(), e);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(review[] reviews) {
                ArrayList<review> revs = new ArrayList<review>(Arrays.asList(reviews));
                if (reviews != null) {
                    list2.setAdapter(new ReviewListAdapter(getContext(), R.layout.review_listview_item, revs));
                    //getTotalHeightofListView(list2);

                }

            }
        }
            @Override
            public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                // Inflate the menu; this adds items to the action bar if it is present.
                inflater.inflate(detailfragment, menu);

                // Retrieve the share menu item
                MenuItem menuItem = menu.findItem(R.id.action_share);

                // Get the provider and hold onto it to set/change the share intent.
                ShareActionProvider mShareActionProvider =
                        (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

                // Attach an intent to this ShareActionProvider.  You can update this at any time,
                // like when the user selects a new piece of data they might like to share.
                if (mShareActionProvider != null) {
                    mShareActionProvider.setShareIntent(createShareForecastIntent());
                } else {
                    Log.d(LOG_TAG, "Share Action Provider is null?");
                }
            }

            private Intent createShareForecastIntent() {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        mMovieStr + MOVIE_SHARE_HASHTAG);
                return shareIntent;
            }


        public static void getTotalHeightofListView(ListView listView) {

            ListAdapter mAdapter = listView.getAdapter();

            int totalHeight = 0;

            for (int i = 0; i < mAdapter.getCount(); i++) {
                View mView = mAdapter.getView(i, null, listView);
                mView.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),

                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                totalHeight += mView.getMeasuredHeight();
                Log.w("HEIGHT" + i, String.valueOf(totalHeight));

            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
            listView.setLayoutParams(params);
            listView.requestLayout();

        }



    }
}

