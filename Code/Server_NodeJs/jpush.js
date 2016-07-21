var JPush = require("jpush-sdk");
var client = JPush.buildClient('f43ecf0e523b1bb8fcdbe4c7', '9838d0e6f6dd57b1d76c4866', null, true);

// 推送
client.push().setPlatform(JPush.ALL)
    .setAudience(JPush.ALL)
    .setNotification('Hi, JPush')
    .send(function(err, res) {
        if (err) {
            console.log(err.message);
        } else {
            console.log('Sendno: ' + res.sendno);
            console.log('Msg_id: ' + res.msg_id);
        }
    });

var msg_id;

// 统计
client.getReportReceiveds(msg_id, function(err, res) {
    if (err) {
        console.log(err.message);
    } else {
        for (var i=0; i<res.length; i++) {
            console.log('Android: ' + res[i].android_received);
            console.log('IOS: ' + res[i].ios_apns_sent);
            console.log(res[i].msg_id);
        }
    }
});