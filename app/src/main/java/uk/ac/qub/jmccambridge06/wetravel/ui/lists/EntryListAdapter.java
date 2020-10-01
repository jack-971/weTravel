package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.models.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

/**
 * Abstract class contains logic for an entry lists adapter
 */
public abstract class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryListViewHolder> {

    /**
     * View holder for entry list adapter
     */
    public static class EntryListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.entry_card_image) CircleImageView image;
        @BindView(R.id.entry_card_name) TextView name;
        @BindView(R.id.card_dates_view) View datesView;
        @BindView(R.id.entry_card_dates) TextView dates;
        @BindView(R.id.card_times_view) View timesView;
        @BindView(R.id.entry_card_times) TextView times;
        @BindView(R.id.card_locations_view) View locationsView;
        @BindView(R.id.entry_card_location) TextView locations;
        @BindView(R.id.card) View card;

        /**
         * Construtor with args
         * @param itemView
         */
        public EntryListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            card.setVisibility(View.INVISIBLE);
        }
    }

    protected String logtag;
    protected ArrayList<ItineraryItem> entryCardList;
    protected Context context;
    protected NetworkResultCallback getLocationsAPI;

    public EntryListAdapter(ArrayList entryCardList, Context context) {
        this.entryCardList = entryCardList;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(@NonNull EntryListViewHolder holder, int position) {
        ItineraryItem current = entryCardList.get(position);
    }

    @Override
    public int getItemCount() {
        return entryCardList.size();
    }

    /**
     * Checks if there is start or finish dates and sets the content
     * @param current
     * @param holder
     */
    protected void checkDisplay(ItineraryItem current, EntryListViewHolder holder) {
        Log.d(logtag, "checkdatedisplay");
        holder.locationsView.setVisibility(View.GONE);
        if (current.getLocation().getId() != null) {
            holder.locations.setText(current.getLocation().getName());
            holder.locationsView.setVisibility(View.VISIBLE);
        }
        holder.name.setText(current.getEntryName());
        if (current.getStartDate() != null) {
            if (current.getEndDate() != null) {
                holder.dates.setText(DateTime.formatDate(current.getStartDate())+" - "+DateTime.formatDate(current.getEndDate()));
            } else {
                holder.dates.setText(DateTime.formatDate(current.getStartDate()));
            }
        } else {
            holder.datesView.setVisibility(View.GONE);
        }
    }

}
