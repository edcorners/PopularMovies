package com.popmovies.edison.popularmovies.activity.fragment;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTask;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.adapter.MovieCursorAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, FetchMoviesTask.Listener {

    public final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int MOVIES_LOADER = 0;
    private static final String SELECTED_MOVIE_KEY = "selected_movie_index";
    private MovieCursorAdapter moviesCursorAdapter;

    @Bind(R.id.movies_grid_view)
    GridView moviesGridView;

    private int selectedMovieIndex;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when a movie has been selected.
         */
        public void onItemSelected(Movie movie);
    }

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sortPref = Utility.getPreferredSortOrder(getContext());
        if (!sortPref.equals(getString(R.string.pref_sort_by_favorites))){
                fetchMovieList();
        }
    }

    private void fetchMovieList() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getContext(),this);
        fetchMoviesTask.execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, selectedMovieIndex will be set to GridView.INVALID_POSITION,
        // so check for that before storing.
        if (selectedMovieIndex != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_MOVIE_KEY, selectedMovieIndex);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,rootView);
        moviesCursorAdapter = new MovieCursorAdapter(getContext(), null, 0);

        moviesGridView.setAdapter(moviesCursorAdapter);
        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ((Callback) getActivity()).onItemSelected(new Movie(cursor));
                selectedMovieIndex = position;
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_MOVIE_KEY)) {
            selectedMovieIndex = savedInstanceState.getInt(SELECTED_MOVIE_KEY);
        }
        return rootView;
    }

    @Override
    public void onTaskComplete() {
        getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
    }

    // Cursor loader

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = Utility.getPreferredSortOrder(getContext());
        return new CursorLoader(getContext(),
                PopMoviesProvider.Movies.withSortingAttribute(sortOrder),
                Movie.MovieColumnProjection.getProjection(),
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "Load finished");
        moviesCursorAdapter.swapCursor(data);
        if (selectedMovieIndex != GridView.INVALID_POSITION) {
            moviesGridView.smoothScrollToPosition(selectedMovieIndex);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        Log.v(LOG_TAG, "Loader initialized");
        super.onActivityCreated(savedInstanceState);
    }

}
