package app.black0ut.de.map_service_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import app.black0ut.de.map_service_android.listitemview.StrategyListItemView;
import app.black0ut.de.map_service_android.data.Strategy;
import app.black0ut.de.map_service_android.listitemview.StrategyListItemView_;

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
