import { NativeModules } from "react-native";

const { IndoorAtlas } = NativeModules;

class NativeEventsInitializer {
    constructor() {
        this._total = 0;
    }

    increase(count = 1) {
        this._total += count;
        if (this._total === 1) {
            IndoorAtlas.listen();
        }
    }

    decrease(count = 1) {
        this._total -= count;
        if (this._total === 0) {
            IndoorAtlas.stop();
        }
    }
}

const instance = new NativeEventsInitializer();

export default instance;
