package com.blogspot.sontx.bottle.view.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.blogspot.sontx.bottle.model.bean.GeoMessage;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

final class GeoMessageMarkerPool {
    private static final int MARKER_WIDTH = 24;
    private static final int MARKER_HEIGHT = 24;

    private final Context context;
    private final GoogleMap map;
    private final int markerWidth;
    private final int markerHeight;
    private List<Marker> markers = new LinkedList<>();
    private Map<Integer, Bitmap> loadedBitmaps = new Hashtable<>();

    public GeoMessageMarkerPool(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;
        markerWidth = (int) dipToPixels(MARKER_WIDTH);
        markerHeight = (int) dipToPixels(MARKER_HEIGHT);
    }

    private float dipToPixels(float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    @NonNull
    private Bitmap getBitmapFromResId(int resId) {
        Bitmap bitmap = loadedBitmaps.get(resId);
        if (bitmap == null) {
            Bitmap srcBitmap = BitmapFactory.decodeResource(context.getResources(), resId);
            if (srcBitmap.getWidth() != markerWidth && srcBitmap.getHeight() != markerHeight) {
                bitmap = Bitmap.createScaledBitmap(srcBitmap, markerWidth, markerHeight, true);
                srcBitmap.recycle();
            } else {
                bitmap = srcBitmap;
            }
            loadedBitmaps.put(resId, bitmap);
        }
        return bitmap;
    }

    void destroy() {
        for (Marker marker : markers) {
            marker.remove();
        }
        Collection<Bitmap> bitmaps = loadedBitmaps.values();
        for (Bitmap bitmap : bitmaps) {
            bitmap.recycle();
        }
    }

    Marker getMarker(int position) {
        return markers.get(position);
    }

    void removeMarkerByMessageId(int messageId) {
        for (int i = markers.size() - 1; i >= 0; i--) {
            Marker marker = markers.get(i);
            GeoMessage geoMessage = (GeoMessage) marker.getTag();
            if (geoMessage != null && geoMessage.getId() == messageId) {
                markers.remove(marker);
                marker.remove();
                break;
            }
        }
    }

    List<GeoMessage> getGeoMessageList() {
        ArrayList<GeoMessage> markerGeoMessages = new ArrayList<>(markers.size());
        for (Marker marker : markers) {
            markerGeoMessages.add((GeoMessage) marker.getTag());
        }
        return markerGeoMessages;
    }

    void replaceMarker(int position, Marker newMarker) {
        markers.set(position, newMarker);
    }

    Marker addMarker(MarkerOptions markerOptions, GeoMessage message) {
        Marker marker = map.addMarker(markerOptions);
        marker.setTag(message);
        markers.add(marker);
        return marker;
    }

    MarkerOptions generateMarkerOptions(LatLng latLng, int bitmapResId) {
        return generateMarkerOptions(latLng, null, null, bitmapResId);
    }

    private MarkerOptions generateMarkerOptions(LatLng latLng, String title, String snippet, int bitmapResId) {
        return new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromResId(bitmapResId)))
                .title(title)
                .snippet(snippet);
    }
}
