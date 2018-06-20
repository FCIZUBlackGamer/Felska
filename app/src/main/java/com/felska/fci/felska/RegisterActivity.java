package com.felska.fci.felska;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    ImageView image;

    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    EditText fname, lname, age, mobile, city, email, bio, pass, conpass;
    String sfname, slname, sage, smobile, scity, semail, sbio, sgender, spass, sconpass;
    Spinner gender;
    Button submit;
    private int image_result = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fname = findViewById(R.id.profile_first_name);
        lname = findViewById(R.id.profile_last_name);
        age = findViewById(R.id.profile_age);
        mobile = findViewById(R.id.profile_mobile);
        pass = findViewById(R.id.profile_pass);
        conpass = findViewById(R.id.profile_con_pass);
        city = findViewById(R.id.profile_city);
        email = findViewById(R.id.profile_email);
        bio = findViewById(R.id.profile_bio);
        gender = findViewById(R.id.profile_gender);
        submit = findViewById(R.id.profile_submit);
        image = findViewById(R.id.profile_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestStoragePermission();
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sfname = fname.getText().toString();
                slname = lname.getText().toString();
                sage = age.getText().toString();
                smobile = mobile.getText().toString();
                scity = city.getText().toString();
                semail = email.getText().toString();
                sbio = bio.getText().toString();
                spass = pass.getText().toString();
                sconpass = conpass.getText().toString();
                sgender = gender.getSelectedItem().toString();
                if (sfname.length() >= 3) {
                    if (slname.length() >= 3) {
                        if (semail.length() >= 6 && semail.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                            if (spass.length() >= 6) {
                                if (sconpass.equals(spass)) {
                                    if (smobile.length() >= 3) {
                                        if (scity.length() >= 3) {
                                            if (sage.length() >= 1) {
                                                if (image_result == 1) {
                                                    try {
                                                        String path = getPath(filePath);
                                                        String uploadId = UUID.randomUUID().toString();
                                                        UploadNotificationConfig config = new UploadNotificationConfig();
                                                        config.setCompletedMessage("Check Login!");
                                                        config.setErrorMessage("Something Went Wrong! ... please try again later");
                                                        //Creating a multi part request
                                                        new MultipartUploadRequest(RegisterActivity.this, uploadId, "https://felska.000webhostapp.com/InsertNewUser.php")
                                                                .addFileToUpload(path, "image") //Adding file
                                                                .addParameter("user_fname", sfname) //Adding text parameter to the request
                                                                .addParameter("user_lname", slname) //Adding text parameter to the request
                                                                .addParameter("user_age", sage) //Adding text parameter to the request
                                                                .addParameter("user_gender", sgender) //Adding text parameter to the request
                                                                .addParameter("user_city", scity) //Adding text parameter to the request
                                                                .addParameter("user_phone", smobile) //Adding text parameter to the request
                                                                .addParameter("user_bio", sbio) //Adding text parameter to the request
                                                                .addParameter("user_password", spass) //Adding text parameter to the request
                                                                .addParameter("user_email", semail) //Adding text parameter to the request
                                                                .setNotificationConfig(new UploadNotificationConfig().setCompletedMessage(""))
                                                                .setMaxRetries(2)
                                                                .startUpload(); //Starting the upload
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    image.setImageResource(R.drawable.ic_person_black_24dp);
                                                    fname.setText("");
                                                    lname.setText("");
                                                    age.setText("");
                                                    mobile.setText("");
                                                    city.setText("");
                                                    email.setText("");
                                                    pass.setText("");
                                                    conpass.setText("");
                                                    bio.setText("");
                                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Image Is Required", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                age.setError("Not Valid Age");
                                            }
                                        } else {
                                            city.setError("Not Valid city");
                                        }
                                    } else {
                                        mobile.setError("Not Valid mobile");
                                    }
                                }else {
                                    conpass.setError("Password Doesn't Match");
                                }
                            }else {
                                pass.setError("Not Valid Password");
                            }
                        } else {
                            email.setError("Not Valid Email");
                        }
                    } else {
                        age.setError("Not Valid name");
                    }
                } else {
                    age.setError("Not Valid name");
                }
            }
        });
    }

    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }


    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Are You Sure To Put This Image Your Profile Image?!")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            image_result = 1;

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            image_result = 0;
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }
    }

    //method to get the file path from uri
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}
