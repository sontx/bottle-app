package com.blogspot.sontx.bottle.view.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.blogspot.sontx.bottle.model.bean.MessageBase;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.presenter.GeoMessageChangePresenterImpl;
import com.blogspot.sontx.bottle.presenter.GeoMessagePresenterImpl;
import com.blogspot.sontx.bottle.presenter.interfaces.GeoMessageChangePresenter;
import com.blogspot.sontx.bottle.presenter.interfaces.GeoMessagePresenter;
import com.blogspot.sontx.bottle.view.activity.WriteMessageActivity;
import com.blogspot.sontx.bottle.view.adapter.GeoMessageInfoWindowAdapter;
import com.blogspot.sontx.bottle.view.interfaces.GeoMessageChangeView;
import com.blogspot.sontx.bottle.view.interfaces.MapMessageView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class ListGeoMessageFragment extends FragmentBase implements
        OnMapReadyCallback,
        MapMessageView,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnInfoWindowClickListener,
        View.OnClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowCloseListener,
        GeoMessageChangeView,
        OnFragmentVisibleChangedListener {

    private static final int REQUEST_CODE_NEW_ROOM_MESSAGE = 1;
    private OnListGeoMessageInteractionListener listener;
    private GeoMessagePresenter geoMessagePresenter;
    private GeoMessageChangePresenter geoMessageChangePresenter;
    private LatLngBounds lastLatLngBounds;
    private boolean preventUpdateMoreMessages = false;
    private GeoMessageMarkerPool markerPool;
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
        geoMessageChangePresenter = new GeoMessageChangePresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_geo_message_map, container, false);

        if (markerPool != null) {
            markerPool.destroy();
            markerPool = null;
        }

        lastLatLngBounds = null;
        preventUpdateMoreMessages = false;

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
        if (markerPool != null)
            markerPool.destroy();
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
        geoMessageChangePresenter.unsubscribe();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_NEW_ROOM_MESSAGE) {
            if (resultCode == Activity.RESULT_OK) {
                String text = data.getStringExtra(WriteMessageActivity.MESSAGE_TEXT);
                String mediaPath = data.getStringExtra(WriteMessageActivity.MESSAGE_MEDIA);
                String type = data.getStringExtra(WriteMessageActivity.MESSAGE_TYPE);
                int emotion = data.getIntExtra(WriteMessageActivity.MESSAGE_EMOTION, 0);

                Coordination currentLocation = App.getInstance().getBottleContext().getCurrentUserSetting().getCurrentLocation();
                geoMessagePresenter.postGeoMessageAsync(text, currentLocation, mediaPath, type, emotion);
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

        GeoMessageFragmentHelper.showMyLocation(map);

        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        map.setInfoWindowAdapter(new GeoMessageInfoWindowAdapter(getActivity()));

        fab.setVisibility(userSetting.getMessageId() > -1 ? View.GONE : View.VISIBLE);

        markerPool = new GeoMessageMarkerPool(getActivity(), map);

        geoMessageChangePresenter.subscribe();
    }

    @Override
    public void showMapMessages(List<GeoMessage> geoMessageList) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        new GeoMessageFragmentHelper.AddMarkersTask(markerPool, currentUserId, geoMessageList).execute();
    }

    @Override
    public void addGeoMessage(GeoMessage addGeoMessage) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();

        List<GeoMessage> geoMessages = new ArrayList<>(1);
        geoMessages.add(addGeoMessage);

        new GeoMessageFragmentHelper.AddMarkersTask(markerPool, currentUserId, geoMessages).execute();

        if (addGeoMessage.getOwner().getId().equals(currentUserId))
            fab.setVisibility(View.GONE);
    }

    @Override
    public synchronized void updateGeoMessage(GeoMessage message) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        new GeoMessageFragmentHelper.UpdateMarkerTask(markerPool, currentUserId, message, null).execute();
    }

    @Override
    public synchronized void updateGeoMessage(GeoMessage message, GeoMessage tempGeoMessage) {
        String currentUserId = App.getInstance().getBottleContext().getCurrentBottleUser().getUid();
        new GeoMessageFragmentHelper.UpdateMarkerTask(markerPool, currentUserId, message, tempGeoMessage).execute();
    }

    @Override
    public LatLngBounds getViewBound() {
        return lastLatLngBounds;
    }

    @Override
    public void removeGeoMessage(int messageId) {
        markerPool.removeMarkerByMessageId(messageId);
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
        Intent intent = new Intent(getContext(), WriteMessageActivity.class);
        intent.putExtra(WriteMessageActivity.SUPPORT_EMOTION, true);
        startActivityForResult(intent, REQUEST_CODE_NEW_ROOM_MESSAGE);
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

            GeoMessage geoMessage = (GeoMessage) marker.getTag();
            geoMessage.setLatitude(marker.getPosition().latitude);
            geoMessage.setLongitude(marker.getPosition().longitude);
            geoMessagePresenter.editGeoMessageAsync(geoMessage);
        } else {
            LatLng myLocation = marker.getPosition();

            Coordination currentLocation = new Coordination();
            currentLocation.setLatitude(myLocation.latitude);
            currentLocation.setLongitude(myLocation.longitude);

            geoMessagePresenter.updateCurrentUserLocationAsync(currentLocation);
        }
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

    @Override
    public void onFragmentVisibleChanged(boolean isVisible) {
    }

    public void removeMessage(MessageBase messageBase) {
        markerPool.removeMarkerByMessageId(messageBase.getId());
        fab.setVisibility(View.VISIBLE);
        geoMessagePresenter.deleteGeoMessageAsync(messageBase.getId());
    }

    public interface OnListGeoMessageInteractionListener {
        void onGeoMessageClick(GeoMessage item);
    }
}
