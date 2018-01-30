package poojab26.popularmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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

import poojab26.popularmovies.Adapter.FavMoviesAdapter;
import poojab26.popularmovies.Adapter.MoviesAdapter;
import poojab26.popularmovies.Data.MoviesContract;
import poojab26.popularmovies.Model.Movie;
import poojab26.popularmovies.Model.MoviesList;
import poojab26.popularmovies.Utilities.APIClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    MoviesAdapter adapter;
    ApiInterface apiInterface;
    RecyclerView recyclerView;
    ProgressBar sortProgress;
    int SpinnerPosition=99;
    RecyclerView.LayoutManager layoutManager;
    Spinner spinner;
    private final String Sort_Spinner_Key = "sort_spinner";

    public static final int MOVIE_LOADER_ID = 0;
    private FavMoviesAdapter favMoviesAdapter;

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
        favMoviesAdapter = new FavMoviesAdapter(this);
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);



    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);

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
        sortProgress.setVisibility(View.GONE);
        recyclerView.setAdapter(favMoviesAdapter);
    }
    private void loadPopularMoviesList() {
        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<MoviesList> call = apiInterface.getPopularMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                List<Movie> movies = response.body().getMovies();
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

    private void loadTopRatedMoviesList() {

        apiInterface = APIClient.getClient().create(ApiInterface.class);

        Call<MoviesList> call = apiInterface.getTopRatedMovies(BuildConfig.API_KEY);
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

                final List<Movie> movies = response.body().getMovies();
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(this) {

            // Initialize a Cursor, this will hold all the task data
            Cursor mTaskData = null;

            // onStartLoading() is called when a loader first starts loading data
            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    // Delivers any previously loaded data immediately
                    deliverResult(mTaskData);
                } else {
                    // Force a new load
                    forceLoad();
                }
            }

            // loadInBackground() performs asynchronous loading of data
            @Override
            public Cursor loadInBackground() {
                // Will implement to load data

                // Query and load all task data in the background; sort by priority
                // [Hint] use a try/catch block to catch any errors in loading data

                try {
                    return getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MoviesContract.MoviesEntry._ID);

                } catch (Exception e) {
                    Log.e("TAG", "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        favMoviesAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        favMoviesAdapter.swapCursor(null);
    }

    /*To load favourite movies in UI*/


}
