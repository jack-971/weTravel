package uk.ac.qub.jmccambridge06.wetravel.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.models.NotificationCentre;
import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.models.UserAccount;
import uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary.TripFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary.TripsFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.NotificationListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.TripListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.lists.UserListFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.login.LoginActivity;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.newsfeed.NewsfeedFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.users.ProfileFragment;
import uk.ac.qub.jmccambridge06.wetravel.ui.users.SettingsFragment;
import uk.ac.qub.jmccambridge06.wetravel.utilities.TokenOperator;

/**
 * Contains controller logic for a logged in user.
 */
public class MainMenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String logTag = "MainMenuActivity";

    private UserAccount userAccount;

    NetworkResultCallback getProfileCallback = null;
    NetworkResultCallback getFriendsCallback = null;

    JsonFetcher jsonFetcher;

    /**
     * Reference to the bottom navigation bar on the menu
     */
    private static BottomNavigationView bottomNav;
    private BottomNavigationItemView notificationItem;
    private View notificationBadge;
    private TextView bagdeText;

    public static FragmentManager fragmentManager;

    private static DrawerLayout drawer;

    private static ActionBarDrawerToggle navToggle;

    private static NavigationView secondaryMenuView;

    public NewsfeedFragment newsfeedFragment;
    public TripsFragment tripsFragment;
    public NotificationListFragment notificationListFragment;
    public ProfileFragment profileFragment;
    public SettingsFragment settingsFragment;
    public UserListFragment searchFragment;
    public UserListFragment adminFriendList;
    public TripListFragment plannedTrips;
    public TripFragment currentTrip;
    public TripListFragment completedTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // retrieve userId from login activity
        Intent intent = getIntent();
        int userId = intent.getIntExtra("userId", 0);

        // create the user account
        if (userId == 0) {
            this.finish();
        } else {
            setUserAccount(new UserAccount(userId
            ));
        }

        // load the user profile and data
        loadProfileCallback(); // loads the callback so when volley request completes this method is executed.
        jsonFetcher = new JsonFetcher(getProfileCallback,this); // create volley request to get profile data
        jsonFetcher.getData(routes.getAdminAccountData(getUserAccount().getUserId())); // send volley request

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
        fragmentManager = getSupportFragmentManager();

        // get notification references
        notificationItem = (BottomNavigationItemView) ((BottomNavigationMenuView) bottomNav.getChildAt(0)).getChildAt(2);
        notificationBadge = LayoutInflater.from(MainMenuActivity.this).inflate(R.layout.notification_badge, bottomNav, false);
        bagdeText = notificationBadge.findViewById(R.id.badge_text_view);
        removeNotificationBadge();

        // set up the display fragments
        if (savedInstanceState == null) {
            newsfeedFragment = new NewsfeedFragment();
            tripsFragment = new TripsFragment();
            profileFragment = new ProfileFragment();
            settingsFragment = new SettingsFragment();
            searchFragment = new UserListFragment();
            currentTrip = new TripFragment();
            plannedTrips = new TripListFragment();
            plannedTrips.setStatus("planned");
            completedTrips = new TripListFragment();
            completedTrips.setStatus("complete");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.main_screen_container, newsfeedFragment, "newsfeed");
            transaction.add(R.id.main_screen_container, tripsFragment, "trips").hide(tripsFragment);
            transaction.commit();
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
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.user_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Method determines whether activity was launched by a push notification, in which case displasy the
     * notifications fragment
     */
    private void onNewNotification() {
        boolean notification = getIntent().getBooleanExtra("notification", false);
        if (notification) {
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().hide(newsfeedFragment).hide(tripsFragment).hide(notificationListFragment).commit();
            setFragment(notificationListFragment, "notifications_fragment",false);
            bottomNav = findViewById(R.id.main_navigation);
            bottomNav.setSelectedItemId(R.id.menu_notifications);
        }
    }

    /**
     * Captures a search query from search activity
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchFragment.setQuery(query);
            setFragment(searchFragment, "search", true);
        }
    }

    /**
     * Link the menu choices to each fragment
     * @param menuItem
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.home:
                for(int count = 0; count < fragmentManager.getBackStackEntryCount(); count++) {
                    fragmentManager.popBackStack();
                }
                break;
            case R.id.view_own_profile:
                setFragment(profileFragment, "admin_profile", true);
                break;
            case R.id.edit_settings:
                setFragment(settingsFragment, "settings", true);
                break;
            case R.id.logout:
                // remove notification key as user no longer logged into device
                JsonFetcher jsonFetcher = new JsonFetcher(new NetworkResultCallback() {
                    @Override
                    public void notifySuccess(JSONObject response) {
                        logout();
                    }

                    @Override
                    public void notifyError(VolleyError error) {
                        Log.d(logTag, "Could not remove notification key");
                        logout();
                    }
                }, getApplicationContext());
                jsonFetcher.addParam("key", null);
                jsonFetcher.addParam("status", "logout");
                jsonFetcher.patchData(routes.postNotificationKey(getUserAccount().getUserId()));
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Launches the login activity, resets the authorization token and finishes the current activity.
     */
    private void logout() {
        // remove authorization token and end activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        TokenOperator.setToken(getApplicationContext(), "");
        startActivity(intent);
        this.finish();
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
                    fragmentManager.beginTransaction().hide(newsfeedFragment).hide(tripsFragment).hide(notificationListFragment).commit();
                    Fragment selectedFragment = null;
                    String tag = "";
                    switch(menuItem.getItemId()) {
                        case R.id.menu_home:
                            selectedFragment = newsfeedFragment;
                            tag = "newsfeed";
                            break;
                        case R.id.menu_trips:
                            selectedFragment = tripsFragment;
                            tripsFragment.setProfile(userAccount.getProfile());
                            tag = "trips";
                            break;
                        case R.id.menu_notifications:
                            selectedFragment = notificationListFragment;
                            removeNotificationBadge();
                            tag = "notifications";
                            break;
                    }
                    setFragment(selectedFragment, tag, false);
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
                super.onBackPressed();
        }
    }

    /**
     * Removes the navbar.
     */
    public void removeNavBar() {
        bottomNav.setVisibility(View.GONE);
    }

    /**
     * Shows the navbar
     */
    public void showNavBar() {
        bottomNav.setVisibility(View.VISIBLE);
    }

    /**
     * Sets a fragmnet on the screen with optional add to backstack boolean
     * @param fragment
     * @param tag
     * @param backstack
     */
    public void setFragment(Fragment fragment, String tag, boolean backstack) {
        if (backstack == true) {
            fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                    fragment, tag).addToBackStack(null).commit();
        } else {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragmentManager.findFragmentById(R.id.main_screen_container)).show(fragment).commit();
        }
    }

    /**
     * Callbacks for loading user profile and mapping to a user class and then the drawer views.
     */
    void loadProfileCallback(){
        getProfileCallback = new NetworkResultCallback() { //network result callback is an interface passed into the volley object
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject user = jsonArray.getJSONObject(0);
                    userAccount.setProfile(new Profile(user, 0));
                    profileFragment.setProfile(userAccount.getProfile());// 0 is admin profile type
                    userAccount.createSettings(user);

                    JSONArray friendArray = response.getJSONArray("friendsList");
                    userAccount.setFriendsList(friendArray);
                    //create newsfeed
                    JSONArray postsArray = response.getJSONArray("posts");
                    userAccount.setNewsfeed(postsArray);
                    newsfeedFragment.updateFeed(userAccount.getNewsfeed());

                    // get notifications - if unread then highlight notifications tab
                    JSONArray notificationArray = response.getJSONArray("notifications");
                    getUserAccount().setNotificationCentre(new NotificationCentre(notificationArray));
                    notificationListFragment = new NotificationListFragment();
                    fragmentManager.beginTransaction().add(R.id.main_screen_container, notificationListFragment,
                            "notifications").hide(notificationListFragment).commit();
                    updateNotificationBadge();
                    // If launched by a notification then display the notification page.
                    onNewNotification();
                    loadDrawer();
                    plannedTrips.setProfile(userAccount.getProfile());
                    completedTrips.setProfile(userAccount.getProfile());
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
    }

    /**
     * Adds a notification badge to the notification menu icon with the number of unread notifications from the notification centre.
     */
    public void updateNotificationBadge() {
        removeNotificationBadge();
        if (getUserAccount().getNotificationCentre().getUnread() > 0) {
            bagdeText.setText(String.valueOf(getUserAccount().getNotificationCentre().getUnread()));
            notificationItem.addView(notificationBadge);
            notificationBadge.setVisibility(View.VISIBLE);
            invalidateOptionsMenu();
        } else {
            removeNotificationBadge();
        }
    }

    /**
     * Removes the notification badge from the notification menu icon.
     */
    public void removeNotificationBadge() {
        notificationItem.removeView(notificationBadge);
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
       Glide.with(this)
               .load(userAccount.getProfile().getProfilePicture())
               .into(drawerPicture);
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }





}
