package cn.beeth0ven.photogallery;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Created by Air on 2017/2/13.
 */

public class PollService extends IntentService {

    private List<Disposable> disposables = new ArrayList<Disposable>();

    public static Intent newInstanse(Context context) {
        return new Intent(context, PollService.class);
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = PollService.newInstanse(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = PollService.newInstanse(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return  pendingIntent != null;
    }

    public PollService() {
        super("PollService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }

        String searchText = QueryPreferences.searchText.getValue();
        String lastResultId = QueryPreferences.lastResultId.getValue();

        Observable<List<Gallery>> galleries = searchText == "" ?
                FlickrFetchr.galleries(1) :
                FlickrFetchr.searchGalleries(searchText);

        disposables.add(galleries
                .subscribe(newGalleries -> {
                    Log.i("PollService", "newGalleries");
                    if (newGalleries.size() == 0) { return; }
                    String resultId = newGalleries.get(0).id;
                    String oldOrNew = resultId.equals(lastResultId) ? "old" : "new";
                    Log.i("PollService", "Got " + oldOrNew + " result: " + resultId);
                    QueryPreferences.lastResultId.setValue(resultId);
                    QueryPreferences.searchText.setValue(QueryPreferences.searchText.getValue());
                }));

        Log.d("PollService", "onHandleIntent");

        Resources resources = getResources();
        Intent intent1 = PhotoGalleryActivity.newInstanse(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(resources.getString(R.string.new_pictures_text))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Disposable disposable: disposables) {
            disposable.dispose();
        }
        disposables.clear();
        Log.d("PollService", "onDestroy");
    }
}
