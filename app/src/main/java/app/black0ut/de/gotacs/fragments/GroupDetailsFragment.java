package app.black0ut.de.gotacs.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import app.black0ut.de.gotacs.data.Connect;
import app.black0ut.de.gotacs.jsoncreator.JSONCreator;
import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.adapter.GroupDetailRecyclerViewAdapter;
import app.black0ut.de.gotacs.data.Group;
import app.black0ut.de.gotacs.data.Status;
import app.black0ut.de.gotacs.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 02.02.2016.
 */
@EFragment(R.layout.fragment_group_details)
public class GroupDetailsFragment extends Fragment {

    SharedPreferences sharedPreferences;
    private Status gsonStatus;
    Group currentGroup;
    ArrayList <String> mMembers;
    String[] mMods;
    String mAdmin;

    RecyclerView.Adapter mAdapter;

    @ViewById
    public RecyclerView mGroupsDetailsRecyclerView;
    @ViewById
    SwipeRefreshLayout swipeRefreshLayout;
    @ViewById
    LinearLayout leaveDeleteLayout;

    //Quelle: https://github.com/excilys/androidannotations/wiki/Save-instance-state
    @InstanceState
    String clickedGroup;

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
        clickedGroup = getArguments().getString("clickedGroup");
        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        final String username = sharedPreferences.getString(User.USERNAME, null);

        gsonStatus = Status.getCurrentStatus(getContext());
        currentGroup = gsonStatus.getGroupFromName(clickedGroup);
        mMembers = new ArrayList<>();
        Collections.addAll(mMembers, currentGroup.getMembers());
        mMods = currentGroup.getMods();
        mAdmin = currentGroup.getAdmin();

        mGroupsDetailsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsDetailsRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new GroupDetailRecyclerViewAdapter(username, mMembers, mMods, mAdmin, currentGroup.getName(),getActivity().getSupportFragmentManager(), getContext());
        mGroupsDetailsRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems(username);
            }
        });
    }

    /**
     * Aktualisiert die Liste der Gruppen.
     */
    void refreshItems(String username) {
        HashMap<String, String> getGroupsMap = new HashMap<>();
        getGroupsMap.put("user", username);

        //mSocket.on("status", status);
        //mSocket.connect();
        setupSocket();
        mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", getGroupsMap).toString());
    }

    /**
     * Aktualisiert den Datensatz des Adapters und stoppt die Animation des SwipeRefreshLayout.
     */
    void onItemsLoadComplete() {
        mAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
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
                    try {
                        emitterStatus = data.getString("status");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("provideGroups")) {
                        //Mapped den ankommenden JSON in ein neues Status Objekt
                        gsonStatus = new Gson().fromJson(data.toString(), Status.class);
                        mMembers.clear();
                        //Gruppennamen aus dem Status Objekt der ArrayList hinzufügen
                        String[] newMembers = gsonStatus.getGroupFromName(clickedGroup).getMembers();
                        for (int i = 0; i < newMembers.length; i++) {
                            mMembers.add(newMembers[i]);
                        }
                        onItemsLoadComplete();
                        Status.setCurrentStatus(gsonStatus, getContext());
                    }
                    mSocket.disconnect();
                    mSocket.off();
                }
            });
        }
    };
}
