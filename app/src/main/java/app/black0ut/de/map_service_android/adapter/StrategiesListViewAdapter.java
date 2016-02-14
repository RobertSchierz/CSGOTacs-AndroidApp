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
public class StrategiesListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Strategy> strategies;

    public StrategiesListViewAdapter(List<Strategy> strategies, Context context) {
        this.strategies = strategies;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        StrategyListItemView strategyListItemView;
        if (convertView == null) {
            strategyListItemView = StrategyListItemView_.build(mContext);
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

}
