package app.black0ut.de.map_service_android.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.fragments.GroupDetailsFragment_;
import app.black0ut.de.map_service_android.fragments.GroupsFragment_;
import app.black0ut.de.map_service_android.viewholder.GroupDetailFooterViewHolder;
import app.black0ut.de.map_service_android.viewholder.GroupDetailViewHolder;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 03.02.2016.
 */
public class GroupDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<String> mMembers;
    private String[] mMods;
    private String mAdmin;
    private Status mCurrentStatus;
    private final String mUsername;
    private final Context mContext;
    private String mMemberName;
    private String mGroupName;
    private AlertDialog mBuilder;
    private FragmentManager mFragmentManager;

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


    // Provide a suitable constructor (depends on the kind of dataset)
    public GroupDetailRecyclerViewAdapter(final String username, final ArrayList<String> members, final String[] mods,
                                          final String admin, final String groupName,final FragmentManager fragmentManager, final Context context) {
        mUsername = username;
        mMembers = members;
        mMods = mods;
        mAdmin = admin;
        mCurrentStatus = Status.getCurrentStatus(context);
        mContext = context;
        mGroupName = groupName;
        mFragmentManager = fragmentManager;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        if (viewType == TYPE_ITEM) {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_detail_cardview, parent, false);
            return new GroupDetailViewHolder(v, new GroupDetailViewHolder.ViewHolderClicks() {
                @Override
                public void onLayout(View caller) {
                    TextView textView = (TextView) caller.findViewById(R.id.memberName);
                    mMemberName = textView.getText().toString();
                    Toast.makeText(caller.getContext(), "Member '" + mMemberName + "' clicked.", Toast.LENGTH_SHORT).show();
                    openDialog(caller);
                }
            });
        } else if (viewType == TYPE_FOOTER) {
            final View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_leave_delete, parent, false);
            return new GroupDetailFooterViewHolder(v);
        }
        return null;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (holder instanceof GroupDetailFooterViewHolder) {
            GroupDetailFooterViewHolder footerHolder = (GroupDetailFooterViewHolder) holder;
            footerHolder.mDeleteGroup.setOnClickListener(this);
            footerHolder.mLeaveGroup.setOnClickListener(this);
            if (mUsername.equals(mAdmin)) {
                footerHolder.mDeleteGroup.setVisibility(View.VISIBLE);
            }
        } else if (holder instanceof GroupDetailViewHolder) {
            GroupDetailViewHolder itemHolder = (GroupDetailViewHolder) holder;
            String currentMember = getMember(position);
            itemHolder.memberName.setText(currentMember);
            if (currentMember.equals(mAdmin)) {
                itemHolder.userStatus.setText("Administrator");
                itemHolder.userStatus.setVisibility(View.VISIBLE);
            } else {
                itemHolder.userStatus.setText("");
                itemHolder.userStatus.setVisibility(View.GONE);
            }
            if (mMods.length != 0) {
                for (String mMod : mMods) {
                    Log.d("TEST", "mMod: " + mMod);
                    if (currentMember.equals(mMod)) {
                        itemHolder.userStatus.setVisibility(View.VISIBLE);
                        itemHolder.userStatus.setText("Moderator");
                    }
                }
            }
        }
    }

    private String getMember(int position) {
        return mMembers.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mMembers.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mMembers.size()) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
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
                break;
            case R.id.promoteMember:
                mSocket.emit("setGroupMod",
                        JSONCreator.createJSON("setGroupMod",
                                "{ 'user' : '" + mMemberName +
                                        "', 'name': '" + mGroupName +
                                        "' }"));
                break;
            case R.id.demoteMember:
                mSocket.emit("unsetGroupMod",
                        JSONCreator.createJSON("unsetGroupMod",
                                "{ 'user' : '" + mMemberName +
                                        "', 'name': '" + mGroupName +
                                        "' }"));
                break;
            case R.id.buttonDeleteGroup:
                mSocket.emit("deleteGroup",
                        JSONCreator.createJSON("deleteGroup",
                                "{ 'user' : '" + mUsername +
                                        "', 'name': '" + mGroupName +
                                        "' }"));
                break;
            case R.id.buttonLeaveGroup:
                mSocket.emit("leaveGroup",
                        JSONCreator.createJSON("leaveGroup",
                                "{ 'user' : '" + mUsername +
                                        "', 'name': '" + mGroupName +
                                        "' }"));
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
                        mBuilder.cancel();
                    }
                    if (emitterStatus.equals("setGroupModFailed")) {

                    }
                    if (emitterStatus.equals("unsetGroupModSuccess")) {
                        List<String> list = new ArrayList<>();
                        Collections.addAll(list, mMods);
                        list.removeAll(Collections.singletonList(mMemberName));
                        mMods = list.toArray(new String[list.size()]);
                        mBuilder.cancel();
                    }
                    if (emitterStatus.equals("unsetGroupModFailed")) {

                    }
                    if (emitterStatus.equals("kickUserSuccess")) {
                        mMembers.removeAll(Collections.singletonList(mMemberName));
                        mBuilder.cancel();
                    }
                    if (emitterStatus.equals("kickUserFailed")) {

                    }
                    if (emitterStatus.equals("leaveGroupSuccess")) {
                        mFragmentManager.popBackStack();
                        //swapFragment();
                    }
                    if (emitterStatus.equals("leaveGroupFailed")) {
                        Toast.makeText(mContext, "Die Gruppe konnte nicht verlassen werden.", Toast.LENGTH_SHORT).show();
                    }
                    if (emitterStatus.equals("deleteGroupSuccess")) {
                        mFragmentManager.popBackStack();
                        //swapFragment();
                    }
                    if (emitterStatus.equals("leaveGroupFailed")) {
                        Toast.makeText(mContext, "Die Gruppe konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                    mSocket.disconnect();
                    mSocket.off();

                }
            });
        }
    };

    public void swapFragment() {
        Fragment fragment = new GroupsFragment_();
        mFragmentManager.beginTransaction()
                .disallowAddToBackStack()
                .replace(R.id.mainFrame, fragment)
                .commit();
        mFragmentManager.executePendingTransactions();
    }
}
