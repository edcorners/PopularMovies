package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabase;
import com.popmovies.edison.popularmovies.data.PopMoviesDatabaseServices;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;
import com.popmovies.edison.popularmovies.webservice.TMDBAPI;
import com.popmovies.edison.popularmovies.webservice.TMDBWebService;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 1/14/2016.
 * Reviews and Trailers are fetched from TMDB only if it's update time. (See PopMoviesDatabaseServices isUpdateTime method)
 * NOTE: Sync adapter was implemented instead but didn't work because of the "40 transactions every 10 seconds" restriction of the API.
 */
public class FetchTrailersAndReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();
    private Context mContext;

    /**
     * Default constructor
     * @param context application context
     */
    public FetchTrailersAndReviewsTask(Context context){
        this.mContext = context;
    }

    /**
     * doInBackground implementation
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(String... params) {
        int movieId = Integer.valueOf(params[0]);
        PopMoviesDatabaseServices databaseServices = new PopMoviesDatabaseServices(mContext);

        String updateKey = PopMoviesDatabase.REVIEWS + "-" + PopMoviesDatabase.TRAILERS + "-" + movieId;
        if (databaseServices.isUpdateTime(null, updateKey)) {
            PagedReviewList pagedReviewList = getReviews(movieId);
            PagedTrailerList pagedTrailerList = getTrailers(movieId);
            databaseServices.updateReviewsAndTrailers(pagedReviewList, pagedTrailerList, updateKey);
        }

        return null;
    }

    /**
     * Fetch reviews from TMBD
     * @param movieId id of a movie from TMDB
     * @return a list of reviews
     */
    private PagedReviewList getReviews(long movieId) {
        PagedReviewList pagedReviewList = new PagedReviewList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBWebService service = retrofit.create(TMDBWebService.class);

        Call<PagedReviewList> reviewsCall = service.getReviews(movieId, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedReviewList> response = reviewsCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedReviewList = response.body();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error fetching reviews from TMBD", e);
        }
        return pagedReviewList;
    }

    /**
     * Fetch trailers from TMDB
     * @param movieId id of a movie from TMDB
     * @return a list of trailers
     */
    private PagedTrailerList getTrailers(long movieId) {
        PagedTrailerList pagedTrailerList = new PagedTrailerList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBWebService service = retrofit.create(TMDBWebService.class);

        Call<PagedTrailerList> videosCall = service.getVideos(movieId, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedTrailerList> response = videosCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedTrailerList = response.body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching trailers from TMDB", e);
        }
        return pagedTrailerList;
    }

}
