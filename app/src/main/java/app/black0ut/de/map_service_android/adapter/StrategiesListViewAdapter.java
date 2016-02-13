package app.black0ut.de.map_service_android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.StrategyListItemView;
import app.black0ut.de.map_service_android.StrategyListItemView_;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.data.Strategy;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 11.02.2016.
 */
@EBean
public class StrategiesListViewAdapter extends BaseAdapter {

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    private SharedPreferences sharedPreferences;
    private String mUsername;
    private List<Strategy> strategies;

    //You can inject the root Android component that depends on your @EBean class, using
    // the @RootContext annotation. Please notice that it only gets injected if the context has the right type.
    //Quelle: https://github.com/excilys/androidannotations/wiki/Enhance-custom-classes
    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        sharedPreferences = context.getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);
        strategies = new ArrayList<>();
        refreshItems();
    }

    void refreshItems() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            HashMap<String, String> getTacsMap = new HashMap<>();
            getTacsMap.put("user", mUsername);

            mSocket.on("status", status);
            mSocket.connect();
            mSocket.emit("getTacs", JSONCreator.createJSON("getTacs", getTacsMap).toString());
        } else {
            Toast.makeText(context, "Du bist leider nicht angemeldet. Bitte melde Dich an.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StrategyListItemView strategyListItemView;
        if (convertView == null) {
            strategyListItemView = StrategyListItemView_.build(context);
        } else {
            strategyListItemView = (StrategyListItemView) convertView;
        }

        strategyListItemView.bind(getItem(position));

        return strategyListItemView;
    }

    @Override
    public int getCount() {
        return strategies.size();
    }

    @Override
    public Strategy getItem(int position) {
        return strategies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Emitter.Listener status = new Emitter.Listener() {
        Activity activity = (Activity) context;

        @Override
        public void call(final Object... args) {
            if (activity == null)
                return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    JSONArray jsonArray;
                    Gson gson = new Gson();
                    try {
                        emitterStatus = data.getString("status");
                        jsonArray = data.getJSONArray("tacs");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("provideTacs")) {

                        Log.d("TEST", "provideTacs");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                strategies.add((Strategy)jsonArray.get(i));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    mSocket.disconnect();
                    mSocket.off();
                }
            });
        }
    };
}
