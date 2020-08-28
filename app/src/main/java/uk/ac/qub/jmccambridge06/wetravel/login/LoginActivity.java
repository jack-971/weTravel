package uk.ac.qub.jmccambridge06.wetravel.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.utilities.TokenOperator;

public class LoginActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

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

    public void setLoginFragment(LoginFragment loginFragment) {
        this.loginFragment = loginFragment;
    }

    public RegisterFragment getRegisterFragment() {
        return registerFragment;
    }

    public void setRegisterFragment(RegisterFragment registerFragment) {
        this.registerFragment = registerFragment;
    }

    public void launchMainMenu(String token) {
        JWT jwt = new JWT(token);
        int userId = jwt.getClaim("userId").asInt();
        TokenOperator.setToken(getApplicationContext(), token);
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.putExtra("userId", userId); //Optional parameters
        startActivity(intent);
        this.finish();
    }
}
