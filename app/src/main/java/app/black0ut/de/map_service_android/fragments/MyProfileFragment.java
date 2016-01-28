package app.black0ut.de.map_service_android.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import app.black0ut.de.map_service_android.JSONCreator;
import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jan-Philipp Altenhof on 19.01.2016.
 */
@EFragment(R.layout.fragment_my_profile)
public class MyProfileFragment extends Fragment {

    SharedPreferences sharedPreferences;

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
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }
    }

    @AfterViews
    public void afterViews() {

    }

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
    @Click
    public void submitButtonClicked() {
        setupSocket();
        //{ user : 'Benutzername', pw : 'Passwort' }
        setUsernamePassword();
        Log.d("TEST", "Username: " + username + " Password: " + password);
        if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
            Toast.makeText(getContext(), "Anmeldung fehlgeschlagen. Benutzername oder Passwort falsch.", Toast.LENGTH_SHORT).show();
        } else {
            String authString = "{ 'user' : '" + username + "', 'pw' : '" + password + "' }";
            mSocket.emit("auth", JSONCreator.createJSON("auth", authString));
        }
    }

    @Click
    public void registerButtonClicked() {
        setupSocket();
        setUsernamePassword();
        if ((username == null || username.equals("")) || (password == null || password.equals(""))) {
            Toast.makeText(getContext(), "Registrierung fehlgeschlagen. Benutzername oder Passwort dürfen nicht leer sein.", Toast.LENGTH_SHORT).show();
        } else {
            String regString = "{ 'user' : '" + username + "', 'pw' : '" + password + "' }";
            mSocket.emit("reg", JSONCreator.createJSON("reg", regString));
        }
    }

    private void setupSocket(){
        mSocket.on("status", status);
        mSocket.connect();
    }

    private Emitter.Listener status = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //Activity activity = (Activity) getContext();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String emitterStatus;
                    try {
                        emitterStatus = data.getString("status");
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
    private void disconnectSocketAndListener(){
        mSocket.disconnect();
        mSocket.off("status", status);
    }

    /**
     * Setzt den Username und den Login Status des Benutzers in den Sharedpreferences.
     * @param isLoggedIn Boolean, ob der nutzer eingeloggt ist oder nicht.
     * @param username Der Username des Benutzers.
     */
    private void setUserStatusAndUsernameInPrefs(boolean isLoggedIn, @Nullable String username){
        User.setsIsLoggedIn(isLoggedIn);
        if (username != null)
            User.setsUsername(username);
        User.saveUserSharedPrefs(getContext());
    }

    /**
     * Setzt den Klassenweiten Status einer Registrierung oder einer Anmeldung.
     * @param status Der zu setztende Status.
     */
    public void setStatus(String status) {
        if (status != null && status.isEmpty())
            Log.d("TEST", "Current status: " + status);
        this.mCurrentStatus = status;
    }

    public void setUsernamePassword(){
        username = editTextName.getText().toString();
        password = editTextPassword.getText().toString();
    }

    public void swapFragment(){
        Fragment fragment = new MyProfileDetailsFragment_();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
    }
}
