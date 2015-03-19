package alih.austinpetsbulletin;
//package org.json;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    ListView mList;
    JSONArray jsonArray;
    List<HashMap<String, String>> entries;
    private static final String urlString = "http://www.cs.utexas.edu/~alih/pets_example.json";
    private static final String BULLETIN_TAG = "AustinPB";

    /* www.cs.utexas.edu/~alih/pets_example.json is used to not repeatedly bang on the
     * Austin server while testing, and to have easily reproducible results. */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        entries = new ArrayList<HashMap<String, String>>();

        // First begin by testing if the network is connected
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data:
            new JSONLoad().execute(urlString);
        } else {
            Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Never network in the UI thread
    private class JSONLoad extends AsyncTask<String, Void, JSONObject> {
        protected JSONObject doInBackground(String... params) {

            JSONLoader jLoader = new JSONLoader();
            JSONObject json = null;

            // params comes from the execute() call: params[0] is the urlString.
            URL url = null;
            try {
                url = new URL(params[0]);
                Log.d(BULLETIN_TAG, "Url to download from: " + url.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            HttpURLConnection urlConnection = null;
            if(url != null){
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(urlConnection != null){
                try {
                    //Time to get JSON from URL
                    Log.d(BULLETIN_TAG , "Loading JSON from URL");
                    json = jLoader.getJSONFromUrl(url.toString());

                    jsonArray = json.getJSONArray("data");

//                    JSONArray c = jsonArray.getJSONArray(0);
//                    JSONArray cc = c.getJSONArray(0);
//                    JSONObject dd = cc.getJSONObject(0);
//                    String address = dd.getString("address");

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        HashMap<String, String> map = new HashMap<String, String>();
                        String address = jsonArray.getString(i);
                        // TODO: Actually parse out the data from JSON, in spite of the broken API
                        map.put("address", address);
                        entries.add(map);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                finally {
                    urlConnection.disconnect();
                }
            }
            return json;
        }

        protected void onPostExecute(JSONObject json) {
            if(json != null){

                mList = (ListView)findViewById(R.id.list);
                ListAdapter adapter = new SimpleAdapter(MainActivity.this, entries,
                        R.layout.json_list_view, new String[] {"address"}, new int[] {R.id.pet_entry});
                mList.setAdapter(adapter);
            }
        }
    }//end private inner Async Task Class.
}
