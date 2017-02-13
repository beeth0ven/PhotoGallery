package cn.beeth0ven.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
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

    public static Observable<List<Gallery>> galleries(int page) {
        Log.i("FlickrFetchr", "galleries");

        Map<String, String> params = new HashMap();
        params.put("method", "flickr.photos.getRecent");
        params.put("page", String.valueOf(page));

        return json(params)
                .map(json -> {
                    String jsonString = json.getJSONObject("photos")
                            .getJSONArray("photo")
                            .toString();
                    List<Gallery> results = new Gson().fromJson(jsonString, new TypeToken<List<Gallery>>(){}.getType());
                    Log.d("FlickrFetchr", "galleries count: " + results.size());
                    return results;
                });
    }

    public static Observable<List<Gallery>> searchGalleries(String text) {
        Log.i("FlickrFetchr", "searchGalleries");

        Map<String, String> params = new HashMap();
        params.put("method", "flickr.photos.search");
        params.put("text", text);

        return json(params)
                .map(json -> {
                    String jsonString = json.getJSONObject("photos")
                            .getJSONArray("photo")
                            .toString();
                    List<Gallery> results = new Gson().fromJson(jsonString, new TypeToken<List<Gallery>>(){}.getType());
                    Log.d("FlickrFetchr", "galleries count: " + results.size());
                    return results;
                });
    }

    private static Observable<JSONObject> json(Map<String, String> params) {
        Observable<JSONObject> result = Observable.create(observer -> {
           try {
               Uri.Builder builder = Uri.parse("https://api.flickr.com/services/rest/")
                       .buildUpon()
                       .appendQueryParameter("api_key", apiKey)
                       .appendQueryParameter("format", "json")
                       .appendQueryParameter("nojsoncallback", "1")
                       .appendQueryParameter("extras", "url_s");

               for (Map.Entry<String, String> entry: params.entrySet()) {
                   builder.appendQueryParameter(entry.getKey(), entry.getValue());
               }

               String url = builder.build().toString();
               Log.i("FlickrFetchr", "url: " + url);
               String jsonString = getUrlString(url);
               JSONObject json = new JSONObject(jsonString);
               observer.onNext(json);
               observer.onComplete();
           } catch (IOException exception) {
               observer.onError(exception);
           }
        });

        return result;
    }
}
