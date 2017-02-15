package cn.beeth0ven.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by Air on 2017/2/15.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    public static Intent newInstance(Context context, Uri uri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }

    @Override
    public void onBackPressed() {
        PhotoPageFragment fragment = (PhotoPageFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (fragment.webView.canGoBack()) {
            fragment.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
