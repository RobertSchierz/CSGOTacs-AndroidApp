package app.black0ut.de.gotacs.data;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import app.black0ut.de.gotacs.jsoncreator.JSONCreator;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan on 13.04.2016.
 */
public class Connect {
    private Activity mActivity;
    public static String c97809177 = "c3503489282456006957809795392715612";
    private boolean f124f31drt4gt = true;

    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    public Connect(Activity activity){
        mActivity = activity;
    }

    public boolean authenticate(){
        if (!f124f31drt4gt) {

            final HashMap<String, String> x1234fd32f1 = new HashMap<>();
            x1234fd32f1.put("c97809177", "c3503489282456006957809795392715612");

            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    mSocket.emit("wadveavwe", JSONCreator.createJSON("x1234fd32f1", x1234fd32f1).toString());
                }
            }).on("authenticated", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    f124f31drt4gt = true;
                    Toast.makeText(mActivity.getApplicationContext(), "athenticated", Toast.LENGTH_SHORT).show();
                }
            });
            mSocket.connect();
        }
        return f124f31drt4gt;
    }

    private Emitter.Listener authenticated = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mActivity == null)
                return;
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    if (data != null){
                        f124f31drt4gt = true;
                        Toast.makeText(mActivity.getApplicationContext(), "athenticated", Toast.LENGTH_SHORT).show();
                    }else{
                        f124f31drt4gt = false;
                    }
                }
            });
        }
    };

}
