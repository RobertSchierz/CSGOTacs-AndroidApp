package app.black0ut.de.map_service_android.fragments;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import app.black0ut.de.map_service_android.data.Connect;
import app.black0ut.de.map_service_android.jsoncreator.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 19.01.2016.
 */

/**
 * Fragment zum Anmelden oder Registrieren eines neuen Benutzerkontos.
 */
@EFragment(R.layout.fragment_my_profile)
public class MyProfileFragment extends Fragment {

    public static String username;
    public static String password;
    public String mCurrentStatus = "";

    @ViewById
    TextView submitButton;
    @ViewById
    TextView registerButton;
    @ViewById
    EditText editTextName;
    @ViewById
    EditText editTextPassword;
    @ViewById
    TextView navHeaderUsername;

    private Socket mSocket;

    //androidannotations erkennt die ID automatisch durch den Namen
    //Der Name der Methode muss dafür genau dem Namen der ID entsprechen
    //In diesem Fall ist die ID 'submitButton'
    //Die Methode muss folgende Struktur haben damit die automatische Erkennung funktioniert:
    /*You can use use two types of notations for method names:
        <resourceID>()
        <resourceId><actionName>()
    The actionName is derived from the annotation name, for example it is Clicked for @Click or Touched for @Touch.

    Quelle: https://github.com/excilys/androidannotations/wiki/InferringIDFromMethodName
    */

    /**
     * Klick-Listener für den Anmelde-Button.
     */
    @Click
    public void submitButtonClicked() {
        setupSocket();
        setUsernamePassword();

        Log.d("TEST", "" + mSocket.connected());

        if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
            Toast.makeText(getContext(), "Anmeldung fehlgeschlagen. Benutzername oder Passwort falsch.", Toast.LENGTH_SHORT).show();
        } else {
            HashMap<String, String> login = new HashMap<>();
            login.put("user", username);
            login.put("pw", password);
            mSocket.emit("auth", JSONCreator.createJSON("auth", login).toString());
        }
    }

    /**
     * Klick-Listener für Registrieren-Button
     */
    @Click
    public void registerButtonClicked() {
        setupSocket();
        setUsernamePassword();
        if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
            Toast.makeText(getContext(), "Registrierung fehlgeschlagen. Benutzername oder Passwort dürfen nicht leer sein.", Toast.LENGTH_SHORT).show();
        } else {
            if (username.length() > 25 || username.length() < 3){

            }
            HashMap<String, String> reg = new HashMap<>();
            reg.put("user", username);
            reg.put("pw", password);
            mSocket.emit("reg", JSONCreator.createJSON("reg", reg).toString());
        }
    }

    /**
     * Stellt eine Socket Verbindung zum Server her.
     */
    private void setupSocket() {
        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.query = "name=" + Connect.c97809177;
            opts.timeout = 5000;
            mSocket = IO.socket("https://dooku.corvus.uberspace.de/", opts);
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }

        mSocket.on("status", status);
        mSocket.connect();

        Log.d("TEST", "" + mSocket.connected());

        /*
        if (!mSocket.connected()){
            Toast.makeText(getContext(), "Es konnte leider keine Verbindung hergestellt werden. Bitte überprüfe die App auf Aktualisierungen.", Toast.LENGTH_SHORT).show();
            mSocket.off();
            mSocket.disconnect();
        }
        */
    }

    /**
     * Socket Listener, welcher auf Antworten des Servers reagiert.
     */
    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (getActivity() == null)
                return;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    try {
                        emitterStatus = data.getString("status");
                        Log.d("TEST", emitterStatus);
                    } catch (JSONException e) {
                        Log.d("TEST", "Fehler beim Auslesen der Daten des JSONs");
                        return;
                    }
                    if (emitterStatus.equals("regSuccess")) {
                        Toast.makeText(getContext(), "Du hast Dich erfolgreich registriert.", Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(true, username);
                        swapFragment();
                    } else if (emitterStatus.equals("regFailed")) {
                        Toast.makeText(getContext(), "Der Benutzername ist bereits vergeben.", Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(false, null);
                    }
                    if (emitterStatus.equals("authSuccess")) {
                        Toast.makeText(getContext(), "Du hast Dich erfolgreich angemeldet.", Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(true, username);
                        swapFragment();
                    } else if (emitterStatus.equals("authFailed")) {
                        Toast.makeText(getContext(), "Benutzername oder Passwort falsch.", Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(false, null);
                    }
                }
            });
        }
    };

    /**
     * Schaltet die Socket Verbindung sowie die Listener aus.
     */
    private void disconnectSocketAndListener() {
        mSocket.disconnect();
        mSocket.off("status", status);
    }

    /**
     * Setzt den Username und den Login Status des Benutzers in den Sharedpreferences.
     *
     * @param isLoggedIn Boolean, ob der nutzer eingeloggt ist oder nicht.
     * @param username   Der Username des Benutzers.
     */
    private void setUserStatusAndUsernameInPrefs(boolean isLoggedIn, @Nullable String username) {
        User.setsIsLoggedIn(isLoggedIn);
        if (username != null)
            User.setsUsername(username);
        User.saveUserSharedPrefs(getContext());
    }

    /**
     * Setzt den Klassenweiten Status einer Registrierung oder einer Anmeldung.
     *
     * @param status Der zu setztende Status.
     */
    public void setStatus(String status) {
        if (status != null && status.isEmpty())
            this.mCurrentStatus = status;
    }

    /**
     * Ließt den Nutzernamen und das Passwort aus den Eingabefeldern aus.
     */
    public void setUsernamePassword() {
        username = editTextName.getText().toString();
        password = editTextPassword.getText().toString();
    }

    /**
     * Tauscht das MyProfileFragment mit einem MyProfileDetailsFragment.
     */
    public void swapFragment() {
        Fragment fragment = new MyProfileDetailsFragment_();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }
}
