package com.elmaghraby.android.moviesapp2.api;

import com.elmaghraby.android.moviesapp2.model.MoviesResponse;
import com.elmaghraby.android.moviesapp2.model.ReviewsResponse;
import com.elmaghraby.android.moviesapp2.model.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/{sort_by}")
    Call<MoviesResponse> getMovies(@Path("sort_by") String sortBy,
                                   @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse>getVidoes(@Path("movie_id") int id, @Query("api_key")String apiKey );

    @GET("movie/{movie_id}/reviews")
    Call<ReviewsResponse>getReviews(@Path("movie_id")int id, @Query("api_key")String apiKey);




}
