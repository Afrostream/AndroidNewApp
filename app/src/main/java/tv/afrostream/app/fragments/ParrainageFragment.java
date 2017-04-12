package tv.afrostream.app.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

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
import java.util.Locale;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 11/04/2017.
 */




public class ParrainageFragment extends Fragment {

    private Toast toast;
    String idcat="";
    String catName="";
    ProgressBar loading_spinner=null;
    public String  TAG=ParrainageFragment.class.getSimpleName();


    private FirebaseAnalytics mFirebaseAnalytics;





    public void getParrainageCode(final EditText txt)
    {
        String urlJsonObj = StaticVar.BaseUrl + "/api/billings/coupons";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("billingProviderName", "afr");
        params.put("couponsCampaignType", "sponsorship");
      //  params.put("couponOpts", "afr");

        loading_spinner.setVisibility(View.VISIBLE);

        if (StaticVar.DevMode)
       params.put("couponsCampaignBillingUuid",StaticVar.CouponUUIDGenStaging);
        else
            params.put("couponsCampaignBillingUuid", StaticVar.CouponUUIDGenProd);


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                loading_spinner.setVisibility(View.GONE);
                try {

                    String code=response.getJSONObject("coupon").getString("code");

                    txt.setText(code);








                } catch (Exception e) {


                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                loading_spinner.setVisibility(View.GONE);
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                try {

                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson = error2.getMessage();
                        JSONObject errorJ = new JSONObject(errorJson);
                        String MessageError = errorJ.getString("error");
                        //FirebaseCrash.log("APIAuth Error :" + MessageError);
                        showToast("Error get code : " + MessageError);

                    }

                } catch (Exception ee) {
                    ee.printStackTrace();
                }


            }


        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer "+StaticVar.access_token);
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //StaticVar.downloadFragment=this;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_parrainage, container, false);






    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



        final MainActivity mainA=(MainActivity)this.getActivity();
        mainA.IsSearchButton=false;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);



        mainA.setSupportActionBar(toolbar);

        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);

        mainA.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainA.getSupportActionBar().setDisplayShowTitleEnabled(true);

        mainA.getSupportActionBar().setTitle(getString(R.string.Parrainage));

        toolbar.setNavigationIcon(R.drawable.ic_menu);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open navigation drawer when click navigation back button

                mainA.drawer.openDrawer(GravityCompat.START);
            }
        });
        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);
        Button bnt_send=(Button)  view.findViewById(R.id.bnt_send_code);
        final EditText txt_code=(EditText)  view.findViewById(R.id.txt_code);

        try{
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        }catch(Exception ee)
        {}


        getParrainageCode(txt_code);

        bnt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txt_code.getText().toString().trim().equals(""))
                {
                    showToast(getString(R.string.parrainage_error));
                    return;
                }

                try {


                    if (mFirebaseAnalytics != null) {

                        Bundle params = new Bundle();
                        params.putString("sponsorship_code", txt_code.getText().toString());



                        mFirebaseAnalytics.logEvent("send_sponsorship_code", params);
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(
                        Intent.EXTRA_TEXT,
                        getString(R.string.text_parrainage) + txt_code.getText().toString() );
                startActivity(Intent.createChooser(send, getString(R.string.share_with)));

            }
        });





    }


}

