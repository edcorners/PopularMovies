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
 */
public class FetchTrailersAndReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();
    private final FetchTrailersAndReviewsTask.Listener listener;
    private Context mContext;
    private boolean updated;

    public FetchTrailersAndReviewsTask(Context context, FetchTrailersAndReviewsTask.Listener listener){
        this.listener = listener;
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        int movieId = Integer.valueOf(params[0]);
        PopMoviesDatabaseServices databaseServices = new PopMoviesDatabaseServices(mContext);

        String updateKey = PopMoviesDatabase.REVIEWS + "-" + PopMoviesDatabase.TRAILERS + "-" + movieId;
        if (databaseServices.isUpdateTime(null, updateKey)) {
            PagedReviewList pagedReviewList = getReviews(movieId);
            PagedTrailerList pagedTrailerList = getTrailers(movieId);
            databaseServices.updateReviewsAndTrailers(pagedReviewList, pagedTrailerList, updateKey);
            this.updated = true;
        }

        return null;
    }

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

    public interface Listener{
        void onTaskComplete();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(updated)
            listener.onTaskComplete();
    }
}
