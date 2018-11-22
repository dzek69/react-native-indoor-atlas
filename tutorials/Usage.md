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
