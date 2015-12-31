package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.MoviesAdapter;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

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
            String voteCountGte = "0";
            String sortBy = params[0];
            List<Movie> movies = null;

            PagedMovieList pagedMovieList = new PagedMovieList();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(TMDBAPI.BASE_URL.getValue())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            TMDBService service = retrofit.create(TMDBService.class);

            if(sortBy.equalsIgnoreCase(getString(R.string.pref_sort_by_rating))){// Trying to get a more reasonable highest-rated list
                voteCountGte = "1000";
            }
            Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy,voteCountGte, BuildConfig.TMDB_API_KEY);

            try {
                Response<PagedMovieList> response = moviesCall.execute();
                Log.v(LOG_TAG,response.raw().toString());
                pagedMovieList = response.body();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
            }
            return pagedMovieList.getResults();
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

    }

}
