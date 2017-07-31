package cmu.edu.expenser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LogonActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String str_email;
    private String str_id;
    private String str_firstname;
    private String str_lastname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.usersettings_fragment_login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
//                Intent barcodeDetect = new Intent(MainActivity.this, BackendActivity.class);
//                startActivity(barcodeDetect);
                Log.e("dd", "SUCCESS");

                GraphRequest.newMeRequest( loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            System.out.println("ERROR");
                        } else {
                            System.out.println("Success");
                            try {
                                String jsonresult = String.valueOf(json);
                                System.out.println("JSON Result" + jsonresult);
                                // str_email = json.getString("email");
                                str_id = json.getString("id");
                                str_firstname = json.getString("first_name");
                                str_lastname = json.getString("last_name");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).executeAsync();

                Intent intent = new Intent(LogonActivity.this, MainActivity.class);
                ArrayList<String> myList = new ArrayList<String>();
                myList.add(str_email);
                myList.add(str_id);
                myList.add(str_firstname);
                myList.add(str_lastname);

                Bundle extras = new Bundle();
                intent.putExtra("mylist", myList);
                startActivity(intent);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
