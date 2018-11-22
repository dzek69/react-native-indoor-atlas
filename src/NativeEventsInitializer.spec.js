import instance from "./NativeEventsInitializer";

import createSpy from "../test/utils/createSpy";

describe("Native Events Initializer", () => {
    const IAMock = {
        listen: createSpy(),
        stop: createSpy(),
    };

    before(() => {
        instance.__Rewire__("IndoorAtlas", IAMock);
    });

    beforeEach(() => {
        IAMock.listen.reset();
        IAMock.stop.reset();
    });

    after(() => {
        instance.__ResetDependency__("IndoorAtlas");
    });

    it("starts listening on first event", () => {
        instance.increase();
        IAMock.listen.calls.must.have.length(1);
        instance.increase();
        IAMock.listen.calls.must.have.length(1);
        instance.increase(2);
        IAMock.listen.calls.must.have.length(1);

        instance.decrease(4); // 0
        IAMock.listen.calls.must.have.length(1);

        instance.increase(2);
        IAMock.listen.calls.must.have.length(2);
        instance.decrease(2);
    });

    it("stops listening on last clear event", () => {
        instance.increase();
        IAMock.stop.calls.must.have.length(0);
        instance.increase();
        IAMock.stop.calls.must.have.length(0);
        instance.decrease();
        IAMock.stop.calls.must.have.length(0);

        instance.decrease();
        IAMock.stop.calls.must.have.length(1);

        instance.increase(2);
        IAMock.stop.calls.must.have.length(1);
        instance.decrease(2);
        IAMock.stop.calls.must.have.length(2);
    });
});
