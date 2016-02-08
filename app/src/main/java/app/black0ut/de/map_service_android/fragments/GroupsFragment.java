package app.black0ut.de.map_service_android.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupsRecyclerViewAdapter;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 30.12.2015.
 */

@EFragment(R.layout.fragment_groups)
public class GroupsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    private String groupName;
    private String groupPassword;
    private String username;
    private Status gsonStatus;
    @ViewById
    public RecyclerView mGroupsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    TextView pullToRefreshText;

    private ArrayList<String> myGroups = new ArrayList<>();
    private ArrayList<Integer> memberCount = new ArrayList<>();

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    @AfterViews
    public void afterViews() {
        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        pullToRefreshText.setVisibility(View.VISIBLE);
        username = sharedPreferences.getString(User.USERNAME, null);

        if (gsonStatus == null) {
            pullToRefreshText.setVisibility(View.VISIBLE);
        } else {
            pullToRefreshText.setVisibility(View.GONE);
        }

        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", "{ 'user' : '" + username + "' }"));

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mGroupsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new GroupsRecyclerViewAdapter(myGroups, memberCount, getActivity().getSupportFragmentManager(), getContext());
        mGroupsRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

    }

    void refreshItems() {
        Log.d("TEST", "refreshItems");
        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", "{ \"user\" : \"" + username + "\" }"));
        // Load complete
    }

    void onItemsLoadComplete() {
        pullToRefreshText.setVisibility(View.GONE);
        // Update the adapter and notify data set changed
        mAdapter.notifyDataSetChanged();
        // Stop refresh animation
        swipeRefreshLayout.setRefreshing(false);
    }

    @Click
    public void fabNewGroupClicked() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View newGroupLayout = factory.inflate(R.layout.new_group, null);
            final EditText etGroupName = (EditText) newGroupLayout.findViewById(R.id.etGroupName);
            final EditText etGroupPassword = (EditText) newGroupLayout.findViewById(R.id.etGroupPassword);

            final AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                    .setTitle("Gruppe erstellen")
                    .setView(newGroupLayout)
                    .setPositiveButton("Erstellen", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            groupName = etGroupName.getText().toString();
                            groupPassword = etGroupPassword.getText().toString();
                            mSocket.on("status", status);
                            mSocket.connect();
                            if (groupName.equals("") || groupPassword.equals("")) {
                                Toast.makeText(getContext(), "Der Gruppenname/das Passwort darf nicht leer sein.", Toast.LENGTH_SHORT).show();
                            } else {
                                //{ 'user' : 'Erstellender Benutzer', 'name' : 'Gruppenname', 'pw' : 'Gruppenpasswort' }
                                String createGroupString = "{ 'user' : '" + username + "', 'name' : '" + groupName + "', 'pw' : '" + groupPassword + "' }";
                                mSocket.emit("createGroup", JSONCreator.createJSON("createGroup", createGroupString));
                            }
                        }
                    })
                    .setNegativeButton("Abbrechen", null)
                    .create();
            builder.show();
        } else {
            Toast.makeText(getContext(), "Du bist leider nicht angemeldet. Bitte melde Dich an.", Toast.LENGTH_SHORT).show();
        }
    }

    @Click
    public void fabJoinGroupClicked() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View newGroupLayout = factory.inflate(R.layout.new_group, null);
            final EditText etGroupName = (EditText) newGroupLayout.findViewById(R.id.etGroupName);
            final EditText etGroupPassword = (EditText) newGroupLayout.findViewById(R.id.etGroupPassword);

            final AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                    .setTitle("Gruppe beitreten")
                    .setView(newGroupLayout)
                    .setPositiveButton("Beitreten", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            groupName = etGroupName.getText().toString();
                            groupPassword = etGroupPassword.getText().toString();
                            mSocket.on("status", status);
                            mSocket.connect();
                            if (groupName.equals("") || groupPassword.equals("")) {
                                Toast.makeText(getContext(), "Der Gruppenname/das Passwort darf nicht leer sein.", Toast.LENGTH_SHORT).show();
                            } else {
                                //{ 'user' : 'Erstellender Benutzer', 'name' : 'Gruppenname', 'pw' : 'Gruppenpasswort' }
                                String createGroupString = "{ 'user' : '" + username + "', 'name' : '" + groupName + "', 'pw' : '" + groupPassword + "' }";
                                mSocket.emit("authGroup", JSONCreator.createJSON("authGroup", createGroupString));
                            }
                        }
                    })
                    .setNegativeButton("Abbrechen", null)
                    .create();
            builder.show();
        } else {
            Toast.makeText(getContext(), "Du bist leider nicht angemeldet. Bitte melde Dich an.", Toast.LENGTH_SHORT).show();
        }
    }

    public void swapFragment() {
        Fragment fragment = new GroupDetailsFragment_();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    String group;
                    try {
                        emitterStatus = data.getString("status");
                        group = data.getString("group");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("createGroupSuccess")) {
                        myGroups.add(group);
                        memberCount.add(1);
                        onItemsLoadComplete();
                        Toast.makeText(getContext(), "Du hast die Gruppe " + groupName + " erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
                    }else if (emitterStatus.equals("createGroupFailed")) {
                        Toast.makeText(getContext(), "Der Gruppenname ist leider bereits vergeben. Probiere einen anderen.", Toast.LENGTH_SHORT).show();
                    }else if (emitterStatus.equals("provideGroups")) {
                        getGsonStatus(data.toString());
                    }else if (emitterStatus.equals("authGroupSuccess")) {
                        getGsonStatus(data.toString());
                    }else if (emitterStatus.equals("authGroupFailed")) {
                        Toast.makeText(getContext(), "Du konntest der Gruppe " + groupName + " nicht beitreten.", Toast.LENGTH_SHORT).show();
                    }
                    mSocket.disconnect();
                    mSocket.off();
                }
            });
        }
    };
    public void getGsonStatus(String data){
        //Mapped den ankommenden JSON in ein neues Status Objekt
        gsonStatus = new Gson().fromJson(data, Status.class);
        myGroups.clear();
        memberCount.clear();
        //Gruppennamen aus dem Status Objekt der ArrayList hinzufügen
        for (int i = 0; i < gsonStatus.getGroups().length; i++) {
            myGroups.add(gsonStatus.getGroups()[i].getName());
            memberCount.add(gsonStatus.getGroups()[i].getMembers().length);
        }
        onItemsLoadComplete();
        Status.setCurrentStatus(gsonStatus, getContext());
    }

/*
    @Override
    public void onDetach() {
        super.onDetach();
        mSocket.disconnect();
    }
    */
}
