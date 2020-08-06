package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.Trip;

public class TripListAdapter extends EntryListAdapter {

    public static class TripListViewHolder extends EntryListViewHolder {

        private String logtag = "trip holder";

        public TripListViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(logtag, "start trip list view holder");
            timesView.setVisibility(View.INVISIBLE);
            description.setVisibility(View.GONE);
        }
    }

    public TripListAdapter(ArrayList<Trip> tripCardList, Context context) {
        super(tripCardList, context);
        logtag = "Trip List Adapter";
    }

    @NonNull
    @Override
    public TripListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trip, parent, false);
        Log.d(logtag, "about to create trip holder");
        TripListViewHolder holder = new TripListViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull EntryListViewHolder holder, int position) {
        Trip current = (Trip) entryCardList.get(position);
        holder.datesView.setVisibility(View.VISIBLE);
        holder.description.setVisibility(View.VISIBLE);
        holder.locationsView.setVisibility(View.VISIBLE);
        Log.d(logtag, "binding" + current.getEntryName());
        checkDisplay(current, holder);

    }

    @Override
    protected void checkDisplay(ItineraryItem current, EntryListViewHolder holder) {
        super.checkDisplay(current, holder);
        Trip trip = (Trip) current;
        try {
            Glide.with(holder.itemView)
                    .applyDefaultRequestOptions(new RequestOptions()
                            .placeholder(R.drawable.ic_trip_placeholder_24dp))
                    .load(trip.getTripPicture())
                    .into(holder.image);
        } catch (Exception e){
            Log.e(logtag, "Error loading Picture");
        }
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripFragment tripFrag = new TripFragment();
                tripFrag.setTrip(trip);
                MainMenuActivity.fragmentManager.beginTransaction().replace(R.id.main_screen_container,
                        tripFrag, "user_trip_fragment").addToBackStack(null).commit();
            }
        });
        holder.card.setVisibility(View.VISIBLE);
    }
}
