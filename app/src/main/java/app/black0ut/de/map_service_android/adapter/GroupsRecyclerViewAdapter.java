package app.black0ut.de.map_service_android.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.InstanceState;

import java.util.ArrayList;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.fragments.GroupDetailsFragment_;

/**
 * Created by Jan-Philipp Altenhof on 25.01.2016.
 */

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.GroupsViewHolder> {

    private ArrayList<String> mDataset = new ArrayList<>();
    private ArrayList<Integer> mMemberCount;
    private Status mCurrentStatus;
    private FragmentManager mFragmentManager;
    public String clickedGroup;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout groupRelativeLayout;
        public TextView groupName;
        public TextView groupMemberCount;
        public ViewHolderClicks mListener;

        public GroupsViewHolder(View v, ViewHolderClicks listener) {
            super(v);
            mListener = listener;
            groupName = (TextView) v.findViewById(R.id.groupName);
            groupMemberCount = (TextView) v.findViewById(R.id.groupMemberCount);
            groupRelativeLayout = (RelativeLayout) v.findViewById(R.id.groupRelativeLayout);
            groupRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v instanceof RelativeLayout) {
                mListener.onLayout(v);
            }
        }

        public interface ViewHolderClicks {
            void onLayout(View caller);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupsRecyclerViewAdapter(ArrayList<String> myDataset, ArrayList<Integer> memberCount, FragmentManager fragmentManager) {
        mDataset = myDataset;
        mMemberCount = memberCount;
        mCurrentStatus = Status.getCurrentStatus();
        mFragmentManager = fragmentManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        // create a new view
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_cardview, parent, false);
        GroupsViewHolder vh = new GroupsViewHolder(v, new GroupsViewHolder.ViewHolderClicks() {
            @Override
            public void onLayout(View caller) {
                getClickedGroup(caller);
            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GroupsViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.groupName.setText(mDataset.get(position));
        holder.groupMemberCount.setText(mMemberCount.get(position).toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void getClickedGroup(View caller) {
        TextView textView = (TextView) caller.findViewById(R.id.groupName);
        clickedGroup = textView.getText().toString();
        Log.d("TEST", "Clicked group: " + clickedGroup);

        if (clickedGroup.equals("")) {
            return;
        }
        swapFragment();
    }

    public void swapFragment() {
        Bundle bundle = new Bundle();
        bundle.putString("clickedGroup", clickedGroup);
        Fragment fragment = new GroupDetailsFragment_();
        fragment.setArguments(bundle);
        mFragmentManager.beginTransaction()
                .addToBackStack(null)
                .replace(R.id.mainFrame, fragment)
                .commit();
        mFragmentManager.executePendingTransactions();
    }
}
