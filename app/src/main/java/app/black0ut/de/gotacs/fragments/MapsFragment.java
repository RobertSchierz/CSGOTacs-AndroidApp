package app.black0ut.de.gotacs.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.adapter.MapsListViewAdapter;
import app.black0ut.de.gotacs.data.Map;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */

/**
 * Fragment für die Liste der auswählbaren Karten.
 */
@EFragment(R.layout.fragment_maps)
public class MapsFragment extends Fragment {

    @ViewById(R.id.maps_listview)
    ListView mapsListView;

    @Bean(MapsListViewAdapter.class)
    MapsListViewAdapter adapter;

    /**
     * Fügt der ListView einen Adapter hinzu.
     */
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
        //Den Namen der geklickten Map global speichern
        Map.clickedMapName = map.mapName;
        swapFragment();
    }

    /**
     * Ersetzt das aktuelle Fragment durch ein MapsDetailFragment, welches das Bild der geklickten
     * Karte anzeigt.
     */
    private void swapFragment(){
        Fragment fragment;
        fragment = new MapsDetailFragment_();
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.replace(R.id.mainFrame, fragment).commit();
    }
}
