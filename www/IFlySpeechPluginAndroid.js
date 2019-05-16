var exec = require('cordova/exec');

exports.startListen = function (success, error,isShowDialog,isShowPunc) {
    exec(success, error, 'IFlySpeechPluginAndroid', 'startListen', [isShowDialog,isShowPunc]);
};
