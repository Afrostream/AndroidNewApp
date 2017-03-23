package tv.afrostream.app.activitys;


import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.vending.billing.IInAppBillingService;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.api.GoogleApiClient;


import com.google.android.gms.common.api.Status;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.adapters.ListviewSubcriptionAdapter;
import tv.afrostream.app.models.ListPlansModel;

/**
 * Created by bahri on 01/02/2017.
 */

public class PaymentActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // You will need to use your live API key even while testing

    public String  TAG=PaymentActivity.class.getSimpleName();
    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;
    CheckBox chkCGU;
    CheckBox chkPay;
    TextView bntPayCondition;
    TextView bntCgu;


    private final int CREATE_NEW_CARD = 0;
    private Toast toast;
    ListView lst;
    ProgressBar loading_spinner=null;
int ListPosition=0;

    Button bntValider;
    Button bnt_google_pay;

    String user_last_name;
    String user_first_name;
    private IntentIntegrator qrScan;

    public FirebaseAnalytics mFirebaseAnalytics;

    ArrayList<ListPlansModel> lstPlans;

    ListPlansModel selectPlan=null;
    MaterialDialog dialog=null;

    IInAppBillingService mService;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };



    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mService != null) {
            unbindService(mServiceConn);
        }
    }

    public void DoResponseGetListPlans(JSONArray response, ListView lst){




        try {

            // Menu mn=  navigationView.getMenu();
            // mn.clear();

          lstPlans = new ArrayList<ListPlansModel>();

            lstPlans.add(new ListPlansModel( "coupon","0",  "0",  getString(R.string.coupon),  getString(R.string.coupondescription),  "",  "",  "",true,false,"afrostream","afrotream"));


            for (int i = 0; i < response.length(); i++) {

                JSONObject movie = (JSONObject) response.get(i);



                String internalPlanUuid=movie.getString("internalPlanUuid");
                String amountInCents=movie.getString("amountInCents");
                String name=movie.getString("name");
                String amount=movie.getString("amount");
                String description=movie.getString("description");
                String currency=movie.getString("currency");
                String periodUnit=movie.getString("periodUnit");
                String periodLength=movie.getString("periodLength");



                JSONObject providerPlans=movie.getJSONObject("providerPlans");

                Boolean isCouponCodeCompatible=false;
                String providerPlanUuid="";
                String providerName="";
                try {

                    JSONObject stripe = providerPlans.getJSONObject("stripe");

                     isCouponCodeCompatible = stripe.getBoolean("isCouponCodeCompatible");
                     providerPlanUuid=stripe.getString("providerPlanUuid");

                    providerName="stripe";

                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }


                try {

                    JSONObject google = providerPlans.getJSONObject("google");

                     isCouponCodeCompatible = google.getBoolean("isCouponCodeCompatible");
                     providerPlanUuid=google.getString("providerPlanUuid");
                    providerName="google";

                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



                lstPlans.add(new ListPlansModel( internalPlanUuid,amount,  amountInCents,  name,  description,  currency,  periodUnit,  periodLength,isCouponCodeCompatible,false,providerPlanUuid,providerName));



            }

        ListviewSubcriptionAdapter adapter = new ListviewSubcriptionAdapter(this,lstPlans);

        lst.setAdapter(adapter);

        loading_spinner.setVisibility(View.GONE);


        lst.setItemChecked(0,true);

        AnimationUtils.enterBottom(bntValider,200);




    } catch (Exception e) {
        e.printStackTrace();
        showToast("Error: " + e.getMessage());
        loading_spinner.setVisibility(View.GONE);
    }



}
    private void makeGetListPlans(final String access_token, final ListView lst) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/billings/internalplans"+"?Country="+StaticVar.CountryCode; //"?filterEnabled=true&filterUserReferenceUuid=249149&filterCountry=FR&filterClientId=" +StaticVar.clientApiID; //+StaticVar.ApiUrlParams;

        //urlJsonObj=urlJsonObj.replace("https://afrostream-backend.herokuapp.com","https://api.afrostream.tv");



        JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET,urlJsonObj,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {





                        DoResponseGetListPlans(response,lst);
                        // loading_spinner.setVisibility(View.GONE);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
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








    private void makeCoupon(final String access_token, final String couponCode) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/billings/coupons"+"?coupon="+couponCode;




        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,urlJsonObj,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        loading_spinner.setVisibility(View.GONE);

                    String nb=response.toString();
                        try{
                            final JSONObject coupon=response.getJSONObject("coupon");
                            String couponStatus=coupon.getString("status");
                            JSONObject internalCouponsCampaign=coupon.getJSONObject("internalCouponsCampaign");
                            final String couponsCampaignType=internalCouponsCampaign.getString("couponsCampaignType");

                            JSONObject provider=internalCouponsCampaign.getJSONObject("provider");
                            final String providerName=provider.getString("providerName");

                            if (!couponsCampaignType.equals("promo"))
                            {

                                if (couponStatus.equals("waiting"))
                                {

                                    JSONObject internalPlan=coupon.getJSONObject("internalPlan");
                                    final String internalPlanUuid=internalPlan.getString("internalPlanUuid");



                                    final MaterialDialog dialogName=new MaterialDialog.Builder(PaymentActivity.this)
                                            .title(R.string.couponInfo)

                                            .customView(R.layout.dialog_coupon_name, false)

                                            .positiveText(R.string.activity_login_forget_validate)
                                            .negativeText(R.string.cancel)
                                           .titleColor(Color.BLACK)
                                            .contentColor(Color.BLACK) // notice no 'res' postfix for literal color
                                            .linkColorAttr(R.attr.colorPrimary)  // notice attr is used instead of none or res for attribute resolving
                                            .dividerColorRes(R.color.colorPrimary)
                                            //.backgroundColorRes(R.color.colorPrimary)
                                            .positiveColorRes(R.color.colorPrimary)
                                            .widgetColorRes(R.color.colorPrimary)
                                            .buttonRippleColorRes(R.color.colorPrimary)
                                            .show();


                                    View positive = dialogName.getActionButton(DialogAction.POSITIVE);

                                    final View dialogView = dialogName.getView();


                                    positive.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {




                                            EditText txtFirstname =(EditText)dialogView.findViewById(R.id.txtFirstname);
                                            EditText txtLastname =(EditText)dialogView.findViewById(R.id.txtLastname);



                                            if (!txtFirstname.getText().toString().equals("") && !txtLastname.getText().toString().equals("") )
                                            {

                                                makeSubcription(StaticVar.access_token,"",txtFirstname.getText().toString(),txtLastname.getText().toString(),providerName,"",couponCode,couponsCampaignType,internalPlanUuid);
                                            }else
                                            {
                                                showToast(getString(R.string.checkFirstnameLastname));
                                            }

                                            //  showToast(txt.getText().toString());


                                        }
                                    });

                                    View negative = dialogName.getActionButton(DialogAction.NEGATIVE);
                                    negative.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialogName.dismiss();

                                        }
                                    });





                                }else
                                {
                                    showToast(getString(R.string.invalidcoupon));
                                }

                            }



                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
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



    private void makeSubcription(final String access_token, String stripeToken, String Firstname,String Lastname,String billingProviderName,String orderIdGoogle,String couponCode,String couponsCampaignTypeValue,String CouponInternalPlanUuid) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/api/billings/subscriptions/" ; //+StaticVar.ApiUrlParams;

        //HashMap<String, String> params = new HashMap<String, String>();

        JSONObject params=new JSONObject();


try {
    params.put("billingProviderName", billingProviderName);
    params.put("firstName", Firstname);
    params.put("lastName", Lastname);
    if (CouponInternalPlanUuid.equals(""))
    params.put("internalPlanUuid", selectPlan.getInternalPlanUuid());
    else
        params.put("internalPlanUuid", CouponInternalPlanUuid);



    HashMap<String, String> paramsSub = new HashMap<String, String>();
    paramsSub.put("couponCode", couponCode);
    if (billingProviderName.equals("stripe") || billingProviderName.equals("google"))paramsSub.put("customerBankAccountToken", stripeToken);
    JSONObject nb = new JSONObject(paramsSub);

    if (billingProviderName.equals("google"))
    {
        nb.put("orderId", orderIdGoogle);
    }

    params.put("subOpts", nb);


    HashMap<String, String> billingInfoSub = new HashMap<String, String>();
    billingInfoSub.put("countryCode", StaticVar.CountryCode);
    if (!couponsCampaignTypeValue.equals(""))billingInfoSub.put("paymentMethod", couponsCampaignTypeValue);




    HashMap<String, String> paymentMethod = new HashMap<String, String>();

    if (billingProviderName.equals("google"))
    {
        paymentMethod.put("paymentMethodType", "googlepay");
    }else
    {
        paymentMethod.put("paymentMethodType", "card");
    }



    JSONObject paymentMethodJ = new JSONObject(paymentMethod);
   // billingInfoSub.put("paymentMethod", "card");


    JSONObject billingInfo = new JSONObject(billingInfoSub);

    billingInfo.put("paymentMethod",paymentMethodJ);
    params.put("billingInfo", billingInfo);




}catch(Exception ee)
{
    ee.printStackTrace();
}



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, params, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        try {


                            if (mFirebaseAnalytics != null) {

                                Bundle params = new Bundle();
                                params.putString("selectPlanName", selectPlan.getName());
                                params.putString("selectPlanProvider", selectPlan.getProviderName());
                                params.putString("selectPlanAmount", selectPlan.getAmount());
                                params.putString("selectPlanCurrency", selectPlan.getCurrency());

                                mFirebaseAnalytics.logEvent("finish_checkout", params);
                            }
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }


                        showToast("Merci pour votre paiement,vous pouvez vous connecter maintenant");
                        StaticVar.subscription=true;
                        StaticVar.FirstLaunch=true;

                        loading_spinner.setVisibility(View.GONE);

                        final Intent intent = new Intent(PaymentActivity.this, MainActivity.class);

                        startActivity(intent);

                        finish();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  loading_spinner.setVisibility(View.GONE);
                VolleyLog.d(TAG, "Error: " + error.getMessage());

                loading_spinner.setVisibility(View.GONE);
                StaticVar.subscription=false;




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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000,
                0 ,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(jsonObjReq);






    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);



        try {
            this.user_first_name = getIntent().getStringExtra("user_first_name");
            this.user_last_name = getIntent().getStringExtra("user_last_name");
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);



        chkCGU=(CheckBox) this.findViewById(R.id.chkCGU);
        chkPay=(CheckBox) this.findViewById(R.id.chkPay);

         bntPayCondition=(TextView) this.findViewById(R.id.bntPayCondition);
         bntCgu=(TextView) this.findViewById(R.id.bntCGU);

        bntCgu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String url = "https://www.afrostream.tv/"+StaticVar.CountryCode.toLowerCase()+"/cgu";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        bntPayCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "https://www.afrostream.tv/pdfs/formulaire-retractation.pdf";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        bntValider=(Button) this.findViewById(R.id.bnt_valider);

        lst=(ListView) this.findViewById(R.id.listplans);

        makeGetListPlans(StaticVar.access_token,lst);

        bntValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!chkCGU.isChecked())
                {
                    showToast(getString(R.string.pleaseacceptcgu));
                    return;
                }
                if (!chkPay.isChecked())
                {
                    showToast(getString(R.string.PleaseAcceptPayCondition));
                    return;
                }


                selectPlan=lstPlans.get(ListPosition);


                if (selectPlan.getInternalPlanUuid().equals("coupon")) {

                      dialog=new MaterialDialog.Builder(PaymentActivity.this)
                            .title(R.string.coupon)
                            .positiveText(R.string.activity_login_forget_validate)
                            .negativeText(R.string.cancel)
                            .neutralText(R.string.scanqrcode)

                            // .inputRangeRes(2, 20, R.color.re)
                            .input(null, null, new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(MaterialDialog dialog, CharSequence input) {
                                    // Do something
                                }
                            }).show();

                    View positive = dialog.getActionButton(DialogAction.POSITIVE);
                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String Coupon=  dialog.getInputEditText().getText().toString();




                            makeCoupon(StaticVar.access_token,Coupon);

                        }
                    });

                    View neutral = dialog.getActionButton(DialogAction.NEUTRAL);
                    neutral.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                           /* BarcodeDetector detector =
                                    new BarcodeDetector.Builder(getApplicationContext())
                                            .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                                            .build();
                            if(!detector.isOperational()){
                                showToast("Could not set up the detector!");
                                return;
                            }*/



                            qrScan = new IntentIntegrator(PaymentActivity.this);

                            qrScan.initiateScan();


                        }
                    });

                    View negative = dialog.getActionButton(DialogAction.NEGATIVE);
                    negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog.dismiss();

                        }
                    });



                    return;
                }

                if (selectPlan!=null) {

                    try {


                        if (mFirebaseAnalytics != null) {

                            Bundle params = new Bundle();
                            params.putString("selectPlanName", selectPlan.getName());
                            params.putString("selectPlanProvider", selectPlan.getProviderName());
                            params.putString("selectPlanAmount", selectPlan.getAmount());
                            params.putString("selectPlanCurrency", selectPlan.getCurrency());

                            mFirebaseAnalytics.logEvent("begin_checkout", params);
                        }
                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }

                    if (selectPlan.getProviderName().equals("stripe")) {
                        Intent intent = new Intent(PaymentActivity.this, CardEditActivity.class);
                        intent.putExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME, "");

                        startActivityForResult(intent, CREATE_NEW_CARD);
                    }else  if (selectPlan.getProviderName().equals("google")) {


                        String sku=selectPlan.getProviderPlanUuid();
                        try {
                            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
                                    sku, "subs", "");




                            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

                            startIntentSenderForResult(pendingIntent.getIntentSender(),
                                    1001, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
                                    Integer.valueOf(0));


                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }



                    }
                }else
                {
                    showToast("Veuillez séléctionné un plan");
                }
            }
        });

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListPosition=position;
            }
        });




       // showAndroidPay();
    }



    public void onStart() {

        super.onStart();


    }

    public void onStop() {

        super.onStop();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {


            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    //JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews


                    String Content=result.getContents();
                   // showToast(Content);
                    dialog.dismiss();

                    makeCoupon(StaticVar.access_token,Content);
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                   // Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }








        if (requestCode == 1001) {
            try {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");

                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
                String purchaseToken = "";
                String orderId = "";
                String productId = "";
                String developerPayload = "";


                try {
                    JSONObject dataJ = new JSONObject(purchaseData);
                    purchaseToken = dataJ.getString("purchaseToken");
                    orderId = dataJ.getString("orderId");
                    productId = dataJ.getString("productId");
                    developerPayload = dataJ.getString("developerPayload");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);
                        String sku = jo.getString("productId");

                        makeSubcription(StaticVar.access_token, purchaseToken,  this.user_first_name , this.user_last_name, "google", orderId,"","","");

                       // showToast("You have bought the " + sku + ". Excellent");
                        return;
                    } catch (JSONException e) {
                        showToast("Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }

                return;
            }catch (Exception ee)
            {
                ee.printStackTrace();
                return;
            }
        }



        if (resultCode == RESULT_OK) {
//            Debug.printToast("Result Code is OK", getApplicationContext());

            final String name = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cardCVC = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            String nb[] =expiry.split("/");
            int cardExpMonth= Integer.parseInt(nb[0]) ;
            int cardExpYear= Integer.parseInt(nb[1]);

            if (requestCode == CREATE_NEW_CARD) {

                Card card = new Card(
                        cardNumber,
                        cardExpMonth,
                        cardExpYear,
                        cardCVC
                );

                card.validateNumber();
                card.validateCVC();


                Stripe stripe = null;
                try {
                    stripe = new Stripe(StaticVar.StripeKey);
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }

                if (stripe!=null)
                {
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to your server
                                makeSubcription(StaticVar.access_token,token.getId(),name,name,"stripe","","","","");
                            }
                            public void onError(Exception error) {
                                // Show localized error message
                                showToast(error.toString());


                            }
                        }
                );
                }



            }
        }


    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onConnected(Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}
}
