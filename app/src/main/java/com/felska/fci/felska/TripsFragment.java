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
class TripsFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<TripData> dataItems;
    String word = "home";
    String URL = "", user_id;
    Database database;
    Cursor cursor;


    static TripsFragment pass(String x) {
        TripsFragment tripsFragment = new TripsFragment();
        tripsFragment.word = x;
        return tripsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_all_trips);
        dataItems = new ArrayList<TripData>();
//        Toast.makeText(getActivity(),word+"",Toast.LENGTH_SHORT).show();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        database = new Database(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cursor = database.ShowData();
        while (cursor.moveToNext()) {
            user_id = cursor.getString(1);
        }
        if (word.equals("home")) {
            URL = "https://felska.000webhostapp.com/GetTrips.php";
        } else if (word.equals("mine")) {
            URL = "https://felska.000webhostapp.com/GetMyTrips.php";
        } else if (word.equals("new")) {
            URL = "https://felska.000webhostapp.com/GetNewTrips.php";
        } else {
            URL = "https://felska.000webhostapp.com/GetTripsWithSearch.php";
        }
        final int size = dataItems.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                dataItems.remove(0);
            }
//            adapter.notifyItemRangeRemoved(0, size);
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        Toast.makeText(getActivity(), word, Toast.LENGTH_SHORT).show();
        progressDialog.setMessage("Loading Data ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("trip_data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                //String id, String name, String date, String description, String imageurl, String from_to
                                TripData item = new TripData(
                                        object.getString("id"),
                                        object.getString("fname") + " " + object.getString("lname"),
                                        object.getString("trip_date"),
                                        object.getString("trip_from_details"),
                                        object.getString("image_url"),
                                        object.getString("trip_from_title"),
                                        object.getString("trip_to_title")
                                );
                                dataItems.add(item);
                            }
                            recyclerView.setAdapter(new TripAdapter(dataItems, new TripAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(TripData item) {
//                                    Toast.makeText(getActivity(), "Item Clicked", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity(), TripDetailsActivity.class);
                                    intent.putExtra("trip_id", item.getId());
                                    startActivity(intent);
                                }
                            }, getActivity()));
//                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("user_id", user_id);
                hashMap.put("word", word);
                return hashMap;
            }
        };
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}