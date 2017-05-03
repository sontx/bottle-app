package com.blogspot.sontx.bottle.view.fragment;

import android.Manifest;
import android.content.Context;
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
import com.blogspot.sontx.bottle.view.adapter.GeoMessageInfoWindowAdapter;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class ListGeoMessageFragment extends FragmentBase implements
        OnMapReadyCallback,
        MapMessageView,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnInfoWindowClickListener {

    private OnListGeoMessageInteractionListener listener;
    private MapMessagePresenter mapMessagePresenter;
    private MapView mapView;
    private GoogleMap map;

    public ListGeoMessageFragment() {
    }

    public static ListGeoMessageFragment newInstance() {
        return new ListGeoMessageFragment();
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListGeoMessageInteractionListener) {
            listener = (OnListGeoMessageInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListGeoMessageInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
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
        map.setOnInfoWindowClickListener(this);

        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());

        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        Coordination currentLocation = userSetting.getCurrentLocation();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11.5F);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .draggable(true));

        map.setInfoWindowAdapter(new GeoMessageInfoWindowAdapter(getActivity()));
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

    @Override
    public void onInfoWindowClick(Marker marker) {
        GeoMessage geoMessage = (GeoMessage) marker.getTag();
        if (listener != null)
            listener.onGeoMessageClick(geoMessage);
        marker.hideInfoWindow();
    }

    private static class MarkerCreatorTask extends AsyncTask<Void, Void, Dictionary<MarkerOptions, GeoMessage>> {
        private final GoogleMap map;
        private final List<GeoMessage> geoMessages;

        private MarkerCreatorTask(GoogleMap map, List<GeoMessage> geoMessages) {
            this.map = map;
            this.geoMessages = geoMessages;
        }

        @Override
        protected Dictionary<MarkerOptions, GeoMessage> doInBackground(Void... params) {
            Dictionary<MarkerOptions, GeoMessage> markers = new Hashtable<>(geoMessages.size());

            for (GeoMessage geoMessage : geoMessages) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(geoMessage.getLatitude(), geoMessage.getLongitude()))
                        .title(geoMessage.getOwner().getDisplayName())
                        .snippet(geoMessage.getText());
                markers.put(markerOptions, geoMessage);
            }

            return markers;
        }

        @Override
        protected void onPostExecute(Dictionary<MarkerOptions, GeoMessage> markers) {
            Enumeration<MarkerOptions> markerOptionsEnumeration = markers.keys();
            while (markerOptionsEnumeration.hasMoreElements()) {
                MarkerOptions markerOptions = markerOptionsEnumeration.nextElement();
                GeoMessage geoMessage = markers.get(markerOptions);
                map.addMarker(markerOptions).setTag(geoMessage);
            }
            super.onPostExecute(markers);
        }
    }

    public interface OnListGeoMessageInteractionListener {
        void onGeoMessageClick(GeoMessage item);
    }
}
