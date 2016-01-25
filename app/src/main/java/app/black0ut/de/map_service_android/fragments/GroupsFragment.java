package app.black0ut.de.map_service_android.fragments;



import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupsRecyclerViewAdapter;

/**
 * Created by Jan-Philipp Altenhof on 30.12.2015.
 */

@EFragment(R.layout.fragment_groups)
public class GroupsFragment extends Fragment {

    @ViewById
    public RecyclerView mGroupsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String [] myDataset = {"Gruppe 1", "Gruppe 2", "Gruppe 3", "Gruppe 4", "Gruppe 5",
            "Gruppe 6", "Gruppe 7", "Gruppe 8", "Gruppe 9", "Gruppe 10"};

    @AfterViews
    public void afterViews(){
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mGroupsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new GroupsRecyclerViewAdapter(myDataset);
        mGroupsRecyclerView.setAdapter(mAdapter);

    }
}
