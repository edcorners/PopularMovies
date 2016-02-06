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
import com.popmovies.edison.popularmovies.data.PopMoviesDatabaseServices;
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
    private PopMoviesDatabaseServices databaseServices;

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
        databaseServices = new PopMoviesDatabaseServices(getContext());
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
        favoriteToggleButton.setChecked(databaseServices.isFavorite(movie));
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
                    databaseServices.insertFavorite(movie);
                    Log.v(LOG_TAG, "Saved as favorite "+movie);
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    databaseServices.deleteFavorite(movie);
                    Log.v(LOG_TAG, "Deleted from favorites "+movie);
                }
            }).start();
        }
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
                    buildReviewView(data);
                }
                break;
            case TRAILERS_LOADER:
                trailersLinearLayout.removeAllViews();
                while(data.moveToNext()){
                    buildTrailerView(data);
                }
                break;
        }
        data.close();
    }

    private void buildTrailerView(Cursor data) {
        final Trailer trailer = new Trailer(data);
        TextView trailerItemTextView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_trailer, null, false);
        trailer.setName(trailerItemTextView);
        trailerItemTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openVideoIntent = new Intent(Intent.ACTION_VIEW, trailer.getVideoUri());
                getContext().startActivity(openVideoIntent);
            }
        });
        trailersLinearLayout.addView(trailerItemTextView);
    }

    private void buildReviewView(Cursor data) {
        Review review = new Review(data);
        LinearLayout reviewItemLinearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.item_review, null, false);
        TextView reviewContentTextView = (TextView) reviewItemLinearLayout.findViewById(R.id.review_content_text_view);
        TextView reviewAuthorTextView = (TextView) reviewItemLinearLayout.findViewById(R.id.review_author_text_view);
        review.setContent(reviewContentTextView);
        review.setAuthor(reviewAuthorTextView);
        reviewsLinearLayout.addView(reviewItemLinearLayout);
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
