package app.black0ut.de.map_service_android.activities;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.fragments.MapsDetailFragment;
import app.black0ut.de.map_service_android.fragments.MapsDetailFragment_;

/**
 * Created by Jan-Philipp Altenhof on 20.01.2016.
 */
@EActivity(R.layout.activity_maps_detail)
public class MapsDetailActivity extends AppCompatActivity {

    @AfterViews
    protected void afterViews() {
        MapsDetailFragment mMapsDetailFragment = MapsDetailFragment_.builder().build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.mainFrame, mMapsDetailFragment)
                .disallowAddToBackStack()
                .commit();

    }
}
