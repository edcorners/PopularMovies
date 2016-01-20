package com.popmovies.edison.popularmovies.service;

import com.popmovies.edison.popularmovies.model.PagedMovieList;
import com.popmovies.edison.popularmovies.model.PagedReviewList;
import com.popmovies.edison.popularmovies.model.PagedTrailerList;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by Edison on 12/30/2015.
 */
public interface TMDBService {

    @GET("3/discover/movie")
    Call<PagedMovieList> getMoviesSortedBy(@Query("sort_by") String sortBy, @Query("vote_count.gte") String voteCount, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/videos")
    Call<PagedTrailerList> getVideos(@Path("id") int movieId, @Query("api_key") String apiKey);

    @GET("3/movie/{id}/reviews")
    Call<PagedReviewList> getReviews(@Path("id") int movieId, @Query("api_key") String apiKey);

}
