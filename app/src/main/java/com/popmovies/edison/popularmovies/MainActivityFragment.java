package com.popmovies.edison.popularmovies;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
    private ArrayList<Movie> movieList;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!restoreState(savedInstanceState)) {
            fetchMovieList();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("movies", movieList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        moviesAdapter = new MoviesAdapter(getContext(), movieList);
        GridView gridView = (GridView) rootView.findViewById(R.id.movies_grid_view);
        gridView.setAdapter(moviesAdapter);

        return rootView;
    }

    private boolean restoreState(Bundle savedInstanceState) {
        boolean restored = true;
        if (savedInstanceState == null || !savedInstanceState.containsKey("movies")) {
            movieList = new ArrayList<Movie>();
            restored = false;
        } else {
            movieList = savedInstanceState.getParcelableArrayList("movies");
        }
        return restored;
    }

    private void fetchMovieList() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_by_popularity));
        fetchMoviesTask.execute(sortBy);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(String... params) {
            List<Movie> movies = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {
                urlConnection = getHttpURLConnection(params[0]);
                reader = getResponseBuffer(urlConnection);
                String moviesJSON = getStringResponse(reader);
                movies = getMovieList(moviesJSON);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error ", e);
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
            movieList = (ArrayList<Movie>) movies;
        }

        @Nullable
        private List<Movie> getMovieList(String moviesJSON) throws IOException, JSONException {
            List<Movie> movies = new ArrayList<Movie>();
            JSONObject moviesJson = new JSONObject(moviesJSON);
            Log.v(LOG_TAG, " JSON Response " + moviesJSON);
            JSONArray resultsArray = moviesJson.getJSONArray(MoviesJSON.RESULTS.getKey());
            for (int i = 0; i < resultsArray.length(); i++) {
                Movie movie = new Movie(resultsArray.getJSONObject(i));
                Log.v(LOG_TAG, " Movie object " + movie);
                movies.add(movie);
            }
            return movies;
        }

        private String getStringResponse(BufferedReader reader) throws IOException {
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            return buffer.toString();
        }

        private BufferedReader getResponseBuffer(HttpURLConnection urlConnection) throws IOException {
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = null;

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader;
        }


        public HttpURLConnection getHttpURLConnection(String sortOrder) throws IOException {
            HttpURLConnection urlConnection = null;

            Uri.Builder uriBuilder = Uri.parse(TMDBAPI.BASE_URL.getValue()).buildUpon()
                    .appendPath(TMDBAPI.DISCOVER.getValue())
                    .appendPath(TMDBAPI.MOVIE.getValue())
                    .appendQueryParameter(TMDBAPI.SORT_BY.getValue(), sortOrder)
                    .appendQueryParameter(TMDBAPI.API_KEY.getValue(), BuildConfig.TMDB_API_KEY);

            if(sortOrder.equalsIgnoreCase(getString(R.string.pref_sort_by_rating))){// Trying to get a more likely highest-rated list
                uriBuilder.appendQueryParameter(getString(R.string.vote_count_gte),"1000");
            }

            Log.v(LOG_TAG, " Built URI " + uriBuilder.build().toString());
            URL url = new URL(uriBuilder.build().toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            return urlConnection;
        }

    }

}
