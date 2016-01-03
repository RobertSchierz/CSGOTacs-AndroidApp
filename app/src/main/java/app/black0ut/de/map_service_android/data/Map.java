package app.black0ut.de.map_service_android.data;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */
public class Map {

    public static final String DUST2 = "Dust 2";
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


    public final String mapName;
    public final int mapPreviewId;

    public Map(String mapName, int mapPreviewId){
        this.mapName = mapName;
        this.mapPreviewId = mapPreviewId;
    }
}
