package tv.afrostream.app.utils;

import tv.afrostream.app.activitys.MainActivity;
import tv.afrostream.app.activitys.MainActivityLocal;
import tv.afrostream.app.activitys.MovieDetailsActivity;
import tv.afrostream.app.activitys.MyDownloadActivity;
import tv.afrostream.app.activitys.SerieDownloadDetailActivity;
import tv.afrostream.app.fragments.CategoriesFragement;
import tv.afrostream.app.fragments.ListFavorisFragment;
import tv.afrostream.app.fragments.MyDownloadFragment;

/**
 * Created by bahri on 11/01/2017.
 */

public class StaticVar {

    public static Boolean DevMode=false;

    public static Boolean FirstLaunch=true;

   public static String BaseUrl=""; // dev

   public static String  drmLicenseUrl=""; //dev
   public static String StripeKey=""; //dev


    public static String clientSecret="0426914d-96bc-46f6-849e-bca34ef7300a"; //prod et dev
    public static String clientApiID="85f700d9-4a80-4913-8223-e0d49fef3a05"; //prod et dev




    public static String DownloadFolderName="AfrostreamDownload";

    public static String LocalFolderName="AfrostreamLocal";




    public static String DRMMerchant= "afrostream";

    public static final String MyPREFERENCES = "Prefs" ;
    public static final String usernamePref = "username";
    public static final String passwordPref = "password";
 public static final String backimagePref = "backimage";

    public static final String remeberMePref = "rememberme";


    public static String user_id="";
    public static String CountryCode="";

    public static String ApiUrlParams="";

    public static String access_token="";
   public static String access_token_api="";
    public static String refresh_token="";
    public static String expires_in="";
    public static String token_type="";
    public static String date_token="";

    public static ListFavorisFragment favFragment;

    public static CategoriesFragement catFragment;
    public static MyDownloadActivity downloadAct;

    public static MainActivity mainAct;
    public static MainActivityLocal mainActLocal;
    public static MovieDetailsActivity MovieDetailsAct;

    public static SerieDownloadDetailActivity SerieDownloadDetailAct;

    public static final String MESSAGE_PROGRESS = "message_progress_download";
    public static String android_id="";

 public static String facebook_image_profil_url="";

    public static Boolean subscription=false;

 public static double densityPixel = 1;

   public static String path_decrypt_download_file="";
    public static String user_picture_url="";

    public static String user_first_name="";
    public static String user_last_name="";
    public static String user_email="";


    public static String  Subscription_subscriptionBillingUuid="";
    public static String  Subscription_isCancelable="";
    public static String Subscription_subStatus="";

    public static String app_version_code="";





}
