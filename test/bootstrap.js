import mockery from "mockery";

mockery.registerSubstitute("react-native", "../test/stubs/node_modules/react-native/index.js");

mockery.enable({
    warnOnReplace: false,
    warnOnUnregistered: false,
});
