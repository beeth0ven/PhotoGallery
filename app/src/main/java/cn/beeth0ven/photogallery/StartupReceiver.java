package cn.beeth0ven.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Air on 2017/2/15.
 */

public class StartupReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("StartupReceiver", "onReceive: " + intent);

        boolean isOn = UserDefaults.isAlarmOn.getValue();
        PollService.setServiceAlarm(context, isOn);
    }
}
