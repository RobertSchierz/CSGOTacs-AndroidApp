package app.black0ut.de.map_service_android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 09.02.2016.
 */
public class GroupDetailFooterViewHolder extends RecyclerView.ViewHolder {
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder

    public LinearLayout mLeaveDeleteLayout;
    public TextView mLeaveGroup;
    public TextView mDeleteGroup;

    public GroupDetailFooterViewHolder(View v) {
        super(v);
        mLeaveGroup = (TextView) v.findViewById(R.id.buttonLeaveGroup);
        mDeleteGroup = (TextView) v.findViewById(R.id.buttonDeleteGroup);
        mLeaveDeleteLayout = (LinearLayout) v.findViewById(R.id.leaveDeleteLayout);
    }

}


