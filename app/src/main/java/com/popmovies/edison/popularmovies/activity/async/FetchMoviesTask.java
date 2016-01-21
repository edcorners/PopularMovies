package com.popmovies.edison.popularmovies.activity.async;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.popmovies.edison.popularmovies.BuildConfig;
import com.popmovies.edison.popularmovies.R;
import com.popmovies.edison.popularmovies.Utility;
import com.popmovies.edison.popularmovies.data.PopMoviesProvider;
import com.popmovies.edison.popularmovies.data.UpdateLogColumns;
import com.popmovies.edison.popularmovies.model.Movie;
import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.TMDBAPI;
import com.popmovies.edison.popularmovies.service.TMDBService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Edison on 12/30/2015.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, List<Movie>> {

    public static final int UPDATE_FREQ_IN_MILLIS = 10800000; //3 hours
    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private FetchMoviesTaskListener<List<Movie>> listener;
    private Context context;

    public FetchMoviesTask(Context context, FetchMoviesTaskListener<List<Movie>> listener){
        this.context = context;
        this.listener = listener;
    }

    private boolean isUpdateTime() throws ParseException {
        boolean updateTime = true;

        String preferredSort = Utility.getPreferredSortOrder(context);
        Cursor cursor = context.getContentResolver().query(PopMoviesProvider.UpdateLogs.withSortingAttribute(preferredSort),null,null,null,null);
        cursor.moveToFirst();
        if(cursor.getCount() >  0){
            int indexForDate =  cursor.getColumnIndex(UpdateLogColumns.DATE);
            Date lastUpdate = Utility.dateTimeFormat.parse(cursor.getString(indexForDate));
            long lastUpdateMillis = lastUpdate.getTime();
            long currentTimeMillis = Calendar.getInstance().getTimeInMillis();
            updateTime = Math.abs(currentTimeMillis - lastUpdateMillis) >= 10800000;
        }
        return updateTime;
    }


    @Override
    protected List<Movie> doInBackground(String... params) {
        String sortBy = params[0];
        String voteCountGte = "0";
        List<Movie> movies = null;

        PagedMovieList pagedMovieList = new PagedMovieList();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDBAPI.BASE_URL.getValue())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TMDBService service = retrofit.create(TMDBService.class);

        if(sortBy.equalsIgnoreCase(context.getString(R.string.pref_sort_by_rating))){
            voteCountGte = params[1];
        }
        Call<PagedMovieList> moviesCall = service.getMoviesSortedBy(sortBy,voteCountGte, BuildConfig.TMDB_API_KEY);

        try {
            Response<PagedMovieList> response = moviesCall.execute();
            Log.v(LOG_TAG, response.raw().toString());
            pagedMovieList = response.body();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        }
        return pagedMovieList.getResults();
    }

    @Override
    protected void onPostExecute(List<Movie> movies) {
        super.onPostExecute(movies);
        listener.onTaskComplete(movies);
    }

}