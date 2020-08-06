package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.ac.qub.jmccambridge06.wetravel.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public abstract class ItineraryFragment extends Fragment {

    //@BindView(R.id.trip_legs_list) RecyclerView legListRecycler;
    @BindView(R.id.leg_list_container) FrameLayout legListContainer;
    @BindView(R.id.add_leg_container) FrameLayout addLegContainer;
    @BindView(R.id.add_leg_button) Button addLegButton;
    @BindView(R.id.no_legs) TextView noLegs;
    @BindView(R.id.edit_leg_container) FrameLayout editLegContainer;

    protected ItineraryItem item;

    /**
     * Constructor with trip argument - used for showing existing trips
     * @param item
     */
    public ItineraryFragment(ItineraryItem item) {
        super();
        this.item = item;
    }

    public ItineraryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainMenuActivity.removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadPage();

    }

    public void loadPage() {
        addLegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if leg details is not visible show, otherwise hide it.
                if (addLegContainer.getVisibility() == View.GONE) {
                    addLegContainer.setVisibility(View.VISIBLE);
                } else {
                    addLegContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    public abstract void loadItinerary();

    public void setItem(Trip item) {
        this.item = item;
        Log.d("tag", "trip has now been set");
    }


}
