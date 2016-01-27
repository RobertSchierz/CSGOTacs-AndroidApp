package app.black0ut.de.map_service_android.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

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
    private String groupPasswort;

    @ViewById
    public RecyclerView mGroupsRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String[] myDataset = {"Gruppe 1", "Gruppe 2", "Gruppe 3", "Gruppe 4", "Gruppe 5",
            "Gruppe 6", "Gruppe 7", "Gruppe 8", "Gruppe 9", "Gruppe 10"};

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
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mGroupsRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mGroupsRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new GroupsRecyclerViewAdapter(myDataset);
        mGroupsRecyclerView.setAdapter(mAdapter);

    }

    @Click
    public void fabNewGroupClicked() {
        String username = sharedPreferences.getString(User.USERNAME, null);

        /*
        new MaterialDialog.Builder(getContext())
                .title("Gruppennamen vergeben")
                        //.content(R.string.input_content)
                .positiveText("Weiter")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("Gruppenname", null, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        groupName = input.toString();
                    }
                }).dismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                new MaterialDialog.Builder(getContext())
                        .title("Gruppenpasswort vergeben")
                                //.content(R.string.input_content)
                        .positiveText("Erstellen")
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Gruppenpasswort", null, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                groupPasswort = input.toString();
                            }
                        }).dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Toast.makeText(getContext(), "Gruppe " + groupName + " erfolgreich erstellt.", Toast.LENGTH_SHORT).show();
                    }
                }).show();
            }
        }).show();
        */
        //mSocket.on("status", status);
        //mSocket.connect();
        //{ 'user' : 'Erstellender Benutzer', 'name' : 'Gruppenname', 'pw' : 'Gruppenpasswort' }

        /*String createGroupString = "{ 'user' : '" + username + "', 'name' : 'Gruppenname', 'pw' : 'Gruppenpasswort' }";
        try {
            emitterStatus = data.getString("status");
        } catch (JSONException e) {
            Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
            return;
        }*/
        Toast.makeText(getContext(), "fabNewGroup Clicked!", Toast.LENGTH_SHORT).show();
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

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Activity activity = (Activity) getContext();
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
                    if (emitterStatus.equals("regSuccess")) {
                        Toast.makeText(getContext(), "Du hast Dich erfolgreich registriert.", Toast.LENGTH_SHORT).show();
                        swapFragment();
                        mSocket.disconnect();
                    } else if (emitterStatus.equals("regFailed")) {
                        mSocket.disconnect();
                    }

                }
            });
        }
    };
}
