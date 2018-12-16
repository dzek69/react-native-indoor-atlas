package com.dzek69.indooratlas;

import android.os.Bundle;
import android.support.annotation.Nullable;

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

public class RNIndoorAtlasModule extends ReactContextBaseJavaModule {
    // @TODO expose status constants
    private IALocationManager locationManager;
    private IALocationListener locationListener;
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
                        IARegion region = location.getRegion();
                        if (region != null) {
                            params.putString("locationName", location.getRegion().getName());
                        }
                        sendEvent(
                            getReactApplicationContext(), "locationChanged", params
                        );
                    }

                    @Override
                    public void onStatusChanged(String s, int code, Bundle bundle) {
                        WritableMap params = Arguments.createMap();
                        params.putDouble("status", code);
                        sendEvent(getReactApplicationContext(), "statusChanged", params);
                    }
                };

                locationManager.requestLocationUpdates(
                    IALocationRequest.create(), locationListener
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
    public String getName()

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
