package uk.ac.qub.jmccambridge06.wetravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

/**
 * Class contains all UI activity for the main menu.
 */
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Reference to the bottom navigation bar on the menu
     */
    private static BottomNavigationView bottomNav;

    private static FragmentManager fragmentManager;

    private static DrawerLayout drawer;

    private static ActionBarDrawerToggle navToggle;

    private static NavigationView secondaryMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load and prepare the drawer
        drawer = findViewById(R.id.secondary_menu_drawer);
        secondaryMenuView = findViewById(R.id.secondary_view);
        secondaryMenuView.setNavigationItemSelectedListener(this);

        // Add drawer toggle on burger bar
        navToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(navToggle);
        navToggle.syncState();

        // Load the main menu fragment for bottom navigation
        bottomNav = findViewById(R.id.main_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            fragmentManager = getSupportFragmentManager();
            loadHome();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.user_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(this, UserSearchResultsActivity.class)));

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.view_own_profile:
                fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.edit_settings:
                fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.tutorial:
                Toast.makeText(this, "Tutorial Started", Toast.LENGTH_SHORT).show();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A listener to wait for clicks on an item of the nav menu. Uses a switch statement
     * to determine what has been clicked and loads the appropriate fragment based on the
     * menu item id.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch(menuItem.getItemId()) {
                        case R.id.menu_home:
                            selectedFragment = new NewsfeedFragment();
                            break;
                        case R.id.menu_trips:
                            selectedFragment = new TripsFragment();
                            break;
                        case R.id.menu_inbox:
                            selectedFragment = new InboxFragment();
                            break;
                        case R.id.menu_notifications:
                            selectedFragment = new NotificationsFragment();
                            break;
                    }
                    fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                            selectedFragment).commit();
                    return true;
                }
            };

    /**
     * If drawer is open the back button will close it otherwise back button operates normally.
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragmentManager.findFragmentById(R.id.main_screen_container) instanceof NewsfeedFragment) {
                super.onBackPressed(); //close the app
            } else {
                bottomNav.setSelectedItemId(R.id.menu_home);
            }

        }
    }

    /**
     * Removes the navbar
     */
    public static void removeNavBar() {
        bottomNav.setVisibility(View.GONE);
    }

    /**
     * Shows the navbar
     */
    public static void showNavBar() {
        bottomNav.setVisibility(View.VISIBLE);
    }

    /**
     * Loads the homes screen.
     */
    private static void loadHome() {
        fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                new NewsfeedFragment()).commit();

    }

    public static void loadUserSearchFragment() {
        fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                new UserSearchFragment()).commit();
    }

}
