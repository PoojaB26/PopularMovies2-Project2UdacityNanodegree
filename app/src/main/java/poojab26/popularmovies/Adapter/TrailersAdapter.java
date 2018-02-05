package poojab26.popularmovies.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import poojab26.popularmovies.Model.Video;
import poojab26.popularmovies.R;

/**
 * Created by poojab26 on 28-Jan-18.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {

    public TrailersAdapter(List<Video> videos, OnItemClickListener listener) {
        this.listener = listener;
        this.videos = videos;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private final OnItemClickListener listener;
    private final List<Video> videos;

    String youtubeLink;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trailers_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position, listener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTrailerTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTrailerTitle = (TextView)itemView.findViewById(R.id.tv_trailername);

        }

        public void bind(final int position, final OnItemClickListener listener ){
            String title = videos.get(position).getName();

            tvTrailerTitle.setText(title);
            youtubeLink = "http://www.youtube.com/watch?v="+videos.get(position).getKey();

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(position);
                    itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(youtubeLink)));

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, youtubeLink);
                    itemView.getContext().startActivity(Intent.createChooser(intent, "Share"));
                    Log.d("TAG", "youtube"+youtubeLink);
                    return true;
                }
            });
        }
    }
}
