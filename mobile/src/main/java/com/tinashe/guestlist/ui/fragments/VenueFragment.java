package com.tinashe.guestlist.ui.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tinashe.guestlist.R;
import com.tinashe.guestlist.utils.Constants;
import com.uber.sdk.android.rides.RequestButton;
import com.uber.sdk.android.rides.RideParameters;

import butterknife.Bind;

/**
 * Created by tinashe on 2016/02/16.
 */
public class VenueFragment extends BaseFragment implements OnMapReadyCallback {

    private static final String IMG_URL = "http://oudelibertas.co.za/wordpress/wp-content/uploads/2013/11/cape-town-routes-unlimited-oude-libertas-stellenbosch-1_06e.jpg";

    @Bind(R.id.btn_navigate)
    Button navigate;

    @Bind(R.id.btn_uber)
    RequestButton uberButton;

    @Nullable
    @Bind(R.id.img_venue)
    ImageView venue;

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_venue;
    }

    @Override
    protected void initialize() {
        super.initialize();

        if (venue == null) {
            final SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            fragment.getMapAsync(this);
        } else {
            Glide.with(getContext())
                    .load(IMG_URL)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(venue);
        }


        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchNavigation();
            }
        });

        RideParameters rideParameters = new RideParameters.Builder()
                .setDropoffLocation((float) Constants.OUDE_LIBERTAS.latitude,
                        (float) Constants.OUDE_LIBERTAS.longitude,
                        getString(R.string.oude), getString(R.string.venue_address))
                .build();
        uberButton.setRideParameters(rideParameters);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Constants.OUDE_LIBERTAS, 12));
            }
        }, 1500);

        googleMap.addMarker(new MarkerOptions()
                .position(Constants.OUDE_LIBERTAS)
                .title(getString(R.string.oude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        );
    }


    private void launchNavigation() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + Constants.OUDE_LIBERTAS.latitude + ", " + Constants.OUDE_LIBERTAS.longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
