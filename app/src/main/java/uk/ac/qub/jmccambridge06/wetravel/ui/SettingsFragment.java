package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener {

    private boolean isOn;
    SharedPreferences sharedPreferences;
    JsonFetcher jsonFetcher;
    NetworkResultCallback settingChangeCallback;
    String logtag = "Settings";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ((MainMenuActivity)getActivity()).removeNavBar();
        setPreferencesFromResource(R.xml.preferences, rootKey);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(getView());
        isOn = false;

        // For each setting in database check if true and update the switch accordingly then set on click listener.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        HashMap<String, Boolean> settings = ((MainMenuActivity)getActivity()).getUserAccount().getSettings();
        for (String key : settings.keySet()) {
            SwitchPreferenceCompat switchPref = null;
            switch (key) {
                case "private":
                    switchPref = (SwitchPreferenceCompat) findPreference("private");
                    break;
                case "location":
                    switchPref = (SwitchPreferenceCompat) findPreference("location");
                    break;
                case "notifications":
                    switchPref = (SwitchPreferenceCompat) findPreference("notifications");
                    break;
            }
            if (settings.get(key) == true) {
                switchPref.setChecked(true);
            } else {
                switchPref.setChecked(false);
            }
            switchPref.setOnPreferenceClickListener(this::onPreferenceClick);
        }
    }

    /**
     * On click finds the value of the preference and constructs the json request depending on if for privacy, location or notification.
     * @param preference
     * @return
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        isOn = sharedPreferences.getBoolean(preference.getKey(), false);

        String value;
        if (isOn) {
            value = "1";
        } else {
            value = "0";
        }

        jsonFetcher = new JsonFetcher(settingChangeCallback, getContext());
        changeSetting();
        jsonFetcher.addParam("value", value);
        String paramType = null;
        switch (preference.getKey()) {
            case "private":
                paramType = "private";
                break;
            case "location":
                paramType = "location";
                break;
            case "notifications":
                paramType = "notifications";
                break;
        }
        jsonFetcher.addParam("type", paramType);
        jsonFetcher.patchData((routes.updateSettings(((MainMenuActivity) getActivity()).getUserAccount().getUserId())));
        return false;
    }

    void changeSetting() {
        settingChangeCallback = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                Log.i(logtag, "Settings updated successfully");
            }

            @Override
            public void notifyError(VolleyError error) {
                Log.e(logtag, "Error updating settings: ");
                error.printStackTrace();
            }
        };
    }


}
