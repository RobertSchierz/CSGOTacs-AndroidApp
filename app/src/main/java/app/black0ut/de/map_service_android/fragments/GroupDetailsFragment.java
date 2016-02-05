package app.black0ut.de.map_service_android.fragments;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupDetailRecyclerViewAdapter;
import app.black0ut.de.map_service_android.data.Group;
import app.black0ut.de.map_service_android.data.Status;

/**
 * Created by Jan-Philipp Altenhof on 02.02.2016.
 */
@EFragment(R.layout.fragment_group_details)
public class GroupDetailsFragment extends Fragment {

    @ViewById
    public RecyclerView mGroupsDetailsRecyclerView;

    //Quelle: https://github.com/excilys/androidannotations/wiki/Save-instance-state
    @InstanceState
    String clickedGroup;

    @AfterViews
    public void afterViews() {
        clickedGroup = getArguments().getString("clickedGroup");

        Status gsonStatus = Status.getCurrentStatus();
        Group currentGroup = gsonStatus.getGroupFromName(clickedGroup);
        String[] mMembers = currentGroup.getMembers();
        String[] mMods = currentGroup.getMods();
        String mAdmin = currentGroup.getAdmin();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mGroupsDetailsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsDetailsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        RecyclerView.Adapter mAdapter = new GroupDetailRecyclerViewAdapter(mMembers, mMods, mAdmin, getContext());
        mGroupsDetailsRecyclerView.setAdapter(mAdapter);
    }
}
