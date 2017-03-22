package tv.afrostream.app.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import tv.afrostream.app.utils.StaticVar;

/**
 * Created by bahri on 20/02/2017.
 */


public class LocalModeService extends IntentService {

    public LocalModeService() {
        super("Download Service");
    }


    String action="";
    String jsonFile="";

    String TAG="LocalModeService";

    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;



    public class DownloadRunnable implements Runnable {

        String url;
        String path;

        public DownloadRunnable(String Url,String Path) {
            this.url = Url;
            this.path = Path;
        }

        @Override
        public void run() {


            int count;
            InputStream input=null;
            OutputStream output=null;
            try {
                URL url = new URL(this.url);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                 input = new BufferedInputStream(url.openStream(),
                        8192);

                // Output stream
                 output = new FileOutputStream(this.path);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    //  publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }finally {

                // flushing output
                try {


                    // closing streams
                    output.close();
                    input.close();
                }catch (Exception ee)
                {
                    ee.printStackTrace();
                }


            }

        }
    }




    public static String SaveSlideJson(JSONArray object) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += "slides.afj";
        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }

        BufferedWriter output = new BufferedWriter(new FileWriter(data));
        output.write(object.toString());
        output.close();

        return path;
    }

    public static String SaveCatMoviesJson(JSONArray object) throws IOException {
        String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        path += "catmovies.afj";
        File data = new File(path);
        if (!data.createNewFile()) {
            data.delete();
            data.createNewFile();
        }

        BufferedWriter output = new BufferedWriter(new FileWriter(data));
        output.write(object.toString());
        output.close();

        return path;
    }

    public void SavaBitmapToFile(Bitmap bitmap,String filename)
    {
        try {

            String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            path +=filename+ ".aim";
            File f = new File(path);


            f.createNewFile();



            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();


            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        }catch (Exception ee)
        {
            ee.printStackTrace();
        }
    }
    public JSONArray FileToJsonArray(File fl)
    {
        BufferedReader input = null;
        JSONArray jsonObject=null;
        try {
            input = new BufferedReader(new FileReader(fl));
            String line;
            StringBuffer content = new StringBuffer();
            char[] buffer = new char[1024];
            int num;
            while ((num = input.read(buffer)) > 0) {
                content.append(buffer, 0, num);
            }
            jsonObject = new JSONArray(content.toString());
            return jsonObject;

        }catch (Exception e) {


            return jsonObject;

        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    protected void onHandleIntent(Intent intent) {

        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                NUMBER_OF_CORES*2,
                NUMBER_OF_CORES*2,
                1,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>()
        );
        action=intent.getExtras().getString("action");
        //jsonFile=intent.getExtras().getString("jsonFile");


        JSONArray jsonArr=null;


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        try{

            Log.d(TAG,"Start Service LocalMode");




            if (action.equals("slides"))
            {

                String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
                File dirSlide = new File(path);
                if (!dirSlide.exists()) {
                    dirSlide.mkdirs();
                }
                path += "slides.afj";

                JSONArray response= FileToJsonArray(new File(path));

                if (response!=null)
                    jsonArr=response;
                else
                return;




                //SaveSlideJson(jsonArr);


                for (int i = 0; i < jsonArr.length(); i++) {
                    try{

                        JSONObject movie = (JSONObject) jsonArr.get(i);
                          String idMovie=movie.getString("_id");
                        String titleMovie=movie.getString("title");
                        final String filename="afro_local_slides_"+idMovie;

                        try{
                            JSONObject posterMovie  =movie.getJSONObject("poster");
                            String urlImageMovie= posterMovie.getString("imgix")+"?&crop=entropy&fit=min&w=550&h=350&q=100&fm=jpg&facepad=1&crop=entropy&auto=compress";

                            String pathImage = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

                            File dir = new File(pathImage);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }

                            pathImage +=filename+ ".aim";
                            DownloadRunnable rn=new  DownloadRunnable(urlImageMovie,pathImage);

                            executor.execute(rn);

                            //new Thread(rn).start();


                        }catch (Exception ee)
                        {


                            ee.printStackTrace();
                        }

                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                }
            }else if (action.equals("CatMovies"))
            {



                String path = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator;
                File dirSlide = new File(path);
                if (!dirSlide.exists()) {
                    dirSlide.mkdirs();
                }
                path += "catmovies.afj";

                JSONArray response= FileToJsonArray(new File(path));

                if (response!=null)
                    jsonArr=response;
                else
                    return;


                for (int i = 0; i < jsonArr.length(); i++) {
                    try{

                        JSONObject cat = (JSONObject) jsonArr.get(i);

                        String label = cat.getString("label");
                        int _id = cat.getInt("_id");
                        JSONArray movies =cat.getJSONArray("movies");



                        for (int x=0 ;x< movies.length();x++)
                        {
                            try{
                                JSONObject movie= (JSONObject) movies.get(x);
                                String titleMovie=movie.getString("title");
                                String idMovie=movie.getString("_id");
                                String labelMovie="";
                                try {
                                    labelMovie = movie.getString("genre");
                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }

                                final String filename="afro_local_"+idMovie;
                                try {
                                    JSONObject posterMovie = movie.getJSONObject("poster");
                                    String urlImageMovie = posterMovie.getString("imgix") + "?&crop=entropy&fit=min&w=250&h=200&q=90&fm=jpg&facepad=1&crop=entropy";


                                    String pathImage = Environment.getExternalStorageDirectory() + File.separator + StaticVar.LocalFolderName + File.separator+filename+ File.separator;

                                    File dir = new File(pathImage);
                                    if (!dir.exists()) {
                                        dir.mkdirs();
                                    }


                                    pathImage +=filename+ ".aim";
                                    DownloadRunnable rn=new  DownloadRunnable(urlImageMovie,pathImage);

                                    executor.execute(rn);


                                }catch (Exception ee)
                                {
                                    ee.printStackTrace();
                                }

                            }catch (Exception ee)
                            {
                                ee.printStackTrace();
                            }

                        }

                    }catch (Exception ee)
                    {
                        ee.printStackTrace();
                    }



                }

            }



        }catch (Exception ee)
        {
            ee.printStackTrace();



        }



    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

        super.onTaskRemoved(rootIntent);
    }




}