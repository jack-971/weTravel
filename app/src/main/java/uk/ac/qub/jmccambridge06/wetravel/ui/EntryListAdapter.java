package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import uk.ac.qub.jmccambridge06.wetravel.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.TripLocation;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.utilities.DateTime;

public abstract class EntryListAdapter extends RecyclerView.Adapter<EntryListAdapter.EntryListViewHolder> {

    public static class EntryListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.entry_card_image) CircleImageView image;
        @BindView(R.id.entry_card_name) TextView name;
        @BindView(R.id.card_dates_view) View datesView;
        @BindView(R.id.entry_card_dates) TextView dates;
        @BindView(R.id.card_times_view) View timesView;
        @BindView(R.id.entry_card_times) TextView times;
        @BindView(R.id.card_description) TextView description;
        @BindView(R.id.card_locations_view) View locationsView;
        @BindView(R.id.entry_card_location) TextView locations;
        @BindView(R.id.card) View card;
        @BindView(R.id.expandableLayout) View expandableView;
        @BindView(R.id.details_container) View detailsContainer;

        public EntryListViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("logtag", "start trip list view holder");
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
        holder.card.setVisibility(View.VISIBLE);
        holder.description.setVisibility(View.VISIBLE);
        holder.datesView.setVisibility(View.VISIBLE);
        holder.locationsView.setVisibility(View.VISIBLE);
        boolean isExpanded = current.isExpanded();
        holder.expandableView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current.setExpanded(!current.isExpanded());
                notifyItemChanged(position);
            }
        });
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
        if (current.getLocation().getId() != null) {
            String locationKey = current.getLocation().getId();
            loadLocation(locationKey, holder, current);
            Log.d("tag", "getting location from API");
            JsonFetcher jsonFetcher = new JsonFetcher(getLocationsAPI, MyApplication.getContext());
            jsonFetcher.getData(routes.getPlacesAPI(locationKey));
        } else {
            holder.locationsView.setVisibility(View.GONE);
            checkOther(current, holder);
        }

    }

    protected void checkOther(ItineraryItem current, EntryListViewHolder holder) {
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

        if (current.getDescription() != null) {
            holder.description.setText((current.getDescription()));
        } else {
            holder.description.setVisibility(View.GONE);
        }
    }

    void loadLocation(String locationId, EntryListViewHolder holder, ItineraryItem current) {
        getLocationsAPI = new NetworkResultCallback() {
            @Override
            public void notifySuccess(JSONObject response) {
                try {
                    JSONArray data = response.getJSONArray("results");
                    JSONObject obj = data.getJSONObject(0);
                    holder.locations.setText(obj.getString("formatted_address"));
                    current.getLocation().setName(obj.getString("formatted_address"));
                    checkOther(current,holder);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void notifyError(VolleyError error) {
                Log.e(logtag, "Error retrieving place");
            }
        };
    }

}
