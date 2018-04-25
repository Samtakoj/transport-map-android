package com.beyondar.android.plugin;

/**
 * Created by alex on 4/16/18.
 */

import android.graphics.Bitmap;

import com.beyondar.android.opengl.renderable.Renderable;
import com.beyondar.android.opengl.texture.Texture;
import com.beyondar.android.plugin.GeoObjectPlugin;
import com.beyondar.android.util.math.geom.Point3;
import com.beyondar.android.world.BeyondarObject;
import com.beyondar.android.world.GeoObject;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

class GoogleMapGeoObjectPlugin implements GeoObjectPlugin {

    private Marker mMarker;
    private LatLng mLatLng;
    private GeoObject mGeoObject;
    private boolean mAttached;
    private GoogleMapWorldPlugin mWorldGoogleMapPlugin;

    public GoogleMapGeoObjectPlugin(GoogleMapWorldPlugin worldGoogleMapPlugin, BeyondarObject beyondarObject) {
        mAttached = false;
        mWorldGoogleMapPlugin = worldGoogleMapPlugin;
        if (mWorldGoogleMapPlugin == null) {
            throw new NullPointerException("The WorldGoogleMapPlugin must not be null");
        }

        setBeyondarObject(beyondarObject);
    }

    private void setBeyondarObject(BeyondarObject beyondarObject) {
        if (beyondarObject instanceof GeoObject) {
            mGeoObject = (GeoObject) beyondarObject;
        } else {
            // throw new
            // IllegalArgumentException("beyondarObject must be a GeoObject");
        }
        if (mGeoObject == null) {
            throw new NullPointerException("The BeyondarObject must not be null");
        }
        mAttached = true;
    }

    @Override
    public void onGeoPositionChanged(double latitude, double longitude, double altitude) {
        if (mMarker == null) {
            return;
        }
        mMarker.setPosition(getLatLng());
    }

    /**
     * Get the {@link LatLng} instance that represents the {@link com.beyondar.android.world.GeoObject GeoObject}. It
     * will try to recycle the {@link LatLng} object if it is possible
     *
     * @return
     */
    public LatLng getLatLng() {
        if (mLatLng == null) {
            mLatLng = new LatLng(mGeoObject.getLatitude(), mGeoObject.getLongitude());
            return mLatLng;
        }

        if (mLatLng.latitude == mGeoObject.getLatitude() && mLatLng.longitude == mGeoObject.getLongitude()) {
            return mLatLng;
        }

        mLatLng = new LatLng(mGeoObject.getLatitude(), mGeoObject.getLongitude());
        return mLatLng;
    }

    /**
     * Set the {@link Marker} that belongs to the {@link com.beyondar.android.world.GeoObject GeoObject}
     *
     * @param marker
     */
    public void setMarker(Marker marker) {
        mMarker = marker;
        mWorldGoogleMapPlugin.registerMarker(mMarker, this);
    }

    /**
     * Get the marker that belongs to the {@link com.beyondar.android.world.GeoObject GeoObject}
     *
     * @return
     */
    public Marker getMarker() {
        return mMarker;
    }

    @Override
    public GeoObject getGeoObject() {
        return mGeoObject;
    }

    /**
     * Create the marker options in order to create the Marker.
     *
     * @param bitmap
     *            The bitmap to use for representing the {@link Marker}
     * @return
     */
    public MarkerOptions createMarkerOptions(Bitmap bitmap) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(mGeoObject.getName());
        markerOptions.position(getLatLng());

        if (bitmap != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
        return markerOptions;
    }

    @Override
    public void onAngleChanged(Point3 angle) {
    }

    @Override
    public void onPositionChanged(Point3 position) {
    }

    @Override
    public void onTextureChanged(Texture texture) {
    }

    @Override
    public void onRenderableChanged(Renderable openglObject) {
    }

    @Override
    public void onFaceToCameraChanged(boolean faceToCamera) {
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
    }

    @Override
    public void onNameChanged(String name) {
        if (mMarker == null) {
            return;
        }
        mMarker.setTitle(name);
    }

    @Override
    public void onImageUriChanged(String uri) {
        mWorldGoogleMapPlugin.setMarkerImage(mMarker, mGeoObject);
    }

    @Override
    public void onDetached() {
        mAttached = false;
        if (mMarker == null) {
            return;
        }
        mMarker.remove();
    }

    @Override
    public boolean isAttached() {
        return mAttached;
    }

    @Override
    public BeyondarObject getbeyondarObject() {
        return getGeoObject();
    }
}
