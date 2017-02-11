package cn.beeth0ven.photogallery;

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
    @SerializedName("url_s")
    public String url;

}
