package app.black0ut.de.map_service_android.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import app.black0ut.de.map_service_android.R;

/**
 * Created by Jan-Philipp Altenhof on 20.01.2016.
 */
@EFragment(R.layout.fragment_my_profile_details)
public class MyProfileDetailsFragment extends android.support.v4.app.Fragment {

    @ViewById
    FloatingActionButton fab;
    @ViewById
    TextView avatarImage;
    @ViewById
    TextView username;
    @ViewById
    RelativeLayout editTextRelativeLayout;
    @ViewById
    RelativeLayout fragment_my_profile;

    @AfterViews
    public void afterViews() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                //Animation Quelle: http://stackoverflow.com/questions/22454839/android-adding-simple-animations-while-setvisibilityview-gone
                /*username.animate()
                        .alpha(0.0f)
                        .setDuration(100)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                username.setVisibility(View.GONE);
                            }
                        });
                        */
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
        });

    }

}
