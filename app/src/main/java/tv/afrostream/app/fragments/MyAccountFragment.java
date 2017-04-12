package tv.afrostream.app.fragments;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.utils.SystemUIUtil;

/**
 * Created by bahri on 31/03/2017.
 */

public class MyAccountFragment extends Fragment {

    private Toast toast;
    String idcat="";
    String catName="";
    ProgressBar loading_spinner=null;
    public String  TAG=MyAccountFragment.class.getSimpleName();
    RecyclerView rc;
    Button bnt_cancel_subscription;
    TextView txtcancel;
    CountryCodePicker txtPhone;
    EditText txtFirstname;
    EditText txtLastname;
    EditText txtDateN;
    EditText txtAdresse;
    EditText txtVille;
    com.countrypicker.CountryCodePicker txtPays;
    RadioButton chkMen;
    RadioButton chkWomen;
    String DateStart="";
    Button bnt_save;

    private FirebaseAnalytics mFirebaseAnalytics;

    DatePickerDialog.OnDateSetListener ondatestart = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            try{
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String nn=sdf.format(newDate.getTime()).toString();
                txtDateN.setText(nn);
                DateStart=nn;

            }catch (Exception ee)
            {
                ee.printStackTrace();
            }


        }
    };

    private void setDateTimeStart() {

        try {
            DatePickerFragment date = new DatePickerFragment();
            /**
             * Set Up Current Date Into dialog
             */
            Calendar calender = Calendar.getInstance();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                calender.setTime(sdf.parse(DateStart));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }// all done

            Bundle args = new Bundle();
            args.putInt("year", calender.get(Calendar.YEAR));
            args.putInt("month", calender.get(Calendar.MONTH));
            args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));


            date.setArguments(args);
            /**
             * Set Call back to capture selected date
             */
            date.setCallBack(ondatestart);
            date.show(getActivity().getSupportFragmentManager(), "Date Picker");
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

    }


    private void makeUpdateInfoUser(final String access_token, String Firstname, String Lastname,String DateN,String Phone,String Country,String City,String Address,String Gender) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }



        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me";


        JSONObject params=new JSONObject();
        try {
            params.put("first_name", Firstname);
            params.put("last_name", Lastname);
            params.put("telephone", Phone);
            params.put("birthDate", DateN);
            params.put("postalAddressCountry", Country);
            params.put("postalAddressCity", City);
            params.put("postalAddressStreet", Address);
            params.put("gender", Gender);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,urlJsonObj,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("test",response.toString());
                        showToast(getString(R.string.save_ok));

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                error.printStackTrace();
                //  VolleyLog.d(TAG, "Error: " + error.getMessage());
                try {
                    if(error.networkResponse != null && error.networkResponse.data != null){
                        VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                        String errorJson=error2.getMessage();
                        JSONObject errorJ=new JSONObject(errorJson);
                        String MessageError=errorJ.getString("error");
                        showToast("Error update user info: " + MessageError);

                    }
                }catch (Exception ee) {
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
                headers.put("Authorization", "Bearer "+access_token);
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(req);






    }


    private void makeGetUserImage(String url, final ImageView mImageView) {


        ImageLoader mImageLoader;

        RequestQueue mRequestQueue= AppController.getInstance().getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

        });

        mImageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {

                mImageView.setImageBitmap(response.getBitmap());


            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });



    }

public void CancelSubscription(final String subscriptionUuid)
{
    String urlJsonObj = StaticVar.BaseUrl + "/api/billings/subscriptions/"+subscriptionUuid+"/cancel";

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("cancel", "");

    JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
            urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

        @Override
        public void onResponse(JSONObject response) {
            Log.d(TAG, response.toString());

            try {

                bnt_cancel_subscription.setVisibility(View.GONE);
                StaticVar.mainAct.makeGetUserInfo(StaticVar.access_token);

                showToast(getString(R.string.canceled_subscription));

                txtcancel.setVisibility(View.VISIBLE);

                try {


                    if (mFirebaseAnalytics != null) {

                        Bundle params = new Bundle();
                        params.putString("subscriptionUuid", subscriptionUuid);



                        mFirebaseAnalytics.logEvent("cancel_subscription", params);
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }








            } catch (Exception e) {


                e.printStackTrace();

            }

        }
    }, new Response.ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.d(TAG, "Error: " + error.getMessage());

            try {

                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                    String errorJson = error2.getMessage();
                    JSONObject errorJ = new JSONObject(errorJson);
                    String MessageError = errorJ.getString("error");
                    //FirebaseCrash.log("APIAuth Error :" + MessageError);
                    showToast("Error: " + MessageError);

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
        return inflater.inflate(R.layout.fragment_my_account, container, false);






    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {



        final MainActivity mainA=(MainActivity)this.getActivity();
        mainA.IsSearchButton=false;
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {


            }catch (Exception ee)
            {
                ee.printStackTrace();
            }
        }

        mainA.setSupportActionBar(toolbar);

        loading_spinner=(ProgressBar) view.findViewById(R.id.loading_spinner);

        mainA.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mainA.getSupportActionBar().setDisplayShowTitleEnabled(true);

        mainA.getSupportActionBar().setTitle(getString(R.string.moncompte));

        toolbar.setNavigationIcon(R.drawable.ic_menu);
        //toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open navigation drawer when click navigation back button

                mainA.drawer.openDrawer(GravityCompat.START);
            }
        });

        TextView txtNomPlan=(TextView)view.findViewById(R.id.txtNomPlan);

        TextView txtPlanPrice=(TextView)view.findViewById(R.id.txtPlanPrice);
        TextView txtDateDebut=(TextView)view.findViewById(R.id.txtDateDebut);
        TextView txtDateFin=(TextView)view.findViewById(R.id.txtDateFin);
        TextView navName=(TextView)view.findViewById(R.id.txtnavname);
        TextView navEmail=(TextView)view.findViewById(R.id.txtnavemail);


         txtFirstname=(EditText) view.findViewById(R.id.txtFirstname);
         txtLastname=(EditText) view.findViewById(R.id.txtLastname);
         txtDateN=(EditText) view.findViewById(R.id.txtDateN);
         txtAdresse=(EditText) view.findViewById(R.id.txtAdresse);
         txtVille=(EditText) view.findViewById(R.id.txtVille);
         txtPays=(com.countrypicker.CountryCodePicker) view.findViewById(R.id.txtPays);
        chkMen=(RadioButton) view.findViewById(R.id.radio_homme);
        chkWomen=(RadioButton) view.findViewById(R.id.radio_femme);
        bnt_save=(Button)  view.findViewById(R.id.bnt_save);

         txtcancel=(TextView)view.findViewById(R.id.txtcancel);
        ImageView navUserPic=(ImageView)view.findViewById(R.id.userimageView);



         bnt_cancel_subscription=(Button)view.findViewById(R.id.bnt_cancel_subscription);

        txtPhone=(CountryCodePicker) view.findViewById(R.id.txtCountryPhone);

        String Language= Locale.getDefault().getLanguage().toUpperCase();

        if (Language.equals("FR")) {
            txtPays.changeLanguage(com.countrypicker.CountryCodePicker.Language.FRENCH);
            txtPhone.changeLanguage(CountryCodePicker.Language.FRENCH);
        }


        SimpleDateFormat formatDateN = new SimpleDateFormat("yyyy-MM-dd");

        txtPhone.setFullNumber(StaticVar.user_phone);
        txtFirstname.setText(StaticVar.user_first_name);
        txtLastname.setText(StaticVar.user_last_name);
        try {
            Date txtDateND = formatDateN.parse(mainA.subPeriodStartedDate);
            txtDateN.setText(formatDateN.format(txtDateND).toString());
            DateStart=formatDateN.format(txtDateND).toString();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
        txtVille.setText(StaticVar.user_postalAddressCity);
        txtAdresse.setText(StaticVar.user_address);

        try {
            if (!StaticVar.user_postalAddressCountry.equals(""))txtPays.setCountryForNameCode(StaticVar.user_postalAddressCountry.toUpperCase());
        }catch (Exception ee){}

        if (StaticVar.user_gender.equals("men"))
        {
            chkMen.setChecked(true);

        }else if (StaticVar.user_gender.equals("women"))
        {
            chkWomen.setChecked(true);
        }

        txtDateN.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setDateTimeStart();
            }
        });

        if (StaticVar.facebook_image_profil_url.equals(""))
            makeGetUserImage(StaticVar.user_picture_url,navUserPic);
        else
            makeGetUserImage(StaticVar.facebook_image_profil_url,navUserPic);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

        String date1="";
        String date2="";
        try {
            Date txtDate1 = format.parse(mainA.subPeriodStartedDate);
            Date txtDate2 = format.parse(mainA.subPeriodEndsDate);
            date1=format2.format(txtDate1).toString();
            date2=format2.format(txtDate2).toString();


        } catch (Exception e) {

            e.printStackTrace();
        }


        navName.setText(StaticVar.user_first_name +" "+StaticVar.user_last_name);
        navEmail.setText(StaticVar.user_email);


            txtNomPlan.setText(mainA.PlanName);
        txtPlanPrice.setText(mainA.PlanAmount +" "+mainA.PlanCurrency);
        txtDateDebut.setText(date1);
        txtDateFin.setText(date2);


        if (StaticVar.Subscription_isCancelable.equals("yes")) {
            bnt_cancel_subscription.setVisibility(View.VISIBLE);
            txtcancel.setVisibility(View.GONE);

        }else
        {
            bnt_cancel_subscription.setVisibility(View.GONE);
            txtcancel.setVisibility(View.VISIBLE);

        }


        try{
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        }catch(Exception ee)
        {}


        bnt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtFirstname.getText().toString().trim().equals("") )
                {
                  showToast(getString(R.string.please_fill_firstname));
                    return;
                }
                if (txtLastname.getText().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_lastname));
                    return;
                }

                if (txtDateN.getText().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_birthday));
                    return;
                }
                if (txtPays.getSelectedCountryCode().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_country));
                    return;
                }

                if (txtAdresse.getText().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_address));
                    return;
                }
                if (txtVille.getText().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_city));
                    return;
                }
                if (txtPhone.getFullNumber().toString().trim().equals("") )
                {
                    showToast(getString(R.string.please_fill_phone));
                    return;
                }

                if (chkMen.isChecked()==false && chkWomen.isChecked()==false)
                {
                    showToast(getString(R.string.please_fill_genre));
                    return;
                }

                String genre="";
                if (chkMen.isChecked())
                    genre="men";
                            else
                    genre="women";

                String country=txtPays.getSelectedCountryNameCode().toString();

                makeUpdateInfoUser(StaticVar.access_token,txtFirstname.getText().toString(),txtLastname.getText().toString(),txtDateN.getText().toString(),txtPhone.getFullNumber().toString()
                        ,country,txtVille.getText().toString(),txtAdresse.getText().toString(),genre);

            }
        });

        txtcancel.setText(getString(R.string.subsription_cancelled)+" "+date2);
        final String finalDate2 = date2;
        bnt_cancel_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.cancel_title_subscription)
                        .content( getActivity().getString(R.string.cancel_question_subscription )+ finalDate2)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .show();

                View positive = dialog.getActionButton(DialogAction.POSITIVE);


                positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        try {

                            CancelSubscription(StaticVar.Subscription_subscriptionBillingUuid);
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }





                        dialog.dismiss();

                    }
                });

            }
        });



    }


}
