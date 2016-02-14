package app.black0ut.de.map_service_android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.black0ut.de.map_service_android.listitemview.MapListItemView;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.listitemview.MapListItemView_;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */

@EBean
public class MapsListViewAdapter extends BaseAdapter{

    List<Map> maps;

    //You can inject the root Android component that depends on your @EBean class, using
    // the @RootContext annotation. Please notice that it only gets injected if the context has the right type.
    //Quelle: https://github.com/excilys/androidannotations/wiki/Enhance-custom-classes
    @RootContext
    Context context;

    @AfterInject
    void initAdapter() {
        maps = new ArrayList<>();
        maps.add(new Map(Map.DUST2, R.drawable.de_dust2));
        maps.add(new Map(Map.TRAIN, R.drawable.de_train));
        maps.add(new Map(Map.MIRAGE, R.drawable.de_mirage));
        maps.add(new Map(Map.INFERNO, R.drawable.de_inferno));
        maps.add(new Map(Map.COBBLESTONE, R.drawable.gd_cbble));
        maps.add(new Map(Map.OVERPASS, R.drawable.de_overpass));
        maps.add(new Map(Map.CACHE, R.drawable.de_cache));
        maps.add(new Map(Map.AZTEC, R.drawable.de_aztec));
        maps.add(new Map(Map.DUST, R.drawable.de_dust));
        maps.add(new Map(Map.VERTIGO, R.drawable.de_vertigo));
        maps.add(new Map(Map.NUKE, R.drawable.de_nuke));
        maps.add(new Map(Map.OFFICE, R.drawable.cs_office));
        maps.add(new Map(Map.ITALY, R.drawable.cs_italy));
        maps.add(new Map(Map.ASSAULT, R.drawable.cs_assault));
        maps.add(new Map(Map.MILITIA, R.drawable.cs_militia));

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
