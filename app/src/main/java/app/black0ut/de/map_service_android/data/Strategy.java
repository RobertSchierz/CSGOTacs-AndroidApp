package app.black0ut.de.map_service_android.data;

import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 10.02.2016.
 */

/**
 * Klasse, welche eine Strategie repräsentiert.
 * Sie wird verwendet, um eine JSON mittels Gson in diese Klasse umzuwandeln.
 */
public class Strategy {
    @Expose
    public long id;
    @Expose
    public String user;
    @Expose
    public String map;
    @Expose
    public String name;
    @Expose
    public String group;
    @Expose
    public Boolean [] drag;
    @Expose
    public Double [] x;
    @Expose
    public Double [] y;

    public Strategy(long id, String user, String map, String name, String group, Boolean[] drag, Double[] x, Double[] y) {
        this.id = id;
        this.user = user;
        this.map = map;
        this.name = name;
        this.group = group;
        this.drag = drag;
        this.x = x;
        this.y = y;
    }
}
