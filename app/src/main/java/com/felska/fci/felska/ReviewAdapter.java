package com.felska.fci.felska;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by USER on 4/11/2018.
 */
public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.VHolder> {
    List<ReviewData> row_items;
    Context context;

    public ReviewAdapter(List<ReviewData> row_items, Context context) {
        this.row_items = row_items;
        this.context = context;
    }

    @Override
    public ReviewAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_row,parent,false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.VHolder holder, int position) {
        final ReviewData noti_item = row_items.get(position);

        holder.name.setText(noti_item.getName());
        holder.date.setText(noti_item.getDate());
        holder.description.setText(noti_item.getDescription());
        holder.ratingBar.setRating(noti_item.getRate());
        Picasso.with(context)
                .load(noti_item.getImageurl())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return row_items.size();
    }

    public class VHolder extends RecyclerView.ViewHolder{
        TextView name, date, description;
        RatingBar ratingBar;
        ImageView imageView;
        public VHolder(View itemView) {
            super( itemView );
            name = (TextView)itemView.findViewById(R.id.review_name);
            date = (TextView)itemView.findViewById(R.id.review_time);
            description = (TextView)itemView.findViewById(R.id.review_description);
            imageView = (ImageView)itemView.findViewById(R.id.review_image);
            ratingBar = (RatingBar) itemView.findViewById(R.id.review_rate);
        }
    }
}
