package com.felska.fci.felska;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import im.amomo.circularimageview.CircularImageView;

/**
 * Created by USER on 4/11/2018.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VHolder> {
    List<NotiData> row_items;
    private final OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(NotiData item);
    }

    public NotificationAdapter(List<NotiData> row_items, OnItemClickListener listener, Context context) {
        this.row_items = row_items;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public NotificationAdapter.VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(NotificationAdapter.VHolder holder, int position) {
        final NotiData noti_item = row_items.get(position);
        holder.bind(row_items.get(position), listener);

        try {
            holder.name.setText(noti_item.getName());
//        noti_item.getId();
            Picasso.with(context)
                    .load(noti_item.getImageurl())
                    .into(holder.imageView);
        }catch (Exception e){
            Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        holder.ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, noti_item.getId(), Toast.LENGTH_SHORT).show();

                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading Data ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/AnswerReview.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String s = URLEncoder.encode(response, "ISO-8859-1");
                                    response = URLDecoder.decode(s, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", noti_item.getId());
                        hashMap.put("state", "1");
                        return hashMap;
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, noti_item.getId(), Toast.LENGTH_SHORT).show();
                final ProgressDialog progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Loading Data ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/AnswerReview.php",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String s = URLEncoder.encode(response, "ISO-8859-1");
                                    response = URLDecoder.decode(s, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", noti_item.getId());
                        hashMap.put("state", "0");
                        return hashMap;
                    }
                };
                Volley.newRequestQueue(context).add(stringRequest);
            }
        });

    }

    @Override
    public int getItemCount() {
        return row_items.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView name;
        CircularImageView imageView;
        Button ok, cancel;

        public VHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.full_name);
            imageView = (CircularImageView) itemView.findViewById(R.id.noti_image);
            ok = itemView.findViewById(R.id.ok);
            cancel = itemView.findViewById(R.id.no);
        }

        public void bind(final NotiData item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
