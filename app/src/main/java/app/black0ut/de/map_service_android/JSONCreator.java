package app.black0ut.de.map_service_android;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Jan-Philipp Altenhof on 27.01.2016.
 */
public class JSONCreator {
    public static JSONObject createJSON(String debugString, HashMap jsonData) {
        JSONObject jsonObject;
        jsonObject = new JSONObject(jsonData);
        Log.d("TEST", "Create JSONObject '" + debugString + "': " + jsonObject.toString());
        return jsonObject;
    }
}
