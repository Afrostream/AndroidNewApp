package tv.afrostream.app.services;

/**
 * Created by bahri on 08/02/2017.
 */




import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.io.IOException;
import java.net.URL;
import java.util.Map;

import tv.afrostream.app.R;
import tv.afrostream.app.activitys.MainActivity;




public class AfrostreamFirebaseMessagingService extends FirebaseMessagingService {

        private static final String TAG = "FirebaseMsgService";



        private int getNotificationIcon() {
                boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
                return useWhiteIcon ? R.drawable.ic_notif : R.drawable.notif;
        }

        /**
         * Called when message is received.
         *
         * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.

        // [START receive_message] */
        @Override
        public void onMessageReceived(RemoteMessage remoteMessage) {
                // [START_EXCLUDE]
                // There are two types of messages data messages and notification messages. Data messages are handled
                // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
                // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
                // is in the foreground. When the app is in the background an automatically generated notification is displayed.
                // When the user taps on the notification they are returned to the app. Messages containing both notification
                // and data payloads are treated as notification messages. The Firebase console always sends notification
                // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
                // [END_EXCLUDE]

                // TODO(developer): Handle FCM messages here.
                // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
                Log.d(TAG, "From: " + remoteMessage.getFrom());

                // Check if message contains a data payload.
                if (remoteMessage.getData().size() > 0) {
                        Log.d(TAG, "Message data payload: " + remoteMessage.getData());
                }

                // Check if message contains a notification payload.
                if (remoteMessage.getNotification() != null) {
                        RemoteMessage.Notification notification = remoteMessage.getNotification();
                        Map<String, String> map = remoteMessage.getData();

                        sendNotification(notification.getTitle(),notification.getBody(),  map);
                }

                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.
        }
        // [END receive_message]

        /**
         * Create and show a simple notification containing the received FCM message.
         *
         * @param messageBody FCM message body received.*/



        private void sendNotification(String title,String messageBody, Map<String, String> map) {
                Intent intent=null ;//= new Intent(this, MainActivity.class);




                //PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);



                try {
                        String action = map.get("action");
                        if (action != null && !"".equals(action)) {
                               if (action.equals("open_url"))
                               {
                                       String open_url = map.get("open_url");

                                       if (!open_url.equals("")) {
                                               if (!open_url.startsWith("http://") && !open_url.startsWith("https://"))
                                                       open_url = "http://" + open_url;

                                               intent = new Intent(Intent.ACTION_VIEW);
                                               intent.setData(Uri.parse(open_url));
                                               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                                       }


                               }else if(action.equals("open_video"))
                               {
                                       String movieID = map.get("movieID");
                                        intent= new Intent(getApplicationContext(), MainActivity.class);



                                       intent.putExtra("action","open_video");
                                       intent.putExtra("movieID",movieID);


                                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                               }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }



                PendingIntent pendingIntent =   PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);



                Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)


                        .setContentIntent(pendingIntent);







                try {
                        String picture_url = map.get("picture_url");
                        if (picture_url != null && !"".equals(picture_url)) {
                                URL url = new URL(picture_url);
                                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                                notificationBuilder. setLargeIcon(bigPicture);
                                notificationBuilder. setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigPicture));


                        }
                } catch (IOException e) {
                        e.printStackTrace();
                }



                notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
              //  notificationBuilder.setLights(Color.YELLOW, 1000, 300);

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, notificationBuilder.build());


        }
}