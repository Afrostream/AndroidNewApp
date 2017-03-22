package tv.afrostream.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.BuildConfig;
import tv.afrostream.app.R;

/**
 * Created by bahri on 30/01/2017.
 */


public class CreateUserActivity extends AppCompatActivity {

    EditText txtFirstname;
    EditText txtLastname;
    EditText txtEmail;
EditText txtPhone;
    EditText txtPassword;
    EditText txtRetypePassword;
    EditText telephone;
    Button bntCreateUser;
    Button bntCancel;

    private Toast toast;

    ProgressBar loading_spinner=null;
    public String  TAG=CreateUserActivity.class.getSimpleName();

    ImageView logo;


    public static final String MyPREFERENCES = "Prefs" ;
    public static final String usernamePref = "username";
    public static final String passwordPref = "password";

    private FirebaseAnalytics mFirebaseAnalytics;

    SharedPreferences sharedpreferences;


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }



    private void makeLogin(final String Email, final String Password, final String Firstname, final String Lastname,final String telephone) {





        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {

                    loading_spinner.setVisibility(View.GONE);


                    String access_token="";
                    access_token=response.getString("access_token");
                    String refresh_token="";
                    refresh_token=response.getString("refresh_token");
                    String expires_in="";
                    expires_in=response.getString("expires_in");
                    String token_type="";
                    token_type=response.getString("token_type");

                    StaticVar.access_token=access_token;
                    StaticVar.refresh_token=refresh_token;
                    StaticVar.expires_in=expires_in;
                    StaticVar.token_type=token_type;






                    makeCreateUser(access_token,Email,Password,Firstname,Lastname,telephone);







                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                loading_spinner.setVisibility(View.GONE);
                try{

                        showToast(error.getMessage());

                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void makeCreateUser(final String access_token, final String Email, String Password, final String Firstname, final String Lastname,final String telephone) {


        if (Email.equals("") || Password.equals(""))
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/";

        HashMap<String, String> params = new HashMap<String, String>();
      //  params.put("grant_type", "password");
      //  params.put("client_id", StaticVar.clientApiID);
      //  params.put("client_secret", StaticVar.clientSecret);
        params.put("email",Email);
        params.put("password", Password);
        params.put("first_name",Firstname);
        params.put("last_name", Lastname);
        params.put("telephone", telephone);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {

                    loading_spinner.setVisibility(View.GONE);




                    String access_token="";
                    access_token=response.getString("access_token");

                    String expires_in="";
                    expires_in=response.getString("expires_in");


                    StaticVar.access_token=access_token;

                    StaticVar.expires_in=expires_in;





                    try {


                        if (mFirebaseAnalytics != null) {

                            Bundle params = new Bundle();
                            params.putString("status", "finish");
                            params.putString("user", Email);
                            params.putString("Firstname", Firstname);
                            params.putString("Lastname", Lastname);



                            mFirebaseAnalytics.logEvent("sign_up", params);
                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                  SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString(usernamePref, Email);

                    editor.putString("access_token", access_token);



                    editor.commit();




                    final Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);

                    startActivity(intent);
                    CreateUserActivity.this.finish();


                } catch (Exception e) {

                    loading_spinner.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                loading_spinner.setVisibility(View.GONE);
                try{


                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");
                        showToast("Error: " + MessageError);

                    }



                }catch(Exception ee){ee.printStackTrace();}


            }



        } ) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization", "Bearer "+access_token);
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }*/

        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_create_user);


        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);
        txtFirstname=(EditText)this.findViewById(R.id.firstname);
        txtLastname=(EditText)this.findViewById(R.id.lastname);
        txtEmail=(EditText)this.findViewById(R.id.email);
        txtPassword=(EditText)this.findViewById(R.id.password);
        txtRetypePassword=(EditText)this.findViewById(R.id.retypepassword);


        txtPhone=(EditText)this.findViewById(R.id.phone);

        bntCreateUser=(Button)this.findViewById(R.id.bnt_create_user);

        bntCancel=(Button)this.findViewById(R.id.bnt_cancel);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        try {


            if (mFirebaseAnalytics != null) {

                Bundle params = new Bundle();
                params.putString("status", "begin");



                mFirebaseAnalytics.logEvent("sign_up", params);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }





        bntCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname=txtFirstname.getText().toString();
                String lastname=txtLastname.getText().toString();
                String email=txtEmail.getText().toString();
                String password=txtPassword.getText().toString();
                String retypepassword=txtRetypePassword.getText().toString();
                String tel=txtPhone.getText().toString();

                if (firstname.equals("") ||lastname.equals("") ||email.equals("") ||password.equals("") ||retypepassword.equals("") )
                {
                    showToast(getString(R.string.checkforms) );
                    return;
                }

                if (!password.equals(retypepassword))
                {
                    showToast(getString(R.string.checkpassword) );
                    return;
                }


                if (tel.equals(""))
                {
                    showToast("Veuillez saisir votre numéro de téléphone");
                    return;
                }


                makeLogin(email,password,firstname,lastname,tel);



            }
        });

        bntCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateUserActivity.this.finish();
            }
        });

        txtFirstname.requestFocus();


        if (this.getIntent().getExtras()!=null) {
            String firstname = this.getIntent().getExtras().getString("firstanme");
            String lastname = this.getIntent().getExtras().getString("lastname");
            String email = this.getIntent().getExtras().getString("email");

            if (email!="" && email!="null")
            {
                txtEmail.setText(email);
                txtFirstname.setText(firstname);

                txtLastname.setText(lastname);
                txtPhone.requestFocus();
            }
        }



    }
}
