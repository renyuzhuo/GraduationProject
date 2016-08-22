var database = require('./database');
var index = require('./index');
var app = index.app;
var http = index.http;
var chat = require('./chat');
var io = chat.io;
var DEBUG = require('./debug').DEBUG;
var redisOption = require('./redisOption');
var moment = require('moment');

app.post('/friends', function(request, response){
    console.log('/friends');
	//从客户端获取用户名密码
	var myId = request.body.myId;
	var token = request.body.token;
	
    if(DEBUG){
        console.log('/friends: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/friends: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.getFriendListStateAll(myId, function(res){
                response.writeHead(200);
                response.end('{"res":"success", "friends":' + JSON.stringify(res) + '}');
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

app.post('/friend', function(request, response){
    console.log('/friend');
    
    //从客户端获取用户名密码
	var myId = request.body.myId;
    var sid = request.body.sid;
	var token = request.body.token;
	
    if(DEBUG){
        console.log('/friend: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/friend: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.getUserInfoByID(sid, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end('{"res":"fail"');
                    return;
                }else{
                    response.writeHead(200);
                    response.end('{"res":"success", "userinfos":' + JSON.stringify(res) + '}');
                }
            });
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/userinfo', function(request, response){
    console.log('/userinfo');
    
    //从客户端获取用户名密码
	var myId = request.body.myId;
    var phone = request.body.phone;
	var token = request.body.token;
	
    if(DEBUG){
        console.log('/userinfo: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/userinfo: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.getUserInfoByPhone(phone, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end('{"res":"fail"');
                    return;
                }else{
                    response.writeHead(200);
                    response.end('{"res":"success", "userinfos":' + JSON.stringify(res) + '}');
                }
            });
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/addFriend', function(request, response){
    console.log('/addFriend');
    
    //从客户端获取用户名密码
	var myId = request.body.myId;
    var sid = request.body.sid;
	var token = request.body.token;
    var remark = request.body.remark;
    var state = request.body.state;
	
    if(DEBUG){
        console.log('/addFriend: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/addFriend: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            var time = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
            if(state == 'invite'){
                database.insertIntoFriend(myId, sid, time, state, remark, function(res){
                    if(res === undefined){
                        response.writeHead(401);
                        response.end('{"res":"fail"');
                        return;
                    }else{
                        response.writeHead(200);
                        response.end('{"res":"success"}');
                        console.log('insert:' + res);
                    }
                });
            }else{
                database.updateFriend(myId, sid, state, remark, function(res){
                    if(res === undefined){
                        response.writeHead(401);
                        response.end('{"res":"fail"');
                        return;
                    }else{
                        response.writeHead(200);
                        response.end('{"res":"success"}');
                        console.log('update:' + res);
                    }
                });
            }
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/updateRemark', function(request, response){
    console.log('/updateRemark');
    
	var myId = request.body.myId;
    var remark = request.body.remark;
	var token = request.body.token;
    var chatFriendId = request.body.chatFriendId;
    
    if(DEBUG){
        console.log('/updateRemark: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/updateRemark: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.updateRemark(chatFriendId, remark, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end('{"res":"fail"');
                    return;
                }else{
                    response.writeHead(200);
                    console.log(JSON.stringify(res));
                    response.end('{"res":"success", "friends":' + JSON.stringify(res) + '}');
                }
            });
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/invite', function(request, response){
    console.log('/invite');

	var myId = request.body.myId;
	var token = request.body.token;
	
    if(DEBUG){
        console.log('/friends: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/friends: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.getInviteFriends(myId, function(res){
                response.writeHead(200);
                response.end('{"res":"success", "friends":' + JSON.stringify(res) + '}');
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

app.post('/submitFriend', function(request, response){
    console.log('/submitFriend');

	var myId = request.body.myId;
	var token = request.body.token;
    var flistid = request.body.flistid;
	
    if(DEBUG){
        console.log('/friends: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/friends: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            var time = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
            database.submitFriend(flistid, time, function(res){
                if(res === true){
                    response.writeHead(200);
                    response.end('{"res":"success"}');
                    return;
                }
            });
        }else{
            console.log('no this token:' + token);
            response.writeHead(403);
            response.end('{"res":"fail"}');
            return;
        }
    });
});

app.post('/deleteFriend', function(request, response){
    console.log('/deleteFriend');

	var myId = request.body.myId;
	var token = request.body.token;
    var toId = request.body.toId;
    var chatFriendListId = request.body.chatFriendListId;
	
    if(DEBUG){
        console.log('/deleteFriend: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/deleteFriend: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.deleteFriend(chatFriendListId, toId, myId, function(res){
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