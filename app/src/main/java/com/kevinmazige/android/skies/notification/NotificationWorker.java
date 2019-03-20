package com.kevinmazige.android.skies.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.kevinmazige.android.skies.R;
import com.kevinmazige.android.skies.ui.path.PathActivity;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static com.kevinmazige.android.skies.notification.VisualPassWorker.ID;
import static com.kevinmazige.android.skies.notification.VisualPassWorker.NAME;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_FAVOURITE_STATUS;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_ID;
import static com.kevinmazige.android.skies.ui.path.PathActivity.EXTRA_SAT_NAME;

public class NotificationWorker extends Worker {

    private NotificationManager mNotificationManager;
    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private Context mContext;


    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @NonNull
    @Override
    public Result doWork() {
        deliverNotification(mContext);
        return Result.success();
    }

    /*
     * create visual pass notifications
     */
    private void deliverNotification(Context context) {


        String name = getInputData().getString(NAME);
        int id = getInputData().getInt(ID, -1);

        /*
         * the notification has a pending intent that will launch the path activity when clicked
         */
        Intent contentIntent = new Intent(context, PathActivity.class);

        contentIntent.putExtra(EXTRA_SAT_ID, id);
        contentIntent.putExtra(EXTRA_SAT_NAME, name);
        contentIntent.putExtra(EXTRA_FAVOURITE_STATUS, true);

        /*
         * This step is necessary for proper up navigation when the path activity is started from a
         * notification
         */
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(contentIntent);

        PendingIntent contentPendingIntent =
                stackBuilder.getPendingIntent(NOTIFICATION_ID, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.satellite)
                .setContentText(name + " beginning visual pass!")
                .setContentIntent(contentPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}
