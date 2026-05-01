package dev.bodid.isstracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import dev.bodid.isstracker.crew.CrewFragment;

public class MainActivity extends AppCompatActivity {

    private MapFragment mapFragment;
    private CrewFragment crewFragment;
    private PassFragment passFragment;
    private Fragment activeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mapFragment = new MapFragment();
            crewFragment = new CrewFragment();
            passFragment = new PassFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, passFragment, "pass").hide(passFragment)
                    .add(R.id.fragment_container, crewFragment, "crew").hide(crewFragment)
                    .add(R.id.fragment_container, mapFragment, "map")
                    .commit();
            activeFragment = mapFragment;
        } else {
            mapFragment = (MapFragment) fm.findFragmentByTag("map");
            crewFragment = (CrewFragment) fm.findFragmentByTag("crew");
            passFragment = (PassFragment) fm.findFragmentByTag("pass");
            if (!crewFragment.isHidden()) {
                activeFragment = crewFragment;
            } else if (!passFragment.isHidden()) {
                activeFragment = passFragment;
            } else {
                activeFragment = mapFragment;
            }
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_map) {
                switchFragment(mapFragment);
            } else if (id == R.id.nav_crew) {
                switchFragment(crewFragment);
            } else if (id == R.id.nav_pass) {
                switchFragment(passFragment);
            }
            return true;
        });
    }

    private void switchFragment(Fragment fragment) {
        if (fragment == activeFragment) {
            return;
        }
        getSupportFragmentManager().beginTransaction()
                .hide(activeFragment)
                .show(fragment)
                .commit();
        activeFragment = fragment;
    }
}
