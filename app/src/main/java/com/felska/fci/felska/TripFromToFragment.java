package com.felska.fci.felska;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

@SuppressLint("ValidFragment")
class TripFromToFragment extends Fragment {
    Button from_to_back, next;
    EditText from_title, from_desc, to_title, to_desc;
    String sfrom_title, sfrom_desc, sto_title, sto_desc;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    TripDatabase tripDatabase;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_from_to, container, false);
        from_to_back = view.findViewById(R.id.from_to_back);
        next = view.findViewById(R.id.from_to_next);
        from_title = view.findViewById(R.id.from_to_from_title);
        from_desc = view.findViewById(R.id.from_to_from_description);
        to_title = view.findViewById(R.id.from_to_to_title);
        to_desc = view.findViewById(R.id.from_to_to_description);
        fragmentManager = getFragmentManager();
        tripDatabase = new TripDatabase(getActivity());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        from_to_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment = new TripTypeFragment();
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, fragment).commit();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfrom_title = from_title.getText().toString();
                sfrom_desc = from_desc.getText().toString();
                sto_title = to_title.getText().toString();
                sto_desc = to_desc.getText().toString();
                if (sfrom_title.length() >= 3) {
                    if (sfrom_desc.length() >= 20) {
                        if (sto_title.length() >= 3) {
                            if (sto_desc.length() >= 20) {
                                // Insert into internal database
//                                Toast.makeText(getActivity(),sfrom_title+"\n"+sfrom_desc+"\n"+sto_title+"\n"+sto_desc,Toast.LENGTH_SHORT).show();
                                tripDatabase.Updatefrom_to("1",sfrom_title,sfrom_desc,sto_title,sto_desc);
//                                cursor = tripDatabase.ShowData();
//                                while (cursor.moveToNext()){
//                                    Log.e("FFFF",cursor.getString(2));
//                                    Log.e("FFFF",cursor.getString(3));
//                                    Log.e("FFFF",cursor.getString(4));
//                                    Log.e("FFFF",cursor.getString(5));
//                                }
                                fragment = new TripTimeFragment();
                                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.container, fragment).commit();
                            } else {
                                to_desc.setError("Must Be At Least 20 digits");
                            }
                        } else {
                            to_title.setError("Must Be At Least 3 digits");
                        }
                    } else {
                        from_desc.setError("Must Be At Least 20 digits");
                    }
                } else {
                    from_title.setError("Must Be At Least 3 digits");
                }
            }
        });

    }
}
