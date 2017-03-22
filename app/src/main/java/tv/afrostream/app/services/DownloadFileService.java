package tv.afrostream.app.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.RemoteViews;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import tv.afrostream.app.R;
import tv.afrostream.app.models.DownloadModel;
import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 15/02/2017.
 */


public class DownloadFileService extends IntentService {

    public DownloadFileService() {
        super("Download Service");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    long totalFileSize;

    Bitmap bmp;
    String file_url="";
    String title="";
    String tmDevice="";
    String TAG="DownloadFileService";
    String PathVideo="";
    String PathImage="";
    RemoteViews remoteViews;
    PendingIntent pStopSelf;
    Boolean StopWork=false;
    PowerManager.WakeLock wakeLock;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("CUSTOM_ACTION")) {
            //stopSelf();
            StopWork=true;

        } else {
            // other stuff
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    protected void onHandleIntent(Intent intent) {


        Log.d(TAG,"Start Service Download");

        if (intent==null)return;


        try {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "MyWakelockTag");
            wakeLock.acquire();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

       // Bundle extras = intent.getExtras();
         //bmp = (Bitmap) extras.getParcelable("imagebitmap");
        try {

            file_url = intent.getExtras().getString("url");
            title = intent.getExtras().getString("title");
            tmDevice = intent.getExtras().getString("tmDevice");
            PathVideo = intent.getExtras().getString("PathVideo");
            PathImage = intent.getExtras().getString("PathImage");
            tmDevice = intent.getExtras().getString("tmDevice");
        }catch (Exception ee) {

            return;

        }

        StopWork=false;

        Intent cancel = new Intent(this, DownloadFileService.class);
        cancel.setAction("CUSTOM_ACTION");
        pStopSelf = PendingIntent.getService(this,
                (int) System.currentTimeMillis(), cancel,
                PendingIntent.FLAG_UPDATE_CURRENT);










         remoteViews = new RemoteViews(getPackageName(),
                R.layout.download_notification);

        try{


            File imgFile = new  File(PathImage);

            if(imgFile.exists()){

                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());





                remoteViews.setImageViewBitmap(R.id.imagenotileft,myBitmap);

            }

        }catch (Exception ee)
        {
            ee.printStackTrace();
        }




        remoteViews.setTextViewText(R.id.txtTitle,title);

        remoteViews.setOnClickPendingIntent(R.id.bntCancel,pStopSelf);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle("Download "+title)
                 .setPriority(Notification.PRIORITY_MAX)


                .setCustomBigContentView(remoteViews)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());




      int count;
        try {
            URL url = new URL(file_url);
            URLConnection conection = url.openConnection();

            conection.setRequestProperty("Accept-Encoding", "identity");
            conection.setConnectTimeout(50000);

            conection.connect();

        /*    HttpURLConnection conection = (HttpURLConnection) url.openConnection();
            conection.setRequestMethod("GET");
            conection.setDoOutput(true);
            conection.setConnectTimeout(30000);
            conection.connect();*/




            // always check HTTP response code first
            int fileSize=100000000;




            // this will be useful so that you can show a tipical 0-100%
            // progress bar
            if (conection.getContentLength()!=-1) fileSize = conection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(url.openStream(), 1024 * 8);


            String pathout =PathVideo;

            // Output stream
            OutputStream output = new FileOutputStream(pathout);


        



            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();


            String test=tmDevice+","+dateFormat.format(date);

            byte dataheader[] = new byte[1024];


            String value= android.util.Base64.encodeToString( test.getBytes(), android.util.Base64.DEFAULT) ;
            int valuecount =value.getBytes().length;

            for (int i=valuecount;i<1024;i++)
            {
                value+="0";
            }

            valuecount =value.getBytes().length;
            dataheader=value.getBytes();



            output.write(dataheader, 0, 1024);



            long startTime = System.currentTimeMillis();
            int timeCount = 1;


            byte data[] = new byte[1024];

            long total = 0;
            totalFileSize = (long) (fileSize / (Math.pow(1024, 2)));
            while ((count = input.read(data)) != -1 && StopWork==false) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                try {
                    double current = Math.round(total / (Math.pow(1024, 2)));

                    long progress = (long) ((total * 100) / fileSize);

                    long currentTime = System.currentTimeMillis() - startTime;

                    DownloadModel download = new DownloadModel();
                    download.setTotalFileSize(totalFileSize);

                    if (currentTime > 1000 * timeCount) {

                        download.setCurrentFileSize((int) current);
                        download.setProgress((int) progress);
                        sendNotification(download);
                        timeCount++;
                    }
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }


                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

          if (StopWork==false)
              onDownloadComplete();
            else
          {
              notificationManager.cancel(0);
          }

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }


    }



    private void downloadFile(String body) throws IOException {

       /* int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream bis = new BufferedInputStream(body.byteStream(), 1024 * 8);
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "file.zip");
        OutputStream output = new FileOutputStream(outputFile);
        long total = 0;
        long startTime = System.currentTimeMillis();
        int timeCount = 1;
        while ((count = bis.read(data)) != -1) {

            total += count;
            totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));
            double current = Math.round(total / (Math.pow(1024, 2)));

            int progress = (int) ((total * 100) / fileSize);

            long currentTime = System.currentTimeMillis() - startTime;

            DownloadModel download = new DownloadModel();
            download.setTotalFileSize(totalFileSize);

            if (currentTime > 1000 * timeCount) {

                download.setCurrentFileSize((int) current);
                download.setProgress(progress);
                sendNotification(download);
                timeCount++;
            }

            output.write(data, 0, count);
        }
        onDownloadComplete();
        output.flush();
        output.close();
        bis.close();*/

    }

    private void sendNotification(DownloadModel download){

        //sendIntent(download);
        try {
            String nn = "";
            if (totalFileSize > 0) {
                notificationBuilder.setProgress(100, (int) download.getProgress(), false);
                nn = download.getCurrentFileSize() + "/" + totalFileSize + " MB";
            } else {
                notificationBuilder.setProgress(0, 0, false);
                nn = download.getCurrentFileSize() + " MB";
            }


            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText(nn);


            Intent cancel = new Intent(this, DownloadFileService.class);
            cancel.setAction("CUSTOM_ACTION");
            pStopSelf = PendingIntent.getService(this,
                    (int) System.currentTimeMillis(), cancel,
                    PendingIntent.FLAG_UPDATE_CURRENT);



            remoteViews.setTextViewText(R.id.txtDescription, nn);
            remoteViews.setProgressBar(R.id.Progress1, 100, (int) download.getProgress(), false);

            remoteViews.setOnClickPendingIntent(R.id.bntCancel,pStopSelf);

            notificationBuilder.setCustomBigContentView(remoteViews);


            notificationBuilder.setStyle(bigTextStyle);

            notificationBuilder.setPriority(Notification.PRIORITY_MAX);

          //  notificationBuilder.setWhen(0);
            //notificationBuilder.addAction(action) ;

            notificationManager.notify(0, notificationBuilder.build());
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }

    private void sendIntent(DownloadModel download){

        Intent intent = new Intent(StaticVar.MESSAGE_PROGRESS);
        intent.putExtra("download",download);
        LocalBroadcastManager.getInstance(DownloadFileService.this).sendBroadcast(intent);
    }

    private void onDownloadComplete(){

       // DownloadModel download = new DownloadModel();
      //  download.setProgress(100);
       // sendIntent(download);
        try {
            if (wakeLock != null) wakeLock.release();
        }catch (Exception ee)
        {

        }
        try {

            notificationManager.cancel(0);
            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentTitle("File Downloaded " + title);
            notificationBuilder.setContentText("File Downloaded " + title);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle(title);
            bigTextStyle.bigText("File Downloaded");

            notificationBuilder.setCustomBigContentView(null);
            notificationBuilder.setStyle(bigTextStyle);
            notificationBuilder.setContent(null);


            notificationManager.notify(0, notificationBuilder.build());
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (wakeLock != null) wakeLock.release();
        }catch (Exception ee)
        {

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        try {
            if (wakeLock != null) wakeLock.release();
        }catch (Exception ee)
        {

        }

        notificationManager.cancel(0);
    }




}