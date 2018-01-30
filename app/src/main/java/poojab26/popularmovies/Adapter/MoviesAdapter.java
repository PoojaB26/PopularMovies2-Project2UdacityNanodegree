package poojab26.popularmovies.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import poojab26.popularmovies.Activity.DetailsActivity;
import poojab26.popularmovies.Data.MoviesContract;
import poojab26.popularmovies.Data.MoviesDbHelper;
import poojab26.popularmovies.MainActivity;
import poojab26.popularmovies.Model.Movie;
import poojab26.popularmovies.R;

import static poojab26.popularmovies.Data.MoviesContract.MoviesEntry.CONTENT_URI;

/**
 * Created by pblead26 on 04-Oct-17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    MoviesDbHelper moviesDbHelper;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private final List<Movie> movies;
    private final OnItemClickListener listener;

    String BASE_PATH = "http://image.tmdb.org/t/p/w185/";



    // data is passed into the constructor
    public MoviesAdapter(List<Movie> movies, OnItemClickListener listener) {
        this.movies = movies;
        this.listener = listener;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        moviesDbHelper= new MoviesDbHelper(parent.getContext());
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position, listener);
    }



    // total number of cells
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName;
        ImageView imgPoster;
        Button favButton;

        ViewHolder(View itemView) {
            super(itemView);

            tvMovieName = (TextView) itemView.findViewById(R.id.tv_moviename);
            imgPoster = (ImageView) itemView.findViewById(R.id.imgPoster);
            favButton = (Button) itemView.findViewById(R.id.favouriteButton);


        }

        public void bind(final int position, final OnItemClickListener listener) {
            String title = movies.get(position).getTitle();
            tvMovieName.setText(title);
            String ImagePath = movies.get(position).getPosterPath();
            Picasso.with(itemView.getContext()).load(BASE_PATH + ImagePath).into(imgPoster);

            final Cursor cursor;
            final SQLiteDatabase db = moviesDbHelper.getReadableDatabase();
            String sql ="SELECT movie_id FROM "+ MoviesContract.MoviesEntry.TABLE_NAME+" WHERE movie_id="+movies.get(position).getId();

            cursor= db.rawQuery(sql,null);
            if(cursor.getCount()>=1)
                favButton.setBackgroundResource(R.drawable.favourite_true);

            favButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TAG", "fav button " + movies.get(position).getTitle());
                   /* if(cursor.getCount()<1) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_ID, movies.get(position).getId());
                        contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, movies.get(position).getTitle());

                        Uri uri = itemView.getContext().getContentResolver().insert(CONTENT_URI, contentValues);
                        itemView.getContext().getContentResolver().notifyChange(uri, null);
                        if (uri != null) {
                            Log.d("TAG", "str" + uri.toString());
                            favButton.setBackgroundResource(R.drawable.favourite_true);
                        } else
                            Log.d("TAG", "uri null");
                    }*/
                }
            });


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                    Movie movie = movies.get(position);
                    Intent i = new Intent(itemView.getContext(), DetailsActivity.class);
                    i.putExtra("Movie", movie);
                    itemView.getContext().startActivity(i);

                }
            });
        }
    }

}
