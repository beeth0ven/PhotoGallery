package cn.beeth0ven.photogallery;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by Air on 2017/2/8.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private FlickrFetchr flickrFetchr = new FlickrFetchr();

    public static PhotoGalleryFragment newInstanse() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        FlickrFetchr.galleries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        galleries -> {
                            Log.d("RxJava", "onNext:" + galleries);
                        },
                        throwable -> {
                            Log.d("RxJava", "onError:" + throwable);
                        },
                        () -> {
                            Log.d("RxJava", "onComplete.");
                        }
                );

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_gallery_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        return view;
    }
}
