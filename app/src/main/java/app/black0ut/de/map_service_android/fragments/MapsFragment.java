package app.black0ut.de.map_service_android.fragments;

import android.app.Fragment;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan on 24.12.15.
 */
@EFragment(R.layout.fragment_maps)
public class MapsFragment extends Fragment {
    @FragmentArg
    int index = 0;

    //@FragmentArg
    //String fragmentTitle = "Default Title";

    //@ViewById(R.id.tv_fragment_title)
    //TextView tvFragmentTitle


    @AfterViews
    public void afterViews() {
        //tvFragmentTitle.setText(fragmentTitle);
    }

}
