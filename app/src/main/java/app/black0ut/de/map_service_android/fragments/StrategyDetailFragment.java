package app.black0ut.de.map_service_android.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

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

import app.black0ut.de.map_service_android.views.DrawingView;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.adapter.GroupDialogAdapter;
import app.black0ut.de.map_service_android.data.LocalStrategy;
import app.black0ut.de.map_service_android.data.Map;
import app.black0ut.de.map_service_android.data.Status;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 13.02.2016.
 */
@EFragment(R.layout.fragment_strategy_detail)
public class StrategyDetailFragment extends Fragment {
    @ViewById(R.id.strat_map_image)
    ImageView mapImage;
    @ViewById(R.id.strat_map_callouts)
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
    FloatingActionButton fabShareStrat;

    public int mapImageHeight;
    public int mapImageWidth;
    private boolean showCalloutsClicked = false;
    private DrawingView mDrawingView;

    public LocalStrategy localStrategy;
    SharedPreferences sharedPreferences;
    private String mUsername;
    private Status gsonStatus;
    private ArrayList<String> myGroups = new ArrayList<>();
    private GroupDialogAdapter mAdapter;
    private AlertDialog mBuilder;
    private String mClickedGroup;

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
            //mSocket = IO.socket("http://chat.socket.io");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    @AfterViews
    public void afterViews() {
        Map.checkMapName(mapImage, mapCallouts, getResources());

        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);

        DrawingView.sPaint.setAntiAlias(true);
        DrawingView.sPaint.setDither(true);
        DrawingView.sPaint.setColor(ContextCompat.getColor(getContext(), R.color.orangePrimary));
        DrawingView.sPaint.setStyle(Paint.Style.STROKE);
        DrawingView.sPaint.setStrokeJoin(Paint.Join.ROUND);
        DrawingView.sPaint.setStrokeCap(Paint.Cap.ROUND);
        DrawingView.sPaint.setStrokeWidth(pxToDp(14));
        loadStrat();

        //LinearLayout canvas = (LinearLayout)getView().findViewById(R.id.canvas);
    }


    public void loadStrat() {
        if (getArguments() != null) {
            stratId = getArguments().getLong("stratId");
            stratUser = getArguments().getString("stratUser");
            stratMap = getArguments().getString("stratMap");
            stratName = getArguments().getString("stratName");
            stratGroup = getArguments().getString("stratGroup");
            stratDrag = getArguments().getBooleanArray("stratDrag");
            stratX = getArguments().getDoubleArray("stratX");
            stratY = getArguments().getDoubleArray("stratY");
            DrawingView.sDrag = stratDrag;
            DrawingView.sX = stratX;
            DrawingView.sY = stratY;
            DrawingView.isStrategy = true;
            DrawingView.isLiveMode = false;

            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            mapImageWidth = metrics.widthPixels;
            mapImageHeight = metrics.widthPixels;

            Log.d("TEST", "Width: " + mapImageWidth);
            Log.d("TEST", "Height: " + mapImageHeight);

            mDrawingView = new DrawingView(getContext());
            //Layout Parameter, um die erstellte View in der Elternview zu zentrieren und auf die Größe des angezeigten Bildes anzupassen
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mapImageWidth, mapImageHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            //Die DrawingView zum RelativeLayout 'canvas' hinzufügen
            canvas.addView(mDrawingView, params);
        }
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
        final AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                .setTitle("Taktik zurücksetzen")
                .setMessage("Möchtest Du deine Zeichnung löschen und von vorn beginnen?")
                .setPositiveButton("Zurücksetzen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        mDrawingView.clearDrawingView();
                    }
                })
                .setNegativeButton("Abbrechen", null)
                .create();
        builder.show();
    }

    /**
     * Klick Listener für den Button, welcher die Callouts anzeigt.
     */
    @Click
    public void fabShowCalloutsClicked() {
        if (!showCalloutsClicked) {
            mapCallouts.setVisibility(View.VISIBLE);
            fabShowCallouts.setImageResource(R.drawable.ic_visibility_off_orange_600_24dp);
            showCalloutsClicked = true;
        } else {
            mapCallouts.setVisibility(View.GONE);
            fabShowCallouts.setImageResource(R.drawable.ic_visibility_orange_600_24dp);
            showCalloutsClicked = false;
        }
    }

    /**
     * Klick Listener für den Button, welcher eine gezeichnete Strategie speichert.
     */
    @Click
    public void fabSaveStratClicked() {
        showSaveDialog();
    }

    @Click
    public void fabShareStratClicked() {
        showShareDialog();
    }

    /**
     * Zeigt einen Dialog zum auswählen einer Gruppe. Dabei werden die Gruppen angezeigt, in welchen
     * sich der Nutzer befindet. Beim Klick auf eine Gruppe wird die
     * aktuelle Strategie mit dieser geteilt.
     */
    private void showShareDialog() {
        HashMap<String, String> getGroupsMap = new HashMap<>();
        getGroupsMap.put("user", mUsername);

        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("getGroups", JSONCreator.createJSON("getGroups", getGroupsMap).toString());

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View shareStratLayout = factory.inflate(R.layout.group_dialog, null);
        final ListView groupDialogListView =
                (ListView) shareStratLayout.findViewById(R.id.groupDialogListView);
        mAdapter = new GroupDialogAdapter(myGroups, getContext());
        groupDialogListView.setAdapter(mAdapter);
        groupDialogListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedGroup = mAdapter.getItem(position);
                HashMap<String, Object> bindTac = new HashMap<>();
                bindTac.put("id", stratId);
                bindTac.put("group", mClickedGroup);
                mSocket.on("status", status);
                mSocket.connect();
                mSocket.emit("bindTac", JSONCreator.createJSON("bindTac", bindTac).toString());
            }
        });

        mBuilder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                .setTitle("Strategie teilen")
                .setView(shareStratLayout)
                .create();
        mBuilder.show();
    }

    /**
     * Zeigt einen Dialog zum bestimmen eines Taktiknamens und zum speichern dieser Taktik.
     */

    private void showSaveDialog() {
        final AlertDialog builder = new AlertDialog.Builder(getActivity(), R.style.CreateGroup)
                .setTitle("Strategie speichern")
                .setMessage("Möchtest Du den aktuellen Stand speichern?")
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        prepareStrategyJson();
                    }
                })
                .setNegativeButton("Abbrechen", null)
                .create();
        builder.show();
    }

    private void prepareStrategyJson() {
        localStrategy = LocalStrategy.getInstance();
        ArrayList<Boolean> dragList = localStrategy.getDragList();
        Boolean[] dragArray = dragList.toArray(new Boolean[dragList.size()]);
        ArrayList<Double> xList = localStrategy.getListX();
        Double[] xArray = xList.toArray(new Double[xList.size()]);
        ArrayList<Double> yList = localStrategy.getListY();
        Double[] yArray = yList.toArray(new Double[yList.size()]);

        HashMap<String, Object> changeTac = new HashMap<>();
        changeTac.put("id", stratId);
        changeTac.put("drag", dragArray);
        changeTac.put("x", xArray);
        changeTac.put("y", yArray);

        mSocket.on("status", status);
        mSocket.connect();
        mSocket.emit("changeTac", JSONCreator.createJSON("changeTac", changeTac).toString());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mapImage.setImageDrawable(null);
        mapCallouts.setImageDrawable(null);
        mapCallouts.setVisibility(View.GONE);
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
                        Log.d("TEST", emitterStatus);
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("changeTacSuccess")) {
                        Toast.makeText(getContext(), "Strategie erfolgreich geändert.", Toast.LENGTH_SHORT).show();
                    }
                    if (emitterStatus.equals("bindTacSuccess")) {
                        Toast.makeText(getContext(), "Strategie '" + stratName +
                                "' erfolgreich mit der Gruppe '" + mClickedGroup +
                                "'geteilt.", Toast.LENGTH_SHORT).show();
                        mBuilder.cancel();
                    }
                    if (emitterStatus.equals("provideGroups")) {
                        getGsonStatus(data.toString());
                    }
                    mSocket.disconnect();
                    mSocket.off();
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
}
