package app.black0ut.de.map_service_android.data;

import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 10.02.2016.
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
    public Integer [] x;
    @Expose
    public Integer [] y;
}
