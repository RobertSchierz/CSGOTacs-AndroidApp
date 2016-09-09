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
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import app.black0ut.de.map_service_android.data.Connect;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
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

/**
 * Fragment, für die Anzeige einer Gruppenliste.
 */
@EFragment(R.layout.fragment_groups)
public class GroupsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    private String groupName;
    private String groupPassword;
    private String mUsername;
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

    /**
     * Stellt eine Socket Verbindung zum Server her.
     */
    private void setupSocket() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "name=" + Connect.c97809177;
            opts.timeout = 5000;
            mSocket = IO.socket("https://dooku.corvus.uberspace.de/", opts);
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

    /**
     * Methode, die beim Starten des Fragments ausgeführt wird.
     * Sie wird verwendet, um Operationen auszuführen, die vor allen anderen ausgeführt werden sollen.
     * Zum Beispiel die Einrichtung eines startenden Fragments.
     * Methoden mit der Annotation '@AfterViews' werden nach der 'setContentView' Methode der
     * generierten Klasse aufgerufen
     * (siehe dazu: https://github.com/excilys/androidannotations/wiki/injecting-views).
     */
    @AfterViews
    public void afterViews() {
        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        pullToRefreshText.setVisibility(View.VISIBLE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);

        if (gsonStatus == null) {
            pullToRefreshText.setVisibility(View.VISIBLE);
        } else {
            pullToRefreshText.setVisibility(View.GONE);
        }

        mGroupsRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GroupsRecyclerViewAdapter(myGroups, memberCount, getActivity().getSupportFragmentManager(), getContext());
        mGroupsRecyclerView.setAdapter(mAdapter);

        refreshItems();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });
    }

    /**
     * Aktualisiert die Liste der Gruppen.
     */
    void refreshItems() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            myGroups.clear();
            memberCount.clear();
            HashMap<String, String> getGroupsMap = new HashMap<>();
            getGroupsMap.put("user", mUsername);

            //mSocket.on("status", status);
            //mSocket.connect();
            setupSocket();
            mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", getGroupsMap).toString());
        } else {
            Toast.makeText(getContext(), getResources().getText(R.string.check_login_status), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Aktualisiert den Datensatz des Adapters und stoppt die Animation des SwipeRefreshLayout.
     */
    void onItemsLoadComplete() {
        pullToRefreshText.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * Klick-Listener für den Button zum Erstellen einer neuen Gruppe.
     * Er öffnet einen Dialog, der nach dem Gruppennamen und dem Passwort fragt.
     */
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
                            //mSocket.on("status", status);
                            //mSocket.connect();
                            setupSocket();
                            if (groupName.equals("") || groupPassword.equals("")) {
                                Toast.makeText(getContext(), getResources().getText(R.string.group_name_or_password_empty), Toast.LENGTH_SHORT).show();
                            } else {
                                HashMap<String, String> createGroupMap = new HashMap<>();
                                createGroupMap.put("user", mUsername);
                                createGroupMap.put("name", groupName);
                                createGroupMap.put("pw", groupPassword);
                                mSocket.emit("createGroup", JSONCreator.createJSON("createGroup", createGroupMap).toString());
                            }
                        }
                    })
                    .setNegativeButton("Abbrechen", null)
                    .create();
            builder.show();
        } else {
            Toast.makeText(getContext(), getResources().getText(R.string.check_login_status), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Klick-Listener für den Button zum Beitreten einer neuen Gruppe.
     * Er öffnet einen Dialog, der nach dem Gruppennamen und dem Passwort fragt.
     */
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
                            //mSocket.on("status", status);
                            //mSocket.connect();
                            setupSocket();
                            if (groupName.equals("") || groupPassword.equals("")) {
                                Toast.makeText(getContext(), getResources().getText(R.string.group_name_or_password_empty), Toast.LENGTH_SHORT).show();
                            } else {
                                HashMap<String, String> joinGroupMap = new HashMap<>();
                                joinGroupMap.put("user", mUsername);
                                joinGroupMap.put("name", groupName);
                                joinGroupMap.put("pw", groupPassword);
                                mSocket.emit("authGroup", JSONCreator.createJSON("authGroup", joinGroupMap).toString());
                            }
                        }
                    })
                    .setNegativeButton("Abbrechen", null)
                    .create();
            builder.show();
        } else {
            Toast.makeText(getContext(), getResources().getText(R.string.check_login_status), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Socket Listener, welcher auf Antworten des Servers reagiert.
     */
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
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("createGroupSuccess")) {
                            refreshItems();
                            Toast.makeText(getContext(), String.format(getResources().getString(R.string.group_created), groupName), Toast.LENGTH_SHORT).show();
                    } else if (emitterStatus.equals("createGroupFailed")) {
                        Toast.makeText(getContext(), "Der Gruppenname ist leider bereits vergeben. Probiere einen anderen.", Toast.LENGTH_SHORT).show();
                    } else if (emitterStatus.equals("provideGroups")) {
                        getGsonStatus(data.toString());
                    } else if (emitterStatus.equals("authGroupSuccess")) {
                        getGsonStatus(data.toString());
                    } else if (emitterStatus.equals("authGroupFailed")) {
                        Toast.makeText(getContext(), "Du konntest der Gruppe " + groupName + " nicht beitreten.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    /**
     * Wandelt eine JSON in ein Java Objekt vom Typ Status um.
     * @param data String mit der JSON.
     */
    public void getGsonStatus(String data) {
        //Mapped den ankommenden JSON in ein neues Status Objekt
        gsonStatus = new Gson().fromJson(data, Status.class);
        myGroups.clear();
        memberCount.clear();
        //Gruppennamen aus dem Status Objekt der ArrayList hinzufügen
        for (int i = 0; i < gsonStatus.getGroups().length; i++) {
            myGroups.add(gsonStatus.getGroups()[i].getName());
            memberCount.add(gsonStatus.getGroups()[i].getMembers().length);
        }
        Status.setCurrentStatus(gsonStatus, getContext());
        onItemsLoadComplete();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSocket.disconnect();
        mSocket.off();
    }

    @Override
    public void onStop() {
        super.onStop();
        mSocket.disconnect();
        mSocket.off();
    }
}
