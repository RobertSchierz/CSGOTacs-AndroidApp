package app.black0ut.de.map_service_android.adapter;

import android.app.Activity;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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
    private Context mContext;
    private String mMemberName;
    private String mGroupName;
    private AlertDialog mBuilder;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

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
    public GroupDetailRecyclerViewAdapter(String[] members, String[] mods, String admin, String groupName, Context context) {
        sharedPreferences = context.getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);

        mMembers = members;
        mMods = mods;
        mAdmin = admin;
        mCurrentStatus = Status.getCurrentStatus(context);
        mContext = context;
        mGroupName = groupName;
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
                mMemberName = textView.getText().toString();
                Toast.makeText(caller.getContext(), "Member '" + mMemberName + "' clicked.", Toast.LENGTH_SHORT).show();
                openDialog(caller);
            }
        });
        return vh;
    }

    private void openDialog(View caller) {
        LayoutInflater factory = LayoutInflater.from(caller.getContext());
        final View manageUser = factory.inflate(R.layout.member_management, null);

        TextView removeMember = (TextView) manageUser.findViewById(R.id.removeMember);
        removeMember.setText(String.format(caller.getResources().getString(R.string.remove_member), mMemberName));

        TextView promoteMember = (TextView) manageUser.findViewById(R.id.promoteMember);
        TextView demoteMember = (TextView) manageUser.findViewById(R.id.demoteMember);
        TextView noAction = (TextView) manageUser.findViewById(R.id.noActionAvailable);

        removeMember.setOnClickListener(this);
        promoteMember.setOnClickListener(this);
        demoteMember.setOnClickListener(this);

        boolean memberIsMod = false;
        boolean userIsMod = false;

        if (mMods.length > 0) {
            for (String mMod : mMods) {
                Log.d("TEST", "mMod: " + mMod);
                if (mMemberName.equals(mMod)) {
                    memberIsMod = true;
                }
                if (mUsername.equals(mMod)) {
                    userIsMod = true;
                }
            }
        }
        if (!mUsername.equals(mMemberName) && (mUsername.equals(mAdmin) || userIsMod) && !mMemberName.equals(mAdmin)) {
            if (memberIsMod) {
                demoteMember.setVisibility(View.VISIBLE);
            } else {
                promoteMember.setVisibility(View.VISIBLE);
            }
            noAction.setVisibility(View.GONE);
            removeMember.setVisibility(View.VISIBLE);
        }
        mBuilder = new AlertDialog.Builder(caller.getContext(), R.style.CreateGroup)
                .setView(manageUser)
                .create();
        mBuilder.show();
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

        mSocket.on("status", status);
        mSocket.connect();

        switch (id) {
            case R.id.removeMember:
                mSocket.emit("kickUser",
                        JSONCreator.createJSON("kickUser",
                                "{ 'user' : '" + mUsername +
                                "', 'name': '" + mGroupName +
                                "', 'kick': '" + mMemberName +
                                "' }"));
                Toast.makeText(v.getContext(), "Remove Click", Toast.LENGTH_SHORT).show();
                break;
            case R.id.promoteMember:
                mSocket.emit("setGroupMod",
                        JSONCreator.createJSON("setGroupMod",
                                "{ 'user' : '" + mMemberName +
                                "', 'name': '" + mGroupName +
                                "' }"));
                Toast.makeText(v.getContext(), "Promote Click", Toast.LENGTH_SHORT).show();
                break;
            case R.id.demoteMember:
                mSocket.emit("unsetGroupMod",
                        JSONCreator.createJSON("unsetGroupMod",
                                "{ 'user' : '" + mMemberName +
                                        "', 'name': '" + mGroupName +
                                        "' }"));
                Toast.makeText(v.getContext(), "Demote Click", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Activity activity = (Activity) mContext;
            if (activity == null)
                return;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    try {
                        emitterStatus = data.getString("status");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("setGroupModSuccess")) {
                        List<String> list = new ArrayList<>();
                        Collections.addAll(list, mMods);
                        list.addAll(Collections.singletonList(mMemberName));
                        mMods = list.toArray(new String[list.size()]);
                    }
                    if (emitterStatus.equals("setGroupModFailed")) {

                    }
                    if (emitterStatus.equals("unsetGroupModSuccess")){
                        List<String> list = new ArrayList<>();
                        Collections.addAll(list, mMods);
                        list.removeAll(Collections.singletonList(mMemberName));
                        mMods = list.toArray(new String[list.size()]);
                    }
                    if (emitterStatus.equals("unsetGroupModFailed")){

                    }
                    if (emitterStatus.equals("kickUserSuccess")) {
                        List<String> list = new ArrayList<>();
                        Collections.addAll(list, mMembers);
                        list.removeAll(Collections.singletonList(mMemberName));
                        mMembers = list.toArray(new String[list.size()]);
                    }
                    if (emitterStatus.equals("kickUserFailed")) {

                    }
                    mBuilder.cancel();
                    notifyDataSetChanged();
                    mSocket.disconnect();
                    mSocket.off();

                }
            });
        }
    };
}
