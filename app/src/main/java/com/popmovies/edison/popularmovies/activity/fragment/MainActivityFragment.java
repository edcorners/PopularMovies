package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTask;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTaskListener;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.MoviesArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements FetchMoviesTaskListener<List<Movie>> {

    protected MoviesArrayAdapter moviesArrayAdapter;
    private ArrayList<Movie> movieList;
    @Bind(R.id.movies_grid_view) GridView gridView;

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
        moviesArrayAdapter = new MoviesArrayAdapter(getContext(), movieList);
        ButterKnife.bind(this,rootView);
        gridView.setAdapter(moviesArrayAdapter);

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
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext(),this);
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String sortBy = sharedPrefs.getString(
                getString(R.string.pref_sort_order_key),
                getString(R.string.pref_sort_by_popularity));
        String voteCount = sharedPrefs.getString(
                getString(R.string.pref_vote_count_key),
                getString(R.string.pref_1000_votes));
        fetchMoviesTask.execute(sortBy, voteCount);
    }

    @Override
    public void onTaskComplete(List<Movie> result) {
        moviesArrayAdapter.clear();
        moviesArrayAdapter.addAll(result);
    }
}
