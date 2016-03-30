package app.black0ut.de.map_service_android.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * Created by Jan-Philipp Altenhof on 28.01.2016.
 */

/**
 * Klasse, welche zum Umwandeln einer JSON in diese Klasse verwendet wird.
 */
public class Status {

    private static SharedPreferences sharedPrefs;
    public static String CURRENT_STATUS_KEY = "currentStatus";

    @Expose
    String status;
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
