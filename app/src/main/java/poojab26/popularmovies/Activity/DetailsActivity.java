package poojab26.popularmovies.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import poojab26.popularmovies.Adapter.ReviewsAdapter;
import poojab26.popularmovies.Adapter.TrailersAdapter;
import poojab26.popularmovies.ApiInterface;
import poojab26.popularmovies.BuildConfig;
import poojab26.popularmovies.Data.MoviesContract;
import poojab26.popularmovies.Data.MoviesDbHelper;
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

import static poojab26.popularmovies.Data.MoviesContract.MoviesEntry.CONTENT_URI;
import static poojab26.popularmovies.MainActivity.MOVIE_LOADER_ID;

public class DetailsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    TextView tvMovieTitle, tvSynopsis, tvRating, tvRelease;
    ImageView tvMovieBackground;
    Button favButton;
    ApiInterface apiInterface;
    String BASE_PATH = "http://image.tmdb.org/t/p/w342/";

    RecyclerView trailersRecyclerView, reviewsRecyclerView;
    LinearLayout trailersLayout;
    MoviesDbHelper moviesDbHelper;
    RecyclerView.LayoutManager trailersLayoutManager, reviewsLayoutManager;

    Movie movie;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        tvMovieBackground = (ImageView)findViewById(R.id.imgBackground);
        tvMovieTitle = (TextView)findViewById(R.id.tvOrigTitle);
        tvSynopsis = (TextView)findViewById(R.id.tvSynopsis);
        tvRating = (TextView)findViewById(R.id.tvRating);
        tvRelease = (TextView)findViewById(R.id.tvRelease);
        favButton = (Button)findViewById(R.id.favouriteButton);
        moviesDbHelper = new MoviesDbHelper(this);

        trailersLayout = (LinearLayout)findViewById(R.id.layoutTrailers);
        trailersRecyclerView = (RecyclerView)findViewById(R.id.rvTrailers);
        trailersLayoutManager = new LinearLayoutManager(this);
        trailersRecyclerView.setLayoutManager(trailersLayoutManager);

        reviewsRecyclerView = (RecyclerView)findViewById(R.id.rvReviews);
        reviewsLayoutManager = new LinearLayoutManager(this);
        reviewsRecyclerView.setLayoutManager(reviewsLayoutManager);
        intent = this.getIntent();

        if(intent!=null)
            movie = intent.getParcelableExtra("Movie");

        loadMovieDetails();

    }

    private void loadMovieDetails(){
        if(checkCountCursor()>=1){
            favButton.setBackgroundResource(R.drawable.favourite_true);
        }
        String path = movie.getBackdropPath();
        Picasso.with(getApplicationContext()).load(BASE_PATH+path).into(tvMovieBackground);
        tvMovieBackground.setScaleType(ImageView.ScaleType.FIT_XY);
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
                trailersRecyclerView.setAdapter(new TrailersAdapter(videos, new TrailersAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                }));

            }

            @Override
            public void onFailure(Call<VideosList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                setContentView(R.layout.layout_no_network);


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
                reviewsRecyclerView.setAdapter(new ReviewsAdapter(reviews));

            }

            @Override
            public void onFailure(Call<ReviewsList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                setContentView(R.layout.layout_no_network);


            }
        });
    }

    public void onClickFavButton(View view) {

        if(checkCountCursor()<1){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_ID, movie.getId());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movie.getTitle());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_POSTERPATH, movie.getPosterPath());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_SYNOPSIS, movie.getOverview());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_USER_RATING, movie.getVoteAverage());
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_RELEASE, movie.getReleaseDate());

            Uri uri = getContentResolver().insert(CONTENT_URI, contentValues);
            if (uri != null) {
                favButton.setBackgroundResource(R.drawable.favourite_true);
            } else
                Log.d("TAG", "uri null");

//TODO refresh loader
        }else{
            String id = movie.getId().toString();
            Uri uri = MoviesContract.MoviesEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(id).build();
            int returnUri = getContentResolver().delete(uri, null, null);
            Log.d("TAG", returnUri+"");
            getContentResolver().notifyChange(uri, null);
            favButton.setBackgroundResource(R.drawable.favourite_false);
//TODO refresh loader

            //    getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, DetailsActivity.this);
        }
    }

    public int checkCountCursor(){
        final Cursor cursor;
        final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
        String sql ="SELECT movie_id FROM "+ MoviesContract.MoviesEntry.TABLE_NAME+" WHERE movie_id="+movie.getId();

        cursor= db.rawQuery(sql,null);
        return cursor.getCount();
    }

    public void onClickTrailerLayout(View view) {
        if(trailersRecyclerView.getVisibility()==View.VISIBLE)
            trailersRecyclerView.setVisibility(View.GONE);
        else
            trailersRecyclerView.setVisibility(View.VISIBLE);
    }
}
