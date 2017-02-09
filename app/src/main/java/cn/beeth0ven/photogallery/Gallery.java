package cn.beeth0ven.photogallery;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Air on 2017/2/8.
 */

public class Gallery {

    public String title;
    public String id;
    public String url;

    public Gallery(JSONObject json) throws JSONException {
        id = json.getString("id");
        title = json.getString("title");

        if (json.has("url_s")) {
            url = json.getString("url_s");
        }
    }

}
