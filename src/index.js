import { NativeModules, NativeEventEmitter } from "react-native";

import eventsInitializer from "./NativeEventsInitializer";

const { IndoorAtlas: NativeIndoorAtlas } = NativeModules;

const NOT_FOUND = -1;

const LOCATION_CHANGED = "locationChanged";
const STATUS_CHANGED = "statusChanged";
const REGION_ENTER = "regionEnter";
const REGION_EXIT = "regionExit";

/**
 * @typedef {string} EventName
 * Name of the event
 */

/**
 * @typedef {Object} EventsList
 * @property {EventName} LOCATION_CHANGED - user location changed event
 * @property {EventName} STATUS_CHANGED - IA connection status changed event
 * @property {EventName} REGION_ENTER - user entered new region
 * @property {EventName} REGION_EXIT - user exited a region
 */

/**
 * @type {EventsList}
 */
const EVENTS = {
    LOCATION_CHANGED,
    STATUS_CHANGED,
    REGION_ENTER,
    REGION_EXIT,
};

/**
 * @typedef {number} StatusCode
 * Indoor Atlas location service status code
 */

/**
 * @typedef {Object} StatusList
 * @property {StatusCode} AVAILABLE - Location service running normally.
 * @property {StatusCode} CALIBRATION_CHANGED - Calibration Quality Indicator.
 * @property {StatusCode} LIMITED - Location service is running but with limited accuracy and functionality.
 * @property {StatusCode} OUT_OF_SERVICE - Location service is not available and the condition is not expected to
 * resolve itself soon.
 * @property {StatusCode} TEMPORARILY_UNAVAILABLE - Location service temporarily unavailable.
 */

/**
 * @type {StatusList}
 */
const STATUS = {
    AVAILABLE: NativeIndoorAtlas.STATUS_AVAILABLE,
    CALIBRATION_CHANGED: NativeIndoorAtlas.STATUS_CALIBRATION_CHANGED,
    LIMITED: NativeIndoorAtlas.STATUS_LIMITED,
    OUT_OF_SERVICE: NativeIndoorAtlas.STATUS_OUT_OF_SERVICE,
    TEMPORARILY_UNAVAILABLE: NativeIndoorAtlas.STATUS_TEMPORARILY_UNAVAILABLE,
};

/**
 * @typedef {number} CalibrationCode
 * Indoor Atlas location calibration code
 */

/**
 * @typedef {Object} CalibrationCodeList
 * @property {CalibrationCode} EXCELLENT - Calibration is not required.
 * @property {CalibrationCode} GOOD - Calibration is acceptable, but recommended.
 * @property {CalibrationCode} POOR - Calibration is required.
 */

/**
 * @type {CalibrationCodeList}
 */
const CALIBRATION = {
    EXCELLENT: NativeIndoorAtlas.CALIBRATION_EXCELLENT,
    GOOD: NativeIndoorAtlas.CALIBRATION_GOOD,
    POOR: NativeIndoorAtlas.CALIBRATION_POOR,
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
            // debug: [],
        };
        for (const key in EVENTS) { // eslint-disable-line guard-for-in
            this._listeners[EVENTS[key]] = [];
        }

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
    STATUS,
    CALIBRATION,
};
