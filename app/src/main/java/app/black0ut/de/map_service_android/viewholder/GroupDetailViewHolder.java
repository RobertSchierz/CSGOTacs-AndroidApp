package app.black0ut.de.map_service_android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 09.02.2016.
 */
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class GroupDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public RelativeLayout groupDetailRelativeLayout;
    public TextView memberName;
    public TextView userStatus;
    public ViewHolderClicks mListener;

    public GroupDetailViewHolder(View v, ViewHolderClicks listener) {
        super(v);
        mListener = listener;
        memberName = (TextView) v.findViewById(R.id.memberName);
        userStatus = (TextView) v.findViewById(R.id.userStatus);
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
