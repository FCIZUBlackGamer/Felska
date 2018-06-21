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
class NotificationFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<NotiData> dataItems;
    String word = "home";
    String URL = "", user_id;
    Database database;
    Cursor cursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_all_requests);
        dataItems = new ArrayList<NotiData>();
//        Toast.makeText(getActivity(),word+"",Toast.LENGTH_SHORT).show();
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
        URL = "https://felska.000webhostapp.com/GetNotificationTrips.php";

        final int size = dataItems.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                dataItems.remove(0);
            }
//            adapter.notifyItemRangeRemoved(0, size);
        }
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading Data ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest( Request.Method.POST, URL,
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
                            JSONArray jsonArray = jsonObject.getJSONArray("trip_data");
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                //String id, String name, String date, String description, String imageurl, String from_to
                                NotiData item = new NotiData(
                                        object.getString("id"),
                                        object.getString("fname")+" "+object.getString("lname"),
                                        object.getString("image_url")
                                );
                                dataItems.add(item);
                            }
                            recyclerView.setAdapter( new NotificationAdapter(dataItems, new NotificationAdapter.OnItemClickListener() {

                                @Override
                                public void onItemClick(NotiData item) {
//                                    Toast.makeText(getActivity(), "Item Clicked", Toast.LENGTH_LONG).show();
//                                    Intent intent=new Intent(getActivity(),TripDetailsActivity.class);
//                                    intent.putExtra("request_id",item.getId());
//                                    startActivity(intent);
                                }
                            },getActivity()) );
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