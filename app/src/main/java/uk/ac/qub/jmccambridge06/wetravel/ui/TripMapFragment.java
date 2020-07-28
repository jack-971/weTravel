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
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public class TripMapFragment extends Fragment {

    private Trip trip;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }
}
