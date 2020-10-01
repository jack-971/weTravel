package uk.ac.qub.jmccambridge06.wetravel.ui.lists;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import uk.ac.qub.jmccambridge06.wetravel.models.Profile;
import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.RelationshipTypesDb;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

/**
 * holds controller logic for a list of users - can be a friend list or search return list
 */
public class UserListFragment extends ListFragment {

    /**
     * represents a search string query - if null it means no query and list is a friends list, otherwise
     * it is a search list
     */
    private String query = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logtag = "FriendListFragment";
        super.onViewCreated(view, savedInstanceState);
        loadCallback();
        getData();
    }

    /**
     * Checks whether it is for a search and determines which route to send.
     */
    void getData() {
        Log.i(logtag, "getting data");
            jsonFetcher = new JsonFetcher(getCallback,getContext());
            String url;
            if (query != null) {
                url = routes.getUserSearchList(query);
            } else {
                url = routes.getUsersRoute(profile.getUserId());
            }
            jsonFetcher.getData(url);
    }

    @Override
    public void fillList(View view, JSONArray jsonArray) throws JSONException {
        Log.i(logtag, "filling list now");
        for (int loop=0; loop<jsonArray.length(); loop++) {
            JSONObject friend = jsonArray.getJSONObject(loop);
            int userId = Integer.parseInt(friend.getString("UserID"));
            Profile profile = ((MainMenuActivity) getActivity()).getUserAccount().checkFriend(userId);
            // For both search and friend list if it exists in userAcc friend list add it from there. If a friend list check that they are
            // friends before adding. If a search can add even if not friends.
            if (query == null) {
                String profileType = friend.getString("RelationshipType");
                if (profileType.equals(RelationshipTypesDb.FRIENDS)) {
                    if (profile != null) {
                        list.add(profile);
                    } else {
                        list.add(new Profile(friend, 2));
                    }
                }
            } else {
                if (profile != null) {
                    list.add(profile);
                } else {
                    list.add(new Profile(friend, 2)); // 2 equates to a user not in friends list
                }
            }
        }
        if (list.size() == 0) {
            noData();
        } else {
            updateList();
        }
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public void setAdapter() {
        adapter = new UserListAdapter(list, getContext());
    }

    @Override
    protected void noData() {
        super.noData();
        noDataText.setText(R.string.error_no_users);
    }
}
