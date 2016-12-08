package app.black0ut.de.gotacs.adapter;

import android.app.Activity;
import android.content.Context;
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
import java.util.HashMap;
import java.util.List;

import app.black0ut.de.gotacs.data.Connect;
import app.black0ut.de.gotacs.jsoncreator.JSONCreator;
import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.data.Status;
import app.black0ut.de.gotacs.viewholder.GroupDetailFooterViewHolder;
import app.black0ut.de.gotacs.viewholder.GroupDetailViewHolder;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 03.02.2016.
 */

/**
 * Klasse, welche die Daten für die Mitgliederliste einer Gruppe bereitstellt.
 * Sie fügt dem Ende der Liste außerdem ein Layout hinzu, welches zwei Buttons beinhaltet.
 * Die Buttons ermöglichen das Löschen und Verlassen einer Gruppe.
 * Des Weiteren ermöglicht die Klasse die Nutzerverwaltung einer Gruppe. Sie stellt einen Dialog
 * zur Verfügung, der bei einem Klick auf ein Gruppenmitglied geöffnet wird.
 * Je nach Nutzerstatus (Administrator, Moderator oder Mitglied) können bestimmte Operationen zur
 * Nutzerverwaltung ausgeführt werden.
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

    /**
     * Stellt eine Socket Verbindung zum Server her.
     */
    private void setupSocket() {
        try {
            //IO.Options opts = new IO.Options();
            //opts.forceNew = true;
            //opts.query = "name=" + Connect.c97809177;
            //opts.timeout = 5000;
            //mSocket = IO.socket("https://dooku.corvus.uberspace.de/", opts);
            mSocket = IO.socket("https://dooku.corvus.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }

        mSocket.on("status", status);
        mSocket.connect();

        Log.d("TEST", "" + mSocket.connected());

        /*
        if (!mSocket.connected()){
            Toast.makeText(getContext(), "Es konnte leider keine Verbindung hergestellt werden. Bitte überprüfe die App auf Aktualisierungen.", Toast.LENGTH_SHORT).show();
            mSocket.off();
            mSocket.disconnect();
        }
        */
    }

    public GroupDetailRecyclerViewAdapter(final String username, final ArrayList<String> members, final String[] mods,
                                          final String admin, final String groupName, final FragmentManager fragmentManager, final Context context) {
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

    /**
     * Öffnet einen Dialog zur Nutzerverwaltung.
     * Er ermöglicht, je nach Nutzerstatus, das Ernennen von Moderatoren oder das entfernen
     * von Mitgliedern aus einer Gruppe.
     * @param caller
     */
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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


    /**
     * Klick-Listener für die Buttons des Dialogs zur Nutzerverwaltung.
     * @param v
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        HashMap<String, String> jsonMap = new HashMap<>();

        //mSocket.on("status", status);
        //mSocket.connect();
        setupSocket();

        switch (id) {
            case R.id.removeMember:
                jsonMap.clear();
                jsonMap.put("user", mUsername);
                jsonMap.put("name", mGroupName);
                jsonMap.put("kick", mMemberName);
                mSocket.emit("kickUser", JSONCreator.createJSON("kickUser", jsonMap).toString());
                break;
            case R.id.promoteMember:
                jsonMap.clear();
                jsonMap.put("user", mUsername);
                jsonMap.put("set", mMemberName);
                jsonMap.put("name", mGroupName);
                mSocket.emit("setGroupMod", JSONCreator.createJSON("setGroupMod", jsonMap).toString());
                break;
            case R.id.demoteMember:
                jsonMap.clear();
                jsonMap.put("user", mUsername);
                jsonMap.put("unset", mMemberName);
                jsonMap.put("name", mGroupName);
                mSocket.emit("unsetGroupMod",
                        JSONCreator.createJSON("unsetGroupMod", jsonMap).toString());
                break;
            case R.id.buttonDeleteGroup:
                jsonMap.clear();
                jsonMap.put("user", mUsername);
                jsonMap.put("name", mGroupName);
                mSocket.emit("deleteGroup", JSONCreator.createJSON("deleteGroup", jsonMap).toString());
                break;
            case R.id.buttonLeaveGroup:
                jsonMap.clear();
                jsonMap.put("user", mUsername);
                jsonMap.put("name", mGroupName);
                mSocket.emit("leaveGroup", JSONCreator.createJSON("leaveGroup", jsonMap).toString());
                break;
        }
    }

    /**
     * Socket Listener, welcher auf Antworten des Servers reagiert.
     */
    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            final Activity activity = (Activity) mContext;
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
                    }
                    if (emitterStatus.equals("leaveGroupFailed")) {
                        Toast.makeText(mContext, activity.getResources().getText(R.string.leave_group_failed), Toast.LENGTH_SHORT).show();
                    }
                    if (emitterStatus.equals("deleteGroupSuccess")) {
                        mFragmentManager.popBackStack();
                    }
                    if (emitterStatus.equals("deleteGroupFailed")) {
                        Toast.makeText(mContext, activity.getResources().getText(R.string.delete_group_failed), Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                    mSocket.disconnect();
                    mSocket.off();

                }
            });
        }
    };
}
