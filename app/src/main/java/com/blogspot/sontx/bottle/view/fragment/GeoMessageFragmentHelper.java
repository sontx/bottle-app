package com.blogspot.sontx.bottle.view.fragment;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.R;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public final class GeoMessageFragmentHelper {

    static synchronized MarkerOptions createCurrentUserMarker(GeoMessage message) {
        return new MarkerOptions()
                .position(new LatLng(message.getLatitude(), message.getLongitude()))
                .title(message.getOwner().getDisplayName())
                .snippet(message.getText())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_vegetable))
                .draggable(true)
                .zIndex(100);
    }

    static void showMyLocation(GoogleMap map) {
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

    static class UpdateMarkerTask extends AsyncTask<Void, Void, Pair<Integer, GeoMessage>> {
        private final GoogleMap map;
        private final GeoMessage geoMessage;
        private final GeoMessage oldGeoMessage;
        private final List<Marker> markers;
        private final String currentUserId;
        private List<GeoMessage> markerGeoMessages;

        UpdateMarkerTask(GoogleMap map,
                         GeoMessage geoMessage,
                         GeoMessage oldGeoMessage,
                         List<Marker> markers, String currentUserId) {
            this.map = map;
            this.geoMessage = geoMessage;
            this.oldGeoMessage = oldGeoMessage;
            this.markers = markers;
            this.currentUserId = currentUserId;
        }

        @Override
        protected void onPreExecute() {
            removePreviousCurrentUserMarkerIfNecessary();
            markerGeoMessages = new ArrayList<>(markers.size());
            for (Marker marker : markers) {
                markerGeoMessages.add((GeoMessage) marker.getTag());
            }
        }

        private void removePreviousCurrentUserMarkerIfNecessary() {
            if (oldGeoMessage != null) {
                for (int i = markers.size() - 1; i >= 0; i--) {
                    Marker marker = markers.get(i);
                    GeoMessage geoMessage = (GeoMessage) marker.getTag();
                    if (geoMessage.getId() == geoMessage.getId()) {
                        markers.remove(marker);
                        marker.remove();
                        break;
                    }
                }
            }
        }

        @Override
        protected Pair<Integer, GeoMessage> doInBackground(Void... params) {
            int i = -1;
            for (GeoMessage _geoMessage : markerGeoMessages) {
                i++;
                if (_geoMessage == null)
                    continue;

                if (oldGeoMessage != null && oldGeoMessage == _geoMessage) {
                    return new Pair<>(i, geoMessage);
                } else if (_geoMessage.getId() == geoMessage.getId()) {
                    return new Pair<>(i, geoMessage);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Pair<Integer, GeoMessage> pair) {
            if (pair != null) {
                GeoMessage message = pair.second;
                Marker marker = markers.get(pair.first);
                LatLng position = marker.getPosition();

                if (position.latitude == message.getLatitude() && position.longitude == message.getLongitude()) {
                    marker.setTag(pair.second);
                } else {
                    MarkerOptions markerOptions = getMarkerOptions(message, currentUserId);
                    Marker newMarker = map.addMarker(markerOptions);
                    newMarker.setTag(message);

                    markers.set(pair.first, newMarker);
                    marker.remove();
                }
            }
        }
    }

    static class AddMarkersTask extends AsyncTask<Void, Void, Dictionary<MarkerOptions, GeoMessage>> {
        private final GoogleMap map;
        private final List<GeoMessage> geoMessages;
        private final List<Marker> markers;
        private List<GeoMessage> markerGeoMessages;
        private final String currentUserId;

        AddMarkersTask(GoogleMap map,
                       List<GeoMessage> geoMessages,
                       List<Marker> markers,
                       String currentUserId) {
            this.map = map;
            this.geoMessages = geoMessages;
            this.markers = markers;
            this.currentUserId = currentUserId;
        }

        @Override
        protected void onPreExecute() {
            markerGeoMessages = new ArrayList<>(markers.size());
            for (Marker marker : markers) {
                markerGeoMessages.add((GeoMessage) marker.getTag());
            }
        }

        @Override
        protected Dictionary<MarkerOptions, GeoMessage> doInBackground(Void... params) {
            Dictionary<MarkerOptions, GeoMessage> markerOptionsList = new Hashtable<>(geoMessages.size());

            for (GeoMessage geoMessage : geoMessages) {
                boolean exist = false;
                for (GeoMessage _geoMessage : markerGeoMessages) {
                    if (_geoMessage.getId() == geoMessage.getId()) {
                        exist = true;
                        break;
                    }
                }

                if (exist)
                    continue;

                MarkerOptions markerOptions = getMarkerOptions(geoMessage, currentUserId);

                markerOptionsList.put(markerOptions, geoMessage);
            }

            return markerOptionsList;
        }

        @Override
        protected void onPostExecute(Dictionary<MarkerOptions, GeoMessage> markerOptionsList) {
            Enumeration<MarkerOptions> markerOptionsEnumeration = markerOptionsList.keys();
            while (markerOptionsEnumeration.hasMoreElements()) {
                MarkerOptions markerOptions = markerOptionsEnumeration.nextElement();
                GeoMessage geoMessage = markerOptionsList.get(markerOptions);
                Marker marker = map.addMarker(markerOptions);
                marker.setTag(geoMessage);
                markers.add(marker);
            }
        }
    }

    @NonNull
    private static MarkerOptions getMarkerOptions(GeoMessage geoMessage, String currentUserId) {
        MarkerOptions markerOptions;
        if (geoMessage.getOwner().getId().equalsIgnoreCase(currentUserId)) {
            markerOptions = createCurrentUserMarker(geoMessage);
        } else {
            markerOptions = new MarkerOptions()
                    .position(new LatLng(geoMessage.getLatitude(), geoMessage.getLongitude()))
                    .title(geoMessage.getOwner().getDisplayName())
                    .snippet(geoMessage.getText());
        }
        return markerOptions;
    }
}
