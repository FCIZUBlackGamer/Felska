package com.felska.fci.felska;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
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

import java.util.HashMap;
import java.util.Map;

public class TripDetailsActivity extends Activity {
    Intent intent;
    String trip_id, phone, email, owner_id, user_id, trip_state;
    ImageView trip_image;
    TextView trip_owner_name, trip_time, trip_type, location_from, location_to, time_start, time_end, money, from_detail, to_detail, car_name, car_num;
    Button goon;
    FloatingActionButton review, call;
    ProgressDialog progressDialog;
    Database database;
    Cursor cursor;

    LayoutInflater inflater;
    View alertLayout;
    RatingBar rateUser;
    EditText review_feedback;
    RequestQueue queue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_details);
        intent = getIntent();
        trip_id = intent.getStringExtra("trip_id");

        database = new Database(this);
        cursor = database.ShowData();
        while (cursor.moveToNext()) {
            user_id = cursor.getString(1);
        }
        trip_state = "";
        owner_id = "wait";
        trip_image = findViewById(R.id.user_image);
        goon = findViewById(R.id.goon);
        review = findViewById(R.id.review);
        call = findViewById(R.id.call);
        trip_owner_name = findViewById(R.id.trip_owner_name);
        trip_time = findViewById(R.id.trip_time);
        trip_type = findViewById(R.id.trip_type);
        location_from = findViewById(R.id.location_from);
        location_to = findViewById(R.id.location_to);
        time_start = findViewById(R.id.time_start);
        time_end = findViewById(R.id.time_end);
        car_name = findViewById(R.id.car_name);
        car_num = findViewById(R.id.car_num);
        to_detail = findViewById(R.id.to_details);
        from_detail = findViewById(R.id.from_details);
        money = findViewById(R.id.money);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("loading ...");
        progressDialog.show();
        queue = Volley.newRequestQueue(TripDetailsActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/GetTripsDteails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressDialog.dismiss();
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray jsonArray = object.getJSONArray("trip_data");

                    final JSONObject object1 = jsonArray.getJSONObject(0);

                    owner_id = object1.getString("owner_id");
                    //https://felska.000webhostapp.com/images/1.jpg
                    if (object1.getString("image_url") != null)
                        Picasso.with(TripDetailsActivity.this)
                                .load(object1.getString("image_url"))
                                .into(trip_image);
//                    Toast.makeText(TripDetailsActivity.this, object1.getString("image_url"), Toast.LENGTH_LONG).show();

                    trip_owner_name.setText(object1.getString("fname") + " " + object1.getString("lname"));
                    trip_time.setText(object1.getString("trip_date"));
                    location_from.setText(object1.getString("trip_from_title"));
                    location_to.setText(object1.getString("trip_to_title"));
                    trip_type.setText(object1.getString("trip_type"));
                    from_detail.setText(object1.getString("trip_from_details"));
                    to_detail.setText(object1.getString("trip_to_details"));
                    money.setText("Budget Average ( " + object1.getString("trip_budget_from") + " : " + object1.getString("trip_budget_to") + " )");
                    time_start.setText(object1.getString("trip_start_time"));
                    time_end.setText(object1.getString("trip_end_time"));
                    car_name.setText(object1.getString("car_city"));
                    car_num.setText(object1.getString("car_id"));
                    phone = object1.getString("mobile");
                    email = object1.getString("email");


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(TripDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(TripDetailsActivity.this, "Bad Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(TripDetailsActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TripDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("trip_id", trip_id);
                return params;
            }
        };
        queue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();




//        Toast.makeText(TripDetailsActivity.this, "User Id: "+user_id, Toast.LENGTH_SHORT).show();
//        Toast.makeText(TripDetailsActivity.this, "Owner Id: "+owner_id, Toast.LENGTH_SHORT).show();
        if (!owner_id.equals("wait")) {
            getTripState();
        }

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null)));
            }
        });

        goon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(TripDetailsActivity.this,trip_state,Toast.LENGTH_SHORT).show();
                if (trip_state.equals("Not Found!")) {
                    // Go On trip!
                    StringRequest request2 = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/JoinTrip.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("confirm");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                Toast.makeText(TripDetailsActivity.this, jsonObject1.getString("response"), Toast.LENGTH_SHORT).show();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof ServerError) {
                                Toast.makeText(TripDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(TripDetailsActivity.this, "Bad Network", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
                            } else if (error instanceof TimeoutError) {
                                Toast.makeText(TripDetailsActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
//                                progressDialog.dismiss();
                            } else {
//                                progressDialog.dismiss();
                                Toast.makeText(TripDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_id", user_id);
                            params.put("owner_id", owner_id);
                            return params;
                        }
                    };
                    queue.add(request2);
                } else if (trip_state.equals("1")) {
                    Toast.makeText(TripDetailsActivity.this, "You are Already Member on that trip!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TripDetailsActivity.this, "You had this trip before or just wait a sec .. we're trying to load your state!", Toast.LENGTH_LONG).show();
                }
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final View v) {
                getTripState();
                if (trip_state.contains("2")) {
                    // Make Review
                    inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    alertLayout = inflater.inflate(R.layout.review_feedback, null);
                    review_feedback = alertLayout.findViewById(R.id.review_feedback);
                    rateUser = alertLayout.findViewById(R.id.review_rate_feedback);
                    final AlertDialog.Builder alert = new AlertDialog.Builder(TripDetailsActivity.this);
                    alert.setTitle("Feedback");
                    alert.setView(alertLayout);
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (v != null) {
                                ViewGroup parent = (ViewGroup) v.getParent();
                                if (parent != null) {
                                    parent.removeAllViews();
                                }
                            }
                        }
                    }).setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (review_feedback.getText().toString().length() > 2) {
                                if (rateUser.getRating() >= 0) {
                                    //Upload to server
                                    final ProgressDialog dialog1 = new ProgressDialog(TripDetailsActivity.this);
                                    dialog1.setCancelable(false);
                                    dialog1.setMessage("Please Wait ...");
                                    dialog1.show();
                                    StringRequest request3 = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/MakeReview.php", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                JSONArray jsonArray = jsonObject.getJSONArray("confirm");
                                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                                Toast.makeText(TripDetailsActivity.this, jsonObject1.getString("response"), Toast.LENGTH_SHORT).show();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            if (error instanceof ServerError) {
                                                Toast.makeText(TripDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                                                dialog1.dismiss();
                                            } else if (error instanceof NetworkError) {
                                                Toast.makeText(TripDetailsActivity.this, "Bad Network", Toast.LENGTH_SHORT).show();
                                                dialog1.dismiss();
                                            } else if (error instanceof TimeoutError) {
                                                Toast.makeText(TripDetailsActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                                                dialog1.dismiss();
                                            } else {
                                                dialog1.dismiss();
                                                Toast.makeText(TripDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }) {

                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> params = new HashMap<String, String>();
                                            params.put("user_id", user_id);
                                            params.put("trip_owner_id", owner_id);
                                            params.put("review_rate", rateUser.getRating()+"");
                                            params.put("review_note", review_feedback.getText().toString()+"");
                                            return params;
                                        }
                                    };
                                    queue.add(request3);
                                } else {
                                    Toast.makeText(TripDetailsActivity.this, "Review Rate Can't be Empty!", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(TripDetailsActivity.this, "Review Can't be Empty!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();

                } else if (trip_state.equals("1")) {
                    Toast.makeText(TripDetailsActivity.this, "Please wait until trip owner data is downloaded OR Wait Until Partner Confirm Your request First!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(TripDetailsActivity.this, "Please wait until trip owner data is downloaded OR You Can't Make Review Before Making a trip!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void getTripState(){
        StringRequest request1 = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/GetTripState.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                trip_state = response;
//                    if (response.equals("Not Found!")) {
//                Toast.makeText(TripDetailsActivity.this, "Trip State: " + response, Toast.LENGTH_SHORT).show();

//                    }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof ServerError) {
                    Toast.makeText(TripDetailsActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof NetworkError) {
                    Toast.makeText(TripDetailsActivity.this, "Bad Network", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (error instanceof TimeoutError) {
                    Toast.makeText(TripDetailsActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(TripDetailsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("owner_id", owner_id);
                return params;
            }
        };
        queue.add(request1);
    }
}
