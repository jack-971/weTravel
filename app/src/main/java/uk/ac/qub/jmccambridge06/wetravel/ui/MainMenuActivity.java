package uk.ac.qub.jmccambridge06.wetravel.ui;

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
import android.transition.CircularPropagation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.UserAccount;
import uk.ac.qub.jmccambridge06.wetravel.UserSearchResultsActivity;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;

/**
 * Class contains all UI activity for the main menu.
 */
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String logTag = "MainMenuActivity";

    private UserAccount userAccount;

    NetworkResultCallback getProfileCallback = null;
    NetworkResultCallback getDrawerCallback = null;

    JsonFetcher jsonFetcher;

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

        // create the user account
        setUserAccount(new UserAccount(1));
        String username = "jack_123"; // this will have been passed by the login activity.
        loadProfileCallback();
        jsonFetcher = new JsonFetcher(getProfileCallback,this);
        jsonFetcher.getData(routes.getUserAccountData(getUserAccount().getUserId()));

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

    /**
     * Add the search bar and functionality
     * @param menu
     * @return
     */
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

    /**
     * Link the menu choices to each fragment
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.view_own_profile:
                Fragment userProfileFragment = new ProfileFragment();
                fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                        userProfileFragment).commit();
                break;
            case R.id.edit_settings:
                fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                        new SettingsFragment()).commit();
                break;
            case R.id.tutorial:
                Toast.makeText(this, R.string.tutorial_started, Toast.LENGTH_SHORT).show();
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

    /**
     * Callbacks for loading user profile and mapping to a user class and then the drawer views.
     */
    void loadProfileCallback(){
        getProfileCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logTag, "Successful JSON profile request:" + response);
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject user = jsonArray.getJSONObject(0);
                    userAccount.setProfile(new Profile(user, getDrawerCallback));

                    Log.i(logTag, "Loaded the drawer");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.d(logTag, "Error on JSON callback for profile");
                error.printStackTrace();
            }
        };

        getDrawerCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                loadDrawer();
            }

            @Override
            public void notifyError(VolleyError error) {
                Log.d(logTag, "Error loading drawer");
                error.printStackTrace();
            }
        };
    }

    /**
     * Loads the drawer views with the user profile information for the user account.
     */
   public void loadDrawer() {
       View header = secondaryMenuView.getHeaderView(0);
       CircleImageView drawerPicture = header.findViewById(R.id.drawer_picture);
       TextView drawerName = header.findViewById(R.id.drawer_name);
       TextView drawerUsername = header.findViewById(R.id.drawer_username);
       drawerName.setText(userAccount.getProfile().getName());
       drawerUsername.setText(userAccount.getProfile().getUsername());
       drawerPicture.setImageBitmap(userAccount.getProfile().getProfilePictureImage());
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

}
