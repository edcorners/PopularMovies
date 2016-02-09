package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.popmovies.edison.popularmovies.data.ReviewColumns;
import com.popmovies.edison.popularmovies.data.TrailerColumns;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.Review;
import com.popmovies.edison.popularmovies.model.Trailer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    public static final String SHARE_TEXT = " - Shared from #PopMoviesApp"; // Used with the share button
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

    private Movie mMovie;
    private PopMoviesDatabaseServices mDatabaseServices;
    private ShareActionProvider mShareActionProvider;

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
        Log.v(LOG_TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        mDatabaseServices = new PopMoviesDatabaseServices(getContext());
        if (arguments != null) {
            mMovie = arguments.getParcelable(getString(R.string.parcelable_movie_key));
            setMovieDetails(mMovie);
            loadMovieTrailersAndReviews(mMovie);
        }

        return mMovie != null ? rootView : null;
    }

    /**
     * Initializes an async taks that loads reviews and trailers information from TMDB
     * @param movie should have at least an id
     */
    private void loadMovieTrailersAndReviews(Movie movie) {
        FetchTrailersAndReviewsTask fetchTrailersAndReviewsTask = new FetchTrailersAndReviewsTask(getContext());
        fetchTrailersAndReviewsTask.execute(String.valueOf(movie.getId()));
    }

    /**
     * Initializes the details view based on a movie object
     * @param movie should contain the basic movie information like poster, title, overview, rating and release date.
     */
    private void setMovieDetails(Movie movie) {
        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        movie.setPoster(getContext(), moviePoster);
        movie.setOverview(movieOverview);
        movie.setRating(getContext(), movieRating);
        movie.setReleaseDate(movieReleaseDate);
        favoriteToggleButton.setChecked(mDatabaseServices.isFavorite(movie));
    }


    /**
     * Favorite button inserts a new favorite when tapped enabled. Deletes when tapped disabled
     * @param favoriteToggle a toggle button must exist in the view
     */
    @OnClick(R.id.details_favorite_toggle_button)
    public void onFavoriteToggleClick(ToggleButton favoriteToggle) {
        if (favoriteToggle.isChecked()) {
            new Thread(new Runnable() {
                @Override
                public void run() { // Preventing favorite button from looking "frozen"
                    mDatabaseServices.insertFavorite(mMovie);
                    Log.v(LOG_TAG, "Saved as favorite "+ mMovie);
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() { //Preventing favorite button from looking "frozen"
                    mDatabaseServices.deleteFavorite(mMovie);
                    Log.v(LOG_TAG, "Deleted from favorites "+ mMovie);
                }
            }).start();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_movie_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mMovie != null && mMovie.hasTrailers()) {
            mShareActionProvider.setShareIntent(createShareTrailerIntent(mMovie.getFirstTrailer()));
        }else{
            mShareActionProvider.setShareIntent(null);
        }
    }

    /**
     * Create a share intent to expose the external youtube URL for the trailer
     * @param trailer not null, should contain a valid url
     * @return intent with a call to an external youtube video
     */
    private Intent createShareTrailerIntent(Trailer trailer) {
        Intent shareIntent = null;
        if(trailer != null) {
            String shareString = trailer.getVideoUri() + SHARE_TEXT;
            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        }
        return shareIntent;
    }

    // Cursor Methods

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;

        if(mMovie != null) {
            switch (id) {
                case REVIEWS_LOADER:
                    Log.v(LOG_TAG, "onCreateLoader REVIEWS_LOADER");
                    cursorLoader = new CursorLoader(getContext(),
                            PopMoviesProvider.Reviews.CONTENT_URI,
                            Review.ReviewColumnProjection.getProjection(),
                            ReviewColumns.MOVIE_ID +"=?",
                            new String[]{String.valueOf(mMovie.getId())},
                            null);
                    break;
                case TRAILERS_LOADER:
                    Log.v(LOG_TAG, "onCreateLoader TRAILERS_LOADER");
                    cursorLoader = new CursorLoader(getContext(),
                            PopMoviesProvider.Trailers.CONTENT_URI,
                            Trailer.TrailerColumnProjection.getProjection(),
                            TrailerColumns.MOVIE_ID +"=?",
                            new String[]{String.valueOf(mMovie.getId())},
                            null);
                    break;
            }
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null ) {
            switch (loader.getId()) {
                case REVIEWS_LOADER:
                    Log.v(LOG_TAG, "LoadFinished REVIEWS_LOADER ");
                    mMovie.clearReviewList();
                    data.moveToPosition(-1); //Rewind cursor
                    reviewsLinearLayout.removeAllViews();
                    while (data.moveToNext()) {
                        buildReviewView(data);
                    }
                    break;
                case TRAILERS_LOADER:
                    Log.v(LOG_TAG, "LoadFinished TRAILERS_LOADER ");
                    mMovie.clearTrailerList();
                    data.moveToPosition(-1); //Rewind cursor
                    trailersLinearLayout.removeAllViews();
                    while (data.moveToNext()) {
                        buildTrailerView(data);
                    }
                    break;
            }
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareTrailerIntent(mMovie.getFirstTrailer()));
            }
        }
    }

    /**
     * Adds a new trailer to the trailer layout
     * @param data cursor containing trailer information
     */
    private void buildTrailerView(Cursor data) {
        final Trailer trailer = new Trailer(data);
        mMovie.addTrailer(trailer);
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

    /**
     * Adds a new review to the review layout
     * @param data cursor containing review information
     */
    private void buildReviewView(Cursor data) {
        Review review = new Review(data);
        mMovie.addReview(review);
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
        Log.v(LOG_TAG, "Initializing loaders");
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
