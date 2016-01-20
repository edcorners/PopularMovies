package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.Review;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 1/14/2016.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, PagedReviewList> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();
    private FetchReviewsTaskListener<PagedReviewList> listener;
    private Context context;

    public FetchReviewsTask(Context context, FetchReviewsTaskListener<PagedReviewList> listener){
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected PagedReviewList doInBackground(String... params) {
        int movieId = Integer.valueOf(params[0]);

        PagedReviewList pagedReviewList = new PagedReviewList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBService service = retrofit.create(TMDBService.class);

        Call<PagedReviewList> reviewsCall = service.getReviews(movieId, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedReviewList> response = reviewsCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedReviewList = response.body();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return pagedReviewList;
    }

    @Override
    protected void onPostExecute(PagedReviewList reviews) {
        super.onPostExecute(reviews);
        listener.onFetchReviewsTaskComplete(reviews);
    }
}
