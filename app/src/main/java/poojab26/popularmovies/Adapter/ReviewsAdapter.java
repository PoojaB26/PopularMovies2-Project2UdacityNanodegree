package poojab26.popularmovies.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import poojab26.popularmovies.Model.Review;
import poojab26.popularmovies.R;

/**
 * Created by poojab26 on 29-Jan-18.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {
    public ReviewsAdapter(List<Review> reviews, OnItemClickListener listener) {
        this.reviews = reviews;
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private final List<Review> reviews;
    private final OnItemClickListener listener;


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_recycler_view_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position, listener);

    }

     @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvReviews;

        public ViewHolder(View itemView) {
            super(itemView);
            tvReviews = (TextView)itemView.findViewById(R.id.tv_reviews);
        }

        public void bind(final int position, final OnItemClickListener listener){
            String reviewsContent = reviews.get(position).getContent();
            tvReviews.setText(reviewsContent);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
