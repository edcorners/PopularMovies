package com.popmovies.edison.popularmovies.activity.fragment;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.popmovies.edison.popularmovies.model.Review;
import com.popmovies.edison.popularmovies.model.ReviewsArrayAdapter;
import com.popmovies.edison.popularmovies.model.Trailer;
import com.popmovies.edison.popularmovies.model.TrailersArrayAdapter;

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

    @Bind(R.id.details_trailers)
    ListView trailersListView;
    private TrailersArrayAdapter trailersArrayAdapter;

    @Bind(R.id.details_reviews) ListView reviewsListView;
    private ReviewsArrayAdapter reviewsArrayAdapter;

    @Bind(R.id.details_movie_title) TextView movieTitle;
    @Bind(R.id.details_title_frame) FrameLayout titleFrame;
    @Bind(R.id.details_movie_poster) ImageView moviePoster;
    @Bind(R.id.details_movie_overview) TextView movieOverview;
    @Bind(R.id.details_movie_rating) TextView movieRating;
    @Bind(R.id.details_movie_year) TextView movieReleaseDate;

    @Bind(R.id.details_scroll_view) ScrollView detailsScrollView;
    private Movie movie;
    private PagedReviewList reviews;
    private PagedTrailerList trailers;

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
        movie = (Movie)caller.getParcelableExtra(getString(R.string.parcelable_movie_key));

        ButterKnife.bind(this, rootView);

        setMovieDetails(movie);
        loadMovieTrailers(movie);
        loadMovieReviews(movie);

        return rootView;
    }

    @OnClick(R.id.details_favorite_toggle_button)
    public void onFavoriteToggleClick(ToggleButton favoriteToggle){
        if(favoriteToggle.isChecked()){
            new Thread(new Runnable() {
                @Override public void run() {
                    insertFavoriteMovie();
                }
            }).start();
        }else{
            new Thread(new Runnable() {
                @Override public void run() {
                    getContext().getContentResolver().delete(PopMoviesProvider.Movies.withId(movie.getId()), null, null);
                }
            }).start();
        }
    }

    private void insertFavoriteMovie() {
        Log.d(LOG_TAG, "insert movie, review, trailer");
        ArrayList<ContentProviderOperation> batchInserts = new ArrayList<>();

        ContentProviderOperation.Builder builder;
        builder = ContentProviderOperation.newInsert(PopMoviesProvider.Movies.CONTENT_URI).withValues(movie.toContentValues()); // insert movie
        batchInserts.add(builder.build());

        for(ContentValues trailerAsCV : trailers.toContentValues()){ // insert trailers
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.Trailers.CONTENT_URI).withValues(trailerAsCV);
            batchInserts.add(builder.build());
        }

        for(ContentValues reviewAsCV : reviews.toContentValues()){ // insert reviews
            builder = ContentProviderOperation.newInsert(PopMoviesProvider.Reviews.CONTENT_URI).withValues(reviewAsCV);
            batchInserts.add(builder.build());
        }

        try{
            getActivity().getContentResolver().applyBatch(PopMoviesProvider.AUTHORITY, batchInserts);
        } catch(RemoteException | OperationApplicationException e){
            Log.e(LOG_TAG, "Error applying batch insert", e);
        }
    }

    private void loadMovieReviews(Movie movie) {
        reviewsArrayAdapter = new ReviewsArrayAdapter(getContext(), new ArrayList<Review>());
        reviewsListView.setAdapter(reviewsArrayAdapter);
        FetchReviewsTask fetchReviewsTask = new FetchReviewsTask(getContext(),this);
        fetchReviewsTask.execute(String.valueOf(movie.getId()));
    }

    private void loadMovieTrailers(Movie movie) {
        trailersArrayAdapter = new TrailersArrayAdapter(getContext(), new ArrayList<Trailer>());
        trailersListView.setAdapter(trailersArrayAdapter);
        FetchTrailersTask fetchTrailersTask = new FetchTrailersTask(getContext(),this);
        fetchTrailersTask.execute(String.valueOf(movie.getId()));
    }

    private void setMovieDetails(Movie movie) {
        movie.setTitle(movieTitle);
        movieTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorWhite));
        titleFrame.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        movie.setPoster(getContext(), moviePoster);
        movie.setOverview(movieOverview);
        movie.setRating(movieRating);
        movie.setReleaseDate(movieReleaseDate);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onFetchReviewsTaskComplete(PagedReviewList reviews) {
        this.reviews = reviews;
        reviewsArrayAdapter.clear();
        Log.v(LOG_TAG, " Review: "+reviews.getReviews());
        reviewsArrayAdapter.addAll(reviews.getReviews());
        detailsScrollView.pageScroll(View.FOCUS_UP);
        detailsScrollView.fullScroll(View.FOCUS_UP);
    }

    @Override
    public void onFetchTrailersTaskComplete(PagedTrailerList trailers) {
        this.trailers = trailers;
        trailersArrayAdapter.clear();
        trailersArrayAdapter.addAll(trailers.getTrailers());
        detailsScrollView.pageScroll(View.FOCUS_UP);
        detailsScrollView.fullScroll(View.FOCUS_UP);
    }
}
