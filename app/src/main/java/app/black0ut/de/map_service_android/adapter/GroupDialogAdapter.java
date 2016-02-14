package app.black0ut.de.map_service_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import app.black0ut.de.map_service_android.GroupDialogListItemView;
import app.black0ut.de.map_service_android.GroupDialogListItemView_;
import app.black0ut.de.map_service_android.StrategyListItemView;
import app.black0ut.de.map_service_android.StrategyListItemView_;
import app.black0ut.de.map_service_android.data.Group;
import app.black0ut.de.map_service_android.data.Strategy;

/**
 * Created by Jan-Philipp Altenhof on 14.02.2016.
 */
public class GroupDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> groups;

    public GroupDialogAdapter(List<String> groups, Context mContext) {
        this.groups = groups;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public String getItem(int position) {
        return groups.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GroupDialogListItemView groupDialogListItemView;
        if (convertView == null) {
            groupDialogListItemView = GroupDialogListItemView_.build(mContext);
        } else {
            groupDialogListItemView = (GroupDialogListItemView) convertView;
        }

        groupDialogListItemView.bind(getItem(position));

        return groupDialogListItemView;
    }
}
