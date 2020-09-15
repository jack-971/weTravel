package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import uk.ac.qub.jmccambridge06.wetravel.R;

public class NotificationsFragment extends Fragment {

    NotificationListFragment notificationListFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).showNavBar();
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /*notificationListFragment = new NotificationListFragment(((MainMenuActivity)getActivity()).getUserAccount().getNotificationCentre().getNotifications());
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.notif,
                legDetailsFragment, "edit_leg_fragment").commit();*/
    }
}
