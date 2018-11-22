# react-native-indoor-atlas

Unofficial Indoor Atlas bridge for React Native projects. W.I.P.

## Install

> This library is meant to be used with recent versions of React Native. It wasn't tested on every version so minimal
version isn't known. It works fine on `0.57.x`.

### Installation steps for Android:

- `npm i react-native-indoor-atlas --save`
- `react-native link`
- Edit `android/build.gradle`, locate `allprojects > repositories`, add there:
```
maven {
    url "http://indooratlas-ltd.bintray.com/mvn-public"
}
```
- Edit `android/app/build.gradle`, locate `dependencies`, add there:
```
implementation "com.indooratlas.android:indooratlas-android-sdk:2.8.3"
```
- Edit `android/app/src/main/AndroidManifest.xml`, locate `manifest > application`, add there:
```xml
<meta-data
    android:name="com.indooratlas.android.sdk.API_KEY"
    android:value="YOUR_API_KEY" />
<meta-data
    android:name="com.indooratlas.android.sdk.API_SECRET"
    android:value="YOUR_API_SECRET" />
```

## Usage

Refer to [documentation](https://dzek69.github.io/react-native-indoor-atlas/) and
[usage examples](https://dzek69.github.io/react-native-indoor-atlas/tutorial-Usage.html).

## Features

> Warning: This project currently supports Android only. iOS support will come, contributions are welcomed.

- get notified when user detected location changes
- get notified when Indoor Atlas connection status changes

## License

MIT
