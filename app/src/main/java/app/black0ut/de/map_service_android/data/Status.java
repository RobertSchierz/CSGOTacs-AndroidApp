package app.black0ut.de.map_service_android.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 28.01.2016.
 */
public class Status {
    /*"{ 'status' : 'provideGroups', 'user' : null, 'member' : null, 'admin' : null, 'mods' : null, " +
            "'groups' : [{ 'name' : 'Gruppenname', 'member' : '[] Gruppenmitglieder', 'admin' : 'Guppenadministrator', " +
            "'mods' : '[] Gruppenmoderatoren' }], 'name' : null, 'group' : 'null', 'groups' : null }"*/

    private static SharedPreferences sharedPrefs;
    public static String CURRENT_STATUS_KEY = "currentStatus";

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

    public static Status getCurrentStatus(Context context) {
        sharedPrefs = context.getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        String jsonToStatus = sharedPrefs.getString(CURRENT_STATUS_KEY, null);
        return new Gson().fromJson(jsonToStatus, Status.class);
    }

    public static void setCurrentStatus(Status status, Context context) {
        sharedPrefs = context.getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences editieren und das Status Objekt als JSON speichern
        SharedPreferences.Editor editor = sharedPrefs.edit();
        String statusToJson = new Gson().toJson(status);
        editor.putString(CURRENT_STATUS_KEY, statusToJson);
        editor.apply();
    }

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

    public Group getGroupFromName(String groupName) {
        //Gruppennamen sind eindeutig und können nicht mehrfach vergeben sein
        for (int i = 0; i < this.groups.length; i++) {
            if (this.groups[i].getName().equals(groupName)) {
                return this.groups[i];
            }
        }
        //Gruppe nicht gefunden
        return null;
    }

    public void setGroups(Group[] groups) {
        this.groups = groups;
    }
}
