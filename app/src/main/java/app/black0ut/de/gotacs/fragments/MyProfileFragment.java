package app.black0ut.de.gotacs.fragments;

import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
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

import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.data.User;
import app.black0ut.de.gotacs.jsoncreator.JSONCreator;
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

    private InputFilter[] filterArrayUsername;
    private InputFilter[] filterArrayPassword;

    private int maxUsernameLenght = 20;
    private int minUsernameLenght = 3;
    private int minPasswordLenght = 6;
    private int maxPasswordLenght = 40;

    @ViewById
    RelativeLayout relativeLayout;
    @ViewById
    TextView submitButton;
    @ViewById
    TextView registerButton;
    @ViewById
    EditText editTextName;
    @ViewById
    EditText editTextPassword;
    //@ViewById
    //TextView navHeaderUsername;

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

    @AfterViews
    public void afterViews() {
        /*filterArrayPassword = new InputFilter[2];
        filterArrayPassword[0] = new InputFilter.LengthFilter(maxPasswordLenght);

        filterArrayUsername = new InputFilter[2];
        filterArrayUsername[0] = new InputFilter.LengthFilter(maxUsernameLenght);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i))) { // Accept only letter & digits ; otherwise just return
                        Toast.makeText(getContext(), getResources().getText(R.string.registration_unsuccessful), Toast.LENGTH_LONG).show();

                        return source;
                    }
                }
                return null;
            }
        };
        filterArrayUsername[1] = filter;
        filterArrayPassword[1] = filter;

        editTextName.setFilters(filterArrayUsername);
        editTextPassword.setFilters(filterArrayPassword);*/
    }

    /**
     * Klick-Listener für den Anmelde-Button.
     */
    @Click
    public void submitButtonClicked() {
        setupSocket();
        setUsernamePassword();

        //Log.d("TEST", "" + mSocket.connected());

        HashMap<String, String> login = new HashMap<>();
        login.put("user", username);
        login.put("pw", password);
        mSocket.emit("auth", JSONCreator.createJSON("auth", login).toString());

    }

    /**
     * Klick-Listener für Registrieren-Button
     */
    @Click
    public void registerButtonClicked() {
        setupSocket();
        setUsernamePassword();

        if (!checkInputData(username, minUsernameLenght, maxUsernameLenght)) {
            Toast.makeText(getContext(), getResources().getText(R.string.username_error), Toast.LENGTH_LONG).show();
        }
        if (!checkInputData(password, minPasswordLenght, maxPasswordLenght)) {
            Toast.makeText(getContext(), getResources().getText(R.string.password_error), Toast.LENGTH_LONG).show();
        } else {
            HashMap<String, String> reg = new HashMap<>();
            reg.put("user", username);
            reg.put("pw", password);
            mSocket.emit("reg", JSONCreator.createJSON("reg", reg).toString());
        }
    }

    private boolean checkInputData(String input, int min, int max) {
        if (input.isEmpty()) {
            return false;
        }
        if ((input.length() >= min) && (input.length() <= max)) {
            for (int i = 0; i < input.length(); i++) {
                if (Character.isSpaceChar(input.charAt(i))) { // Accept only letter & digits ; otherwise just return
                    return false;
                }
            }
        }else{
            return false;
        }
        return true;
    }

    /**
     * Stellt eine Socket Verbindung zum Server her.
     */
    private void setupSocket() {
        try {
            //IO.Options opts = new IO.Options();
            //opts.forceNew = true;
            //opts.query = "name=" + Connect.c97809177;
            //opts.timeout = 5000;
            //mSocket = IO.socket("https://dooku.corvus.uberspace.de/", opts);
            mSocket = IO.socket("https://dooku.corvus.uberspace.de/");
        } catch (URISyntaxException e) {
            Log.d("FEHLER", "mSocket nicht verbunden!");
        }

        mSocket.on("status", status);
        mSocket.connect();

        //Log.d("TEST", "" + mSocket.connected());

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
                        Toast.makeText(getContext(), getResources().getText(R.string.registration_successful), Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(true, username);
                        swapFragment();
                    } else if (emitterStatus.equals("regFailed")) {
                        Toast.makeText(getContext(), getResources().getText(R.string.username_in_use), Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(false, null);
                    }
                    if (emitterStatus.equals("authSuccess")) {
                        Toast.makeText(getContext(), getResources().getText(R.string.login_successful), Toast.LENGTH_SHORT).show();
                        disconnectSocketAndListener();
                        setUserStatusAndUsernameInPrefs(true, username);
                        swapFragment();
                    } else if (emitterStatus.equals("authFailed")) {
                        Toast.makeText(getContext(), getResources().getText(R.string.username_password_wrong), Toast.LENGTH_SHORT).show();
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
