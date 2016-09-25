package app.black0ut.de.gotacs.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.black0ut.de.gotacs.R;

/**
 * Created by Jan-Philipp Altenhof on 09.02.2016.
 */

/**
 * Wird als ViewHolder für die Buttons am Ende der Mitgliederliste verwendet.
 */
public class GroupDetailFooterViewHolder extends RecyclerView.ViewHolder {


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