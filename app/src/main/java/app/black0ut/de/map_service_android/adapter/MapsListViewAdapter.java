package app.black0ut.de.map_service_android.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import app.black0ut.de.map_service_android.MapListItemView;
import app.black0ut.de.map_service_android.MapListItemView_;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */

@EBean
public class MapsListViewAdapter extends BaseAdapter{

    List<Map> maps;

    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        maps = new ArrayList<>();
        maps.add(new Map("Dust 2", R.drawable.ic_map_orange_600_18dp));
        maps.add(new Map("Train", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Mirage", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Inferno", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Cobblestone", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Overpass", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Cache", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Aztec", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Dust", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Vertigo", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Nuke", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Office", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Italy", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Assault", R.drawable.ic_group_orange_600_18dp));
        maps.add(new Map("Militia", R.drawable.ic_group_orange_600_18dp));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /*
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listview_icon_text, parent, false);
        ImageView mapPreview = (ImageView) rowView.findViewById(R.id.map_preview);
        TextView mapName = (TextView) rowView.findViewById(R.id.map_name);
        mapPreview.setImageResource(R.drawable.ic_account_circle_orange_600_18dp);
        mapName.setText(values[position]);
        */

        MapListItemView mapListItemView;
        if(convertView == null){
            mapListItemView = MapListItemView_.build(context);
        }else{
            mapListItemView = (MapListItemView) convertView;
        }

        mapListItemView.bind(getItem(position));

        return mapListItemView;
    }

    @Override
    public int getCount() {
        return maps.size();
    }

    @Override
    public Map getItem(int position) {
        return maps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
