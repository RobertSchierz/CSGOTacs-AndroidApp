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
import android.widget.EditText;
import android.widget.ImageView;
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

import app.black0ut.de.map_service_android.DrawingView;
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

    public LocalStrategy localStrategy;
    SharedPreferences sharedPreferences;
    private String mUsername;
    private DrawingView mDrawingView;

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

        DrawingView.sPaint.setAntiAlias(true);
        DrawingView.sPaint.setDither(true);
        DrawingView.sPaint.setColor(ContextCompat.getColor(getContext(), R.color.orangePrimary));
        DrawingView.sPaint.setStyle(Paint.Style.STROKE);
        DrawingView.sPaint.setStrokeJoin(Paint.Join.ROUND);
        DrawingView.sPaint.setStrokeCap(Paint.Cap.ROUND);
        DrawingView.sPaint.setStrokeWidth(pxToDp(14));

        //LinearLayout canvas = (LinearLayout)getView().findViewById(R.id.canvas);
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

            mDrawingView = new DrawingView(getContext());
            //Layout Parameter, um die erstellte View in der Elternview zu zentrieren und auf die Größe des angezeigten Bildes anzupassen
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mapImageWidth, mapImageHeight);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            //Die DrawingView zum RelativeLayout 'canvas' hinzufügen
            canvas.addView(mDrawingView, params);

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
        showDialog();
    }

    /**
     * Klick Listener für den Button, welcher den Live Modus startet.
     */
    @Click
    public void fabLiveModeClicked(){
        Toast.makeText(getContext(), "Live Mode will be available soon :-)", Toast.LENGTH_SHORT).show();
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
                    } else if (emitterStatus.equals("createTacFailed")) {
                        Toast.makeText(getContext(), "Strategie konnte nicht gespeichert werden.", Toast.LENGTH_SHORT).show();
                    }
                    mSocket.disconnect();
                    mSocket.off();
                }
            });
        }
    };

}
