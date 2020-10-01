package uk.ac.qub.jmccambridge06.wetravel.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.utilities.TokenOperator;

/**
 * Activity holding the login and registration logic
 */
public class LoginActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;
    private boolean notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (!TokenOperator.getToken(getApplicationContext()).equals("")) {
            launchMainMenu(TokenOperator.getToken(getApplicationContext()));
        }

        loginFragment = new LoginFragment();
        registerFragment = new RegisterFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.login_container,
                loginFragment, "login_fragment").addToBackStack(null).commit();

    }



    public LoginFragment getLoginFragment() {
        return loginFragment;
    }

    public RegisterFragment getRegisterFragment() {
        return registerFragment;
    }

    /**
     * Launches the MainMenuActivity passing in a user id and notification if there is one.
     * Sets the authorization token.
     * Finishes the login activity
     * @param token
     */
    public void launchMainMenu(String token) {
        JWT jwt = new JWT(token);
        int userId = jwt.getClaim("userId").asInt();
        TokenOperator.setToken(getApplicationContext(), token);

        // get notification from firebase notification intent (if exists)
        Intent startUpIntent = getIntent();
        boolean notification = startUpIntent.getBooleanExtra("notification", false);
        // put the user id into the intent to pass to MainMenuActivity - put notification detail in as well
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("notification", notification);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // add boolean to confirm if cold boot from notification.
        this.notification = intent.getBooleanExtra("notification", false);
    }
}
