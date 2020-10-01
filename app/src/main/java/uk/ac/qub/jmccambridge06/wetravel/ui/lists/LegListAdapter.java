package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.ui.Itinerary.LegItineraryFragment;

/**
 * Holds controller logic for a leg lists adapter
 */
public class LegListAdapter extends EntryListAdapter {

    /**
     * Leg view holder
     */
    public static class LegListViewHolder extends EntryListViewHolder {

        /**
         * Constructs the leg view
         * @param itemView
         */
        public LegListViewHolder(@NonNull View itemView) {
            super(itemView);
            timesView.setVisibility(View.INVISIBLE);
            image.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public LegListAdapter.LegListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trip, parent, false);
        return new LegListAdapter.LegListViewHolder(v);
    }

    public LegListAdapter(ArrayList<Leg> legCardList, Context context) {
        super(legCardList, context);
        logtag = "Leg List Adapter";
    }

    @Override
    public void onBindViewHolder(@NonNull EntryListViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        Leg current = (Leg) entryCardList.get(position);
        checkDisplay(current, holder);
        holder.card.setVisibility(View.VISIBLE);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                        new LegItineraryFragment(current), "leg_itinerary_fragment").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
