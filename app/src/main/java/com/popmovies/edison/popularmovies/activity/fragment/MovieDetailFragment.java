package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.popmovies.edison.popularmovies.activity.async.FetchTrailersAndReviewsTask;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;
import com.popmovies.edison.popularmovies.model.Review;
import com.popmovies.edison.popularmovies.model.Trailer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment
        implements FetchTrailersAndReviewsTask.Listener, LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = MovieDetailFragment.class.getSimpleName();
    private static final int REVIEWS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;

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
        ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        if (arguments != null) {
            movie = arguments.getParcelable(getString(R.string.parcelable_movie_key));
            setMovieDetails(movie);
            loadMovieTrailersAndReviews(movie);
        }

        return movie!= null ? rootView : null;
    }

    private void setMovieDetails(Movie movie) {
        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        movie.setPoster(getContext(), moviePoster);
        movie.setOverview(movieOverview);
        movie.setRating(getContext(), movieRating);
        movie.setReleaseDate(movieReleaseDate);
        updateFavoriteButtonState(movie);
    }

    private void updateFavoriteButtonState(Movie movie) {
        Cursor movieCursor = getContext().getContentResolver().query(PopMoviesProvider.Movies.withMovieId(movie.getId()), null, null, null, null);
        if(movieCursor != null) {
            movieCursor.moveToFirst();
            if (movieCursor.getCount() > 0) {
                favoriteToggleButton.setChecked(true);
            } else {
                favoriteToggleButton.setChecked(false);
            }
            movieCursor.close();
        }
    }

    private void loadMovieTrailersAndReviews(Movie movie) {
        FetchTrailersAndReviewsTask fetchTrailersAndReviewsTask = new FetchTrailersAndReviewsTask(getContext(), this);
        fetchTrailersAndReviewsTask.execute(String.valueOf(movie.getId()));
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
    public void onTaskComplete() {
        getLoaderManager().restartLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().restartLoader(TRAILERS_LOADER, null, this);
    }

    // Cursor Methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        if(movie != null) {
            switch (id) {
                case REVIEWS_LOADER:
                    cursorLoader = new CursorLoader(getContext(),
                            PopMoviesProvider.Reviews.withMovieId(movie.getId()),
                            Review.ReviewColumnProjection.getProjection(),
                            null,
                            null,
                            null);
                    break;
                case TRAILERS_LOADER:
                    cursorLoader = new CursorLoader(getContext(),
                            PopMoviesProvider.Trailers.withMovieId(movie.getId()),
                            Trailer.TrailerColumnProjection.getProjection(),
                            null,
                            null,
                            null);
                    break;
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case REVIEWS_LOADER:
                reviewsLinearLayout.removeAllViews();
                while(data.moveToNext()){
                    Review review = new Review(data);
                    LinearLayout reviewItemLinearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_review, null, false);
                    TextView reviewContentTextView = (TextView) reviewItemLinearLayout.findViewById(R.id.review_content_text_view);
                    TextView reviewAuthorTextView = (TextView) reviewItemLinearLayout.findViewById(R.id.review_author_text_view);
                    review.setContent(reviewContentTextView);
                    review.setAuthor(reviewAuthorTextView);
                    reviewsLinearLayout.addView(reviewItemLinearLayout);
                }
                break;
            case TRAILERS_LOADER:
                trailersLinearLayout.removeAllViews();
                while(data.moveToNext()){
                    Trailer trailer = new Trailer(data);
                    TextView trailerItemTextView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, null, false);
                    trailer.setName(trailerItemTextView);
                    trailersLinearLayout.addView(trailerItemTextView);
                }
                break;
        }
        data.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {  }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        Log.v(LOG_TAG, "Loader initialized");
        super.onActivityCreated(savedInstanceState);
    }
}
