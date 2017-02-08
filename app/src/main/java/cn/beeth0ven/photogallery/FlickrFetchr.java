package cn.beeth0ven.photogallery;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;

/**
 * Created by Air on 2017/2/8.
 */

public class FlickrFetchr {

    private final String apiKey = "42bfe56a2112914841a702c7dd873cbc";

    public byte[] getUrlBytes(String urlSpec) throws IOException {
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

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    private static Observable<Void> galleries() {
        return Observable.create(observer -> {
            try {
                String result = new FlickrFetchr().getUrlString("https://www.bignerdranch.com");
                observer.onComplete();;
                Log.i("PhotoGalleryFragment", "Fetched contents of URL: " + result);
            } catch (IOException exception) {
                Log.e("PhotoGalleryFragment", "Failed to fetch URL: ", exception);
                observer.onError(exception);
            }
        });
    }
}


