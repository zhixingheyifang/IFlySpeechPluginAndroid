1.调用方式
xunfeiListenSpeaking.startListen(function (data) {
            alert(data);
        }, function (data) {
            alert(data);
        },true,true);
2.key有效期为15天，过后要重新取官网下载sdk替换包