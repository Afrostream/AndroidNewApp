package tv.afrostream.app.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import tv.afrostream.app.ListDownloadSerieEpisodeCardView;
import tv.afrostream.app.activitys.PlayerActivity;
import tv.afrostream.app.models.MovieItemModel;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 15/02/2017.
 */




public class ListDownloadSerieEpisodeCardAdapter extends RecyclerView.Adapter {


    public ArrayList<MovieItemModel> moviesArray;




    private UUID getDrmUuid(String typeString) throws ParserException {
        switch (typeString.toLowerCase()) {
            case "widevine":
                return C.WIDEVINE_UUID;
            case "playready":
                return C.PLAYREADY_UUID;
            default:
                try {
                    return UUID.fromString(typeString);
                } catch (RuntimeException e) {
                    throw new ParserException("Unsupported drm type: " + typeString);
                }
        }
    }
    private abstract static class Sample {

        public final String name;
        public final boolean preferExtensionDecoders;
        public final UUID drmSchemeUuid;
        public final String drmLicenseUrl;
        public final String[] drmKeyRequestProperties;

        public Sample(String name, UUID drmSchemeUuid, String drmLicenseUrl,
                      String[] drmKeyRequestProperties, boolean preferExtensionDecoders) {
            this.name = name;
            this.drmSchemeUuid = drmSchemeUuid;
            this.drmLicenseUrl = drmLicenseUrl;
            this.drmKeyRequestProperties = drmKeyRequestProperties;
            this.preferExtensionDecoders = preferExtensionDecoders;
        }

        public Intent buildIntent(Context context) {
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, preferExtensionDecoders);
            if (drmSchemeUuid != null) {
                intent.putExtra(PlayerActivity.DRM_SCHEME_UUID_EXTRA, drmSchemeUuid.toString());
                intent.putExtra(PlayerActivity.DRM_LICENSE_URL, drmLicenseUrl);
                intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES, drmKeyRequestProperties);
                intent.putExtra(PlayerActivity.VIDEO_NAME, name);
            }
            return intent;
        }

    }

    private static final class UriSample extends Sample {

        public final String uri;
        public final String uriSmooth;
        public final String imageurl;
        public final String extension;
        public final String Video_ID;
        public final int playerPosition;
        public final String fpathOut;

        public UriSample(String name,String Video_ID,UUID drmSchemeUuid, String drmLicenseUrl,
                         String[] drmKeyRequestProperties, boolean preferExtensionDecoders, String uri,String uriSmooth,String imageurl,
                         String extension,int playerPosition,String fpathOut) {
            super(name, drmSchemeUuid, drmLicenseUrl, drmKeyRequestProperties, preferExtensionDecoders);
            this.uri = uri;
            this.extension = extension;
            this.uriSmooth=uriSmooth;
            this.imageurl=imageurl;
            this.Video_ID=Video_ID;
            this.playerPosition=playerPosition;
            this.fpathOut=fpathOut;
        }

        @Override
        public Intent buildIntent(Context context) {
            return super.buildIntent(context)
                    .setData(Uri.parse(uri))
                    .putExtra("uriSmooth",uriSmooth)
                    .putExtra("imageUrl",imageurl)
                    .putExtra("fpathOut",fpathOut)

                    .putExtra("Video_ID",Video_ID)
                    .putExtra("playerPosition",playerPosition)
                    .putExtra("video_type","")
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }

    }


    public class DecryptVideoFileAsync extends AsyncTask<String, Integer, Sample>{

        String fpath;
        String filename;
        String ImageUrl ;
        Context context;

        public DecryptVideoFileAsync(String fpath,String filename,String ImageUrl ,Context context){
            this.fpath = fpath;
            this.filename = filename;
            this.ImageUrl = ImageUrl;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (StaticVar.SerieDownloadDetailAct!=null)StaticVar.SerieDownloadDetailAct.ShowLoadingDialog();
        }

        @Override
        protected Sample doInBackground(String... params) {



            String state = Environment.getExternalStorageState();


            BufferedReader br = null;

            BufferedWriter bw  = null;


       
            String tmDevice = StaticVar.android_id;



            try {
                File file = context.getFilesDir();
                //String fpath =file.getAbsolutePath() +   File.separator +"SampleVideo_1280x720_1mb.mp4";
               // String fpathOut =file.getAbsolutePath() +   File.separator +filename +".avd";

                String fpathOut =filename +".avd";

                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();


                String value ="mp4 format encoding avc1.14.234"+ sha256("mp4 format encoding avc1.14.234"+dateFormat.format(date));
                int valuecount =value.getBytes().length;

                InputStream in = new FileInputStream(new File (fpath));


                //byte b2[]= Arrays.copyOfRange( b, 5, valuecount);

                // String str = new String(b2);



                OutputStream out = context.openFileOutput(fpathOut, Context.MODE_PRIVATE);





                byte dataheader[] = new byte[1024];



                int lenHeader;

                lenHeader = in.read(dataheader,0,1024);

                String nbHeader=new String (dataheader);


                nbHeader=nbHeader.substring(0,nbHeader.indexOf("00000"));

                byte[] data = android.util.Base64.decode(nbHeader, android.util.Base64.DEFAULT);
                String newHeader = new String(data, "UTF-8");




                String SplitHeader [] =newHeader.split(",");

                String androidId="";
                String FileDate="";
                Date Filedt=null;

                try
                {
                    androidId=SplitHeader[0];
                    FileDate=SplitHeader[1];

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    Filedt=formatter.parse(FileDate);
                }catch ( final Exception ee)
                {

                    final String errorM=ee.getMessage();

                    Handler handler =  new Handler(context.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(context,"Error :"+ errorM, Toast.LENGTH_LONG).show();
                        }
                    });

                    ee.printStackTrace();
                }


                if (!tmDevice.equals(androidId))
                {
                    Handler handler =  new Handler(context.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(context, "Incorrect device", Toast.LENGTH_LONG).show();
                        }
                    });

                    out.close();
                    return null;
                }

                Date dateNow = new Date();

                long diff = dateNow.getTime() - Filedt.getTime();
                long DateDiff= TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                if (DateDiff>5)
                {
                    Handler handler =  new Handler(context.getMainLooper());
                    handler.post( new Runnable(){
                        public void run(){
                            Toast.makeText(context, "Expiry file", Toast.LENGTH_LONG).show();
                        }
                    });
                    out.close();
                    return null;
                }



                byte[] bufstrat = new byte[16];


                byte[] bufvalue = new byte[valuecount];
                int len;

                len = in.read(bufstrat);
                out.write(bufstrat, 0, len);
                len = in.read(bufvalue);

                String nb8=new String (bufvalue);
                out.write(bufvalue, 0, 0);


                // Transfer bytes from in to out
                byte[] buf = new byte[1024*4];

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                out.flush();
                out.close();

                // copy(new File (fpath),new File (fpathOut));





                fpathOut =file.getAbsolutePath() +   File.separator +filename +".avd";

                String uri="file:///"+fpathOut;


                String extension = "mp4";
                // String extension = "mpd";
                String name=filename;
                UUID drmSchemeUuid= null;
                try {

                    drmSchemeUuid = getDrmUuid("");
                } catch (ParserException e) {
                    e.printStackTrace();
                }

                String drmLicenseUrl="";
                String[] drmKeyRequestProperties=null;

                ArrayList<String> drmKeyRequestPropertiesList = new ArrayList<>();


                drmKeyRequestProperties = drmKeyRequestPropertiesList.toArray(new String[0]);





                int playerPosition=0;

                String videoDashUrl=uri;
                String videoSmoothUrl=uri;
                String videoID="";

                Sample smp=new UriSample( name,videoID,  drmSchemeUuid,  drmLicenseUrl,   drmKeyRequestProperties,  false,videoDashUrl,videoSmoothUrl,ImageUrl,extension,playerPosition,fpathOut) ;

                return smp;






            }catch (Exception ee)
            {
                ee.printStackTrace();
            }


            return  null;

        }


        @Override
        protected void onPostExecute(Sample smp) {
            if (StaticVar.SerieDownloadDetailAct!=null)StaticVar.SerieDownloadDetailAct.dialogLoading.dismiss();
            if(smp!=null) {
                Intent nb = smp.buildIntent(context);
                nb.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                try {
                    StaticVar.path_decrypt_download_file = nb.getStringExtra("fpathOut");
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }

                context.startActivity(nb);
            }

        }

    }




    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public  String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public void readVideoFromFile (String fpath,String filename,String ImageUrl ,Context context)
    {


    }






    public ListDownloadSerieEpisodeCardAdapter( ArrayList<MovieItemModel>  chipsArray) {

        this.moviesArray = chipsArray;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {




        return new ChipViewHolder(new ListDownloadSerieEpisodeCardView(parent));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ((ListDownloadSerieEpisodeCardView)holder.itemView).displayItem(moviesArray.get(position));


        holder.itemView.                setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieItemModel item=moviesArray.get(position);

            File fl=new File(item.pathvideoFile);
                if (fl.exists()) {
                    if (StaticVar.SerieDownloadDetailAct!=null) {
                        DecryptVideoFileAsync asynctask = new DecryptVideoFileAsync(item.pathvideoFile, item.title, item.coverImageUrl, StaticVar.SerieDownloadDetailAct.getApplicationContext());
                        asynctask.execute("");
                    }


                }else
                {
                   if (StaticVar.SerieDownloadDetailAct!=null)StaticVar.SerieDownloadDetailAct.showToast("Video file not exist");
                }



            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesArray.size();
    }

    private class ChipViewHolder extends RecyclerView.ViewHolder {

        public ChipViewHolder(View itemView) {
            super(itemView);
        }
    }
}

