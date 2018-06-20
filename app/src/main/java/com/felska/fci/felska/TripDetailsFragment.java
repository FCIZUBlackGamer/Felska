package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressLint("ValidFragment")
class TripDetailsFragment extends Fragment {
    Button back, next;
    EditText detail_car_name, detail_car_serial, detail_description;
    String sdetail_car_name, sdetail_car_serial, sdetail_description;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    TripDatabase tripDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        detail_car_name = view.findViewById(R.id.detail_car_name);
        detail_car_serial = view.findViewById(R.id.detail_car_serial);
        detail_description = view.findViewById(R.id.detail_description);
        back = view.findViewById(R.id.detail_back);
        next = view.findViewById(R.id.detail_next);
        fragmentManager = getFragmentManager();
        tripDatabase = new TripDatabase(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new TripFromToFragment();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sdetail_car_name = detail_car_name.getText().toString();
                sdetail_car_serial = detail_car_serial.getText().toString();
                sdetail_description = detail_description.getText().toString();
                Toast.makeText(getActivity(),sdetail_car_name+"\n"+sdetail_car_serial+"\n"+sdetail_description,Toast.LENGTH_SHORT).show();
                if (sdetail_car_name.length() >= 5) {
                    if (sdetail_car_serial.length() >= 6) {
                        if (sdetail_description.length() >= 20) {
                            // Insert into internal database
                            tripDatabase.Updatedetails("1",sdetail_car_name,sdetail_car_serial,sdetail_description);
                            fragment = new TripPaymentFragment();
                            final FragmentTransaction transaction = fragmentManager.beginTransaction();
                            transaction.replace(R.id.container, fragment).commit();
                        } else {
                            detail_description.setError("Must Be At Least 20 digits");
                        }
                    } else {
                        detail_car_serial.setError("Must Be At Least 6 digits");
                    }
                } else {
                    detail_car_name.setError("Must Be At Least 5 digits");
                }
            }
        });
    }
}
