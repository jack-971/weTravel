package uk.ac.qub.jmccambridge06.wetravel.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import uk.ac.qub.jmccambridge06.wetravel.models.Activity;
import uk.ac.qub.jmccambridge06.wetravel.models.Leg;
import uk.ac.qub.jmccambridge06.wetravel.R;

public class ActivityListAdapter extends EntryListAdapter {

    public static class ActivityListViewHolder extends EntryListViewHolder {

        public ActivityListViewHolder(@NonNull View itemView) {
            super(itemView);
            timesView.setVisibility(View.INVISIBLE);
            image.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public ActivityListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_trip, parent, false);
        return new ActivityListViewHolder(v);
    }

    public ActivityListAdapter(ArrayList<Leg> legCardList, Context context) {
        super(legCardList, context);
        logtag = "Activity List Adapter";
    }

    @Override
    public void onBindViewHolder(@NonNull EntryListViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        Activity current = (Activity) entryCardList.get(position);
        checkDisplay(current, holder);
        holder.card.setVisibility(View.VISIBLE);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.main_screen_container,
                        new ActivityDetailsFragment(current)).addToBackStack(null).commit();
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
