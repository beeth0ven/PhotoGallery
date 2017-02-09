package cn.beeth0ven.photogallery;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Air on 2017/2/8.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter = new GalleryAdapter(new ArrayList<Gallery>());
    private FlickrFetchr flickrFetchr = new FlickrFetchr();
    private List<Disposable> disposables = new ArrayList<Disposable>();

    public static PhotoGalleryFragment newInstanse() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        disposables.add(FlickrFetchr.galleries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        galleries -> {
                            Log.d("RxJava", "onNext:" + galleries);
                            galleryAdapter.galleries.addAll(galleries);
                            galleryAdapter.notifyDataSetChanged();
                        },
                        throwable -> {
                            Log.d("RxJava", "onError:" + throwable);
                        },
                        () -> {
                            Log.d("RxJava", "onComplete.");
                        }
                )
        );


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Disposable disposable: disposables) {
            disposable.dispose();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_gallery_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(galleryAdapter);
        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }

        public void bindGallery(Gallery gallery) {
            textView.setText(gallery.title);
        }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

        public List<Gallery> galleries;

        public GalleryAdapter(List<Gallery> galleries) {
            this.galleries = galleries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(new TextView(getActivity()));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindGallery(galleries.get(position));
        }

        @Override
        public int getItemCount() {
            return galleries.size();
        }
    }
}
