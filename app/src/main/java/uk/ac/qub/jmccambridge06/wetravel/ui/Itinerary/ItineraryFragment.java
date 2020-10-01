package uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary;

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
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.models.Trip;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * Abstract class holds logic for an itinerary page
 */
public abstract class ItineraryFragment extends Fragment {

    @BindView(R.id.leg_list_container) FrameLayout entryListContainer;
    @BindView(R.id.add_leg_container) FrameLayout addEntryContainer;
    @BindView(R.id.add_leg_button) Button addEntryButton;
    @BindView(R.id.no_legs) TextView noEntries;
    @BindView(R.id.edit_leg_container) FrameLayout editEntryContainer;

    protected ItineraryItem item;

    /**
     * Constructor with parent item argument - used for showing existing items
     * @param item
     */
    public ItineraryFragment(ItineraryItem item) {
        super();
        this.item = item;
    }

    /**
     * Default constructor
     */
    public ItineraryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainMenuActivity)getActivity()).removeNavBar();
        return inflater.inflate(R.layout.fragment_trip_itinerary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        loadPage();
    }

    /**
     * adds the new entry page to the fragment
     */
    public void loadPage() {
        addEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if entry details is not visible show, otherwise hide it.
                if (addEntryContainer.getVisibility() == View.GONE) {
                    addEntryContainer.setVisibility(View.VISIBLE);
                } else {
                    addEntryContainer.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * Loads itinerary attached to the item
     */
    public abstract void loadItinerary();

    public void setItem(Trip item) {
        this.item = item;
    }


}
