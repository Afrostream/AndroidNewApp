package tv.afrostream.app.activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tv.afrostream.app.AppController;
import tv.afrostream.app.R;
import tv.afrostream.app.adapters.ListviewSubcriptionAdapter;
import tv.afrostream.app.models.ListPlansModel;
import tv.afrostream.app.utils.AnimationUtils;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 22/02/2017.
 */



public class PaymentActivityDemo extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    // You will need to use your live API key even while testing

    public String  TAG=PaymentActivity.class.getSimpleName();
    // Unique identifiers for asynchronous requests:
    private static final int LOAD_MASKED_WALLET_REQUEST_CODE = 1000;
    private static final int LOAD_FULL_WALLET_REQUEST_CODE = 1001;





    private final int CREATE_NEW_CARD = 0;
    private Toast toast;
    ListView lst;
    ProgressBar loading_spinner=null;
    int ListPosition=0;

    Button bntValider;


    ArrayList<ListPlansModel> lstPlans;

    ListPlansModel selectPlan=null;


    private void showToast(String message) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }



    private void makeLogin(final String Username, final String Password) {


        if (Username.equals("") || Password.equals(""))
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        loading_spinner.setVisibility(View.VISIBLE);
        String urlJsonObj= StaticVar.BaseUrl+"/auth/oauth2/token";

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("grant_type", "password");
        params.put("client_id", StaticVar.clientApiID);
        params.put("client_secret", StaticVar.clientSecret);
        params.put("username",Username);
        params.put("password", Password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.d(TAG, response.toString());

                try {




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


                    StaticVar.subscription=true;

                    Bundle conData = new Bundle();
                    conData.putString("param_result", selectPlan.getName());
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    PaymentActivityDemo.this.setResult(RESULT_OK, intent);


                    PaymentActivityDemo.this.finish();



                    loading_spinner.setVisibility(View.GONE);


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
                    if (error.networkResponse.statusCode==403) {
                        showToast(getString(R.string.activity_login_error_login_incorrect));
                    }else
                    {
                        showToast(getString(R.string.activity_login_error_login_problem)+ " status code : "+error.networkResponse.statusCode);
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
                headers.put("Content-Type", "application/json");
                // headers.put("key", "Value");
                return headers;
            }
        };

        // Adding request to request queue


        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


    private void makeGetListPlans(final String access_token, final ListView lst) {


        lstPlans = new ArrayList<ListPlansModel>();





           // lstPlans.add(new ListPlansModel( internalPlanUuid,amount,  amountInCents,  name,  description,  currency,  periodUnit,  periodLength,isCouponCodeCompatible));

        lstPlans.add(new ListPlansModel( "","1.50",  "150",  "As You Go",  "1 Movie Or 3 TV Series EP + 48H Of Unlimited Content + Trailers",  "$",  getString(R.string.days),  "2",false,false,"",""));

        lstPlans.add(new ListPlansModel( "","3",  "300",  "As You Go with data",  "1 Movie Or 3 TV Series EP + Mobile Data + 48H Of Unlimited Content + Trailers",  "$",  getString(R.string.days),  "2",false,true,"",""));
        ListviewSubcriptionAdapter adapter = new ListviewSubcriptionAdapter(this,lstPlans);

        lst.setAdapter(adapter);

        loading_spinner.setVisibility(View.GONE);


        lst.setItemChecked(0,true);

        AnimationUtils.enterBottom(bntValider,200);






    }



    private void makeSubcription(final String access_token, String stripeToken, String CardHolderName) {


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
            params.put("billingProviderName", "stripe");
            params.put("firstName", CardHolderName);
            params.put("lastName", CardHolderName);
            params.put("internalPlanUuid", selectPlan.getInternalPlanUuid());

            HashMap<String, String> paramsSub = new HashMap<String, String>();
            paramsSub.put("couponCode", "");
            paramsSub.put("customerBankAccountToken", stripeToken);
            JSONObject nb = new JSONObject(paramsSub);

            params.put("subOpts", nb);


            HashMap<String, String> billingInfoSub = new HashMap<String, String>();
            billingInfoSub.put("countryCode", StaticVar.CountryCode);


            HashMap<String, String> paymentMethod = new HashMap<String, String>();
            paymentMethod.put("paymentMethodType", "card");


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


                showToast("Merci pour votre paiement,vous pouvez vous connecter maintenant");

                loading_spinner.setVisibility(View.GONE);
                finish();


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

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(20000,
                0 ,//DefaultRetryPolicy.DEFAULT_MAX_RETRIES
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(jsonObjReq);






    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);





        loading_spinner=(ProgressBar) this.findViewById(R.id.loading_spinner);



        bntValider=(Button) this.findViewById(R.id.bnt_valider);

        lst=(ListView) this.findViewById(R.id.listplans);

        makeGetListPlans(StaticVar.access_token,lst);

        bntValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectPlan=lstPlans.get(ListPosition);

                if (selectPlan!=null) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(PaymentActivityDemo.this)
                            .title(R.string.confirmpay)
                            .content(R.string.confirmpayquestion)
                            .positiveText(R.string.yes)
                            .negativeText(R.string.no)
                            .show();

                    View positive = dialog.getActionButton(DialogAction.POSITIVE);


                    positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String imagePath="";
                            try {


                                makeLogin("bahri@afrostream.tv","Paris@2010*");

                                dialog.dismiss();

                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }

                        }
                    });
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
                    stripe = new Stripe( getApplicationContext(), StaticVar.StripeKey);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (stripe!=null)
                {
                    stripe.createToken(
                            card,
                            new TokenCallback() {
                                public void onSuccess(Token token) {
                                    // Send token to your server
                                    makeSubcription(StaticVar.access_token,token.getId(),name);
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
