package uk.ac.qub.jmccambridge06.wetravel.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;
import uk.ac.qub.jmccambridge06.wetravel.utilities.TokenOperator;

public class LoginFragment extends Fragment {

    @BindView(R.id.login_register) TextView registerButton;
    @BindView(R.id.login_button) View loginButton;
    @BindView(R.id.login_username) EditText username;
    @BindView(R.id.login_password) EditText password;

    NetworkResultCallback loginCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container,
                        ((LoginActivity)getActivity()).getRegisterFragment(), "register_fragment").addToBackStack(null).commit();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
                JsonFetcher jsonFetcher = new JsonFetcher(loginCallback, getContext());
                jsonFetcher.addParam("username", username.getText().toString());
                jsonFetcher.addParam("password", password.getText().toString());
                jsonFetcher.postDataVolley(routes.loginUser());
            }
        });
    }

    private void login() {
        loginCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    String token = response.getString("token");
                    ((LoginActivity)getActivity()).launchMainMenu(token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // extract web token
                // decrypt web token
                // get userId
                // launch activity - setting user account to that retrieved user id.
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
