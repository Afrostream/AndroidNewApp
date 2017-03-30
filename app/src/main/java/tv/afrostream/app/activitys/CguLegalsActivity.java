package tv.afrostream.app.activitys;


import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;

import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.crash.FirebaseCrash;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 30/03/2017.
 */

public class CguLegalsActivity extends AppCompatActivity {

    TextView txtcgu;
    ProgressBar loading_spinner;
    public String  TAG=CguLegalsActivity.class.getSimpleName();
    private Toast toast;

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private  void OnResponseCguLegals(JSONObject response)
    {


        try {

            loading_spinner.setVisibility(View.GONE);

            String txt=response.getString("html");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                txtcgu.setText(Html.fromHtml(txt));
            }
            else
            {
                txtcgu.setText(Html.fromHtml(txt, Html.FROM_HTML_MODE_COMPACT));

            }








        } catch (Exception e) {


            e.printStackTrace();
            showToast("Error: " + e.getMessage());
        }
        loading_spinner.setVisibility(View.GONE);

    }


    private void makeGetCgu() {





        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/cgu"+StaticVar.ApiUrlParams;




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");


                loading_spinner.setVisibility(View.GONE);

                JSONObject response=new JSONObject(data) ;

                OnResponseCguLegals(response);


            } catch (Exception e) {
                loading_spinner.setVisibility(View.GONE);
                e.printStackTrace();
                FirebaseCrash.log(e.getMessage() +" -- "+e.getStackTrace());
            }
        }
        else {



            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    loading_spinner.setVisibility(View.GONE);

                    OnResponseCguLegals(response);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    loading_spinner.setVisibility(View.GONE);




                    try {
                        if(error.networkResponse != null && error.networkResponse.data != null){
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson=error2.getMessage();
                            JSONObject errorJ=new JSONObject(errorJson);
                            String MessageError=errorJ.getString("error");

                            showToast("Error: " + MessageError);


                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                }



            } ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer "+StaticVar.access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);

        }


    }







    private void makeGetLegals() {





        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/legals"+StaticVar.ApiUrlParams;




        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(urlJsonObj);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");


                loading_spinner.setVisibility(View.GONE);

                JSONObject response=new JSONObject(data) ;

                OnResponseCguLegals(response);


            } catch (Exception e) {
                loading_spinner.setVisibility(View.GONE);
                e.printStackTrace();
                FirebaseCrash.log(e.getMessage() +" -- "+e.getStackTrace());
            }
        }
        else {



            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                    urlJsonObj, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    loading_spinner.setVisibility(View.GONE);

                    OnResponseCguLegals(response);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    loading_spinner.setVisibility(View.GONE);




                    try {
                        if(error.networkResponse != null && error.networkResponse.data != null){
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson=error2.getMessage();
                            JSONObject errorJ=new JSONObject(errorJson);
                            String MessageError=errorJ.getString("error");

                            showToast("Error: " + MessageError);


                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                }



            } ) {

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer "+StaticVar.access_token);
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);

        }


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_cgu_legals);

        txtcgu=(TextView)findViewById(R.id.txtcgu);
        loading_spinner=(ProgressBar) findViewById(R.id.loading_spinner);


        if (this.getIntent().getExtras()!=null) {
            String typetxt = this.getIntent().getExtras().getString("typetxt");


            if (typetxt.equals("cgu"))
            {

                makeGetCgu();

                }  else   {

                makeGetLegals();

            }
        }





    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:
                onBackPressed();
                return true;






            default:

                return super.onOptionsItemSelected(item);

        }
    }
}
