package uk.ac.qub.jmccambridge06.wetravel.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

import uk.ac.qub.jmccambridge06.wetravel.R;
import uk.ac.qub.jmccambridge06.wetravel.network.JsonFetcher;
import uk.ac.qub.jmccambridge06.wetravel.network.NetworkResultCallback;
import uk.ac.qub.jmccambridge06.wetravel.network.routes;
import uk.ac.qub.jmccambridge06.wetravel.ui.MainMenuActivity;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class Locater implements LocationListener {

    private static final int REQUEST_FINE_LOCATION = 1;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 02;
    private LocationManager locationManager;
    private Context context;
    private LocationListener locationListener;
    private double latitude;
    private double longditude;
    private NetworkResultCallback networkResultCallback;

    public Locater(Context context) {
        this.context = context;
    }

    public void checkSystem() {

    }

    public void getLocation(NetworkResultCallback placesCallback) {
        this.networkResultCallback = placesCallback;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == false) {
            Toast.makeText(context.getApplicationContext(), R.string.no_gps, Toast.LENGTH_SHORT).show();
        }
        // check has permissions been granted
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            // If access not granted then return
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        // request the location
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longditude = location.getLongitude();
        Toast.makeText(context.getApplicationContext(), "location changed, latitude: "+latitude+" longditude: "+longditude, Toast.LENGTH_SHORT).show();
        // stop the location from updating infinitely
        locationManager.removeUpdates(this);
        // prepare url for json fetcher and send - will get called back in the original class
        JsonFetcher jsonFetcher = new JsonFetcher(networkResultCallback, context);
        jsonFetcher.getData(routes.getPlacesNearby(latitude, longditude));

        /*Places.initialize(context, Locations.key);
        PlacesClient placesClient = Places.createClient(context);
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.ADDRESS_COMPONENTS);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(context);
        startActivityForResult((Activity) context,intent, AUTOCOMPLETE_REQUEST_CODE, null);*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == AutocompleteActivity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(context.getApplicationContext(), "location selected" + place, Toast.LENGTH_SHORT).show();
                Log.i("entry", "Place: " +place.getAddressComponents().toString());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("entry", status.getStatusMessage());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // The user canceled the operation.
                Log.i("entry", "error");
            }
        }*/
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
