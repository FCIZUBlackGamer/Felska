package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint("ValidFragment")
class TripTimeFragment extends Fragment {
    Button from_to_back, next;
    EditText from, to;
    String sfrom, sto;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private DatePickerDialog.OnDateSetListener DatePicker1, DatePicker2;
    TripDatabase tripDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time,container,false);
        from = view.findViewById(R.id.time_start);
        to = view.findViewById(R.id.time_end);
        from_to_back = view.findViewById(R.id.time_back);
        next = view.findViewById(R.id.time_next);
        fragmentManager = getFragmentManager();
        tripDatabase = new TripDatabase(getActivity());
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SimpleDateFormat")
    @Override
    public void onStart() {
        super.onStart();
        from_to_back.setOnClickListener(new View.OnClickListener() {
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
                sfrom = from.getText().toString();
                sto = to.getText().toString();
                if (sfrom.length()>=8){
                    if (sto.length()>=8){
                        // Insert into internal database
//                        Toast.makeText(getActivity(),sfrom+"\n"+sto,Toast.LENGTH_SHORT).show();
                        tripDatabase.Updatetime("1",sfrom,sto);
                        fragment = new TripDetailsFragment();
                        final FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.container, fragment).commit();

                    }else {
                        to.setError("Invalid Date");
                    }
                }else {
                    from.setError("Invalid Date");
                }
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , DatePicker1
                        , year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

            }
        });

        DatePicker1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                from.setText(month + "/" + dayOfMonth + "/" + year);
            }
        };

        to.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , DatePicker2
                        , year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();

            }
        });

        DatePicker2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                to.setText(month + "/" + dayOfMonth + "/" + year);
            }
        };
    }
}
