package com.blogspot.sontx.bottle.view.fragment;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.blogspot.sontx.bottle.App;
import com.blogspot.sontx.bottle.model.bean.Coordination;
import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.blogspot.sontx.bottle.model.bean.UserSetting;
import com.blogspot.sontx.bottle.model.dummy.DummyEmotions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class GeoMessageFragmentHelper {

    static void showMyLocation(GoogleMap map) {
        UserSetting userSetting = App.getInstance().getBottleContext().getCurrentUserSetting();
        Coordination currentLocation = userSetting.getCurrentLocation();
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13.5F);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Your Location")
                .draggable(true));
    }

    static abstract class MarkerTask<A, B, C> extends AsyncTask<A, B, C> {
        protected final GeoMessageMarkerPool markerPool;
        protected final String currentUserId;
        protected List<GeoMessage> tempMarkerGeoMessageList;

        protected MarkerTask(GeoMessageMarkerPool markerPool, String currentUserId) {
            this.markerPool = markerPool;
            this.currentUserId = currentUserId;
        }

        @Override
        protected void onPreExecute() {
            tempMarkerGeoMessageList = markerPool.getGeoMessageList();
        }

        @NonNull
        protected MarkerOptions getMarkerOptions(GeoMessage message) {
            MarkerOptions markerOptions;
            if (message.getOwner().getId().equalsIgnoreCase(currentUserId)) {
                int emotionResId = DummyEmotions.getEmotionResId(message.getEmotion(), true);
                markerOptions = markerPool.generateMarkerOptions(
                        new LatLng(message.getLatitude(), message.getLongitude()),
                        emotionResId);
                markerOptions.draggable(true);
                markerOptions.zIndex(100);
            } else {
                int emotionResId = DummyEmotions.getEmotionResId(message.getEmotion(), false);
                markerOptions = markerPool.generateMarkerOptions(
                        new LatLng(message.getLatitude(), message.getLongitude()),
                        emotionResId);
            }
            return markerOptions;
        }
    }

    static class UpdateMarkerTask extends MarkerTask<Void, Void, Pair<Integer, GeoMessage>> {
        private final GeoMessage updateGeoMessage;
        private final GeoMessage oldTempGeoMessage;// optional

        UpdateMarkerTask(GeoMessageMarkerPool markerPool,
                         String currentUserId,
                         GeoMessage updateGeoMessage,
                         GeoMessage oldTempGeoMessage) {
            super(markerPool, currentUserId);
            this.updateGeoMessage = updateGeoMessage;
            this.oldTempGeoMessage = oldTempGeoMessage;
        }

        @Override
        protected void onPreExecute() {
            if (oldTempGeoMessage != null)
                markerPool.removeMarkerByMessageId(updateGeoMessage.getId());
            super.onPreExecute();
        }

        @Override
        protected Pair<Integer, GeoMessage> doInBackground(Void... params) {
            int i = -1;
            for (GeoMessage _geoMessage : tempMarkerGeoMessageList) {
                i++;
                if (_geoMessage == null)
                    continue;

                if (oldTempGeoMessage != null && oldTempGeoMessage == _geoMessage) {
                    return new Pair<>(i, updateGeoMessage);
                } else if (_geoMessage.getId() == updateGeoMessage.getId()) {
                    return new Pair<>(i, updateGeoMessage);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Pair<Integer, GeoMessage> pair) {
            if (pair != null) {
                GeoMessage message = pair.second;
                Marker marker = markerPool.getMarker(pair.first);
                LatLng position = marker.getPosition();

                // position it doesn't change
                if (position.latitude == message.getLatitude() && position.longitude == message.getLongitude()) {
                    marker.setTag(pair.second);
                } else {// it changes
                    MarkerOptions markerOptions = getMarkerOptions(message);
                    Marker newMarker = markerPool.addMarker(markerOptions, message);

                    markerPool.replaceMarker(pair.first, newMarker);
                    marker.remove();
                }
            }
        }
    }

    static class AddMarkersTask extends MarkerTask<Void, Void, Map<MarkerOptions, GeoMessage>> {
        private final List<GeoMessage> addGeoMessages;

        AddMarkersTask(GeoMessageMarkerPool markerPool,
                       String currentUserId,
                       List<GeoMessage> addGeoMessages) {
            super(markerPool, currentUserId);
            this.addGeoMessages = addGeoMessages;
        }

        @Override
        protected Map<MarkerOptions, GeoMessage> doInBackground(Void... params) {
            Map<MarkerOptions, GeoMessage> addMarkerOptionsList = new HashMap<>(addGeoMessages.size());

            for (GeoMessage addGeoMessage : addGeoMessages) {
                boolean exist = false;
                for (GeoMessage markerGeoMessage : tempMarkerGeoMessageList) {
                    if (markerGeoMessage.getId() == addGeoMessage.getId()) {
                        exist = true;
                        break;
                    }
                }

                if (exist)
                    continue;

                MarkerOptions addMarkerOptions = getMarkerOptions(addGeoMessage);
                addMarkerOptionsList.put(addMarkerOptions, addGeoMessage);
            }

            return addMarkerOptionsList;
        }

        @Override
        protected void onPostExecute(Map<MarkerOptions, GeoMessage> addMarkerOptionsList) {
            Set<MarkerOptions> markerOptionsSet = addMarkerOptionsList.keySet();
            for (MarkerOptions markerOptions : markerOptionsSet) {
                GeoMessage geoMessage = addMarkerOptionsList.get(markerOptions);
                markerPool.addMarker(markerOptions, geoMessage);
            }
        }
    }
}
