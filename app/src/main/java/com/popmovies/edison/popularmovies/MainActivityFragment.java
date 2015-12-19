package com.popmovies.edison.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.MoviesAdapter;
import com.popmovies.edison.popularmovies.model.MoviesJSON;
import com.popmovies.edison.popularmovies.model.TMDBAPI;

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
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    protected MoviesAdapter moviesAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        String sortBy = "popularity.desc";
        fetchMoviesTask.execute(sortBy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesAdapter = new MoviesAdapter(getContext(),new ArrayList<Movie>());
        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(moviesAdapter);

        return rootView;
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            List<Movie> movies = null;

            try {
                Uri.Builder uriBuilder = Uri.parse(TMDBAPI.BASE_URL.getValue()).buildUpon()
                        .appendPath(TMDBAPI.DISCOVER.getValue())
                        .appendPath(TMDBAPI.MOVIE.getValue())
                        .appendQueryParameter(TMDBAPI.SORT_BY.getValue(), params[0])
                        .appendQueryParameter(TMDBAPI.API_KEY.getValue(), BuildConfig.TMDB_API_KEY);

                Log.v(LOG_TAG, " Built URI " + uriBuilder.build().toString());
                URL url = new URL(uriBuilder.build().toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
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
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                String moviesJsonStr = buffer.toString();
                JSONObject moviesJson = new JSONObject(moviesJsonStr);
                Log.v(LOG_TAG, " JSON Response " + moviesJsonStr);
                JSONArray resultsArray = moviesJson.getJSONArray(MoviesJSON.RESULTS.getKey());
                movies = new ArrayList<Movie>();
                for(int i=0; i< resultsArray.length(); i++) {
                    Movie movie = new Movie(resultsArray.getJSONObject(i));
                    Log.v(LOG_TAG, " Movie object " + movie);
                    movies.add(movie);
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            super.onPostExecute(movies);
            moviesAdapter.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                moviesAdapter.addAll(movies);
            }
        }

    }

}
