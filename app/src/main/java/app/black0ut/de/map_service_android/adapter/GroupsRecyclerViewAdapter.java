package app.black0ut.de.map_service_android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 25.01.2016.
 */

public class GroupsRecyclerViewAdapter extends RecyclerView.Adapter<GroupsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<String> mDataset = new ArrayList<>();
    private ArrayList<Integer> memberCount;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView groupName;
        public TextView groupMemberCount;

        public ViewHolder(View v) {
            super(v);
            groupName = (TextView) v.findViewById(R.id.groupName);
            groupMemberCount = (TextView) v.findViewById(R.id.groupMemberCount);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupsRecyclerViewAdapter(ArrayList<String> myDataset, ArrayList<Integer> memberCount) {
        mDataset = myDataset;
        this.memberCount = memberCount;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GroupsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.groups_cardview, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.groupName.setText(mDataset.get(position));
        holder.groupMemberCount.setText(memberCount.get(position).toString());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
