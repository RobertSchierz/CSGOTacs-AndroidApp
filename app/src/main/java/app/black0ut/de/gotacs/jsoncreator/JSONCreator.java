package app.black0ut.de.gotacs.jsoncreator;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Jan-Philipp Altenhof on 27.01.2016.
 */

/**
 * Klasse zum generieren einer JSON aus einer HashMap.
 */
public class JSONCreator {
    /**
     * Methode zum generieren einer Json aus einer HashMap.
     * @param debugString String für eine Konsolenausgabe.
     * @param jsonData HashMap, aus der eine JSON erzeugt wird.
     * @return Generierte JSON.
     */
    public static JSONObject createJSON(String debugString, HashMap jsonData) {
        JSONObject jsonObject;
        jsonObject = new JSONObject(jsonData);
        Log.d("TEST", "Create JSONObject '" + debugString + "': " + jsonObject.toString());
        return jsonObject;
    }
}
