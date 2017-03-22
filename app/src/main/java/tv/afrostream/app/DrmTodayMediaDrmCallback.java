package tv.afrostream.app;

/**
 * Created by bahri on 19/01/2017.
 */

import android.annotation.TargetApi;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import com.google.android.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import com.google.android.exoplayer2.drm.MediaDrmCallback;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tv.afrostream.app.utils.StaticVar;


@TargetApi(18)
public final class DrmTodayMediaDrmCallback implements MediaDrmCallback {

    private static final Map<String, String> PLAYREADY_KEY_REQUEST_PROPERTIES;
    static {
        PLAYREADY_KEY_REQUEST_PROPERTIES = new HashMap<>();
        PLAYREADY_KEY_REQUEST_PROPERTIES.put("Content-Type", "text/xml");
        PLAYREADY_KEY_REQUEST_PROPERTIES.put("SOAPAction",
                "http://schemas.microsoft.com/DRM/2007/03/protocols/AcquireLicense");
    }

    private final HttpDataSource.Factory dataSourceFactory;
    private final String defaultUrl;
    private final Map<String, String> keyRequestProperties;

    /**
     * @param defaultUrl The default license URL.
     * @param dataSourceFactory A factory from which to obtain {@link HttpDataSource} instances.
     */
    public DrmTodayMediaDrmCallback(String defaultUrl, HttpDataSource.Factory dataSourceFactory) {
        this(defaultUrl, dataSourceFactory, null);
    }

    /**
     * @param defaultUrl The default license URL.
     * @param dataSourceFactory A factory from which to obtain {@link HttpDataSource} instances.
     * @param keyRequestProperties Request properties to set when making key requests, or null.
     */
    public DrmTodayMediaDrmCallback(String defaultUrl, HttpDataSource.Factory dataSourceFactory,
                                    Map<String, String> keyRequestProperties) {
        this.dataSourceFactory = dataSourceFactory;
        this.defaultUrl = defaultUrl;
        this.keyRequestProperties = keyRequestProperties;
    }

    @Override
    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest request) throws IOException {
        String url = request.getDefaultUrl() + "&signedRequest=" + new String(request.getData());
        return executePost(url, new byte[0], null);
    }

    @Override
    public byte[] executeKeyRequest(UUID uuid, KeyRequest request) throws Exception {
        String url = request.getDefaultUrl();
        if (TextUtils.isEmpty(url)) {
            url = defaultUrl;
        }
        Map<String, String> requestProperties = new HashMap<>();
        requestProperties.put("Content-Type", "application/octet-stream");
        if (C.PLAYREADY_UUID.equals(uuid)) {
            requestProperties.putAll(PLAYREADY_KEY_REQUEST_PROPERTIES);
        }
        if (keyRequestProperties != null) {
            requestProperties.putAll(keyRequestProperties);
        }
        return executePost(url, request.getData(), requestProperties);
    }

    private byte[] executePost(String url, byte[] data, Map<String, String> requestProperties)
            throws IOException {
        HttpDataSource dataSource = dataSourceFactory.createDataSource();
        if (requestProperties != null) {
            for (Map.Entry<String, String> requestProperty : requestProperties.entrySet()) {
                dataSource.setRequestProperty(requestProperty.getKey(), requestProperty.getValue());
            }
        }

        JSONObject js=new JSONObject ();
        try {
            js.put("userId", StaticVar.user_id);
            js.put("sessionId", StaticVar.access_token);
            js.put("merchant", StaticVar.DRMMerchant);
        }catch ( Exception ee)
        {
            ee.printStackTrace();
        }


        byte[] dataBase64 = new byte[0];
        try {
            dataBase64 = js.toString().getBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String base64 =android.util.Base64.encodeToString(dataBase64, Base64.DEFAULT);
        dataSource.setRequestProperty("x-dt-custom-data", base64);

        DataSpec dataSpec = new DataSpec(Uri.parse(url), data, 0, 0, C.LENGTH_UNSET, null,
                DataSpec.FLAG_ALLOW_GZIP);
        DataSourceInputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);
        try {
            return Util.toByteArray(inputStream);
        } finally {
            inputStream.close();
        }
    }

}


