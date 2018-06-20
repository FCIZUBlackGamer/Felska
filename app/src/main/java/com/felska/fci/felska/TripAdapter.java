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
public class TripAdapter extends RecyclerView.Adapter<TripAdapter.VHolder> {
    List<TripData> row_items;
    private final OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(TripData item);
    }

    public TripAdapter(List<TripData> row_items, OnItemClickListener listener, Context context) {
        this.row_items = row_items;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public TripAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trip_row,parent,false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(TripAdapter.VHolder holder, int position) {
        final TripData noti_item = row_items.get(position);
        holder.bind(row_items.get(position), listener);

        holder.name.setText(noti_item.getName());
        holder.date.setText(noti_item.getDate());
        holder.from_to.setText("From: "+noti_item.getFrom()+"\n"+"To: "+noti_item.getTo());
        holder.description.setText(noti_item.getDescription());
        noti_item.getId();
        Picasso.with(context)
                .load(noti_item.getImageurl())
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return row_items.size();
    }

    public class VHolder extends RecyclerView.ViewHolder{
        TextView name, date, description, from_to;
        ImageView imageView;
        public VHolder(View itemView) {
            super( itemView );
            name = (TextView)itemView.findViewById(R.id.trip_owner_name);
            date = (TextView)itemView.findViewById(R.id.trip_time);
            description = (TextView)itemView.findViewById(R.id.trip_description);
            from_to = (TextView)itemView.findViewById(R.id.trip_from_to);
            imageView = (ImageView)itemView.findViewById(R.id.trip_image);
        }
        public void bind(final TripData item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
