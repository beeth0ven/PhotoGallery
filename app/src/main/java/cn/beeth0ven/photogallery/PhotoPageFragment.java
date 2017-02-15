package cn.beeth0ven.photogallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import cn.beeth0ven.photogallery.RxExtension.RxFragment;

/**
 * Created by Air on 2017/2/15.
 */

public class PhotoPageFragment extends RxFragment {

    private Uri uri;
    public WebView webView;
    private ProgressBar progressBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("uri", uri);
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uri = getArguments().getParcelable("uri");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_page_fragment, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(100);

        webView = (WebView) view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d("PhotoPageFragment", "onProgressChanged: " + newProgress);
                switch (newProgress) {
                    case 100:
                        progressBar.setVisibility(View.GONE);
                        break;
                    default:
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                activity.getSupportActionBar().setSubtitle(title);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                Uri uri = Uri.parse(url);
                switch (uri.getScheme().toUpperCase()) {
                    case "HTTP":
                        return false;
                    case "HTTPS":
                        return false;
                    default:
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        getActivity().startActivity(intent);
                        return true;
                }
            }
        });

        webView.loadUrl(uri.toString());

        return view;
    }

}
