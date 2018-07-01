package com.felska.fci.felska;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import im.amomo.circularimageview.CircularImageView;

import static android.app.Activity.RESULT_OK;

@SuppressLint("ValidFragment")
class ProfileFragment extends Fragment {
    Database database;
    Cursor cursor;
    String getemail;
    Button back, edit, done;
    CircularImageView imageView;
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

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        database = new Database(getActivity());
        back = view.findViewById(R.id.profile_back);
        edit = view.findViewById(R.id.profile_edit_profile);
        done = view.findViewById(R.id.profile_done_profile);

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

        done.setVisibility(View.GONE);
//        edit.setVisibility(View.GONE);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    state = 1;
                    requestStoragePermission();
                    fname.setEnabled(true);
                    lname.setEnabled(true);
                    city.setEnabled(true);
                    age.setEnabled(true);
                    gender.setEnabled(true);
                    bio.setEnabled(true);
                    mobile.setEnabled(true);
                    done.setVisibility(View.VISIBLE);
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
//                        }
//                    });
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 1){
                    // upload to server
                        fname.setEnabled(false);
                        lname.setEnabled(false);
                        city.setEnabled(false);
                        age.setEnabled(false);
                        gender.setEnabled(false);
                        bio.setEnabled(false);
                        mobile.setEnabled(false);
                        done.setVisibility(View.GONE);

//                    try {
//                        String path = getPath(filePath);
//                        if (path == null)
//                            path = "1";
//                        String uploadId = UUID.randomUUID().toString();
//
//                        //Creating a multi part request
//                        new MultipartUploadRequest(getActivity(), uploadId, "https://felska.000webhostapp.com/UpdateProfileInfo.php")
//                                .addFileToUpload(path, "image") //Adding file
//                                .addParameter("user_fname", fname.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_lname", lname.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_age", age.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_gender", gender.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_city", city.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_phone", mobile.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_bio", bio.getText().toString()) //Adding text parameter to the request
//                                .addParameter("user_email", email.getText().toString()) //Adding text parameter to the request
//                                .setNotificationConfig(new UploadNotificationConfig())
//                                .setMaxRetries(2)
//                                .startUpload(); //Starting the upload
//                        state = 0;
//
//                    } catch (Exception exc) {
//                        Toast.makeText(getActivity(), "Here", Toast.LENGTH_SHORT).show();
//                    }
                    final ProgressDialog progressDialog2 = new ProgressDialog(getActivity());
                    progressDialog2.setMessage("Please Wait ...");
                    progressDialog2.setCancelable(false);
                    progressDialog2.show();
                    RequestQueue queue = Volley.newRequestQueue(getActivity());
                    StringRequest request = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/UpdateProfileInfo.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressDialog2.dismiss();

                            state = 0;
                            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof ServerError) {
                                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                                progressDialog2.dismiss();
                            } else if (error instanceof NetworkError) {
                                Toast.makeText(getActivity(), "Bad Network", Toast.LENGTH_SHORT).show();
                                progressDialog2.dismiss();
                            } else if (error instanceof TimeoutError) {
                                Toast.makeText(getActivity(), "Timeout Error", Toast.LENGTH_SHORT).show();
                                progressDialog2.dismiss();
                            } else {
                                progressDialog2.dismiss();
                                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("user_fname", fname.getText().toString());
                            params.put("user_lname", lname.getText().toString());
                            params.put("user_age", age.getText().toString());
                            params.put("user_gender", gender.getText().toString());
                            params.put("user_city", city.getText().toString());
                            params.put("user_phone", mobile.getText().toString());
                            params.put("user_bio", bio.getText().toString());
                            params.put("user_email", email.getText().toString());
                            return params;
                        }
                    };

                    queue.add(request);

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
                    try{
                        Picasso.with(getActivity())
                                .load(object1.getString("image_url"))
                                .into(imageView);
                    }catch (Exception e){
//                        Toast.makeText()
                    }
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

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(getActivity(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(), "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                try {
                    String path = getPath(filePath);
                    if (path == null)
                        path = "1";
                    String uploadId = UUID.randomUUID().toString();

                    //Creating a multi part request
                    new MultipartUploadRequest(getActivity(), uploadId, "https://felska.000webhostapp.com/UpdateProfileImage.php")
                            .addFileToUpload(path, "image") //Adding file
//                            .addParameter("user_fname", fname.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_lname", lname.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_age", age.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_gender", gender.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_city", city.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_phone", mobile.getText().toString()) //Adding text parameter to the request
//                            .addParameter("user_bio", bio.getText().toString()) //Adding text parameter to the request
                            .addParameter("user_email", email.getText().toString()) //Adding text parameter to the request
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload(); //Starting the upload
                }catch (Exception e){

                }
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
