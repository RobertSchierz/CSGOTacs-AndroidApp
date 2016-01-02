package app.black0ut.de.map_service_android.fragments;


import android.support.v4.app.Fragment;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.MapsListViewAdapter;
import app.black0ut.de.map_service_android.data.Map;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */

@EFragment(R.layout.fragment_maps)
public class MapsFragment extends Fragment {

    String[] maps;

    @ViewById(R.id.maps_listview)
    ListView mapsListView;

    @Bean(MapsListViewAdapter.class)
    MapsListViewAdapter adapter;

    @AfterViews
    public void bindAdapter(){
        //hier werden die Vorschau-Bilder der Karten gespeichert
        //adapter = new MapsListViewAdapter(this.getContext(), maps);
        mapsListView.setAdapter(adapter);
    }

    @ItemClick(R.id.maps_listview)
    void mapsListViewItemClicked(Map map){
        Toast.makeText(this.getContext(), map.mapName, Toast.LENGTH_SHORT).show();
    }
}
