package com.algonquincollege.doir0008.doorsopenottawa;

import android.os.Bundle;
import android.widget.TextView;

import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Geocoder;
import android.location.Address;
import android.widget.Toast;

import java.util.Locale;

/**
 * Displaying single building info and google map.
 *
 * @author Ryan Doiron (doir0008@algonquinlive.com)
 *
 */

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private TextView buildingAddress;
    private TextView buildingDescription;
    private TextView buildingName;
    private TextView buildingOpenHours;

    private String address;

    private GoogleMap mMap;
    private Geocoder mGeocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        buildingAddress = (TextView) findViewById( R.id.buildingAddress );
        buildingDescription = (TextView) findViewById(R.id.buildingDescription);
        buildingName = (TextView) findViewById(R.id.buildingName);
        buildingOpenHours = (TextView) findViewById(R.id.buildingOpenHours);

        // Get the bundle of extras that was sent to this activity
        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ) {
            String buildingAddressFromMainActivity = bundle.getString( "buildingAddress" );
            String buildingDescriptionMainLoginActivity = bundle.getString( "buildingDescription" );
            String buildingNameMainLoginActivity = bundle.getString( "buildingName" );
            String buildingOpenHoursMainLoginActivity = bundle.getString( "buildingOpenHours" );

            address = bundle.getString( "buildingAddress" );

            buildingAddress.setText( buildingAddressFromMainActivity );
            buildingDescription.setText( buildingDescriptionMainLoginActivity );
            buildingName.setText( buildingNameMainLoginActivity );
            buildingOpenHours.setText( buildingOpenHoursMainLoginActivity );
        }

        // instantiate
        mGeocoder = new Geocoder( this, Locale.CANADA );

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        pin(address);
    }

    /** Locate and pin locationName to the map. */
    private void pin( String locationName ) {
        try {
            Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng( address.getLatitude(), address.getLongitude() );
            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );
            mMap.moveCamera(CameraUpdateFactory.zoomTo(16.0f));
            mMap.moveCamera( CameraUpdateFactory.newLatLng(ll) );
            // Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            // Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_SHORT).show();
            // fallback pin to Ottawa
            pin("Ottawa, On");
        }
    }





}
