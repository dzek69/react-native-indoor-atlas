package com.dzek69.indooratlas;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;

import com.facebook.react.modules.core.DeviceEventManagerModule;

import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;

public class RNIndoorAtlasModule extends ReactContextBaseJavaModule {
    // @TODO expose status constants
    private IALocationManager locationManager;
    private IALocationListener locationListener;
    private IARegion.Listener regionListener;
    private boolean listening;

    public RNIndoorAtlasModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void sendEvent(ReactContext context, String eventName, @Nullable WritableMap params) {
        context
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    private void sendDebugMessage(String message) {
        WritableMap params = Arguments.createMap();
        params.putString("message", message);
        sendEvent(getReactApplicationContext(), "debug", params);
    }

    @ReactMethod
    public void listen() {
        // sendDebugMessage("listen()");
        if (listening) {
            // sendDebugMessage("already listening");
            return;
        }
        // sendDebugMessage("was not listening");

        listening = true;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                // sendDebugMessage("listenrunnable started");
                locationManager = IALocationManager.create(getReactApplicationContext());
                locationListener = new IALocationListener() {
                    @Override
                    public void onLocationChanged(IALocation location) {
                        // sendDebugMessage("changed");
                        WritableMap params = Arguments.createMap();

                        params.putDouble("lat", location.getLatitude());
                        params.putDouble("lng", location.getLongitude());
                        params.putDouble("accuracy", location.getAccuracy());
                        params.putDouble("altitude", location.getAltitude());
                        params.putInt("floorLevel", location.getFloorLevel());
                        params.putDouble("floorCertainty", location.getFloorCertainty());
                        params.putDouble("bearing", location.getBearing());

                        sendEvent(
                            getReactApplicationContext(), "locationChanged", params
                        );
                    }

                    @Override
                    public void onStatusChanged(String s, int code, Bundle extra) {
                        WritableMap params = Arguments.createMap();
                        params.putDouble("status", code);
                        if (code == IALocationManager.STATUS_CALIBRATION_CHANGED) {
                            int quality = extra.getInt("quality");
                            params.putInt("quality", quality);
                        }
                        sendEvent(getReactApplicationContext(), "statusChanged", params);
                    }
                };

                regionListener = new IARegion.Listener() {
                    @Override
                    public void onEnterRegion(IARegion iaRegion) {
                        WritableMap regionMap = Arguments.createMap();

                        regionMap.putString("id", iaRegion.getId());
                        regionMap.putString("name", iaRegion.getName());

                        IAFloorPlan floorPlan = iaRegion.getFloorPlan();
                        if (floorPlan != null) {
                            WritableMap floorMap = Arguments.createMap();
                            WritableMap floorPointsMap = Arguments.createMap();

                            IALatLng bottomLeft = floorPlan.getBottomLeft();
                            if (bottomLeft != null) {
                                WritableMap bottomLeftMap = Arguments.createMap();
                                bottomLeftMap.putDouble("lat", bottomLeft.latitude);
                                bottomLeftMap.putDouble("lng", bottomLeft.longitude);
                                floorPointsMap.putMap("bottomLeft", bottomLeftMap);
                            }

                            IALatLng topRight = floorPlan.getTopRight();
                            if (topRight != null) {
                                WritableMap topRightMap = Arguments.createMap();
                                topRightMap.putDouble("lat", topRight.latitude);
                                topRightMap.putDouble("lng", topRight.longitude);
                                floorPointsMap.putMap("topRight", topRightMap);
                            }

                            IALatLng topLeft = floorPlan.getTopLeft();
                            if (topLeft != null) {
                                WritableMap topLeftMap = Arguments.createMap();
                                topLeftMap.putDouble("lat", topLeft.latitude);
                                topLeftMap.putDouble("lng", topLeft.longitude);
                                floorPointsMap.putMap("topLeft", topLeftMap);
                            }

                            IALatLng center = floorPlan.getCenter();
                            if (center != null) {
                                WritableMap centerMap = Arguments.createMap();
                                centerMap.putDouble("lat", center.latitude);
                                centerMap.putDouble("lng", center.longitude);
                                floorPointsMap.putMap("center", centerMap);
                            }

                            floorMap.putMap("points", floorPointsMap);
                            floorMap.putString("url", floorPlan.getUrl());
                            floorMap.putDouble("bearing", floorPlan.getBearing());
                            floorMap.putDouble("bitmapHeight", floorPlan.getBitmapHeight());
                            floorMap.putDouble("bitmapWidth", floorPlan.getBitmapWidth());

                            floorMap.putInt("floorLevel", floorPlan.getFloorLevel());
                            floorMap.putDouble("metersToPixels", floorPlan.getMetersToPixels());
                            floorMap.putDouble("pixelsToMeters", floorPlan.getPixelsToMeters());

                            regionMap.putMap("floor", floorMap);
                        }

                        sendEvent(getReactApplicationContext(), "regionEnter", regionMap);
                    }

                    @Override
                    public void onExitRegion(IARegion iaRegion) {
                        String id = iaRegion.getId();
                        WritableMap params = Arguments.createMap();
                        params.putString("id", id);
                        sendEvent(getReactApplicationContext(), "regionExit", params);
                    }
                };

                locationManager.requestLocationUpdates(
                    IALocationRequest.create(), locationListener
                );

                locationManager.registerRegionListener(
                    regionListener
                );
            }
        };
        getCurrentActivity().runOnUiThread(task);
    }

    @ReactMethod
    public void stop() {
        if (!listening) {
            return;
        }

        listening = false;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                locationManager.removeLocationUpdates(locationListener);
                locationListener = null;
                locationManager.destroy();
                locationManager = null;
            }
        };
        getCurrentActivity().runOnUiThread(task);
    }

    @Override
    public String getName() {
        return "IndoorAtlas";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("STATUS_AVAILABLE", IALocationManager.STATUS_AVAILABLE);
        constants.put("STATUS_CALIBRATION_CHANGED", IALocationManager.STATUS_CALIBRATION_CHANGED);
        constants.put("STATUS_LIMITED", IALocationManager.STATUS_LIMITED);
        constants.put("STATUS_OUT_OF_SERVICE", IALocationManager.STATUS_OUT_OF_SERVICE);
        constants.put("STATUS_TEMPORARILY_UNAVAILABLE",
            IALocationManager.STATUS_TEMPORARILY_UNAVAILABLE
        );
        constants.put("CALIBRATION_EXCELLENT", IALocationManager.CALIBRATION_EXCELLENT);
        constants.put("CALIBRATION_GOOD", IALocationManager.CALIBRATION_GOOD);
        constants.put("CALIBRATION_POOR", IALocationManager.CALIBRATION_POOR);
        return constants;
    }
}
