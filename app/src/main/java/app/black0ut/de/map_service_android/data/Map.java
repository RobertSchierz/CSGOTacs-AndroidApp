package app.black0ut.de.map_service_android.data;


import android.content.res.Resources;
import android.util.Log;
import android.widget.ImageView;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.tasks.BitmapWorkerTask;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */
public class Map {

    public static final String DUST2 = "Dust2";
    public static final String TRAIN = "Train";
    public static final String MIRAGE = "Mirage";
    public static final String INFERNO = "Inferno";
    public static final String COBBLESTONE = "Cobblestone";
    public static final String OVERPASS = "Overpass";
    public static final String CACHE = "Cache";
    public static final String AZTEC = "Aztec";
    public static final String DUST = "Dust";
    public static final String VERTIGO = "Vertigo";
    public static final String NUKE = "Nuke";
    public static final String OFFICE = "Office";
    public static final String ITALY = "Italy";
    public static final String ASSAULT = "Assault";
    public static final String MILITIA = "Militia";

    //Variable zum speichern der geklickten Map im MapsFragment
    public static String clickedMapName = "";

    public final String mapName;
    public final int mapPreviewId;

    public Map(String mapName, int mapPreviewId){
        this.mapName = mapName;
        this.mapPreviewId = mapPreviewId;
    }

    public static void loadMapBitmap(int resId, ImageView imageView, Resources resources) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, resources);
        task.execute(resId);
    }

    public static void loadCalloutBitmap(int resId, ImageView imageView, Resources resources) {
        BitmapWorkerTask task = new BitmapWorkerTask(imageView, resources);
        task.execute(resId);
    }

    /**
     * Prüft den Namen der geklickten Karte und setzt dann die ImageResource der ImageView auf das passende Bild.
     */
    public static void checkMapName(ImageView mapImage, ImageView mapCallouts, Resources resources) {
        switch (Map.clickedMapName) {
            case Map.ASSAULT:
                loadMapBitmap(R.drawable.cs_assault_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.AZTEC:
                loadMapBitmap(R.drawable.de_aztec_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.CACHE:
                loadMapBitmap(R.drawable.de_cache_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_cache_radar_spectate_callout, mapCallouts, resources);
                break;
            case Map.COBBLESTONE:
                loadMapBitmap(R.drawable.de_cbble_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_cbble_radar_callout, mapCallouts, resources);
                break;
            case Map.DUST:
                loadMapBitmap(R.drawable.de_dust_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.DUST2:
                loadMapBitmap(R.drawable.de_dust2_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_dust2_radar_spectate_callout, mapCallouts, resources);
                break;
            case Map.INFERNO:
                loadMapBitmap(R.drawable.de_inferno_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_inferno_radar_spectate_callout, mapCallouts, resources);
                break;
            case Map.ITALY:
                loadMapBitmap(R.drawable.cs_italy_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.MILITIA:
                loadMapBitmap(R.drawable.cs_militia_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.MIRAGE:
                loadMapBitmap(R.drawable.de_mirage_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_mirage_radar_spectate_callout, mapCallouts, resources);
                break;
            case Map.NUKE:
                loadMapBitmap(R.drawable.de_nuke_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.OFFICE:
                loadMapBitmap(R.drawable.cs_office_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            case Map.OVERPASS:
                loadMapBitmap(R.drawable.de_overpass_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_overpass_radar_callout, mapCallouts, resources);
                break;
            case Map.TRAIN:
                loadMapBitmap(R.drawable.de_train_radar_spectate, mapImage, resources);
                loadCalloutBitmap(R.drawable.de_train_radar_spectate_callout, mapCallouts, resources);
                break;
            case Map.VERTIGO:
                loadMapBitmap(R.drawable.de_vertigo_radar, mapImage, resources);
                loadCalloutBitmap(R.drawable.no_callouts_available, mapCallouts, resources);
                break;
            default:
                Log.d("MAP CLICK", "No image for the clicked Map.");
        }
    }
}
