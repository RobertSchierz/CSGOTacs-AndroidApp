package app.black0ut.de.map_service_android.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.data.User;

/**
 * Created by Jan-Philipp Altenhof on 20.01.2016.
 */
@EFragment(R.layout.fragment_my_profile_details)
public class MyProfileDetailsFragment extends android.support.v4.app.Fragment {

    public SharedPreferences sharedPrefs;

    @ViewById
    FloatingActionButton fabEdit;
    @ViewById
    FloatingActionButton fabCancel;
    @ViewById
    TextView avatarImage;
    @ViewById
    TextView username;
    @ViewById
    RelativeLayout editTextRelativeLayout;
    @ViewById
    RelativeLayout fragment_my_profile;
    @ViewById
    EditText editTextNameSignedIn;
    @ViewById
    EditText editTextPasswordSignedIn;
    @ViewById
    TextView logoutButton;

    @AfterViews
    public void afterViews() {
        sharedPrefs = getContext().getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
        username.setText(sharedPrefs.getString(User.USERNAME, null));
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                editTextNameSignedIn.setText(MyProfileFragment.username);
                editTextPasswordSignedIn.setText(MyProfileFragment.password);
                avatarImage.setText("+");
                username.setVisibility(View.GONE);
                editTextRelativeLayout.animate()
                        .alpha(1.0f)
                        .translationY(-username.getHeight())
                        .setDuration(500)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                editTextRelativeLayout.setVisibility(View.VISIBLE);
                            }
                        });
            }
        });*/
    }

    @Click
    public void avatarImageClicked(){
        Toast.makeText(getContext(), "Add new avatar image", Toast.LENGTH_SHORT).show();
    }

    @Click
    public void logoutButtonClicked(){
        //Den User aus den SharedPrefs löschen damit er sich neu anmelden muss
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(User.USERNAME);
        editor.putBoolean(User.IS_LOGGED_IN, false);
        editor.apply();
        //Zurück zum Login Fragment
        Fragment fragment = new MyProfileFragment_();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right)
                .replace(R.id.mainFrame, fragment)
                .commit();
        fragmentManager.executePendingTransactions();
        Toast.makeText(getContext(), "Du hast Dich erfolgreich ausgeloggt.", Toast.LENGTH_SHORT).show();
    }

    @Click
    public void fabEditClicked(){
        //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show();
        //Animation Quelle: http://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
        editTextNameSignedIn.setText(sharedPrefs.getString(User.USERNAME, null));
        editTextPasswordSignedIn.setText(MyProfileFragment.password);
        avatarImage.setText("+");
        username.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
        //Animation Quelle: http://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
        editTextRelativeLayout.animate()
                .alpha(1.0f)
                //.translationY(-username.getHeight()-logoutButton.getHeight())
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        editTextRelativeLayout.setVisibility(View.VISIBLE);
                    }
                });
        fabEdit.setVisibility(View.GONE);
        fabCancel.setVisibility(View.VISIBLE);
    }
    @Click
    public void fabCancelClicked(){
        editTextRelativeLayout.setVisibility(View.GONE);
        fabCancel.setVisibility(View.GONE);
        avatarImage.setText("Avatar");
        fabEdit.setVisibility(View.VISIBLE);
        username.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);

    }
}
