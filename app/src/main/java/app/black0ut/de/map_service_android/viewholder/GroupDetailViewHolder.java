package app.black0ut.de.map_service_android.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 09.02.2016.
 */

/**
 * Wird als ViewHolder für die einzelnen Listenelemente der Mitgliederliste verwendet.
 */
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

    /**
     * Interface für die Implementierung eines Klick-Listeners.
     */
    public interface ViewHolderClicks {
        void onLayout(View caller);
    }
}
