package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.activity.async.FetchReviewsTask;
import com.popmovies.edison.popularmovies.activity.async.FetchReviewsTaskListener;
import com.popmovies.edison.popularmovies.activity.async.FetchTrailersTask;
import com.popmovies.edison.popularmovies.activity.async.FetchTrailersTaskListener;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;
import com.popmovies.edison.popularmovies.model.adapter.ReviewsArrayAdapter;
import com.popmovies.edison.popularmovies.model.adapter.TrailersArrayAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment
        implements FetchReviewsTaskListener<PagedReviewList>, FetchTrailersTaskListener<PagedTrailerList> {

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    @Bind(R.id.details_trailers_linear_layout)
    LinearLayout trailersLinearLayout;

    @Bind(R.id.details_reviews_linear_layout)
    LinearLayout reviewsLinearLayout;

    @Bind(R.id.details_movie_title)
    TextView movieTitle;
    @Bind(R.id.details_title_frame)
    FrameLayout titleFrame;
    @Bind(R.id.details_movie_poster)
    ImageView moviePoster;
    @Bind(R.id.details_movie_overview)
    TextView movieOverview;
    @Bind(R.id.details_movie_rating)
    TextView movieRating;
    @Bind(R.id.details_movie_year)
    TextView movieReleaseDate;
    @Bind(R.id.details_favorite_toggle_button)
    ToggleButton favoriteToggleButton;

    @Bind(R.id.details_scroll_view)
    ScrollView detailsScrollView;
    private Movie movie;
    private PagedReviewList pagedReviewList;
    private PagedTrailerList pagedTrailerList;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        Intent caller = getActivity().getIntent();
        movie = (Movie) caller.getParcelableExtra(getString(R.string.parcelable_movie_key));

        ButterKnife.bind(this, rootView);

        setMovieDetails(movie);
        loadMovieTrailers(movie);
        loadMovieReviews(movie);

        return rootView;
    }

    private void setMovieDetails(Movie movie) {
        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        movie.setPoster(getContext(), moviePoster);
        movie.setOverview(movieOverview);
        movie.setRating(movieRating);
        movie.setReleaseDate(movieReleaseDate);
        updateFavoriteButtonState(movie);
    }

    private void updateFavoriteButtonState(Movie movie) {
        Cursor movieCursor = getContext().getContentResolver().query(PopMoviesProvider.Movies.withMovieId(movie.getId()), null, null, null, null);
        movieCursor.moveToFirst();
        if (movieCursor.getCount() > 0) {
            favoriteToggleButton.setChecked(true);
        } else {
            favoriteToggleButton.setChecked(false);
        }
    }

    private void loadMovieReviews(Movie movie) {
        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getContext(), this);
        fetchReviewsTask.execute(String.valueOf(movie.getId()));
    }

    private void loadMovieTrailers(Movie movie) {
        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(getContext(), this);
        fetchTrailersTask.execute(String.valueOf(movie.getId()));
    }

    @OnClick(R.id.details_favorite_toggle_button)
    public void onFavoriteToggleClick(ToggleButton favoriteToggle) {
        if (favoriteToggle.isChecked()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    insertFavoriteMovie();
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    deleteFavoriteMovie();
                }
            }).start();
        }
    }

    private void deleteFavoriteMovie() {
        ArrayList<ContentProviderOperation> batchDeletes = getBatchDeletes();
        try {
            getActivity().getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchDeletes);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    @NonNull
    private ArrayList<ContentProviderOperation> getBatchDeletes() {
        ArrayList<ContentProviderOperation> batchDeletes = new ArrayList<>();

        ContentProviderOperation.Builder builder;
        builder = ContentProviderOperation.newDelete(PopMoviesProvider.Trailers.withMovieId(movie.getId())); // delete trailers
        batchDeletes.add(builder.build());
        builder = ContentProviderOperation.newDelete(PopMoviesProvider.Reviews.withMovieId(movie.getId())); // delete reviews
        batchDeletes.add(builder.build());
        builder = ContentProviderOperation.newDelete(PopMoviesProvider.Movies.withMovieId(movie.getId())); // delete movie
        batchDeletes.add(builder.build());
        return batchDeletes;
    }

    private void insertFavoriteMovie() {
        Log.d(LOG_TAG, "Insert movie, reviews and trailers");
        ArrayList<ContentProviderOperation> batchInserts = getBatchInserts();
        try {
            getActivity().getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchInserts);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    private ArrayList<ContentProviderOperation> getBatchInserts() {
        ArrayList<ContentProviderOperation> batchInserts = new ArrayList<>();

        ContentProviderOperation.Builder builder;
        builder = ContentProviderOperation.newInsert(PopMoviesProvider.Movies.CONTENT_URI).withValues(movie.toContentValues()); // insert movie
        batchInserts.add(builder.build());

        for (ContentValues trailerAsCV : pagedTrailerList.toContentValues()) { // insert pagedTrailerList
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.Trailers.CONTENT_URI).withValues(trailerAsCV);
            batchInserts.add(builder.build());
        }

        for (ContentValues reviewAsCV : pagedReviewList.toContentValues()) { // insert pagedReviewList
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.Reviews.CONTENT_URI).withValues(reviewAsCV);
            batchInserts.add(builder.build());
        }

        return batchInserts;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFetchReviewsTaskComplete(PagedReviewList pagedReviewList) {
        this.pagedReviewList = pagedReviewList;
        ReviewsArrayAdapter reviewsArrayAdapter = new ReviewsArrayAdapter(getContext(), pagedReviewList.getReviews());

        for (int i = 0; i < reviewsArrayAdapter.getCount(); i++) {
            reviewsLinearLayout.addView(reviewsArrayAdapter.getView(i, null, null));
        }
    }


    @Override
    public void onFetchTrailersTaskComplete(PagedTrailerList pagedTrailerList) {
        this.pagedTrailerList = pagedTrailerList;
        TrailersArrayAdapter trailersArrayAdapter = new TrailersArrayAdapter(getContext(), pagedTrailerList.getTrailers());

        for (int i = 0; i < trailersArrayAdapter.getCount(); i++) {
            trailersLinearLayout.addView(trailersArrayAdapter.getView(i, null, null));
        }
    }
}
