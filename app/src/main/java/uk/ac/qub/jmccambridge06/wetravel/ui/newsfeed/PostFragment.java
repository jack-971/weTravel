package uk.ac.qub.jmccambridge06.wetravel.ui.newsfeed;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.ui.TripEntryFragment;

public class PostFragment extends TripEntryFragment {

    public PostFragment(ItineraryItem item) {
        super(item);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //galleryButton.setVisibility(View.GONE);
        //galleryContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void sendData() {

    }

    @Override
    protected void saveTripData() {

    }

    @Override
    protected void leave() {

    }
}
