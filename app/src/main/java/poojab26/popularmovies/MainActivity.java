package poojab26.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    int flag=-1;
    static int count;
    int currentVisiblePosition;
    MoviesAdapter adapter;
    ApiInterface apiInterface;
    RecyclerView recyclerView;
    ProgressBar sortProgress;
    int SpinnerPosition;
    RecyclerView.LayoutManager layoutManager;
    // Spinner spinner;
    private final String Spinner_Position_String = "spinner_position", Scroll_Position_String = "scroll_position",
            SharedPrefPositions = "sharedPrefPositions";

    public static final int MOVIE_LOADER_ID = 0;
    private FavMoviesAdapter favMoviesAdapter;
    private Toolbar mToolbar;

    private Spinner mSpinner;
    int numberOfColumns = 2;
    String TAG = "TAG";
    Bundle savedInstance = null;
    SharedPreferences sharedPref;

    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_main);
        sortProgress = (ProgressBar) findViewById(R.id.sortProgress);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner_nav);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


        recyclerView = (RecyclerView) findViewById(R.id.rvMovies);

        layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        favMoviesAdapter = new FavMoviesAdapter(this, new FavMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);


        Context context = getApplicationContext();
        sharedPref = context.getSharedPreferences(
                SharedPrefPositions, Context.MODE_PRIVATE);

        editor = sharedPref.edit();


    }

    @Override
    protected void onPause() {
        super.onPause();
        flag=1;
        currentVisiblePosition = ((GridLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

        editor.putInt(Spinner_Position_String, SpinnerPosition);
        editor.putInt(Scroll_Position_String, currentVisiblePosition);
        Log.d(TAG, "onPause :"+currentVisiblePosition + "FLAG :"  + flag);
        editor.apply();

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "OnResume & Bundle:" +savedInstance + " flag " + flag);
        super.onResume();

        SpinnerPosition = sharedPref.getInt(Spinner_Position_String, 0);
        currentVisiblePosition = sharedPref.getInt(Scroll_Position_String, 0);
        Log.d(TAG, "OnResume var: " + currentVisiblePosition);

        layoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        favMoviesAdapter = new FavMoviesAdapter(this, new FavMoviesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        ArrayAdapter<CharSequence> menuArrayAadapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array_spinner, android.R.layout.simple_list_item_1);
        menuArrayAadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        mSpinner.setAdapter(menuArrayAadapter);
         count=0;
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                int sPosition = sharedPref.getInt(Spinner_Position_String, 0);
                int pScroll = sharedPref.getInt(Scroll_Position_String, 0);
                Log.d(TAG, "OnItemSelected :" + pScroll+" shared & var->" + currentVisiblePosition);
                    SpinnerPosition = parent.getSelectedItemPosition();


                Log.d(TAG, "onItemSelected flag" + flag);
                if(flag==1) { //orientation change
                    SpinnerPosition = sPosition;
                    currentVisiblePosition = pScroll;
                }else{
                    currentVisiblePosition = 0;
                }

                editor.putInt(Spinner_Position_String, SpinnerPosition);
                editor.putInt(Scroll_Position_String, currentVisiblePosition);

                Log.d(TAG, "OnItemSelected var: "+currentVisiblePosition);
                editor.apply();
                loadClasses();
                count++;
                Log.d(TAG, "count "+count);
                if(count>1 && SpinnerPosition!=0) {
                    flag = -1;
                    count=0;
                }else if(SpinnerPosition==0)
                    flag =-1;

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "OnRestore flag "+flag);
        if (savedInstanceState != null) {
            savedInstance = savedInstanceState;
            flag=1; //orientation changed
        }else{
            Log.d(TAG, "null instance");
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Spinner_Position_String, SpinnerPosition);
        outState.putInt(Scroll_Position_String, currentVisiblePosition);
        Log.d(TAG, "OnSave flag: " + flag + " var-> " + currentVisiblePosition);

        editor.putInt(Spinner_Position_String, SpinnerPosition);
        editor.putInt(Scroll_Position_String, currentVisiblePosition);
        editor.commit();
    }


    private void loadClasses() {
        SpinnerPosition = sharedPref.getInt(Spinner_Position_String, 0);
        Log.d(TAG, "loadClasses for " + SpinnerPosition +"  var:" + currentVisiblePosition);
        mSpinner.setSelection(SpinnerPosition);

        if (SpinnerPosition == 0) {
            sortProgress.setVisibility(View.VISIBLE);
            loadPopularMoviesList();
        } else if (SpinnerPosition == 1) {
            sortProgress.setVisibility(View.VISIBLE);
            loadTopRatedMoviesList();
        } else if (SpinnerPosition == 2) {
            sortProgress.setVisibility(View.VISIBLE);
            loadFavouriteMovies();
        }
        //


    }

    private void loadFavouriteMovies() {
        sortProgress.setVisibility(View.GONE);
        recyclerView.setAdapter(favMoviesAdapter);
        Log.d(TAG, "loadFavMovies " + currentVisiblePosition);
        recyclerView.scrollToPosition(currentVisiblePosition);
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
                    @Override
                    public void onItemClick(int position) {

                    }
                }));
                Log.d(TAG, "Popular "+currentVisiblePosition);
                recyclerView.scrollToPosition(currentVisiblePosition);


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
                sortProgress.setVisibility(View.GONE);
                //currentVisiblePosition = 0;
                recyclerView.setAdapter(new MoviesAdapter(movies, new MoviesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                }));
                Log.d(TAG, "Top "+currentVisiblePosition);
                recyclerView.scrollToPosition(currentVisiblePosition);

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


}
