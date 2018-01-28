package poojab26.popularmovies.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import poojab26.popularmovies.Adapter.ReviewsAdapter;
import poojab26.popularmovies.Adapter.TrailersAdapter;
import poojab26.popularmovies.ApiInterface;
import poojab26.popularmovies.BuildConfig;
import poojab26.popularmovies.Model.Movie;
import poojab26.popularmovies.Model.Review;
import poojab26.popularmovies.Model.ReviewsList;
import poojab26.popularmovies.Model.Video;
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
    RecyclerView trailersRecyclerView, reviewsRecyclerView;
    TrailersAdapter trailersAdapter;
    ReviewsAdapter reviewsAdapter;
    RecyclerView.LayoutManager trailersLayoutManager, reviewsLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvMovieBackground = (ImageView)findViewById(R.id.imgBackground);
        tvMovieTitle = (TextView)findViewById(R.id.tvOrigTitle);
        tvSynopsis = (TextView)findViewById(R.id.tvSynopsis);
        tvRating = (TextView)findViewById(R.id.tvRating);
        tvRelease = (TextView)findViewById(R.id.tvRelease);

        trailersRecyclerView = (RecyclerView)findViewById(R.id.rvTrailers);
        trailersLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(trailersLayoutManager);

        reviewsRecyclerView = (RecyclerView)findViewById(R.id.rvReviews);
        reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);


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
        loadReviews(movie.getId().longValue());

    }

    private void loadTrailers(long id) {

        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<VideosList> call = apiInterface.getMovieVideos(id, BuildConfig.API_KEY);
        call.enqueue(new Callback<VideosList>() {
            @Override
            public void onResponse(Call<VideosList> call, Response<VideosList> response) {

                List<Video> videos = response.body().getVideos();
                if(videos.size()>0)
                     Log.d("TAG", videos.get(0).getKey());
                trailersRecyclerView.setAdapter(new TrailersAdapter(videos, new TrailersAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                }));

            }

            @Override
            public void onFailure(Call<VideosList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                //setContentView(R.layout.layout_no_network);


            }
        });
    }
    private void loadReviews(long id) {

        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<ReviewsList> call = apiInterface.getMovieReviews(id, BuildConfig.API_KEY);
        call.enqueue(new Callback<ReviewsList>() {
            @Override
            public void onResponse(Call<ReviewsList> call, Response<ReviewsList> response) {

                List<Review> reviews = response.body().getReviews();
                if(reviews.size()>0)
                    Log.d("TAG", reviews.get(0).getContent());
                reviewsRecyclerView.setAdapter(new ReviewsAdapter(reviews));

            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                //setContentView(R.layout.layout_no_network);


            }
        });
    }

}
