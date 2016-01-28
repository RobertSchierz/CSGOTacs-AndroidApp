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
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupsRecyclerViewAdapter;
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
    /*
        @ViewById
        EditText etGroupName;
        @ViewById
        EditText etGroupPassword;
    */
    @ViewById
    public RecyclerView mGroupsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;

    /*private String[] myGroups = {"Gruppe 1", "Gruppe 2", "Gruppe 3", "Gruppe 4", "Gruppe 5",
            "Gruppe 6", "Gruppe 7", "Gruppe 8", "Gruppe 9", "Gruppe 10"};
            */
    private ArrayList<String> myGroups = new ArrayList<>();

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

        username = sharedPreferences.getString(User.USERNAME, null);

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



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
            }
        });

    }

    void refreshItems() {

        // Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        // Update the adapter and notify data set changed
        // ...

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

    public void swapFragment() {
        Fragment fragment = new MyProfileDetailsFragment_();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }

    String name;
    String[] member;
    String admin;
    String[] mods;

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
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
                    if (emitterStatus.equals("createGroupSuccess")) {
                        Toast.makeText(getContext(), "Du hast die Gruppe " + groupName + " erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
                        mSocket.disconnect();
                    } else if (emitterStatus.equals("createGroupFailed")) {
                        Toast.makeText(getContext(), "Der Gruppenname ist leider bereits vergeben. Probiere einen anderen.", Toast.LENGTH_SHORT).show();
                        mSocket.disconnect();
                    } else if (emitterStatus.equals("provideGroups")) {
                        try {
                            JSONArray groups = data.getJSONArray("groups");
                            for (int i = 0; i < groups.length(); i++) {
                                JSONObject group = groups.getJSONObject(i);

                                name = group.getString("name");
                                myGroups.add(name);

                                JSONArray memberArray = group.getJSONArray("member");
                                member = new String[memberArray.length()];
                                for (int j = 0; j < memberArray.length(); j++) {
                                    member[j] = memberArray.getString(j);
                                }

                                admin = group.getString("admin");

                                JSONArray modsArray = group.getJSONArray("mods");
                                mods = new String[modsArray.length()];
                                for (int j = 0; j < modsArray.length(); j++) {
                                    mods[j] = modsArray.getString(j);
                                }
                            }
                            mAdapter = new GroupsRecyclerViewAdapter(myGroups);
                            mGroupsRecyclerView.setAdapter(mAdapter);
                            mSocket.disconnect();
                        } catch (JSONException e) {
                            Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        }
                    }
                }
            });
        }
    };
/*
    @Override
    public void onDetach() {
        super.onDetach();
        mSocket.disconnect();
    }
    */
}
