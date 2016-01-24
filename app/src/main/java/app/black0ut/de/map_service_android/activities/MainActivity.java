package app.black0ut.de.map_service_android.activities;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import app.black0ut.de.map_service_android.R;
import app.black0ut.de.map_service_android.fragments.GroupsFragment_;
import app.black0ut.de.map_service_android.fragments.MainContentFragment_;
import app.black0ut.de.map_service_android.fragments.MapsFragment_;
import app.black0ut.de.map_service_android.fragments.MyProfileFragment_;
import app.black0ut.de.map_service_android.fragments.StrategiesFragment_;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener {

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
            mCurrentFragment = new MainContentFragment_();
            mFt.beginTransaction().add(R.id.mainFrame, mCurrentFragment).commit();
        }

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings pressed", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Fragments ersetzen
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //ft.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (id == R.id.nav_home) {
            mCurrentFragment = MainContentFragment_.builder().build();
            getSupportActionBar().setTitle(R.string.nav_home);
            swapFragment(mCurrentFragment);
        } else if (id == R.id.nav_maps) {
            mCurrentFragment = MapsFragment_.builder().build();
            getSupportActionBar().setTitle(R.string.nav_maps);
            swapFragment(mCurrentFragment);
        } else if (id == R.id.nav_groups) {
            getSupportActionBar().setTitle(R.string.nav_groups);
            mCurrentFragment = GroupsFragment_.builder().build();
            swapFragment(mCurrentFragment);
        } else if (id == R.id.nav_strategies) {
            getSupportActionBar().setTitle(R.string.nav_strategies);
            mCurrentFragment = StrategiesFragment_.builder().build();
            swapFragment(mCurrentFragment);
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle(R.string.nav_profile);
            mCurrentFragment = MyProfileFragment_.builder().build();
            swapFragment(mCurrentFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Tauscht die gelieferten Fragments mit dem mainFrame aus
     *
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

    @Override
    public void onBackStackChanged() {
        mToggle.setDrawerIndicatorEnabled(mFt.getBackStackEntryCount() == 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(mFt.getBackStackEntryCount() > 0);
        mToggle.syncState();
    }
}
