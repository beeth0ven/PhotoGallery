package cn.beeth0ven.photogallery;

import android.preference.PreferenceManager;

import cn.beeth0ven.photogallery.RxExtension.ComputedVariable;
import cn.beeth0ven.photogallery.RxExtension.MyVoid;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Air on 2017/2/13.
 */

public class UserDefaults {

    public static ComputedVariable<String> searchText =  new ComputedVariable<String>(
            () -> {
                return PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .getString("searchText", "");
            },
            newValue -> {
                PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .edit()
                        .putString("searchText", newValue)
                        .apply();
            }
    );

    public static ComputedVariable<String> lastResultId =  new ComputedVariable<String>(
            () -> {
                return PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .getString("lastResultId", "");
            },
            newValue -> {
                PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .edit()
                        .putString("lastResultId", newValue)
                        .apply();
            }
    );

    public static ComputedVariable<Boolean> isAlarmOn = new ComputedVariable<Boolean>(
            () -> {
                return PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .getBoolean("isAlarmOn", false);
            },
            newValue -> {
                PreferenceManager.getDefaultSharedPreferences(MyApplication.context)
                        .edit()
                        .putBoolean("isAlarmOn", newValue)
                        .apply();
            }
    );

}

