package poojab26.popularmovies;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.List;

import poojab26.popularmovies.Adapter.MoviesAdapter;
import poojab26.popularmovies.Model.Movie;
import poojab26.popularmovies.Model.MoviesList;
import poojab26.popularmovies.Utilities.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    MoviesAdapter adapter;
    ApiInterface apiInterface;
    RecyclerView recyclerView;
    ProgressBar sortProgress;
    int SpinnerPosition=99;
    RecyclerView.LayoutManager layoutManager;
    Spinner spinner;
    private final String Sort_Spinner_Key = "sort_spinner";

    int flag = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TAG", "OnCreate");

        setContentView(R.layout.activity_main);
        sortProgress = (ProgressBar)findViewById(R.id.sortProgress);


        recyclerView = (RecyclerView) findViewById(R.id.rvMovies);
        int numberOfColumns = 2;
        layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("TAG", "Restore");
        if(savedInstanceState!=null) {
            SpinnerPosition = savedInstanceState.getInt(Sort_Spinner_Key);
            flag = 1;
           // loadClasses(SpinnerPosition);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("TAG", "Save");

        super.onSaveInstanceState(outState);
        outState.putInt(Sort_Spinner_Key, SpinnerPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d("TAG", "Menu");

        getMenuInflater().inflate(R.menu.home_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> menuArrayAadapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array_spinner, android.R.layout.simple_list_item_1);
        menuArrayAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(menuArrayAadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d("TAG", SpinnerPosition+"" + parent.getSelectedItemPosition());
                    if(flag==0)
                        SpinnerPosition = parent.getSelectedItemPosition();
                    loadClasses(SpinnerPosition);

            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

        return true;
    }

    private void loadClasses(int SpinnerPosition){
        Log.d("TAG", "loadClasses");


        if(SpinnerPosition==0) {
            sortProgress.setVisibility(View.VISIBLE);
            loadPopularMoviesList();
        }

        else if (SpinnerPosition==1) {
            sortProgress.setVisibility(View.VISIBLE);
            loadTopRatedMoviesList();
        }

        else if(SpinnerPosition==2){
            sortProgress.setVisibility(View.VISIBLE);
            loadFavouriteMovies();
        }

    }
    private void loadFavouriteMovies() {
    }

    private void loadPopularMoviesList() {
        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<MoviesList> call = apiInterface.getPopularMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                List<Movie> movies = response.body().getMovies();
                // adapter = new MoviesAdapter(movies, R.layout.movie_recycler_view_item, MainActivity.this);
                sortProgress.setVisibility(View.GONE);
              //  recyclerView.setAdapter(adapter);
                recyclerView.setAdapter(new MoviesAdapter(movies, new MoviesAdapter.OnItemClickListener() {
                    @Override public void onItemClick(int position) {
                    }
                }));
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                setContentView(R.layout.layout_no_network);


            }
        });
    }

    private void loadTopRatedMoviesList() {

        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<MoviesList> call = apiInterface.getTopRatedMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                List<Movie> movies = response.body().getMovies();
                Log.d("TAG", movies.get(0).getOriginalLanguage());
                sortProgress.setVisibility(View.GONE);

                recyclerView.setAdapter(new MoviesAdapter(movies, new MoviesAdapter.OnItemClickListener() {
                    @Override public void onItemClick(int position) {
                    }
                }));
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.d("Error", t.getMessage());
                setContentView(R.layout.layout_no_network);


            }
        });
    }

}
