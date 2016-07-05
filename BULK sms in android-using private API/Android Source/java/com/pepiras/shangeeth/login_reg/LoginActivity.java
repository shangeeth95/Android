package com.shangeeth.login_reg;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private EditText e_username;
    private EditText e_password;
    public static final String USER_NAME = "USERNAME";
    String username;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //get data from text fields
        e_username = (EditText) findViewById(R.id.username);
        e_password = (EditText) findViewById(R.id.password);
        //Create a Button for toast and link it by its id Id and invoke the Onclick Listener by button id
        Button button_nextpage=(Button)findViewById(R.id.RegisterButton);
        button_nextpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
            //NOTE: Add the activity in Manifest.xml
        });

    }//end of oncreate
    public void invokeLogin(View view){
        username = e_username.getText().toString();
        password = e_password.getText().toString();

        login(username, password);
    }
    private void login(final String username, String password) {

        class LoginAsync extends AsyncTask<String, Void, String> {

            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Loading...");
            }

            @Override
            protected String doInBackground(String... params) {
                String uname = params[0];
                String pass = params[1];

                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", uname));
                nameValuePairs.add(new BasicNameValuePair("password", pass));
                String result = null;
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(
                            "http://localhost/bulk/login.php");  // Replace your local IP with localhost if it does not work 
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    is = entity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                String s = result.trim();
                loadingDialog.dismiss();
                Log.d("FIND","TEST:"+s);
                if(s.equalsIgnoreCase("Please Fill The Fileds")){
                    Toast.makeText(getApplicationContext(), "Please Fill The Fileds", Toast.LENGTH_LONG).show();

                }else{
                    if (s.equalsIgnoreCase("success")) {
                        Intent intent = new Intent(LoginActivity.this,UserActivity.class);
                        intent.putExtra(USER_NAME, username);
                        finish();
                         startActivity(intent);
                        //startActivity(new Intent(LoginActivity.this, UserActivity.class));


                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid User Name or Password", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        LoginAsync la = new LoginAsync();
        la.execute(username, password);

    }

}
