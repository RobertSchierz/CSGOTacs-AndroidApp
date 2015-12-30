package app.black0ut.de.map_service_android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.net.URISyntaxException;

import app.black0ut.de.map_service_android.fragments.GroupsFragment_;
import app.black0ut.de.map_service_android.fragments.MainContentFragment_;
import app.black0ut.de.map_service_android.fragments.MapsFragment_;
import app.black0ut.de.map_service_android.fragments.StrategiesFragment_;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://p4dme.shaula.uberspace.de");
        } catch (URISyntaxException e) {}
    }

    //TODO
    //Testnachricht senden
    private void attemptSend() {
        String json = "{x: 1, y: 2}";
        if (TextUtils.isEmpty(json)) {
            return;
        }
        mSocket.emit("json", json);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Fragment mainFragment = new MainContentFragment_();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, mainFragment).commit();


        //Socket verbinden
        mSocket.connect();

        //Methode zum Nachrichten senden aufrufen
        attemptSend();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
        Fragment fragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_maps) {
            Toast.makeText(this, "nav_maps clicked", Toast.LENGTH_SHORT).show();
            fragment = new MapsFragment_();
            ft.replace(R.id.mainFrame, fragment).commit();
        } else if (id == R.id.nav_groups) {
            Toast.makeText(this, "nav_groups clicked", Toast.LENGTH_SHORT).show();
            fragment = new GroupsFragment_();
            ft.replace(R.id.mainFrame, fragment).commit();
        } else if (id == R.id.nav_strategies) {
            Toast.makeText(this, "nav_groups clicked", Toast.LENGTH_SHORT).show();
            fragment = new StrategiesFragment_();
            ft.replace(R.id.mainFrame, fragment).commit();
        } else if (id == R.id.nav_profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
