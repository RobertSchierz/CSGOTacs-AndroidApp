package app.black0ut.de.map_service_android.data;

import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 28.01.2016.
 */
public class Status {
    /*"{ 'status' : 'provideGroups', 'user' : null, 'member' : null, 'admin' : null, 'mods' : null, " +
            "'groups' : [{ 'name' : 'Gruppenname', 'member' : '[] Gruppenmitglieder', 'admin' : 'Guppenadministrator', " +
            "'mods' : '[] Gruppenmoderatoren' }], 'name' : null, 'group' : 'null', 'groups' : null }"*/
    @Expose
    String status;
    @Expose
    String user;
    @Expose
    String member;
    @Expose
    String admin;
    @Expose
    String mods;
    @Expose
    Group[] groups;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getMods() {
        return mods;
    }

    public void setMods(String mods) {
        this.mods = mods;
    }

    public Group[] getGroups() {
        return groups;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
}
