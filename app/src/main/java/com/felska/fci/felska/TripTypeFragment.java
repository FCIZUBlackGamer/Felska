package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

@SuppressLint("ValidFragment")
class TripTypeFragment extends Fragment {
    Button trip_type_back, next, car_share;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String state = "";
    private Fragment fragment;
    private FragmentManager fragmentManager;
    TripDatabase tripDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_type, container, false);
        trip_type_back = view.findViewById(R.id.trip_type_back);
        next = view.findViewById(R.id.trip_type_next);
        car_share = view.findViewById(R.id.car_share);
        fragmentManager = getFragmentManager();
        radioGroup = view.findViewById(R.id.trip_type_radio);
        tripDatabase = new TripDatabase(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        trip_type_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new TripsFragment();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
            }
        });

        car_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                state = "Car Share";
                //radioGroup.clearCheck();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                radioButton = (RadioButton) group.findViewById(selectedId);
                if (radioButton.getText().toString().equals("Internal")) {
                    state = "Internal";
                } else if (radioButton.getText().toString().equals("External")) {
                    state = "External";
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!state.equals("")) {
                    // Insert into internal database
                    tripDatabase.Updatetrip_type("1",state);
                    fragment = new TripFromToFragment();
                    final FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.container, fragment).commit();
                } else {
                    Snackbar.make(v, "Must select Trip Type", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    }
}
