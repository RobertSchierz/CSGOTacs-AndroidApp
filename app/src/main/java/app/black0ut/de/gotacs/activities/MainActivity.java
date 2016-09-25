package app.black0ut.de.gotacs.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import app.black0ut.de.gotacs.R;
import app.black0ut.de.gotacs.data.User;
import app.black0ut.de.gotacs.fragments.GroupsFragment_;
import app.black0ut.de.gotacs.fragments.MapsFragment_;
import app.black0ut.de.gotacs.fragments.MyProfileDetailsFragment_;
import app.black0ut.de.gotacs.fragments.MyProfileFragment_;
import app.black0ut.de.gotacs.fragments.StrategiesFragment_;

/**
 * MainActivity, welche die Toolbar und das Hauptmenü implementiert.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

    SharedPreferences sharedPreferences;
    FragmentManager mFt;
    Fragment mCurrentFragment;
    ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFt = getSupportFragmentManager();
        mFt.addOnBackStackChangedListener(this);

        //Initiales Fragment erstellen
        if (savedInstanceState == null) {
            mCurrentFragment = new MapsFragment_();
            mFt.beginTransaction().add(R.id.mainFrame, mCurrentFragment).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(mToggle);
        mToggle.syncState();

        //Quelle: http://stackoverflow.com/questions/28930501/back-navigation-with-fragments-toolbar
        mToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Fragments ersetzen
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        //if (id == R.id.nav_home) {
        //    mCurrentFragment = MainContentFragment_.builder().build();
        //    getSupportActionBar().setTitle(R.string.nav_home);
        //} else
        if (id == R.id.nav_maps) {
            mCurrentFragment = MapsFragment_.builder().build();
            getSupportActionBar().setTitle(R.string.nav_maps);
        } else if (id == R.id.nav_groups) {
            getSupportActionBar().setTitle(R.string.nav_groups);
            mCurrentFragment = GroupsFragment_.builder().build();
        } else if (id == R.id.nav_strategies) {
            getSupportActionBar().setTitle(R.string.nav_strategies);
            mCurrentFragment = StrategiesFragment_.builder().build();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle(R.string.nav_profile);
            sharedPreferences = getSharedPreferences(User.PREFERENCES, Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean(User.IS_LOGGED_IN, false)){
                mCurrentFragment = MyProfileDetailsFragment_.builder().build();
            }else{
                mCurrentFragment = MyProfileFragment_.builder().build();
            }
        }

        swapFragment(mCurrentFragment);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Tauscht die gelieferten Fragments mit dem mainFrame aus
     * @param fragment Das mit dem mainFrame auszutauschende Fragment.
     */
    protected void swapFragment(final Fragment fragment) {
        if (fragment == null) {
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.mainFrame, fragment)
                .disallowAddToBackStack()
                .commit();
        fragmentManager.executePendingTransactions();
    }

    /**
     * Ändert, je nach Backstack, das Burger-Icon zu einem Pfeil.
     */
    @Override
    public void onBackStackChanged() {
        mToggle.setDrawerIndicatorEnabled(mFt.getBackStackEntryCount() == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(mFt.getBackStackEntryCount() > 0);
        mToggle.syncState();
    }
}
