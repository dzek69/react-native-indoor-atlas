const createSpy = () => {
    const fn = function(...args) {
        fn.calls.push(args);
    };
    fn.calls = [];
    fn.reset = function reset() {
        this.calls.length = 0;
    };
    return fn;
};

export default createSpy;
