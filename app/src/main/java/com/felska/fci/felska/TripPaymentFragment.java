package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

@SuppressLint("ValidFragment")
class TripPaymentFragment extends Fragment {
    Button back, next;
    EditText payment_from, payment_to;
    String spayment_from, spayment_to;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    TripDatabase tripDatabase;
    Database database;
    Cursor cursor, cursor1;
    ProgressDialog progressDialog;
    String[] data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        payment_from = view.findViewById(R.id.payment_from);
        payment_to = view.findViewById(R.id.payment_to);
        back = view.findViewById(R.id.payment_back);
        next = view.findViewById(R.id.payment_next);
        fragmentManager = getFragmentManager();
        tripDatabase = new TripDatabase(getActivity());
        database = new Database(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        cursor1 = database.ShowData();
        data = new String[14];
        while (cursor1.moveToNext()){
            data[0] = cursor1.getString(1);
        }
        cursor = tripDatabase.ShowData();
        while (cursor.moveToNext()){
            data[1] = cursor.getString(2);
            data[2] = cursor.getString(3);
            data[3] = cursor.getString(4);
            data[4] = cursor.getString(5);
            data[5] = cursor.getString(6);
            data[6] = cursor.getString(7);
            data[7] = cursor.getString(8);
            data[8] = cursor.getString(9);
            data[9] = cursor.getString(10);
            data[10] = cursor.getString(11);
        }
//        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setMessage(s).show();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new TripDetailsFragment();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spayment_from = payment_from.getText().toString();
                spayment_to = payment_to.getText().toString();
//                Toast.makeText(getActivity(),spayment_from+"\n"+spayment_to,Toast.LENGTH_SHORT).show();
                if (spayment_from.length() >= 1) {
                    if (spayment_to.length() >= 1) {
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.setMessage("Please Wait ...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        // Upload to server
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        StringRequest request = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/MakeTrip.php", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
                                if (response.equals("Trip Is Online")) {
                                    fragment = new TripsFragment();
                                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    transaction.replace(R.id.container, fragment).commit();
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
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) {

                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("user_id", data[0]+"");
                                params.put("car_id", data[9]+"");
                                params.put("car_city", data[8]+"");
                                params.put("trip_type", data[1]+"");
                                params.put("trip_from_title", data[2]+"");
                                params.put("trip_from_details", data[3]+"");
                                params.put("trip_to_title", data[4]+"");
                                params.put("trip_to_details", data[5]+"");
                                params.put("trip_start_time", data[6]+"");
                                params.put("trip_end_time", data[7]+"");
                                params.put("trip_budget_from", spayment_from);
                                params.put("trip_budget_to", spayment_to);
                                params.put("other_details", data[10]+"");
                                return params;
                            }
                        };
                        queue.add(request);
                    } else {
                        payment_to.setError("Must Be At Least 1 digits");
                    }
                } else {
                    payment_from.setError("Must Be At Least 1 digits");
                }

            }
        });
    }
}
