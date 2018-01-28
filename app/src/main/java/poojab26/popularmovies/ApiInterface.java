package poojab26.popularmovies;

import poojab26.popularmovies.Model.MoviesList;
import poojab26.popularmovies.Model.ReviewsList;
import poojab26.popularmovies.Model.VideosList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by pblead26 on 04-Oct-17.
 */

public interface ApiInterface {


    @GET("movie/popular")
    Call<MoviesList> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<MoviesList> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<VideosList> getMovieVideos(@Path("id") long movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/reviews")
    Call<ReviewsList> getMovieReviews(@Path("id") long movieId, @Query("api_key") String apiKey);

}
