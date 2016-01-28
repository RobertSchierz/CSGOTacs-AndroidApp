package app.black0ut.de.map_service_android;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Jan-Philipp Altenhof on 27.01.2016.
 */
public class JSONCreator {
    public static JSONObject createJSON(String debugString, String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            Log.d("TEST", "Create JSONObject '" + debugString + "': " + jsonObject.toString());
        } catch (JSONException e) {
            Log.d("TEST", "new JSONObject Failed (" + debugString + ")");
            return null;
        }
        return jsonObject;
    }
}
