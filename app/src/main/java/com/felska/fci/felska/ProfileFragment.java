package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
class ProfileFragment extends Fragment {
    Database database;
    Cursor cursor;
    String getemail;
    Button back, edit;
    ImageView imageView;
    int state = 0;
    RatingBar ratingBar;
    TextView user_name, reviews, all_reviews;
    EditText fname, lname, age, mobile, gender, city, email, bio;
    ProgressDialog progressDialog;
    RecyclerView recyclerView;
    ArrayList<ReviewData> dataItems;
    String user_id;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    int revew = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = new Database(getActivity());
        back = view.findViewById(R.id.profile_back);
        edit = view.findViewById(R.id.profile_edit_profile);

        ratingBar = view.findViewById(R.id.profile_star);
        user_name = view.findViewById(R.id.profile_name);
        fname = view.findViewById(R.id.profile_first_name);
        lname = view.findViewById(R.id.profile_last_name);
        age = view.findViewById(R.id.profile_age);
        mobile = view.findViewById(R.id.profile_mobile);
        gender = view.findViewById(R.id.profile_gender);
        city = view.findViewById(R.id.profile_city);
        email = view.findViewById(R.id.profile_email);
        bio = view.findViewById(R.id.profile_bio);
        imageView = view.findViewById(R.id.profile_image);

        fragmentManager = getFragmentManager();
        reviews = view.findViewById(R.id.profile_reviews);
        all_reviews = view.findViewById(R.id.profile_all_reviews);
        recyclerView = (RecyclerView) view.findViewById(R.id.profile_list_all_reviews);
        dataItems = new ArrayList<ReviewData>();
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        database = new Database(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        state = 0;
        cursor = database.ShowData();
        while (cursor.moveToNext()) {
            user_id = cursor.getString(1);
            getemail = cursor.getString(2);
        }

        all_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new AllReview();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
            }
        });

        final ProgressDialog progressDialog1 = new ProgressDialog(getActivity());
        progressDialog1.setMessage("Loading Data ...");
        progressDialog1.show();
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
                        progressDialog1.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("review");
                            for (int i=0; i<1; i++){
                                revew ++;
                                JSONObject object = jsonArray.getJSONObject(i);
//                                String name, String date, String description, String imageurl, float rate
                                ReviewData item = new ReviewData(
                                        object.getString("fname")+" "+object.getString("lname"),
                                        object.getString("review_date"),
                                        object.getString("trip_desc"),
                                        object.getString("image_url"),
                                        Float.parseFloat(object.getString("review_date"))
                                );
                                dataItems.add(item);
                            }
                            reviews.setText("Reviews ("+revew+")");
                            recyclerView.setAdapter( new ReviewAdapter(dataItems, getActivity() ));
//                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog1.dismiss();
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

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    state = 1;
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    city.setEnabled(true);
                    age.setEnabled(true);
                    gender.setEnabled(true);
                    bio.setEnabled(true);
                    mobile.setEnabled(true);
                } else if (state == 1) {
                    state = 0;
                    fname.setEnabled(false);
                    lname.setEnabled(false);
                    city.setEnabled(false);
                    age.setEnabled(false);
                    gender.setEnabled(false);
                    bio.setEnabled(false);
                    mobile.setEnabled(false);

                    // upload to server


                }
            }
        });

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading ...");
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/GetUserProfile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("user_data");

                    final JSONObject object1 = jsonArray.getJSONObject(0);
                    if (object1.getString("image_url") !=null)
                    Picasso.with(getActivity())
                            .load(object1.getString("image_url"))
                            .into(imageView);
                    user_name.setText(object1.getString("fname") + " " + object1.getString("lname"));
                    fname.setText(object1.getString("fname"));
                    lname.setText(object1.getString("lname"));
                    mobile.setText(object1.getString("mobile"));
                    email.setText(object1.getString("email"));
                    age.setText(object1.getString("age"));
                    gender.setText(object1.getString("gender"));
                    bio.setText(object1.getString("bio"));
                    city.setText(object1.getString("city"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getActivity(), "Bad Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Timeout Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", getemail);
                return params;
            }
        };

        queue.add(request);

        StringRequest request1 = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/GetRateProfile.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                ratingBar.setRating(Float.parseFloat(response));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(getActivity(), "Bad Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(getActivity(), "Timeout Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", getemail);
                return params;
            }
        };
        queue.add(request1);

    }
}
