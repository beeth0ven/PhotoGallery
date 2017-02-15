package cn.beeth0ven.photogallery.RxExtension;

import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * Created by Air on 2017/2/15.
 */

public class RxFragment extends Fragment {

    protected List<Disposable> disposables = new ArrayList<Disposable>();

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Disposable disposable: disposables) {
            disposable.dispose();
        }
        disposables.clear();
    }

}
