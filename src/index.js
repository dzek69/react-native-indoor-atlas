import { NativeModules, NativeEventEmitter } from "react-native";

import eventsInitializer from "./NativeEventsInitializer";

const { IndoorAtlas: NativeIndoorAtlas } = NativeModules;

const NOT_FOUND = -1;

class IndoorAtlas {
    constructor() {
        this._listeners = {
            locationChanged: [],
            statusChanged: [],
            // debug: [],
        };

        this.on = this.addListener;
        this.off = this.removeListener;

        const indoorAtlasEventEmitter = new NativeEventEmitter(NativeIndoorAtlas);

        Object.keys(this._listeners).forEach(eventName => {
            const propertyName = "_" + eventName + "Subscription";
            const listener = this._handleIndoorAtlasEvent.bind(this, eventName);
            this[propertyName] = indoorAtlasEventEmitter.addListener(eventName, listener);
        });
    }

    _handleIndoorAtlasEvent(eventName, data) {
        const array = this._listeners[eventName];
        if (!array) {
            return;
        }

        array.forEach(listener => listener(data));
    }

    addListener(eventName, listener) {
        const array = this._listeners[eventName];
        if (!array) {
            throw new Error("Unknown event");
        }
        if (typeof listener !== "function") {
            throw new TypeError("Listener must be a function");
        }
        if (array.includes(listener)) {
            throw new Error("Listener already attached");
        }

        array.push(listener);
        eventsInitializer.increase();
    }

    removeListener(eventName, listener) {
        const array = this._listeners[eventName];
        if (!array) {
            throw new Error("Unknown event");
        }
        const index = array.indexOf(listener);
        if (index === NOT_FOUND) {
            throw new Error("Listener wasn't attached or is already removed");
        }

        array.splice(index, 1);
        eventsInitializer.decrease();
    }

    destroy() {
        Object.entries(this._listeners).forEach(([eventName, eventArray]) => {
            const subscriberPropertyName = "_" + eventName + "Subscription";
            this[subscriberPropertyName].remove();

            eventsInitializer.decrease(eventArray.length);
            eventArray.length = 0; // eslint-disable-line no-param-reassign
        });
    }
}

export default IndoorAtlas;
