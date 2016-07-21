var database = require('./database');
var index = require('./index');
var app = index.app;
var http = index.http;
var chat = require('./chat');
var io = chat.io;
var DEBUG = require('./debug').DEBUG;
var redisOption = require('./redisOption');

app.post('/updateNickname', function(request, response){
    console.log('/updateNickname');
	var myId = request.body.myId;
	var token = request.body.token;
    var nickname = request.body.nickname;
	
    if(DEBUG){
        console.log('/userinfo: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/userinfo: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.updateUsername(userid, nickname, function(res){
                console.log('update nickname success');
                response.writeHead(200);
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

app.post('/updateHeap', function(request, response){
    console.log('/updateHeap');
	var myId = request.body.myId;
	var token = request.body.token;
    var heap = request.body.heap;
	
    if(DEBUG){
        console.log('/userinfo: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/userinfo: myId: ' + myId);
    }
    if(token === undefined || myId === undefined){
        console.log('no token:' + token);
        response.writeHead(403);
        response.end('{"res":"fail"}');
        return;
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.updateHeap(userid, heap, function(res){
                console.log('update heap success');
                response.writeHead(200);
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
