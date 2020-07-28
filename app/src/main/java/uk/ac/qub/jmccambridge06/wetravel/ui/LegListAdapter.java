package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.ItineraryItem;
import uk.ac.qub.jmccambridge06.wetravel.Leg;
import uk.ac.qub.jmccambridge06.wetravel.MyApplication;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class LegListAdapter extends EntryListAdapter {

    public static class LegListViewHolder extends EntryListViewHolder {

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
        LegDetailsFragment legDetailsFragment = new LegDetailsFragment(current);
        ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.details_container,
                legDetailsFragment).commit();
    }
}
