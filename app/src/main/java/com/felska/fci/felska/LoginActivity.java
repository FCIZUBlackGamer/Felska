package com.felska.fci.felska;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    Button sign_in, sign_up;
    EditText email, pass;
    String semail, spass;
    TextView forgetPass;
    Database database;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        forgetPass = findViewById(R.id.login_forget_pass);
        database = new Database(this);
        cursor =  database.ShowData();
        if (cursor.moveToNext()){
            if (cursor.getString(3).equals("1")){
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                semail = email.getText().toString();
                spass = pass.getText().toString();
                if (semail.length()>0 && spass.length()>0) {
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    StringRequest request = new StringRequest(Request.Method.POST, "https://felska.000webhostapp.com/Login.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject object = new JSONObject(response);
                                JSONArray jsonArray = object.getJSONArray("result");
                                final JSONObject object1 = jsonArray.getJSONObject(0);
                                if (object1.getString("response").equals("Ok")) {
                                    String user_id = object1.getString("id");
                                    database.UpdateData("1",user_id,semail,"1");
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                }else {
                                    Toast.makeText(LoginActivity.this, "No Such Email", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof ServerError)
                                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                            else if (error instanceof NetworkError)
                                Toast.makeText(LoginActivity.this, "Bad Network", Toast.LENGTH_SHORT).show();
                            else if (error instanceof TimeoutError)
                                Toast.makeText(LoginActivity.this, "Timeout Error", Toast.LENGTH_SHORT).show();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("email", semail);
                            params.put("password", spass);
                            return params;
                        }
                    };
                    queue.add(request);
                }else {
                    Toast.makeText(LoginActivity.this, "Email And Password Are Required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
