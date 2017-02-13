package cn.beeth0ven.photogallery;

import cn.beeth0ven.photogallery.RxExtension.ComputedVariable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Air on 2017/2/8.
 */

public class PhotoGalleryFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView loadingTextView;
    private GalleryAdapter galleryAdapter = new GalleryAdapter(new ArrayList<Gallery>());
    private FlickrFetchr flickrFetchr = new FlickrFetchr();
    private List<Disposable> disposables = new ArrayList<Disposable>();
//    private BehaviorSubject<Integer> currentPage = BehaviorSubject.createDefault(1);
    private ComputedVariable<String> searchText;
    private PublishSubject<Integer> recycleViewWidth = PublishSubject.create();
    private boolean isLoading = false;
    private boolean isViewDidLoad = false;

    public static PhotoGalleryFragment newInstanse() {
        return new PhotoGalleryFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        searchText =  new ComputedVariable<String>(
                () -> {
                    return PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString("PhotoGalleryFragment.searchText", "");
                },
                newValue -> {
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .edit()
                            .putString("PhotoGalleryFragment.searchText", newValue)
                            .apply();
                }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Disposable disposable: disposables) {
            disposable.dispose();
        }
        disposables.clear();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.photo_gallery_fragment, menu);

        MenuItem searchMenuItem = menu.findItem(R.id.searchMenuItem);
        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setQuery(searchText.getValue(), false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText.setValue(newText);
                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_gallery_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//            Log.d("PhotoGalleryFragment", "onGlobalLayout");
            recycleViewWidth.onNext(recyclerView.getWidth());
        });

        disposables.add(recycleViewWidth.distinctUntilChanged()
            .subscribe(width -> {
                int coulumns = width / 360;
                recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), coulumns));
                recyclerView.setAdapter(galleryAdapter);
            })
        );

        disposables.add(searchText.asObservable()
                .filter(text -> !text.isEmpty())
                .debounce(1, TimeUnit.SECONDS)
//                .flatMap(FlickrFetchr::galleries)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(newText ->  {
                    Log.d("PhotoGalleryFragment", "newText: " + newText);
                    loadingTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                })
                .flatMap(FlickrFetchr::searchGalleries)
                .subscribe(
                        galleries -> {
                            Log.d("RxJava", "onNext");
                            galleryAdapter.galleries = galleries;
                            galleryAdapter.notifyDataSetChanged();
                            isLoading = false;
                            loadingTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        },
                        throwable -> {
                            Log.d("RxJava", "onError:" + throwable);
                        },
                        () -> {
                            Log.d("RxJava", "onComplete.");
                        }
                )
        );

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
//                int totolCount = layoutManager.getItemCount();
//                int visibleCount = layoutManager.getChildCount();
//                int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
//                if (firstVisiblePosition + visibleCount >= totolCount) {
//                    if (isLoading) { return; }
//                    isLoading = true;
//                    Log.d("PhotoGalleryFragment", "Loading...");
//                    currentPage.onNext(currentPage.getValue() + 1);
//                }
//            }
//        });

        loadingTextView = (TextView) view.findViewById(R.id.loadingTextView);

        return view;
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }

        public void bindGallery(Gallery gallery) {
            Picasso.with(getActivity())
                    .load(gallery.url)
//                    .placeholder(R.drawable.bill_up_close)
                    .into(imageView);
        }
    }

    private class GalleryAdapter extends RecyclerView.Adapter<ViewHolder> {

        public List<Gallery> galleries;

        public GalleryAdapter(List<Gallery> galleries) {
            this.galleries = galleries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.image_view_cell, parent, false);
            return new ViewHolder(view);
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
