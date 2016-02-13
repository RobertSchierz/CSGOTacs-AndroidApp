package app.black0ut.de.map_service_android.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.StrategyListItemView;
import app.black0ut.de.map_service_android.adapter.MapsListViewAdapter;
import app.black0ut.de.map_service_android.adapter.StrategiesListViewAdapter;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.data.Strategy;

/**
 * Created by Jan-Philipp Altenhof on 30.12.2015.
 */

@EFragment(R.layout.fragment_strategies)
public class StrategiesFragment extends Fragment{
    @ViewById
    ListView strategiesListView;

    @Bean(StrategiesListViewAdapter.class)
    StrategiesListViewAdapter adapter;

    @AfterViews
    public void bindAdapter(){
        strategiesListView.setAdapter(adapter);
    }

    /**
     * Diese Methode behandelt das Tippen auf die verschiedenen Listenelemente.
     * @param strategy Ein Parameter vom Typ Strategy, welcher die geklickte Strategie beinhaltet.
     */
    @ItemClick
    void strategiesListViewItemClicked(Strategy strategy){
        Toast.makeText(this.getContext(), "Clicked: " + strategy.name, Toast.LENGTH_SHORT).show();
    }
}
