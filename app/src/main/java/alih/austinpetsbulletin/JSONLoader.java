package alih.austinpetsbulletin;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class JSONLoader {

    static InputStream mIns;
    static JSONObject mJObj;
    static String mJson;

    public JSONObject getJSONFromUrl(String url) {
        //make http request download to download the JSON
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            mIns = httpEntity.getContent();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(mIns, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append (line + "\n");
            }
            mIns.close();
            mJson = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer error", "Error converting result " +e.toString());
        }
        //try to parse the string to a json object
        try {
            mJObj = new JSONObject (mJson);
        } catch (JSONException e) {
            Log.e("JSON Parse", "Error parsing data " + e.toString());
        }
        return mJObj;
    }
}
