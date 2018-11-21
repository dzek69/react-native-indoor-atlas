package com.reactlibrary;

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

public class IndoorAtlasModule extends ReactContextBaseJavaModule {
    // @TODO expose status constants
    private IALocationManager locationManager;
    private IALocationListener locationListener;
    private boolean listening;

    public IndoorAtlasModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    private void sendEvent(ReactContext context, String eventName, @Nullable WritableMap params) {
        context
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    @ReactMethod
    public void listen() {
        if (listening) {
            return;
        }

        listening = true;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                locationManager = IALocationManager.create(getReactApplicationContext());
                locationListener = new IALocationListener() {
                    @Override
                    public void onLocationChanged(IALocation location) {
                        WritableMap params = Arguments.createMap();
                        params.putDouble("lat", location.getLatitude());
                        params.putDouble("lng", location.getLongitude());
                        params.putDouble("accuracy", location.getAccuracy());
                        params.putString("locationName", location.getRegion().getName());
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
                }

                locationManager.requestLocationUpdates(
                    IALocationRequest.create(), locationListener
                );
            }
        };
        getCurrentActivity().runOnUiThread(task);
    }

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
}
