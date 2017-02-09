package cn.beeth0ven.photogallery;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Air on 2017/2/8.
 */

public class FlickrFetchr {

    private static final String apiKey = "42bfe56a2112914841a702c7dd873cbc";

    public static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with" + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            return outputStream.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public static Observable<List<Gallery>> galleries() {
        Log.i("FlickrFetchr", "galleries");
        Observable<JSONObject> galleries = Observable.create(observer -> {
            try {
//                String url = Uri.parse("https://api.flickr.com/services/rest/")
//                        .buildUpon()
//                        .appendQueryParameter("method", "flickr.photos.getRecent")
//                        .appendQueryParameter("api_key", apiKey)
//                        .appendQueryParameter("format", "json")
//                        .appendQueryParameter("nojsoncallback", "1")
//                        .appendQueryParameter("extras", "url_s")
//                        .build().toString();
                String url = Uri.parse("https://api.flickr.com/services/rest/")
                        .buildUpon()
                        .appendQueryParameter("method", "flickr.photos.getRecent")
                        .appendQueryParameter("api_key", apiKey)
                        .appendQueryParameter("format", "json")
                        .appendQueryParameter("nojsoncallback", "1")
                        .appendQueryParameter("extras", "url_s")
                        .build().toString();
                Log.i("FlickrFetchr", "URL: " + url);
                String jsonString = getUrlString(url);
                JSONObject json = new JSONObject(jsonString);
                observer.onNext(json);
                observer.onComplete();
                Log.i("FlickrFetchr", "Fetched contents of URL: ");
            } catch (IOException exception) {
                observer.onError(exception);
                Log.e("FlickrFetchr", "Failed to fetch URL: ", exception);
            }
        });

        return galleries
                .map(json -> {
                    JSONArray jsons = json.getJSONObject("photos")
                            .getJSONArray("photo");

                    List<Gallery> results = new ArrayList<Gallery>();
                    for (int i = 0; i < jsons.length(); i++) {
                        Gallery gallerie = new Gallery(jsons.getJSONObject(i));
                        results.add(gallerie);
                    }
                    Log.d("FlickrFetchr", "galleries count: " + results.size());
                    Log.d("FlickrFetchr", "jsons: " + jsons);
                    return results;
                }).subscribeOn(Schedulers.newThread());
    }
    
    private static List toList(JSONArray jsons) throws JSONException {
        List objects = new ArrayList();
        for (int i = 0; i < jsons.length(); i++) {
            objects.add(jsons.get(i));
        }
        return objects;
    }
}


