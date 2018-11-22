import { NativeModules, NativeEventEmitter } from "react-native";

import eventsInitializer from "./NativeEventsInitializer";

const { IndoorAtlas: NativeIndoorAtlas } = NativeModules;

const NOT_FOUND = -1;

const LOCATION_CHANGED = "locationChanged";
const STATUS_CHANGED = "statusChanged";

/**
 * @typedef {string} EventName
 * Name of the event
 */

/**
 * @typedef {Object} EventsList
 * @property {EventName} LOCATION_CHANGED - user location changed evenet
 * @property {EventName} STATUS_CHANGED - IA connection status changed evenet
 */

/**
 * @type {EventsList}
 */
const EVENTS = {
    LOCATION_CHANGED,
    STATUS_CHANGED,
};

/**
 * Indoor Atlas events handling class
 */
class IndoorAtlas {
    /**
     * Constructs new instance. Takes no arguments.
     */
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

    /**
     * Registers new event listener
     *
     * @param {EventName} eventName
     * @param {function} listener
     * @returns {void}
     */
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

    /**
     * Unregisters event listener
     *
     * @param {EventName} eventName
     * @param {function} listener
     * @returns {void}
     */
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

    /**
     * Destroys instance. Unregisters all listeners automatically.
     *
     * @returns {void}
     */
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
export {
    EVENTS,
};
