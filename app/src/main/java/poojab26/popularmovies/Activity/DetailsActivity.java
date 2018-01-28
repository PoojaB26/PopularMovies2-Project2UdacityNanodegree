package poojab26.popularmovies.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import poojab26.popularmovies.Adapter.MoviesAdapter;
import poojab26.popularmovies.ApiInterface;
import poojab26.popularmovies.BuildConfig;
import poojab26.popularmovies.Model.Movie;
import poojab26.popularmovies.Model.MoviesList;
import poojab26.popularmovies.Model.Result;
import poojab26.popularmovies.Model.VideosList;
import poojab26.popularmovies.R;
import poojab26.popularmovies.Utilities.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailsActivity extends AppCompatActivity {
    TextView tvMovieTitle, tvSynopsis, tvRating, tvRelease;
    ImageView tvMovieBackground;
    ApiInterface apiInterface;
    String BASE_PATH = "http://image.tmdb.org/t/p/w342/";
    String Title, Synopsis, Rating, Release, URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvMovieBackground = (ImageView)findViewById(R.id.imgBackground);
        tvMovieTitle = (TextView)findViewById(R.id.tvOrigTitle);
        tvSynopsis = (TextView)findViewById(R.id.tvSynopsis);
        tvRating = (TextView)findViewById(R.id.tvRating);
        tvRelease = (TextView)findViewById(R.id.tvRelease);

        loadMovieDetails();
    }

    private void loadMovieDetails(){
        Intent in = this.getIntent();
        Movie movie = in.getParcelableExtra("Movie");
        String path = movie.getBackdropPath();
        Picasso.with(getApplicationContext()).load(BASE_PATH+path).into(tvMovieBackground);

        tvMovieTitle.setText(movie.getOriginalTitle());
        tvSynopsis.setText(movie.getOverview());
        tvRating.setVisibility(View.VISIBLE);
        tvRating.setText(String.valueOf(movie.getVoteAverage()));
        tvRelease.setText(getString(R.string.release_date) +movie.getReleaseDate());
        loadTrailers(movie.getId().longValue());
        Log.d("TAG", movie.getId().toString());

    }

    private void loadTrailers(long id) {

        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<VideosList> call = apiInterface.getMovieVideos(id, BuildConfig.API_KEY);
        call.enqueue(new Callback<VideosList>() {
            @Override
            public void onResponse(Call<VideosList> call, Response<VideosList> response) {

                List<Result> videos = response.body().getResults();
                Log.d("TAG", videos.get(0).getKey());

            }

            @Override
            public void onFailure(Call<VideosList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                //setContentView(R.layout.layout_no_network);


            }
        });
    }

}
