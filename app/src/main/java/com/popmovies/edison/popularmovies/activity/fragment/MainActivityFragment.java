package com.popmovies.edison.popularmovies.activity.fragment;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.activity.MovieDetailActivity;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTask;
import com.popmovies.edison.popularmovies.activity.async.FetchMoviesTaskListener;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.adapter.MovieCursorAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,FetchMoviesTaskListener<Void> {

    private static final int MOVIES_LOADER = 0;
    protected MovieCursorAdapter moviesCursorAdapter;
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
        ButterKnife.bind(this,rootView);
        moviesCursorAdapter = new MovieCursorAdapter(getContext(), null, 0);

        gridView.setAdapter(moviesCursorAdapter);
        /*gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                Intent detailsIntent = new Intent(getActivity(), MovieDetailActivity.class);
                detailsIntent.putExtra(getString(R.string.parcelable_movie_key), new Movie(cursor));
                startActivity(detailsIntent);
            }
        });*/

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
        fetchMoviesTask.execute();
    }

    @Override
    public void onTaskComplete(Void result) {
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
        moviesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
