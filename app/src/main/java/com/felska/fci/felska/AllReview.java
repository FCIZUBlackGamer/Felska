package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
class AllReview extends Fragment {
    RecyclerView recyclerView;
    ArrayList<ReviewData> dataItems;
    String user_id;
    Database database;
    Cursor cursor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_reviews,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_all_reviews);
        dataItems = new ArrayList<ReviewData>();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        database = new Database(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cursor = database.ShowData();
        while (cursor.moveToNext()){
            user_id = cursor.getString(1);
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Data ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, "https://felska.000webhostapp.com/GetReviews.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String s = URLEncoder.encode(response,"ISO-8859-1");
                            response = URLDecoder.decode(s,"UTF-8");
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("review");
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
//                                String name, String date, String description, String imageurl, float rate
                                try{
                                    ReviewData item = new ReviewData(
                                            object.getString("fname") + " " + object.getString("lname"),
                                            object.getString("review_date"),
                                            object.getString("trip_desc"),
                                            object.getString("image_url"),
                                            Float.parseFloat(object.getString("trip_rate"))
                                    );
                                    dataItems.add(item);
                                }catch (Exception e){
                                    ReviewData item = new ReviewData(
                                            object.getString("fname") + " " + object.getString("lname"),
                                            object.getString("review_date"),
                                            object.getString("trip_desc"),
                                            object.getString("image_url"),
                                            0.0f);
                                    dataItems.add(item);
                                }
                            }
                            recyclerView.setAdapter( new ReviewAdapter(dataItems, getActivity() ));
//                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("user_id",user_id);
                return hashMap;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}
