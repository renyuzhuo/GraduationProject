var database = require('./database');
var index = require('./index');
var app = index.app;
var http = index.http;
var chat = require('./chat');
var io = chat.io;
var DEBUG = require('./debug').DEBUG;
var redisOption = require('./redisOption');
var moment = require('moment');

app.post('/feedback', function(request, response){
    console.log('/feedback');
	//从客户端获取用户名密码
	var myId = request.body.myId;
	var token = request.body.token;
    var message = request.body.message;
	
    if(DEBUG){
        console.log('/feedback: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/feedback: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            var timeDate = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
            database.insertFeedback(myId, message, timeDate, function(res){
                response.writeHead(200);
                console.log(res);
                response.end('{"res":"success"}');
                return;
            });
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/getAllFeedback', function(request, response){
    console.log('/getAllFeedback');
    var maxid = request.body.maxid;
    database.getAllFeedback(maxid, function(res){
        response.writeHead(200);
        //console.log(res);
        response.end('{"res":"success", "feedbacks":' + JSON.stringify(res) + '}');
        return;
    });
});

