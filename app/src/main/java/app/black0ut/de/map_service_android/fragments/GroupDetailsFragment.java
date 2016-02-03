package app.black0ut.de.map_service_android.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupDetailRecyclerViewAdapter;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.data.User;

/**
 * Created by Jan-Philipp Altenhof on 02.02.2016.
 */
@EFragment(R.layout.fragment_group_details)
public class GroupDetailsFragment extends Fragment {
    SharedPreferences sharedPreferences;
    private String groupName;
    private String groupPassword;
    private String username;
    private Status gsonStatus;
    private boolean openedFirstTime;
    private String[] mMembers;
    private String[] mMods;

    @ViewById
    public RecyclerView mGroupsDetailsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    //Quelle: https://github.com/excilys/androidannotations/wiki/Save-instance-state
    @InstanceState
    String clickedGroup;

    @AfterViews
    public void afterViews() {
        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        username = sharedPreferences.getString(User.USERNAME, null);
        openedFirstTime = sharedPreferences.getBoolean(User.OPENS_GROUPS_FIRST_TIME, true);
        clickedGroup = getArguments().getString("clickedGroup");

        gsonStatus = Status.getCurrentStatus();
        mMembers = gsonStatus.getGroupFromName(clickedGroup).getMembers();
        mMods = gsonStatus.getGroupFromName(clickedGroup).getMods();

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mGroupsDetailsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsDetailsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new GroupDetailRecyclerViewAdapter(mMembers, mMods);
        mGroupsDetailsRecyclerView.setAdapter(mAdapter);
    }
}
