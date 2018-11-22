# react-native-indoor-atlas

## Basic Usage

```javascript
import React from "react";
import IndoorAtlas, { EVENTS } from "react-native-indoor-atlas";

class MyComponent extends React.Component {
    componentDidMount() {
        // create new instance
        this.ia = new IndoorAtlas();
        
        // attach listeners
        ia.addListener(EVENTS.LOCATION_CHANGED, this.handleLocationChange);
        ia.addListener(EVENTS.STATUS_CHANGED, this.handleStatusChange);
    }
    
    componentWillUnmount() {
        // destroy instance, which will deattach listeners automatically
        this.ia.destroy();
    }
    
    // render ...
}
```

## Available events list, description and data they provides

### `EVENTS.LOCATION_CHANGED`

Triggered when Indoor Atlas detects that user location is changed. 

```javascript
{
    lat: number, // latitude, ie: 50.033464
    lng: number, // longitude, ie: 22.035206
    accuracy: number, // accuracy in meters, ie: 2; 2.30; 3.15
    locationName: string, // matched location/floor/plan name
}
```

### `EVENTS.STATUS_CHANGED`

Triggered when Indoor Atlas connection status changes. See IA documentation for details.
In future releases status codes will be exposed as constants. 

```javascript
{
    status: number, // status code from Indoor Atlas
}
```
