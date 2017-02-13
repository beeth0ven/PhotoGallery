package cn.beeth0ven.photogallery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
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
                }));

        Log.d("PollService", "onHandleIntent");
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
