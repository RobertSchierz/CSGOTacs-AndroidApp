package app.black0ut.de.map_service_android.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by Jan-Philipp Altenhof on 26.01.2016.
 */
public class User {
    //Style Guidelines
    //http://source.android.com/source/code-style.html#follow-field-naming-conventions
    public static String sUsername = "";
    public static boolean sIsLoggedIn = false;

    public static final String PREFERENCES = "userPrefs" ;
    public static final String USERNAME = "usernameKey";
    public static final String IS_LOGGED_IN = "isLoggedInKey";

    public static void saveUserSharedPrefs(Context context){
        SharedPreferences sharedPrefs =
                context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        //SharedPreferences editieren und die Nutzerdaten speichern
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(USERNAME, getsUsername());
        editor.putBoolean(IS_LOGGED_IN, issIsLoggedIn());
        editor.apply();

        Log.d("TEST", "SharedPrefs saved - Username: " + sharedPrefs.getString(USERNAME, null) + " IsLoggedIn: " + sharedPrefs.getBoolean(IS_LOGGED_IN, false));
    }

    public static String getsUsername() {
        return sUsername;
    }

    public static void setsUsername(String sUsername) {
        User.sUsername = sUsername;
    }

    public static boolean issIsLoggedIn() {
        return sIsLoggedIn;
    }

    public static void setsIsLoggedIn(boolean sIsLoggedIn) {
        User.sIsLoggedIn = sIsLoggedIn;
    }
}
