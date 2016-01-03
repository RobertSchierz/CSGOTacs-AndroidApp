package app.black0ut.de.map_service_android.fragments;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
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

    @ViewById(R.id.map_image)
    ImageView mapImage;

    @ViewById(R.id.maps_listview)
    ListView mapsListView;

    @Bean(MapsListViewAdapter.class)
    MapsListViewAdapter adapter;

    @AfterViews
    public void bindAdapter(){
        //hier werden die Vorschau-Bilder der Karten gespeichert
        mapsListView.setAdapter(adapter);
    }

    /**
     * Diese Methode behandelt das Tippen auf die verschiedenen Listenelemente.
     * @param map Ein Parameter vom Typ Map, welcher die geklickte Karte beinhaltet.
     */
    @ItemClick(R.id.maps_listview)
    void mapsListViewItemClicked(Map map){
        //Fragments ersetzen
        Fragment fragment;
        /*
        switch(map.mapName){
            case Map.ASSAULT: mapImage.setImageResource(R.drawable.de_cbble_radar_callout);
                break;
            default: mapImage.setImageResource(R.drawable.de_dust2_radar_callout);
        }*/

        fragment = new MapsDetailFragment_();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.mainFrame, fragment).commit();


        Toast.makeText(this.getContext(), map.mapName, Toast.LENGTH_SHORT).show();
    }
}
