package app.black0ut.de.gotacs.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import app.black0ut.de.gotacs.listitemview.GroupDialogListItemView;
import app.black0ut.de.gotacs.listitemview.GroupDialogListItemView_;

/**
 * Created by Jan-Philipp Altenhof on 14.02.2016.
 */

/**
 * Klasse, welche die Daten für die Liste im Gruppenauswahldialog zur Verfügung stellt.
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
