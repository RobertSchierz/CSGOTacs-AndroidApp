package app.black0ut.de.map_service_android.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Status;

/**
 * Created by Jan-Philipp Altenhof on 03.02.2016.
 */
public class GroupDetailRecyclerViewAdapter extends RecyclerView.Adapter<GroupDetailRecyclerViewAdapter.GroupDetailViewHolder> {

    private String[] mMembers;
    private String[] mMods;
    private ArrayList<Integer> mMemberCount;
    private Status mCurrentStatus;
    private FragmentManager mFragmentManager;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GroupDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public RelativeLayout groupDetailRelativeLayout;
        public TextView memberName;
        public ViewHolderClicks mListener;

        public GroupDetailViewHolder(View v, ViewHolderClicks listener) {
            super(v);
            mListener = listener;
            memberName = (TextView) v.findViewById(R.id.memberName);
            groupDetailRelativeLayout = (RelativeLayout) v.findViewById(R.id.groupDetailRelativeLayout);
            groupDetailRelativeLayout.setOnClickListener(this);
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
    public GroupDetailRecyclerViewAdapter(String[] members, String[] mods) {
        mMembers = members;
        mMods = mods;
        mCurrentStatus = Status.getCurrentStatus();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupDetailRecyclerViewAdapter.GroupDetailViewHolder onCreateViewHolder(ViewGroup parent,
                                                                                   int viewType) {
        // create a new view
        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_detail_cardview, parent, false);
        GroupDetailViewHolder vh = new GroupDetailViewHolder(v, new GroupDetailViewHolder.ViewHolderClicks() {
            @Override
            public void onLayout(View caller) {

            }
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GroupDetailViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.groupName.setText(mMembers.get(position));
        //holder.groupMemberCount.setText(mMemberCount.get(position).toString());
        holder.memberName.setText(mMembers[position]);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMembers.length;
    }

}
