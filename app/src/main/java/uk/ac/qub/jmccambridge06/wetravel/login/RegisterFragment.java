package uk.ac.qub.jmccambridge06.wetravel.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;
import uk.ac.qub.jmccambridge06.wetravel.utilities.EditTextDateClicker;

public class RegisterFragment extends Fragment {

    @BindView(R.id.register_login) TextView loginButton;
    @BindView(R.id.register_button) View registerButton;
    @BindView(R.id.register_username) EditText username;
    @BindView(R.id.register_password) EditText password;
    @BindView(R.id.register_password_format) TextView passwordFormat;
    @BindView(R.id.register_confirm_password) EditText passwordConfirm;
    @BindView(R.id.register_match_passwords) TextView matchPasswords;
    @BindView(R.id.register_name) EditText name;
    @BindView(R.id.register_dob) EditText dob;

    NetworkResultCallback registerCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.registration_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        dob.setOnClickListener(new EditTextDateClicker(getContext(), dob, null));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.login_container,
                        ((LoginActivity)getActivity()).getLoginFragment(), "login_fragment").addToBackStack(null).commit();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("") || dob.getText().toString().equals("") || password.getText().toString().equals("")
                    || passwordConfirm.getText().toString().equals("") || name.getText().toString().equals("")) {
                    Toast.makeText(getContext(), R.string.enter_all, Toast.LENGTH_SHORT).show();
                } else if (passwordFormat.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getContext(), R.string.format_password, Toast.LENGTH_SHORT).show();
                } else if (matchPasswords.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getContext(), R.string.match_passwords, Toast.LENGTH_SHORT).show();
                }
                // check passwords match
                register();
                JsonFetcher jsonFetcher = new JsonFetcher(registerCallback, getContext());
                jsonFetcher.addParam("username", username.getText().toString());
                jsonFetcher.addParam("password", password.getText().toString());
                jsonFetcher.addParam("name", name.getText().toString());
                jsonFetcher.addParam("dob", DateTime.dateToMilliseconds(dob.getText().toString()));
                jsonFetcher.addParam("date", DateTime.dateToMilliseconds(DateTime.todaysDate()));
                jsonFetcher.postDataVolley(routes.registerUser());
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    if (!password.getText().toString().equals(passwordConfirm.getText().toString())) {
                        matchPasswords.setVisibility(View.VISIBLE);
                    } else {
                        matchPasswords.setVisibility(View.GONE);
                    }
                    if (password.getText().toString().matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$")) {
                        passwordFormat.setVisibility(View.GONE);
                    } else {
                        passwordFormat.setVisibility(View.VISIBLE);
                    }
                }

            }
        };
        password.addTextChangedListener(textWatcher);
        passwordConfirm.addTextChangedListener(textWatcher);
    }

    private void register(){
        registerCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i("tag", response.toString());
                try {
                    String message = response.getString("data");
                    if (message.equals("not unique")) {
                        Toast.makeText(getContext(), R.string.username_taken, Toast.LENGTH_SHORT).show();
                    } else {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void notifyError(VolleyError error) {
                Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
            }
        };
    }

}
