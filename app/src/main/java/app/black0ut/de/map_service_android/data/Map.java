package app.black0ut.de.map_service_android.data;


/**
 * Created by Jan-Philipp Altenhof on 02.01.2016.
 */
public class Map {

    public static final String DUST2 = "de_dust2";
    public static final String TRAIN = "de_train";
    public static final String MIRAGE = "de_mirage";
    public static final String INFERNO = "de_inferno";
    public static final String COBBLESTONE = "gd_cbble";
    public static final String OVERPASS = "de_overpass";
    public static final String CACHE = "de_cache";
    public static final String AZTEC = "de_aztec";
    public static final String DUST = "de_dust";
    public static final String VERTIGO = "de_vertigo";
    public static final String NUKE = "de_nuke";
    public static final String OFFICE = "cs_office";
    public static final String ITALY = "cs_italy";
    public static final String ASSAULT = "cs_assault";
    public static final String MILITIA = "cs_militia";

    //Variable zum speichern der geklickten Map im MapsFragment
    public static String clickedMapName = "";

    public final String mapName;
    public final int mapPreviewId;

    public Map(String mapName, int mapPreviewId){
        this.mapName = mapName;
        this.mapPreviewId = mapPreviewId;
    }
}
