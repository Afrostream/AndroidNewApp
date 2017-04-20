package tv.afrostream.app.activitys;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastState;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.images.WebImage;

import org.json.JSONObject;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import tv.afrostream.app.EventLogger;
import tv.afrostream.app.utils.StaticVar;
import tv.afrostream.app.AppController;
import tv.afrostream.app.DrmTodayMediaDrmCallback;

import tv.afrostream.app.models.MediaItem;
import tv.afrostream.app.PlayerControl;
import tv.afrostream.app.R;
import tv.afrostream.app.TrackSelectionHelper;

/**
 * Created by bahri on 19/01/2017.
 */

public class PlayerActivity extends FragmentActivity implements View.OnClickListener, ExoPlayer.EventListener,
        PlaybackControlView.VisibilityListener {

    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";
    public static final String DRM_LICENSE_URL = "drm_license_url";
    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";
    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";

    public static final String VIDEO_NAME = "video_name";

    public static final String ACTION_VIEW = "com.example.bahri.pocplayer.action.VIEW";
    public static final String EXTENSION_EXTRA = "extension";

    public static final String ACTION_VIEW_LIST =
            "com.example.bahri.pocplayer.action.VIEW_LIST";
    public static final String URI_LIST_EXTRA = "uri_list";
    public static final String EXTENSION_LIST_EXTRA = "extension_list";

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;
    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private Handler mainHandler;
    private Timeline.Window window;
    private EventLogger eventLogger;
    private SimpleExoPlayerView simpleExoPlayerView;
    private LinearLayout debugRootView;

    private LinearLayout castView;
    private LinearLayout view_topright;

    private TextView debugTextView;
    private Button retryButton;
    private ImageView logoafro;
    private ImageView bntClose;
    private LinearLayout layoutbntClose;

    private DataSource.Factory mediaDataSourceFactory;
    private SimpleExoPlayer player;
    private DefaultTrackSelector trackSelector;
    private TrackSelectionHelper trackSelectionHelper;
    private DebugTextViewHelper debugViewHelper;
    private boolean playerNeedsSource;
private String videoSmoothUrl="";
    private boolean shouldAutoPlay;
    private boolean isTimelineStatic;
    private int playerWindow;
    private long playerPosition;

    private MediaRouter mMediaRouter;
    private MediaRouteSelector mMediaRouteSelector;
    private MediaRouter.Callback mMediaRouterCallback;
    private MediaRouteButton mMediaRouteButton;

private  ProgressBar loading_spinner;
    private PlaybackLocation mLocation;
    private CastContext mCastContext;
    private CastSession mCastSession;
    private CastDevice mSelectedDevice;
    private int mRouteCount = 0;
    private CastStateListener mCastStateListener;

    PlayerControl ExoPlayerControl;

    private MediaItem mSelectedMedia;

    public String  TAG=PlayerActivity.class.getSimpleName();
    Timer timer;

    TimerTask timerTask;

    final Handler timer_handler = new Handler();

    View decorView;
    View rootView;
    private LoadControl loadControl = null;

    String VideoName="";
    String Video_ID="";
    int playerPositionIntent=0;
    String video_type="";
private String imageUrl="";
    private SessionManagerListener<CastSession> mSessionManagerListener;
    private PlaybackState mPlaybackState;

    public SharedPreferences sharedpreferences;

    private MediaController mediaController;

    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }


    public Boolean IfTokenExpire()
    {

        if (!StaticVar.date_token.equals("") && !StaticVar.expires_in.equals("")) {
            try {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                Date date_t = sdf.parse(StaticVar.date_token);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date_t);
                int second = Integer.parseInt(StaticVar.expires_in);
                // calendar.add(Calendar.SECOND, second);
                Date dt = calendar.getTime();
                Date date_Now = new Date();
                long second_diff = TimeUnit.MILLISECONDS.toSeconds((date_Now.getTime() - dt.getTime()) );

                second_diff+=7200;
                if (second_diff > second) {
                    return true;
                } else {
                    return false;
                }


            } catch (Exception ee) {
                ee.getStackTrace();
                return true;
            }
        }else {


            return true;

        }
    }

    public void RefreshToken()
    {
        if (IfTokenExpire()) {

            String urlJsonObj = StaticVar.BaseUrl + "/auth/oauth2/token";

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("grant_type", "refresh_token");
            params.put("refresh_token", StaticVar.refresh_token);
            params.put("client_id", StaticVar.clientApiID);
            params.put("client_secret", StaticVar.clientSecret);


            JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                    urlJsonObj, new JSONObject(params), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    // Log.d(TAG, response.toString());

                    try {




                        String access_token = "";
                        access_token = response.getString("access_token");
                        String refresh_token = "";
                        refresh_token = response.getString("refresh_token");
                        String expires_in = "";
                        expires_in = response.getString("expires_in");


                        StaticVar.access_token = access_token;
                        StaticVar.refresh_token = refresh_token;
                        StaticVar.expires_in = expires_in;

                        synchronized (this) {

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            String currentDateandTime = "";

                            try {


                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                currentDateandTime = sdf.format(new Date());
                            } catch (Exception ee) {
                                ee.getStackTrace();
                            }
                            StaticVar.date_token=currentDateandTime;


                            editor.putString("access_token", access_token);
                            editor.putString("refresh_token", refresh_token);
                            editor.putString("expires_in", expires_in);
                            editor.putString("date_token", currentDateandTime);

                            editor.commit();
                        }


                    } catch (Exception e) {


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

                    try {

                        if (error.networkResponse != null && error.networkResponse.data != null) {
                            VolleyError error2 = new VolleyError(new String(error.networkResponse.data));
                            String errorJson = error2.getMessage();
                            JSONObject errorJ = new JSONObject(errorJson);
                            String MessageError = errorJ.getString("error");
                            //FirebaseCrash.log("APIAuth Error :" + MessageError);
                            //showToast("Error: " + MessageError);

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
                    // headers.put("key", "Value");
                    return headers;
                }
            };

            // Adding request to request queue


            AppController.getInstance().addToRequestQueue(jsonObjReq);
        }
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();


        timer.schedule(timerTask, 3000, 60000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                timer_handler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        try {

                            if (ExoPlayerControl != null && !Video_ID.equals(""))
                                makeUpdateVideoInformation(StaticVar.access_token, Video_ID, ExoPlayerControl.getCurrentPosition());

                            RefreshToken();

                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }

                    }
                });
            }
        };
    }






    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {

            requestWindowFeature(Window.FEATURE_NO_TITLE);

            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

try {
     decorView = getWindow().getDecorView();

    sharedpreferences = getSharedPreferences(StaticVar.MyPREFERENCES, Context.MODE_PRIVATE);





}catch (Exception ee)
{
    ee.printStackTrace();
}


        shouldAutoPlay = true;
        mediaDataSourceFactory = buildDataSourceFactory(true);
        mainHandler = new Handler();
        window = new Timeline.Window();
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        try {
            setContentView(R.layout.player_activity);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
         rootView = findViewById(R.id.root);
        rootView.setOnClickListener(this);
        debugRootView = (LinearLayout) findViewById(R.id.controls_root);

        mediaController = new MediaController(this);


        mediaController.setAnchorView(rootView);


        loading_spinner=(ProgressBar) findViewById(R.id.loading_spinner);
        castView = (LinearLayout) findViewById(R.id.controls_cast);

        view_topright= (LinearLayout) findViewById(R.id.controls_topright);


        debugTextView = (TextView) findViewById(R.id.debug_text_view);
        // retryButton = (Button) findViewById(R.id.retry_button);

        logoafro= (ImageView) findViewById(R.id.imageViewLogo);

        bntClose= (ImageView) findViewById(R.id.bntClose);

        layoutbntClose=(LinearLayout) findViewById(R.id.LayoutbntClose);

        layoutbntClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerActivity.this.finish();
            }
        });
        bntClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerActivity.this.finish();
            }
        });



        // retryButton.setOnClickListener(this);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.player_view);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();





        debugTextView.setVisibility(View.GONE);



        mCastStateListener = new CastStateListener() {
            @Override
            public void onCastStateChanged(int newState) {
                if (newState != CastState.NO_DEVICES_AVAILABLE) {
                    // showIntroductoryOverlay();
                }
            }
        };










        try {
            mMediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
            CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),mMediaRouteButton);
            setupCastListener();

            mCastContext = CastContext.getSharedInstance(this);
            mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(this, savedInstanceState);
            mCastSession = mCastContext.getSessionManager().getCurrentCastSession();


        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


    }

    @Override
    public void onNewIntent(Intent intent) {
        releasePlayer();
        isTimelineStatic = false;
        setIntent(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

        initializePlayer();

    }

    @Override
    public void onResume() {





        super.onResume();

        try {

            if (mCastContext != null)
                mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener,
                        CastSession.class);
            if (mCastSession == null && mCastContext != null) {
                // Get the current session if there is one
                mCastSession = mCastContext.getSessionManager().getCurrentCastSession();
            }

        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        if (( player == null)) {
            initializePlayer();
        }

        startTimer();
    }

    @Override
    public void onPause() {


        super.onPause();
        try {

            if (mCastContext != null)
                mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener,
                        CastSession.class);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        releasePlayer();
        stoptimertask();

    }

    @Override
    public void onStop() {
        super.onStop();

        releasePlayer();
        stoptimertask();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initializePlayer();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    // Activity input

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        // Show the controls on any key event.
        simpleExoPlayerView.showController();
        // If the event was not handled then see if the player view can handle it as a media key event.
        return super.dispatchKeyEvent(event) || simpleExoPlayerView.dispatchMediaKeyEvent(event);
    }

    // OnClickListener methods

    @Override
    public void onClick(View view) {
        try {
            if (view == retryButton) {
                initializePlayer();
            } else if (view.getParent() == debugRootView || view.getParent() == view_topright) {
                MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
                if (mappedTrackInfo != null) {
                    String tag = ((ImageView) view).getTag().toString();
                    String title = "";
                    if (tag.equals("0"))
                        title = getString(R.string.videoqualite);
                    else if (tag.equals("1"))
                        title = getString(R.string.AudioLanguage);
                    else if (tag.equals("2"))
                        title = getString(R.string.videocaption);


                    trackSelectionHelper.showSelectionDialog(this, title,
                            trackSelector.getCurrentMappedTrackInfo(), (int) view.getTag());
                }
            }
        }catch (Exception ee)
        {
            ee.getStackTrace();
        }
    }

    // PlaybackControlView.VisibilityListener implementation

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!StaticVar.path_decrypt_download_file.equals(""))
        {
            try {
                File f=new File(StaticVar.path_decrypt_download_file );
                if (f.exists()) {
                    f.delete();
                }


            }catch (Exception ee)
            {
                ee.printStackTrace();
            }

            StaticVar.path_decrypt_download_file="";
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {


        try {

            if (visibility ==View.VISIBLE) {

               decorView.setSystemUiVisibility(

                       View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                               | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                               | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                               | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                               | View.SYSTEM_UI_FLAG_FULLSCREEN
                               | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


            } else {
                int uiOptions =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        debugRootView.setVisibility(visibility);
        castView.setVisibility(visibility);





    }

    // Internal methods

    private void initializePlayer() {
        Intent intent = getIntent();

        if (player == null) {
            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);

            VideoName=intent.getStringExtra(VIDEO_NAME);

            Video_ID=intent.getStringExtra("Video_ID");
            playerPositionIntent=intent.getIntExtra("playerPosition",0);

            video_type=intent.getStringExtra("video_type");

            videoSmoothUrl=intent.getStringExtra("uriSmooth");
            imageUrl=intent.getStringExtra("imageUrl");
            UUID drmSchemeUuid = intent.hasExtra(DRM_SCHEME_UUID_EXTRA)
                    ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;
            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;
            if (drmSchemeUuid != null) {
                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);
                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);
                Map<String, String> keyRequestProperties;
                if (keyRequestPropertiesArray == null || keyRequestPropertiesArray.length < 2) {
                    keyRequestProperties = null;
                } else {
                    keyRequestProperties = new HashMap<>();
                    for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {
                        keyRequestProperties.put(keyRequestPropertiesArray[i],
                                keyRequestPropertiesArray[i + 1]);
                    }
                }
                try {
                    drmSessionManager = buildDrmSessionManager(drmSchemeUuid, drmLicenseUrl,
                            keyRequestProperties);
                } catch (UnsupportedDrmException e) {
                    int errorStringId = Util.SDK_INT < 18 ? R.string.error_drm_not_supported
                            : (e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME
                            ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown);
                    showToast(errorStringId);
                    return;
                }
            }

            @SimpleExoPlayer.ExtensionRendererMode int extensionRendererMode =
                    ((AppController) getApplication()).useExtensionRenderers()
                            ? (preferExtensionDecoders ? SimpleExoPlayer.EXTENSION_RENDERER_MODE_PREFER
                            : SimpleExoPlayer.EXTENSION_RENDERER_MODE_ON)
                            : SimpleExoPlayer.EXTENSION_RENDERER_MODE_OFF;


            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);


            trackSelectionHelper = new TrackSelectionHelper(trackSelector, videoTrackSelectionFactory);

             loadControl = new DefaultLoadControl();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl,
                    drmSessionManager, extensionRendererMode);

            player.addListener(this);
            ExoPlayerControl =new PlayerControl(player);








            eventLogger = new EventLogger(trackSelector);
            player.addListener(eventLogger);
            player.setAudioDebugListener(eventLogger);
            player.setVideoDebugListener(eventLogger);
            player.setMetadataOutput(eventLogger);



            simpleExoPlayerView.setPlayer(player);




            if (isTimelineStatic) {
                if (playerPosition == C.TIME_UNSET) {
                    player.seekToDefaultPosition(playerWindow);
                } else {
                    player.seekTo(playerWindow, playerPosition);
                }
            }
            player.setPlayWhenReady(shouldAutoPlay);
            debugViewHelper = new DebugTextViewHelper(player, debugTextView);
            // debugViewHelper.start();
            playerNeedsSource = true;


        }
        if (playerNeedsSource) {
            String action = intent.getAction();
            Uri[] uris;
            String[] extensions;
            if (ACTION_VIEW.equals(action)) {
                uris = new Uri[] {intent.getData()};
                extensions = new String[] {intent.getStringExtra(EXTENSION_EXTRA)  };
                videoSmoothUrl=intent.getStringExtra("uriSmooth");
                imageUrl=intent.getStringExtra("imageUrl");
                Video_ID=intent.getStringExtra("Video_ID");
                playerPositionIntent=intent.getIntExtra("playerPosition",0);
                video_type=intent.getStringExtra("video_type");

            } else if (ACTION_VIEW_LIST.equals(action)) {
                String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);
                uris = new Uri[uriStrings.length];
                for (int i = 0; i < uriStrings.length; i++) {
                    uris[i] = Uri.parse(uriStrings[i]);
                }
                extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);
                if (extensions == null) {
                    extensions = new String[uriStrings.length];
                }
            } else {
                showToast(getString(R.string.unexpected_intent_action, action));
                return;
            }
            if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {
                // The player will be reinitialized if the permission is granted.
                return;
            }
            MediaSource[] mediaSources = new MediaSource[uris.length];
            for (int i = 0; i < uris.length; i++) {
                mediaSources[i] = buildMediaSource(uris[i], extensions[i]);
            }
            MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]
                    : new ConcatenatingMediaSource(mediaSources);

/*
            String pathout = Environment
                    .getExternalStorageDirectory().toString()
                    + "/blackish_s01ep01.fr.vtt";
            Uri srtUri=null;
                try {

                    srtUri = Uri.parse(new File(pathout).toString());
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }



            Format textFormat = Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT,
                    null, Format.NO_VALUE, Format.NO_VALUE, "fr", null);
            MediaSource textMediaSource = new SingleSampleMediaSource(srtUri, mediaDataSourceFactory,
                    textFormat, C.TIME_UNSET);
            MediaSource mediaSourceWithText = new MergingMediaSource(mediaSource, textMediaSource);

*/
            player.prepare(mediaSource, !isTimelineStatic, !isTimelineStatic);
            playerNeedsSource = false;
            updateButtonVisibilities();


            MediaItem media = new MediaItem();
            media.setUrl(uris[0].toString());
            media.setTitle(VideoName);
            // media.setSubTitle("caption_fra");
            media.setStudio("");
            media.addImage(imageUrl);
            media.setContentType(extensions[0]);
            mSelectedMedia=media;


            if (video_type.equals("live"))
            {

                try {
                    if (player != null  ) player.seekTo(playerWindow, 0);

                    Handler handler = new Handler(Looper.getMainLooper());

                    handler.postDelayed(new Runnable() {


                        @Override
                        public void run() {

                        try {
                            if (player != null) player.seekTo(playerWindow, 0);
                        }catch (Exception ee)
                        {
                            ee.printStackTrace();
                        }


                        }
                    }, 1000 );

                    SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.exo_progress);
                    seekBar.setVisibility(View.GONE);


                    TextView txtduration = (TextView) rootView.findViewById(R.id.exo_duration);
                    TextView txtposition = (TextView) rootView.findViewById(R.id.exo_position);


                    txtduration.setVisibility(View.GONE);
                    txtposition.setVisibility(View.GONE);

                }catch (Exception ee)
                {
                    ee.getStackTrace();
                }


            }else

            {
                try {

                    SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.exo_progress);
                    seekBar.setVisibility(View.VISIBLE);


                    TextView txtduration = (TextView) rootView.findViewById(R.id.exo_duration);
                    TextView txtposition = (TextView) rootView.findViewById(R.id.exo_position);
                    txtduration.setVisibility(View.VISIBLE);
                    txtposition.setVisibility(View.VISIBLE);
                }catch (Exception ee)
                {
                    ee.getStackTrace();
                }
            }

           if (playerPositionIntent!=0)
               try {
                   player.seekTo(playerWindow, playerPositionIntent);
               }catch (Exception ee)
               {
                   ee.printStackTrace();
               }

        }





    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {
        int type = Util.inferContentType(!TextUtils.isEmpty(overrideExtension) ? "." + overrideExtension
                : uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_DASH:
                return new DashMediaSource(uri, buildDataSourceFactory(false),
                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),
                        mainHandler, eventLogger);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManager(UUID uuid,
                                                                           String licenseUrl, Map<String, String> keyRequestProperties) throws UnsupportedDrmException {
        if (Util.SDK_INT < 18) {
            return null;
        }
        DrmTodayMediaDrmCallback drmCallback = new DrmTodayMediaDrmCallback(licenseUrl,
                buildHttpDataSourceFactory(false), keyRequestProperties);


        return new DefaultDrmSessionManager<>(uuid,
                FrameworkMediaDrm.newInstance(uuid), drmCallback, null, mainHandler, eventLogger);
    }

    private void releasePlayer() {
        if (player != null) {
            // debugViewHelper.stop();
            //debugViewHelper = null;
            shouldAutoPlay = player.getPlayWhenReady();
            playerWindow = player.getCurrentWindowIndex();
            playerPosition = C.TIME_UNSET;
            Timeline timeline = player.getCurrentTimeline();
            if (!timeline.isEmpty() && timeline.getWindow(playerWindow, window).isSeekable) {
                playerPosition = player.getCurrentPosition();
            }
            player.release();
            player = null;
            trackSelector = null;
            trackSelectionHelper = null;
            eventLogger = null;
        }
    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *     DataSource factory.
     * @return A new DataSource factory.
     */
    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {
        try {
            return ((AppController) getApplication()).buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);
        }catch(Exception ee)
        {

            ee.printStackTrace();
            return  null;
        }
    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *     DataSource factory.
     * @return A new HttpDataSource factory.
     */
    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {
        return ((AppController) getApplication())
                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);



    }

    // ExoPlayer.EventListener implementation

    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing.
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == ExoPlayer.STATE_ENDED) {
            showControls();
        }else if (playbackState == ExoPlayer.STATE_BUFFERING)
        {
            loading_spinner.setVisibility(View.VISIBLE);
        }else if (playbackState == ExoPlayer.STATE_READY )
        {

            loading_spinner.setVisibility(View.GONE);

        }else
        {
            loading_spinner.setVisibility(View.GONE);
        }
        updateButtonVisibilities();
    }

    @Override
    public void onPositionDiscontinuity() {
        // Do nothing.
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        isTimelineStatic = !timeline.isEmpty()
                && !timeline.getWindow(timeline.getWindowCount() - 1, window).isDynamic;
    }

    @Override
    public void onPlayerError(ExoPlaybackException e) {
        String errorString = null;
        if (e.type == ExoPlaybackException.TYPE_RENDERER) {
            Exception cause = e.getRendererException();
            if (cause instanceof MediaCodecRenderer.DecoderInitializationException) {
                // Special case for decoder initialization failures.
                MediaCodecRenderer.DecoderInitializationException decoderInitializationException =
                        (MediaCodecRenderer.DecoderInitializationException) cause;
                if (decoderInitializationException.decoderName == null) {
                    if (decoderInitializationException.getCause() instanceof MediaCodecUtil.DecoderQueryException) {
                        errorString = getString(R.string.error_querying_decoders);
                    } else if (decoderInitializationException.secureDecoderRequired) {
                        errorString = getString(R.string.error_no_secure_decoder,
                                decoderInitializationException.mimeType);
                    } else {
                        errorString = getString(R.string.error_no_decoder,
                                decoderInitializationException.mimeType);
                    }
                } else {
                    errorString = getString(R.string.error_instantiating_decoder,
                            decoderInitializationException.decoderName);
                }
            }
        }
        if (errorString != null) {
            showToast(errorString);
        }
        playerNeedsSource = true;
        updateButtonVisibilities();
        showControls();
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        updateButtonVisibilities();
        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo != null) {
            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_video);
            }
            if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)
                    == MappingTrackSelector.MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {
                showToast(R.string.error_unsupported_audio);
            }
        }
    }

    // User controls




    private void updateButtonVisibilities() {
        debugRootView.removeAllViews();
        view_topright.removeAllViews();
        //  retryButton.setVisibility(playerNeedsSource ? View.VISIBLE : View.GONE);
        //debugRootView.addView(retryButton);





        logoafro.setVisibility(View.VISIBLE);
        logoafro.setImageResource(R.drawable.logo300);
        debugRootView.addView(logoafro);



        debugRootView.addView(view_topright);


        if (player == null) {
            return;
        }

        MappingTrackSelector.MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();
        if (mappedTrackInfo == null) {
            return;
        }

        for (int i = 0; i < mappedTrackInfo.length; i++) {
            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);
            if (trackGroups.length != 0) {
                ImageView button = new ImageView(this);
                Drawable label=null;

                switch (player.getRendererType(i)) {
                    case C.TRACK_TYPE_AUDIO:

                        button.setImageResource(R.drawable.audio);
                        break;
                    case C.TRACK_TYPE_VIDEO:

                        button.setImageResource(R.drawable.video);
                        break;
                    case C.TRACK_TYPE_TEXT:

                        button.setImageResource(R.drawable.ic_closed_caption_white_24dp);
                        break;
                    default:
                        continue;
                }
                // button.setText(label);



                button.setTag(i);

                //button.setLayoutParams(marginParams);


                view_topright.addView(button);
                button.setOnClickListener(this);


            }
        }







    }

    private void showControls() {
        debugRootView.setVisibility(View.VISIBLE);
        castView.setVisibility(View.VISIBLE);
    }

    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private MediaInfo buildRemoteMediaInfo() {
        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);

        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, mSelectedMedia.getStudio());
        movieMetadata.putString(MediaMetadata.KEY_TITLE, mSelectedMedia.getTitle());

        try {
            movieMetadata.addImage(new WebImage(Uri.parse(mSelectedMedia.getImage(0))));

        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

        //String urlHLS=mSelectedMedia.getUrl().replace(".mpd",".m3u8");
        //String urlHLS="https://origin.cdn.afrostream.net/vod/42_FILMLM_16020937/a3f1fd3dd16babf4.ism/MANIFEST";

        //String urlHLS= "https://origin.cdn.afrostream.net/vod/24hourlovebis/d4eed726882a4be3-drm.ism/MANIFEST";
        return new MediaInfo.Builder(videoSmoothUrl)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType(mSelectedMedia.getContentType())



                .setMetadata(movieMetadata)
                .setStreamDuration(mSelectedMedia.getDuration() * 1000)
                .build();
    }
    private void loadRemoteMedia(int position, boolean autoPlay) {
        try {
            if (mCastSession == null) {
                return;
            }
            final RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
            if (remoteMediaClient == null) {
                return;
            }

        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {



            @Override
            public void onStatusUpdated() {
                try {
                    Thread.sleep(500);
                    Intent intent = new Intent(PlayerActivity.this , ExpandedControlsActivity.class);
                    startActivity(intent);
                    remoteMediaClient.removeListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onMetadataUpdated() {
            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }

            @Override
            public void onAdBreakStatusUpdated() {

            }
        });
        remoteMediaClient.load(buildRemoteMediaInfo(), autoPlay, position);
        }catch (Exception  ee)
        {
            return;
        }
    }



    private void makeUpdateVideoInformation(final String access_token, String videoID, int playerPosition) {


        if (access_token.equals("") )
        {

            showToast(this.getString(R.string.activity_login_error_login_empty) );
            return;
        }


        playerPosition=playerPosition/1000;
        String urlJsonObj= StaticVar.BaseUrl+"/api/users/me/videos/"+videoID;


        JSONObject params=new JSONObject();
        try {
            params.put("playerPosition", playerPosition);
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,urlJsonObj,params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                        Log.d("test",response.toString());

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
                       // showToast("Error update player position: " + MessageError);

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






    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
                // setCoverArtStatus(null);

            } else {

                //setCoverArtStatus(mSelectedMedia.getImage(0));
            }
        } else {

            //setCoverArtStatus(mSelectedMedia.getImage(0));

        }
    }
    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {
                onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
             onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
               // onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
             //   onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            }

            @Override
            public void onSessionEnding(CastSession session) {
               // onApplicationDisconnected();
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            }

            private void onApplicationConnected(CastSession castSession) {

                try {
                    mCastSession = castSession;

                    int position = (int) player.getCurrentPosition();
                    if (null != mSelectedMedia) {

                        if (mPlaybackState == PlaybackState.PLAYING) {
                            ExoPlayerControl.pause();

                            loadRemoteMedia(position, true);
                            return;
                        } else {
                            mPlaybackState = PlaybackState.IDLE;
                            updatePlaybackLocation(PlaybackLocation.REMOTE);
                        }
                    }
                    //updatePlayButton(mPlaybackState);
                    invalidateOptionsMenu();


                    if (mSelectedMedia != null)
                        loadRemoteMedia(position, true);
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }

            private void onApplicationDisconnected() {

                try {

                    updatePlaybackLocation(PlaybackLocation.LOCAL);
                    mPlaybackState = PlaybackState.IDLE;
                    mLocation = PlaybackLocation.LOCAL;
                    //  updatePlayButton(mPlaybackState);
                    invalidateOptionsMenu();
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

            }
        };
    }

}
