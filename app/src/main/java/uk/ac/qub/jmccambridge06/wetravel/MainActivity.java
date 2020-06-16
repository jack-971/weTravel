package uk.ac.qub.jmccambridge06.wetravel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigationBar = findViewById(R.id.main_navigation);
        navigationBar.setOnNavigationItemSelectedListener(navListener);


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
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                            selectedFragment).commit();
                    return true;
                }
            };
}
