package com.popmovies.edison.popularmovies.service;

import com.popmovies.edison.popularmovies.model.PagedMovieList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Edison on 12/30/2015.
 */
public interface TMDBService {

    @GET("3/discover/movie")
    Call<PagedMovieList> getMoviesSortedBy(@Query("sort_by") String sortBy, @Query("vote_count.gte") String voteCount, @Query("api_key") String apiKey);

}
