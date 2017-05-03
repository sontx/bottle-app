package com.blogspot.sontx.bottle.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.presenter.GeoMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.GeoMessagePresenter;
import com.blogspot.sontx.bottle.view.activity.WriteMessageActivity;
import com.blogspot.sontx.bottle.view.adapter.GeoMessageInfoWindowAdapter;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import butterknife.ButterKnife;

public class ListGeoMessageFragment extends FragmentBase implements
        OnMapReadyCallback,
        MapMessageView,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowCloseListener {

    private static final int REQUEST_CODE_NEW_ROOM_MESSAGE = 1;
    private OnListGeoMessageInteractionListener listener;
    private GeoMessagePresenter geoMessagePresenter;
    private Marker currentMarker;
    private LatLngBounds lastLatLngBounds;
    private boolean preventUpdateMoreMessages = false;
    private MapView mapView;
    private GoogleMap map;
    private FloatingActionButton fab;

    public ListGeoMessageFragment() {
    }

    public static ListGeoMessageFragment newInstance() {
        return new ListGeoMessageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoMessagePresenter = new GeoMessagePresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_message_map, container, false);

        fab = ButterKnife.findById(view, R.id.fab);
        fab.setOnClickListener(this);
        fab.setVisibility(View.GONE);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_ROOM_MESSAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String text = data.getStringExtra(WriteMessageActivity.MESSAGE_TEXT);
                String mediaPath = data.getStringExtra(WriteMessageActivity.MESSAGE_MEDIA);
                String type = data.getStringExtra(WriteMessageActivity.MESSAGE_TYPE);

                Coordination currentLocation = App.getInstance().getBottleContext().getCurrentUserSetting().getCurrentLocation();
                geoMessagePresenter.postGeoMessageAsync(text, currentLocation, mediaPath, type);
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
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
        map.setOnMarkerClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnInfoWindowCloseListener(this);
        map.setOnMarkerDragListener(this);

        map.getUiSettings().setMyLocationButtonEnabled(false);

        map.setMyLocationEnabled(true);
        MapsInitializer.initialize(this.getActivity());

        showMyLocation(map);

        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        map.setInfoWindowAdapter(new GeoMessageInfoWindowAdapter(getActivity()));

        fab.setVisibility(userSetting.getCurrentRoomId() > -1 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showMapMessages(List<GeoMessage> geoMessageList) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        new MarkerCreatorTask(map, geoMessageList, currentUserId).execute();
    }

    @Override
    public synchronized void updateRoomMessage(GeoMessage result, GeoMessage tempGeoMessage) {
        if (currentMarker != null) {
            currentMarker.setTag(result);
        }
    }

    @Override
    public void addGeoMessage(GeoMessage tempGeoMessage) {
        MarkerOptions markerOptions = createCurrentUserMarker(tempGeoMessage);

        Marker marker = map.addMarker(markerOptions);
        marker.setTag(tempGeoMessage);

        currentMarker = marker;
        fab.setVisibility(View.GONE);
    }

    @Override
    public void onCameraIdle() {
        if (preventUpdateMoreMessages)
            return;

        LatLngBounds latLngBounds = map.getProjection().getVisibleRegion().latLngBounds;

        if (lastLatLngBounds == null || !lastLatLngBounds.equals(latLngBounds)) {
            double latitudeRadius = latLngBounds.northeast.latitude - latLngBounds.southwest.latitude;
            double longitudeRadius = latLngBounds.northeast.longitude - latLngBounds.southwest.longitude;
            geoMessagePresenter.getMapMessagesAroundMyLocationAsync(latitudeRadius, longitudeRadius);

            lastLatLngBounds = latLngBounds;
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        GeoMessage geoMessage = (GeoMessage) marker.getTag();
        if (listener != null)
            listener.onGeoMessageClick(geoMessage);
        marker.hideInfoWindow();
    }

    @Override
    public void onClick(View v) {
        startActivityForResult(new Intent(getContext(), WriteMessageActivity.class), REQUEST_CODE_NEW_ROOM_MESSAGE);
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (marker.getTag() instanceof GeoMessage) {
            this.currentMarker = marker;

            GeoMessage geoMessage = (GeoMessage) currentMarker.getTag();
            geoMessage.setLatitude(currentMarker.getPosition().latitude);
            geoMessage.setLongitude(currentMarker.getPosition().longitude);
            geoMessagePresenter.editGeoMessageAsync(geoMessage);
        } else {
            LatLng myLocation = marker.getPosition();

            Coordination currentLocation = new Coordination();
            currentLocation.setLatitude(myLocation.latitude);
            currentLocation.setLongitude(myLocation.longitude);

            geoMessagePresenter.updateCurrentUserLocationAsync(currentLocation);
        }
    }

    private static synchronized MarkerOptions createCurrentUserMarker(GeoMessage message) {
        return new MarkerOptions()
                .position(new LatLng(message.getLatitude(), message.getLongitude()))
                .title(message.getOwner().getDisplayName())
                .snippet(message.getText())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vegetable))
                .draggable(true)
                .zIndex(100);
    }

    private static void showMyLocation(GoogleMap map) {
        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        Coordination currentLocation = userSetting.getCurrentLocation();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.5F);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .draggable(true));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        preventUpdateMoreMessages = true;
        return false;
    }

    @Override
    public void onInfoWindowClose(Marker marker) {
        preventUpdateMoreMessages = false;
    }

    private static class MarkerCreatorTask extends AsyncTask<Void, Void, Dictionary<MarkerOptions, GeoMessage>> {
        private final GoogleMap map;
        private final List<GeoMessage> geoMessages;
        private final String currentUserId;

        private MarkerCreatorTask(GoogleMap map, List<GeoMessage> geoMessages, String currentUserId) {
            this.map = map;
            this.geoMessages = geoMessages;
            this.currentUserId = currentUserId;
        }

        @Override
        protected Dictionary<MarkerOptions, GeoMessage> doInBackground(Void... params) {
            Dictionary<MarkerOptions, GeoMessage> markers = new Hashtable<>(geoMessages.size());

            for (GeoMessage geoMessage : geoMessages) {
                MarkerOptions markerOptions;
                if (geoMessage.getOwner().getId().equalsIgnoreCase(currentUserId)) {
                    markerOptions = createCurrentUserMarker(geoMessage);
                } else {
                    markerOptions = new MarkerOptions()
                            .position(new LatLng(geoMessage.getLatitude(), geoMessage.getLongitude()))
                            .title(geoMessage.getOwner().getDisplayName())
                            .snippet(geoMessage.getText());
                }
                markers.put(markerOptions, geoMessage);
            }

            return markers;
        }

        @Override
        protected void onPostExecute(Dictionary<MarkerOptions, GeoMessage> markers) {
            map.clear();
            showMyLocation(map);
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
