package app.black0ut.de.map_service_android.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
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
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URISyntaxException;
import java.util.ArrayList;

import app.black0ut.de.map_service_android.DrawingView;
import app.black0ut.de.map_service_android.JSONCreator;
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

@EFragment(R.layout.fragment_maps_detail)
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

    private int BITMAP_WIDHT = 1024;
    private int BITMAP_HEIGHT = 1024;

    public int mapImageHeight;
    public int mapImageWidth;
    public Bitmap bitmap;
    private boolean showCalloutsClicked = false;

    public LocalStrategy localStrategy;
    SharedPreferences sharedPreferences;
    private String mUsername;

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

        checkMapName();

        sharedPreferences = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        mUsername = sharedPreferences.getString(User.USERNAME, null);

        DrawingView.mPaint.setAntiAlias(true);
        DrawingView.mPaint.setDither(true);
        DrawingView.mPaint.setColor(ContextCompat.getColor(getContext(), R.color.orangePrimary));
        DrawingView.mPaint.setStyle(Paint.Style.STROKE);
        DrawingView.mPaint.setStrokeJoin(Paint.Join.ROUND);
        DrawingView.mPaint.setStrokeCap(Paint.Cap.ROUND);
        DrawingView.mPaint.setStrokeWidth(pxToDp(14));

        //LinearLayout canvas = (LinearLayout)getView().findViewById(R.id.canvas);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public void loadMapBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    public void loadCalloutBitmap(int resId, ImageView imageView) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView);
        task.execute(resId);
    }

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Prüft den Namen der geklickten Karte und setzt dann die ImageResource der ImageView auf das passende Bild.
     */
    private void checkMapName() {
        switch (Map.clickedMapName) {
            case Map.ASSAULT:
                loadMapBitmap(R.drawable.cs_assault_radar, mapImage);
                break;
            case Map.AZTEC:
                loadMapBitmap(R.drawable.de_aztec_radar_spectate, mapImage);
                break;
            case Map.CACHE:
                loadMapBitmap(R.drawable.de_cache_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_cache_radar_spectate_callout, mapCallouts);
                break;
            case Map.COBBLESTONE:
                loadMapBitmap(R.drawable.de_cbble_radar, mapImage);
                loadCalloutBitmap(R.drawable.de_cbble_radar_callout, mapCallouts);
                break;
            case Map.DUST:
                loadMapBitmap(R.drawable.de_dust_radar_spectate, mapImage);
                break;
            case Map.DUST2:
                loadMapBitmap(R.drawable.de_dust2_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_dust2_radar_spectate_callout, mapCallouts);
                break;
            case Map.INFERNO:
                loadMapBitmap(R.drawable.de_inferno_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_inferno_radar_spectate_callout, mapCallouts);
                break;
            case Map.ITALY:
                loadMapBitmap(R.drawable.cs_italy_radar, mapImage);
                break;
            case Map.MILITIA:
                loadMapBitmap(R.drawable.cs_militia_radar_spectate, mapImage);
                break;
            case Map.MIRAGE:
                loadMapBitmap(R.drawable.de_mirage_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_mirage_radar_spectate_callout, mapCallouts);
                break;
            case Map.NUKE:
                loadMapBitmap(R.drawable.de_nuke_radar_spectate, mapImage);
                break;
            case Map.OFFICE:
                loadMapBitmap(R.drawable.cs_office_radar, mapImage);
                break;
            case Map.OVERPASS:
                loadMapBitmap(R.drawable.de_overpass_radar, mapImage);
                loadCalloutBitmap(R.drawable.de_overpass_radar_callout, mapCallouts);
                break;
            case Map.TRAIN:
                loadMapBitmap(R.drawable.de_train_radar_spectate, mapImage);
                loadCalloutBitmap(R.drawable.de_train_radar_spectate_callout, mapCallouts);
                break;
            case Map.VERTIGO:
                loadMapBitmap(R.drawable.de_vertigo_radar, mapImage);
                break;
            default:
                Log.d("MAP CLICK", "No image for the clicked Map.");
        }
    }

    /**
     * Klick Listener für den Button, welcher den Modus des Zeichnens auf einer Karte aktiviert.
     */
    @Click
    public void fabEditStratClicked() {
        mapImageWidth = mapImage.getWidth();
        mapImageHeight = mapImage.getHeight();

        DrawingView mDrawingView = new DrawingView(getContext());
        //Layout Parameter, um die erstellte View in der Elternview zu zentrieren und auf die Größe des angezeigten Bildes anzupassen
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mapImageWidth, mapImageHeight);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        //Die DrawingView zum RelativeLayout 'canvas' hinzufügen
        canvas.addView(mDrawingView, params);

        fabSaveStrat.setVisibility(View.VISIBLE);

        Log.d("TEST", "MapsDetailFragment Height: " + mapImageHeight + "Widht: " + mapImageWidth);
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
     * Zeigt einen Dialog zum bestimmen eines Taktiknamens und zum speichern dieser Taktik.
     */
    private void showDialog(){
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

    private void prepareStrategyJson(String stratName){
        localStrategy = LocalStrategy.getInstance();
        ArrayList<Boolean> dragList = localStrategy.getDragList();
        Boolean [] dragArray = dragList.toArray(new Boolean[dragList.size()]);
        ArrayList<Integer> xList = localStrategy.getListX();
        Integer [] xArray = xList.toArray(new Integer[xList.size()]);
        ArrayList<Integer> yList = localStrategy.getListY();
        Integer [] yArray = yList.toArray(new Integer[yList.size()]);
        Strategy strategy = new Strategy();
        strategy.id = System.currentTimeMillis();
        strategy.user = mUsername;
        strategy.map = Map.clickedMapName;
        strategy.name = stratName;
        strategy.drag = dragArray;
        strategy.x = xArray;
        strategy.y = yArray;
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

    /**
     * Quelle: http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     */
    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(getResources(), data, BITMAP_WIDHT, BITMAP_HEIGHT);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
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
