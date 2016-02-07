package app.black0ut.de.map_service_android.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.data.User;

/**
 * Created by Jan-Philipp Altenhof on 03.02.2016.
 */
public class GroupDetailRecyclerViewAdapter extends RecyclerView.Adapter<GroupDetailRecyclerViewAdapter.GroupDetailViewHolder> implements View.OnClickListener {

    private String[] mMembers;
    private String[] mMods;
    private String mAdmin;
    private ArrayList<Integer> mMemberCount;
    private Status mCurrentStatus;
    private FragmentManager mFragmentManager;
    private SharedPreferences sharedPreferences;
    private String mUsername;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class GroupDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupDetailRecyclerViewAdapter(String[] members, String[] mods, String admin, Context context) {
        mMembers = members;
        mMods = mods;
        mAdmin = admin;
        mCurrentStatus = Status.getCurrentStatus();
        sharedPreferences = context.getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);
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
                TextView textView = (TextView) caller.findViewById(R.id.memberName);
                Toast.makeText(caller.getContext(), "Member '" + textView.getText().toString() + "' clicked.", Toast.LENGTH_SHORT).show();
                openDialog(caller, textView.getText().toString());
            }
        });
        return vh;
    }

    private void openDialog(View caller, String memberName) {
        LayoutInflater factory = LayoutInflater.from(caller.getContext());
        final View manageUser = factory.inflate(R.layout.member_management, null);

        TextView removeMember = (TextView) manageUser.findViewById(R.id.removeMember);
        removeMember.setText(String.format(caller.getResources().getString(R.string.remove_member), memberName));

        TextView promoteMember = (TextView) manageUser.findViewById(R.id.promoteMember);
        TextView demoteMember = (TextView) manageUser.findViewById(R.id.demoteMember);

        removeMember.setOnClickListener(this);
        promoteMember.setOnClickListener(this);
        demoteMember.setOnClickListener(this);

        boolean memberIsMod = false;
        boolean userIsMod = false;

        if (mMods.length > 0) {
            for (String mMod : mMods) {
                Log.d("TEST", "mMod: " + mMod);
                if (memberName.equals(mMod)) {
                    memberIsMod = true;
                }
                if (mUsername.equals(mMod)) {
                    userIsMod = true;
                }
            }
        }
        if (!mUsername.equals(memberName) && (mUsername.equals(mAdmin) || userIsMod) && !memberName.equals(mAdmin)) {
            if (memberIsMod){
                demoteMember.setVisibility(View.VISIBLE);
            }else{
                promoteMember.setVisibility(View.VISIBLE);
            }
            final AlertDialog builder = new AlertDialog.Builder(caller.getContext(), R.style.CreateGroup)
                    .setView(manageUser)
                    .create();
            builder.show();
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GroupDetailViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.memberName.setText(mMembers[position]);
        if (mMembers[position].equals(mAdmin)) {
            holder.userStatus.setText("Administrator");
            holder.userStatus.setVisibility(View.VISIBLE);
        } else {
            holder.userStatus.setText("");
            holder.userStatus.setVisibility(View.GONE);
        }
        if (mMods.length != 0) {
            for (String mMod : mMods) {
                Log.d("TEST", "mMod: " + mMod);
                if (mMembers[position].equals(mMod)) {
                    holder.userStatus.setVisibility(View.VISIBLE);
                    holder.userStatus.setText("Moderator");
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMembers.length;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.removeMember:
                Toast.makeText(v.getContext(), "Remove Click", Toast.LENGTH_SHORT).show();
                break;
            case R.id.promoteMember:
                Toast.makeText(v.getContext(), "Promote Click", Toast.LENGTH_SHORT).show();
                break;
            case R.id.demoteMember:
                Toast.makeText(v.getContext(), "Demote Click", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
