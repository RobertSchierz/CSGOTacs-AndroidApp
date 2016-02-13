package app.black0ut.de.map_service_android.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.StrategiesListViewAdapter;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.data.Strategy;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 30.12.2015.
 */

@EFragment(R.layout.fragment_strategies)
public class StrategiesFragment extends Fragment {

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    private SharedPreferences sharedPreferences;
    private List<Strategy> strategies = new ArrayList<>();
    private String mUsername;

    @ViewById
    public ListView strategiesListView;
    @ViewById
    public TextView noStrats;

    private StrategiesListViewAdapter adapter;

    @AfterViews
    public void bindAdapter() {
        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);
        adapter = new StrategiesListViewAdapter(strategies, getContext());
        strategiesListView.setAdapter(adapter);
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
            Toast.makeText(getContext(), "Du bist leider nicht angemeldet. Bitte melde Dich an.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Diese Methode behandelt das Tippen auf die verschiedenen Listenelemente.
     *
     * @param strategy Ein Parameter vom Typ Strategy, welcher die geklickte Strategie beinhaltet.
     */
    @ItemClick
    void strategiesListViewItemClicked(Strategy strategy) {
        Map.clickedMapName = strategy.map;
        swapFragment(strategy);
    }

    /**
     * Ersetzt das aktuelle Fragment durch ein MapsDetailFragment, welches das Bild mit der geklickten
     * Strategie anzeigt.
     */
    public void swapFragment(Strategy strategy) {
        //Das Strategy Objekt über das Bundle an das nächste Fragment weitergeben
        Bundle bundle = new Bundle();
        bundle.putLong("stratId", strategy.id);
        bundle.putString("stratUser", strategy.user);
        bundle.putString("stratMap", strategy.map);
        bundle.putString("stratName", strategy.name);
        bundle.putString("stratGroup", strategy.group);
        bundle.putBooleanArray("stratDrag", toPrimitiveArray(strategy.drag));
        bundle.putDoubleArray("stratX", toPrimitiveArray(strategy.x));
        bundle.putDoubleArray("stratY", toPrimitiveArray(strategy.y));

        Fragment fragment = new StrategyDetailFragment_();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    private boolean[] toPrimitiveArray(final Boolean [] booleanArray) {
        final boolean[] primitives = new boolean[booleanArray.length];
        int index = 0;
        for (Boolean object : booleanArray) {
            primitives[index++] = object;
        }
        return primitives;
    }

    private double[] toPrimitiveArray(final Double [] doubleArray) {
        final double[] primitives = new double[doubleArray.length];
        int index = 0;
        for (Double object : doubleArray) {
            primitives[index++] = object;
        }
        return primitives;
    }

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    JSONArray jsonArray;
                    try {
                        emitterStatus = data.getString("status");
                        jsonArray = data.getJSONArray("tacs");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("provideTacs")) {
                        if (jsonArray != null) {
                            readJsonData(jsonArray);
                            noStrats.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getContext(), "Unsere Wichtel konnten Deine Strategien leider nicht laden. Bitte versuche es später erneut.", Toast.LENGTH_LONG).show();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    mSocket.disconnect();
                    mSocket.off();
                }
            });
        }
    };

    @Background
    void readJsonData(JSONArray jsonArray){
        strategies.clear();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject arrayItem = jsonArray.getJSONObject(i);
                long id = arrayItem.getLong("id");
                String map = arrayItem.getString("map");
                String name = arrayItem.getString("name");
                String group = arrayItem.getString("group");
                //Arrays aus dem JSON
                JSONArray jsonDragArray = arrayItem.getJSONArray("drag");
                JSONArray jsonXArray = arrayItem.getJSONArray("x");
                JSONArray jsonYArray = arrayItem.getJSONArray("y");

                //Arrays, die mit den Werten aus den JSON Arrays befüllt werden
                ArrayList<Boolean> drag = new ArrayList<>();
                ArrayList<Double> x = new ArrayList<>();
                ArrayList<Double> y = new ArrayList<>();

                for (int j = 0; j < jsonDragArray.length(); j++) {
                    drag.add(jsonDragArray.getBoolean(j));
                }
                for (int j = 0; j < jsonXArray.length(); j++) {
                    x.add(jsonXArray.getDouble(j));
                }
                for (int j = 0; j < jsonYArray.length(); j++) {
                    y.add(jsonYArray.getDouble(j));
                }

                //ArrayLists in normale Arrays umwandeln
                Boolean [] dragArray = drag.toArray(new Boolean[drag.size()]);
                Double [] xArray = x.toArray(new Double[x.size()]);
                Double [] yArray = y.toArray(new Double[y.size()]);
                Strategy strategy = new Strategy(id, mUsername, map, name, group, dragArray, xArray, yArray);
                strategies.add(strategy);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
