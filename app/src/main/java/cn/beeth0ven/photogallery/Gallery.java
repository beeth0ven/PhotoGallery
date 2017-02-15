package cn.beeth0ven.photogallery;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Air on 2017/2/8.
 */

public class Gallery {

    public String title;
    public String id;
    public String owner;
    @SerializedName("url_s")
    public String url;

    public Uri getWebUri() {
        return Uri.parse("http://www.flickr.com/photos/")
                .buildUpon()
                .appendPath(owner)
                .appendPath(id)
                .build();
    }

}
