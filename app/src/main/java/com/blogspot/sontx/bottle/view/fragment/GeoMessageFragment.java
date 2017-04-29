package com.blogspot.sontx.bottle.view.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.presenter.MapMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.MapMessagePresenter;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class GeoMessageFragment extends FragmentBase implements OnMapReadyCallback, MapMessageView, GoogleMap.OnCameraIdleListener {
    private MapMessagePresenter mapMessagePresenter;
    private MapView mapView;
    private GoogleMap map;

    public GeoMessageFragment() {
    }

    public static GeoMessageFragment newInstance() {
        return new GeoMessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapMessagePresenter = new MapMessagePresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_message_map, container, false);

        mapView = (MapView) view.findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private boolean checkPermission() {
        return !(ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        if (!checkPermission())
            return;

        map.setOnCameraIdleListener(this);

        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());

        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        Coordination currentLocation = userSetting.getCurrentLocation();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .draggable(true));
    }

    @Override
    public void showMapMessages(List<GeoMessage> geoMessageList) {
        new MarkerCreatorTask(map, geoMessageList).execute();
    }

    @Override
    public void onCameraIdle() {
        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
        double latitudeRadius = latLngBounds.northeast.latitude - latLngBounds.southwest.latitude;
        double longitudeRadius = latLngBounds.northeast.longitude - latLngBounds.southwest.longitude;
        mapMessagePresenter.getMapMessagesAroundMyLocationAsync(latitudeRadius, longitudeRadius);
    }

    private static class MarkerCreatorTask extends AsyncTask<Void, Void, List<MarkerOptions>> {
        private final GoogleMap map;
        private final List<GeoMessage> geoMessages;

        private MarkerCreatorTask(GoogleMap map, List<GeoMessage> geoMessages) {
            this.map = map;
            this.geoMessages = geoMessages;
        }

        @Override
        protected List<MarkerOptions> doInBackground(Void... params) {
            List<MarkerOptions> markers = new ArrayList<>(geoMessages.size());

            for (GeoMessage geoMessage : geoMessages) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(geoMessage.getLatitude(), geoMessage.getLongitude()))
                        .title(geoMessage.getOwner().getDisplayName())
                        .snippet(geoMessage.getText());
                markers.add(markerOptions);
            }

            return markers;
        }

        @Override
        protected void onPostExecute(List<MarkerOptions> markers) {
            for (MarkerOptions marker : markers) {
                map.addMarker(marker);
            }
            super.onPostExecute(markers);
        }
    }
}
