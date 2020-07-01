package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uk.ac.qub.jmccambridge06.wetravel.R;

public class InboxFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.showNavBar();
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

}
