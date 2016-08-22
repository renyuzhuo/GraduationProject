var moment = require('moment');
var index = require('./index');
var app = index.app;
var database = require('./database');
var DEBUG = require('./debug').DEBUG;

TopClient = require('./topClient').TopClient;

var client = new TopClient({
    'appkey': '23337189',
    'appsecret': '8805062e1d02b9c579591249f43ef0bf',
    'REST_URL': 'http://gw.api.taobao.com/router/rest'
});

var code, time;

app.post('/code', function(request, response){
    var phoneNumber = request.body.phoneNumber;
    code = (moment().utcOffset('+08:00') + '').toString().substr(7,6);
    time = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
    database.code(phoneNumber, time, code, function(res){
        if(res === undefined){
            console.log('err insert into chat_register with code: ' + code + ', time:' + time + ', phoneNumber: ' + phoneNumber);
        }
        console.log('/register');
        client.execute('alibaba.aliqin.fc.sms.num.send', {
            'extend':'123456',
            'sms_type':'normal',
            'sms_free_sign_name':'注册验证',
            'sms_param':'{\"code\":\"' + code + '\",\"product\":\"Chat\"}',
            'rec_num':phoneNumber,
            'sms_template_code':'SMS_7270716'
        }, function(error, result) {
            if(error){
                console.log(error);
            }else{
                console.log(result);
                response.writeHead(200);
                response.end('{"res":"success"}');
            }
        });
    });
});

app.post('/register', function(request, response) {
    var username = request.body.username;
    var password = request.body.password;
    var nickname = request.body.nickname;
    var type = request.body.type;
    if(nickname === undefined){
        nickname = '';
    }
    if(type === undefined){
        type = 1;
    }
    database.register(username, password, nickname, type, function(id) {
        if(id === undefined){
            response.writeHead(500);
			response.end('{"res":"server err"}');
        }else{
            console.log(id);
            response.writeHead(200);
			response.end('{"res":"success", "id":' + id + '}');
        }
    })
});

app.post('/identifyingCode', function(request, response) {
    var phoneNumber = request.body.phoneNumber;
    var identifyingCode = request.body.identifyingCode;
    database.identifyingCode(phoneNumber, identifyingCode, function(res) {
        response.writeHead(200);
        response.end('{"res":"success"}');
    });
});

app.post('/validate', function(request, response) {
    var phone = request.body.phone;
    database.validateUsername(phone, function(res) {
        if(res === undefined){
            response.writeHead(500);
            response.end();
        }else{
            if(res[0] === undefined){
                response.writeHead(200);
                response.end('{"res":"success"}');
            }else{
                response.writeHead(200);
                response.end('{"res":"error"}');
            }
        }
    });
});
