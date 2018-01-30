package poojab26.popularmovies.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import poojab26.popularmovies.Data.MoviesContract;
import poojab26.popularmovies.R;

/**
 * Created by poojab26 on 30-Jan-18.
 */
public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.ViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public FavMoviesAdapter(Context mContext) {
        this.mContext = mContext;
    }


    @Override
    public FavMoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.movie_recycler_view_item, parent, false
        );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavMoviesAdapter.ViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry._ID);
        int movieIdIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_ID);
        int titleIndex = mCursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_TITLE);

        mCursor.moveToPosition(position);
        holder.favButton.setBackgroundResource(R.drawable.favourite_true);

        final int id = mCursor.getInt(idIndex);
        int movieId = mCursor.getInt(movieIdIndex);
        String movieTitle = mCursor.getString(titleIndex);

        holder.itemView.setTag(id);
        holder.tvMovieName.setText(movieTitle);
        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete item from content provider
                //refresh loader
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor cursor){
        if(mCursor==cursor)
            return null;
        Cursor temp = mCursor;
        this.mCursor = cursor;

        if(cursor!=null)
            notifyDataSetChanged();
        return temp;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovieName;
        ImageView imgPoster;
        Button favButton;

        ViewHolder(View itemView) {
            super(itemView);

            tvMovieName = (TextView) itemView.findViewById(R.id.tv_moviename);
            imgPoster = (ImageView) itemView.findViewById(R.id.imgPoster);
            favButton = (Button) itemView.findViewById(R.id.favouriteButton);

        }
    }
}
