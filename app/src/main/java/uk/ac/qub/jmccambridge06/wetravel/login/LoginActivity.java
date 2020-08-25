package uk.ac.qub.jmccambridge06.wetravel.login;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import uk.ac.qub.jmccambridge06.wetravel.R;

public class LoginActivity extends AppCompatActivity {

    LoginFragment loginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);



        loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.login_container,
                loginFragment, "login_fragment").addToBackStack(null).commit();

        if (savedInstanceState == null) {

        }
    }

}
