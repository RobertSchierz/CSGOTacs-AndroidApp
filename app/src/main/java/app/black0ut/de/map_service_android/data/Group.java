package app.black0ut.de.map_service_android.data;

import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 28.01.2016.
 */

/**
 * Klasse, welche eine Gruppe repräsentiert.
 * Sie wird verwendet, um eine JSON in diese umzuwandeln.
 */
public class Group {
    @Expose
    String name;
    @Expose
    String [] member;
    @Expose
    String admin;
    @Expose
    String [] mods;

    public Group(String name, String[] member, String admin, String[] mods) {
        this.name = name;
        this.member = member;
        this.admin = admin;
        this.mods = mods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getMembers() {
        return member;
    }

    public void setMember(String[] member) {
        this.member = member;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String[] getMods() {
        return mods;
    }

    public void setMods(String[] mods) {
        this.mods = mods;
    }
}
