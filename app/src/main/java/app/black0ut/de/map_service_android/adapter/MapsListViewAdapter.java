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
import java.util.Collections;
import java.util.Comparator;
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
        maps.add(new Map("Dust 2", R.drawable.de_dust2));
        maps.add(new Map("Train", R.drawable.de_train));
        maps.add(new Map("Mirage", R.drawable.de_mirage));
        maps.add(new Map("Inferno", R.drawable.de_inferno));
        maps.add(new Map("Cobblestone", R.drawable.gd_cbble));
        maps.add(new Map("Overpass", R.drawable.de_overpass));
        maps.add(new Map("Cache", R.drawable.de_cache));
        maps.add(new Map("Aztec", R.drawable.de_aztec));
        maps.add(new Map("Dust", R.drawable.de_dust));
        maps.add(new Map("Vertigo", R.drawable.de_vertigo));
        maps.add(new Map("Nuke", R.drawable.de_nuke));
        maps.add(new Map("Office", R.drawable.cs_office));
        maps.add(new Map("Italy", R.drawable.cs_italy));
        maps.add(new Map("Assault", R.drawable.cs_assault));
        maps.add(new Map("Militia", R.drawable.cs_militia));

        //Die Liste alphabetisch sortieren
        Collections.sort(maps, new Comparator<Map>() {
            public int compare(Map m1, Map m2) {
                return m1.mapName.compareTo(m2.mapName);
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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
