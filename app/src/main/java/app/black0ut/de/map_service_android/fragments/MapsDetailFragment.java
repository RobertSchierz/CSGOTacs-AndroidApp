package app.black0ut.de.map_service_android.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.InstanceState;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import app.black0ut.de.map_service_android.adapter.GroupDialogAdapter;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
import app.black0ut.de.map_service_android.views.DrawingView;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.LocalStrategy;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.data.Strategy;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 03.01.16.
 * <p/>
 * Diese Klasse beschreibt das Fragment, welches für das Darstellen einer ausgewählten Karte zuständig ist.
 * Es beinhaltet ein Layout mit zwei 'ImageViews' welche zum einen das Bild der Karte ohne Callouts
 * und zum Anderen die Karte inklusive Callouts anzeigen.
 * Die 'ImageView' für die Callouts kann bei Bedarf durch den Nutzer an- und abgeschaltet werden.
 */

@EFragment(R.layout.fragment_map_detail)
public class MapsDetailFragment extends Fragment {

    @ViewById(R.id.map_image)
    ImageView mapImage;

    @ViewById(R.id.map_callouts)
    ImageView mapCallouts;

    @ViewById(R.id.canvas)
    RelativeLayout canvas;

    @ViewById
    FloatingActionButton fabShowCallouts;
    @ViewById
    FloatingActionButton fabSaveStrat;
    @ViewById
    FloatingActionButton fabEditStrat;
    @ViewById
    FloatingActionButton fabLiveMode;

    public int mapImageHeight;
    public int mapImageWidth;
    public Bitmap bitmap;
    private boolean showCalloutsClicked = false;
    private boolean editStratClicked = false;
    private boolean liveModeClicked = false;

    public LocalStrategy localStrategy;
    SharedPreferences sharedPreferences;
    private static String mUsername;
    private DrawingView mDrawingView;

    //Live Modus Dialog Variablen
    private AlertDialog mBuilder;
    private String mClickedGroup;
    private GroupDialogAdapter mAdapter;
    private ArrayList<String> myGroups = new ArrayList<>();
    private Status gsonStatus;

    //Live Content Variablen
    private static String mRoom;

    //Quelle: https://github.com/excilys/androidannotations/wiki/Save-instance-state
    @InstanceState
    Long stratId;
    @InstanceState
    String stratUser;
    @InstanceState
    String stratMap;
    @InstanceState
    String stratName;
    @InstanceState
    String stratGroup;
    @InstanceState
    boolean[] stratDrag;
    @InstanceState
    double[] stratX;
    @InstanceState
    double[] stratY;

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
        if (getArguments() != null) {
            stratId = getArguments().getLong("stratId");
            stratUser = getArguments().getString("stratUser");
            stratMap = getArguments().getString("stratMap");
            stratName = getArguments().getString("stratName");
            stratGroup = getArguments().getString("stratGroup");
            stratDrag = getArguments().getBooleanArray("stratDrag");
            stratX = getArguments().getDoubleArray("stratX");
            stratY = getArguments().getDoubleArray("stratY");
        }

        Map.checkMapName(mapImage, mapCallouts, getResources());

        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Klick Listener für den Button, welcher den Modus des Zeichnens auf einer Karte aktiviert.
     */
    @Click
    public void fabEditStratClicked() {
        if (!editStratClicked) {
            editStratClicked = true;

            mapImageWidth = mapImage.getWidth();
            mapImageHeight = mapImage.getHeight();

            DrawingView.isStrategy = false;
            DrawingView.isLiveMode = false;

            addDrawingViewToCanvas();

            fabSaveStrat.setVisibility(View.VISIBLE);
            fabEditStrat.setImageResource(R.drawable.ic_clear_orange_600_24dp);

            Log.d("TEST", "MapsDetailFragment Height: " + mapImageHeight + "Widht: " + mapImageWidth);
        } else {
            editStratClicked = false;
            mDrawingView.clearDrawingView();
            mDrawingView = null;
            canvas.removeAllViews();
            fabSaveStrat.setVisibility(View.GONE);
            fabEditStrat.setImageResource(R.drawable.ic_gesture_orange_600_24dp);

        }
    }

    private void addDrawingViewToCanvas() {
        mDrawingView = new DrawingView(getContext());
        //Layout Parameter, um die erstellte View in der Elternview zu zentrieren und auf die Größe des angezeigten Bildes anzupassen
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mapImageWidth, mapImageHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        //Die DrawingView zum RelativeLayout 'canvas' hinzufügen
        canvas.addView(mDrawingView, params);
    }

    /**
     * Klick Listener für den Button, welcher die Callouts anzeigt.
     */
    @Click
    public void fabShowCalloutsClicked() {
        if (showCalloutsClicked) {
            mapCallouts.setVisibility(View.VISIBLE);
            fabShowCallouts.setImageResource(R.drawable.ic_visibility_orange_600_24dp);
            showCalloutsClicked = false;
        } else {
            mapCallouts.setVisibility(View.GONE);
            fabShowCallouts.setImageResource(R.drawable.ic_visibility_off_orange_600_24dp);
            showCalloutsClicked = true;
        }
    }

    /**
     * Klick Listener für den Button, welcher eine gezeichnete Strategie speichert.
     */
    @Click
    public void fabSaveStratClicked() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            showDialog();
        }else{
            Toast.makeText(getContext(), "Bitte melde Dich an.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Klick Listener für den Button, welcher den Live Modus startet.
     */
    @Click
    public void fabLiveModeClicked() {
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {
            if (!liveModeClicked) {
                liveModeClicked = true;
                mapImageWidth = mapImage.getWidth();
                mapImageHeight = mapImage.getHeight();
                fabLiveMode.setImageResource(R.drawable.ic_clear_orange_600_24dp);
                fabEditStrat.setVisibility(View.GONE);
                showLiveModeDialog();
            } else {
                liveModeClicked = false;
                fabLiveMode.setImageResource(R.drawable.ic_fiber_manual_record_orange_600_24dp);
                fabEditStrat.setVisibility(View.VISIBLE);
                mDrawingView.clearDrawingView();
                mDrawingView.closeSocket();
                mDrawingView = null;
                canvas.removeAllViews();
                leaveGroupLive();
            }
        } else {
            Toast.makeText(getContext(), "Um den Live Modus zu starten, musst du Dich anmelden.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Zeigt einen Dialog zum auswählen einer Gruppe. Dabei werden die Gruppen angezeigt, in welchen
     * sich der Nutzer befindet. Beim Klick auf eine Gruppe wird der Live Modus mit dieser Gruppe
     * gestartet.
     */
    private void showLiveModeDialog() {
        HashMap<String, String> getGroupsMap = new HashMap<>();
        getGroupsMap.put("user", mUsername);

        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", getGroupsMap).toString());

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View chooseGroupLayout = factory.inflate(R.layout.group_dialog, null);
        final ListView groupDialogListView =
                (ListView) chooseGroupLayout.findViewById(R.id.groupDialogListView);
        mAdapter = new GroupDialogAdapter(myGroups, getContext());
        groupDialogListView.setAdapter(mAdapter);
        groupDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedGroup = mAdapter.getItem(position);
                DrawingView.isStrategy = false;
                DrawingView.isLiveMode = true;
                HashMap<String, String> joinGroupLive = new HashMap<>();
                joinGroupLive.put("user", mUsername);
                joinGroupLive.put("group", mClickedGroup);
                joinGroupLive.put("map", Map.clickedMapName);
                mSocket.on("status", status);
                mSocket.connect();
                mSocket.emit("joinGroupLive", JSONCreator.createJSON("joinGroupLive", joinGroupLive).toString());
            }
        });

        mBuilder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                .setTitle("Live Modus starten.")
                .setMessage("Wähle eine Gruppe für den Live Modus: ")
                .setView(chooseGroupLayout)
                .create();
        mBuilder.show();
    }

    /**
     * Zeigt einen Dialog zum bestimmen eines Taktiknamens und zum speichern dieser Taktik.
     */
    private void showDialog() {
        localStrategy = LocalStrategy.getInstance();
        if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)) {

            LayoutInflater factory = LayoutInflater.from(getContext());
            final View newStratLayout = factory.inflate(R.layout.new_strat, null);
            final EditText etStratName = (EditText) newStratLayout.findViewById(R.id.etStratName);

            final AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                    .setTitle("Strategie speichern")
                    .setView(newStratLayout)
                    .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            String stratName = etStratName.getText().toString();
                            if (stratName.equals("")) {
                                Toast.makeText(getContext(), "Du musst deiner Strategie einen Namen geben", Toast.LENGTH_SHORT).show();
                            } else {
                                prepareStrategyJson(stratName);
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

    private void prepareStrategyJson(String stratName) {
        localStrategy = LocalStrategy.getInstance();
        ArrayList<Boolean> dragList = localStrategy.getDragList();
        Boolean[] dragArray = dragList.toArray(new Boolean[dragList.size()]);
        ArrayList<Double> xList = localStrategy.getListX();
        Double[] xArray = xList.toArray(new Double[xList.size()]);
        ArrayList<Double> yList = localStrategy.getListY();
        Double[] yArray = yList.toArray(new Double[yList.size()]);
        Strategy strategy = new Strategy(System.currentTimeMillis(), mUsername, Map.clickedMapName,
                stratName, null, dragArray, xArray, yArray);
        Gson gson = new Gson();
        String createTac = gson.toJson(strategy);
        Log.d("TEST", createTac);

        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("createTac", createTac);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapImage.setImageDrawable(null);
        mapCallouts.setImageDrawable(null);
        mapCallouts.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (liveModeClicked) {
            liveModeClicked = false;
            fabLiveMode.setImageResource(R.drawable.ic_fiber_manual_record_orange_600_24dp);
            fabEditStrat.setVisibility(View.VISIBLE);
            if (mDrawingView != null) {
                mDrawingView.clearDrawingView();
                mDrawingView.closeSocket();
                mDrawingView = null;
            }
            canvas.removeAllViews();
            leaveGroupLive();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (liveModeClicked) {
            liveModeClicked = false;
            fabLiveMode.setImageResource(R.drawable.ic_fiber_manual_record_orange_600_24dp);
            fabEditStrat.setVisibility(View.VISIBLE);
            if (mDrawingView != null) {
                mDrawingView.clearDrawingView();
                mDrawingView.closeSocket();
                mDrawingView = null;
            }
            canvas.removeAllViews();
            leaveGroupLive();
        }
    }

    private void leaveGroupLive() {
        HashMap<String, String> leaveGroupLive = new HashMap<>();
        leaveGroupLive.put("room", mRoom);
        mSocket.emit("leaveGroupLive", JSONCreator.createJSON("leaveGroupLive", leaveGroupLive).toString());
        mSocket.disconnect();
        mSocket.off();
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
                    try {
                        emitterStatus = data.getString("status");
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("createTacSuccess")) {
                        Toast.makeText(getContext(), "Strategie erfolgreich gespeichert.", Toast.LENGTH_SHORT).show();
                        mSocket.disconnect();
                        mSocket.off();
                    } else if (emitterStatus.equals("createTacFailed")) {
                        Toast.makeText(getContext(), "Strategie konnte nicht gespeichert werden.", Toast.LENGTH_SHORT).show();
                        mSocket.disconnect();
                        mSocket.off();
                    }
                    if (emitterStatus.equals("provideGroups")) {
                        getGsonStatus(data.toString());
                    }
                    if (emitterStatus.equals("connectedClients")) {
                        addDrawingViewToCanvas();
                        mBuilder.cancel();
                    }
                    if (emitterStatus.equals("provideRoomName")) {
                        try {
                            mRoom = data.getString("room");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    if (emitterStatus.equals("liveContent")) {
                        try {
                            mRoom = data.getString("room");
                            String user = data.getString("user");
                            double startX = data.getDouble("startX");
                            double startY = data.getDouble("startY");
                            double x = data.getDouble("x");
                            double y = data.getDouble("y");
                            boolean drag = data.getBoolean("drag");
                            mDrawingView.drawLiveContent(drag, x, y, startX, startY);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    public void getGsonStatus(String data) {
        //Mapped den ankommenden JSON in ein neues Status Objekt
        gsonStatus = new Gson().fromJson(data, Status.class);
        myGroups.clear();
        //Gruppennamen aus dem Status Objekt der ArrayList hinzufügen
        for (int i = 0; i < gsonStatus.getGroups().length; i++) {
            myGroups.add(gsonStatus.getGroups()[i].getName());
        }
        Status.setCurrentStatus(gsonStatus, getContext());
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mAdapter.notifyDataSetChanged();
    }

    public static String getRoom() {
        return mRoom;
    }

    public static String getUsername() {
        return mUsername;
    }

}
