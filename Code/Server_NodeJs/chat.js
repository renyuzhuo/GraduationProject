var database = require('./database');
var index = require('./index');
var app = index.app;
var http = index.http;
var redisOption = require('./redisOption');
var io = require('socket.io')(http);
var moment = require('moment');
var DEBUG = require('./debug').DEBUG;

var clients = [];
var sockets = [];

console.log('init chat.js');

// namespace为chat
io = io.of('/chat');
io.on('connection', function(socket){
	console.log('connection');
    
    // 用户注册在线
	socket.on('online', function(msg){
		console.log('new user: ' + msg + '; socket id: ' + socket.id);
		
        var token = msg;
        
        redisOption.getUserIdByToken(token, function (userid) {
            if(userid === undefined){
                console.log('no this token:' + token);
                io.emit('err_' + token, 'no this token, please login');
                return;
            }else{
                console.log('somebody online');
            }
        })
	});
    
    socket.on('disconnect', function(msg){
        console.log('disconnect and do nothing');
    });
    
    socket.on('private', function(message) {
        var token = message['token'];
		
        redisOption.getUserIdByToken(token, function(userid) {
            if(userid === undefined){
                // 没有这个token
                console.error('chat -> no this token in map');
                io.emit('err_' + token, 'no this token, please login');
                return;
            }
            
            var fromuser = message['fromuser'];
            if(fromuser == userid){
                var timeDate = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
                message['time'] = timeDate;
                console.log('private: ' + message['fromuser'] + ' --> ' + message['touser'] + ' : ' + message['message']);
                
                database.insertMessage(message, function(res){
                    var touser = message['touser'];
                    var ackId = message['id'];
                    message['id'] = res['insertId'];
                    
                    delete(message['token']);
                    message['fromstate'] = 'read';
                    
                    io.emit(message['touser'] + '', message);
                    io.emit('ack_' + fromuser + '', {'messageId' : ackId, 'serverTime' : timeDate, 'newMessageId' : message['id']});
                });

            }else{
                console.error('who? mapwho: ' + fromuser + ', fromuser: ' + fromuser);
                io.emit('err', 'mapuser != fromuser');
                return;
            }
        });
    });
    
    socket.on('read', function(receipt) {
		var token = receipt['token'];
		
        redisOption.getUserIdByToken(token, function(userID){
            if(userID === undefined){
                // 没有这个token
                console.error('chat -> no this token in map');
                io.emit('err_' + token, 'no this token, please login');
                return;
            }
            
            var messageId = receipt['id'];
            
            console.log('user ' + userID + ' read ' + messageId);		
                
            database.readMessage(userID, messageId, function(res){
                console.log('read message ' + messageId + 'finish');
            });
        });
	});
	
    socket.on('history', function(userinfo){
        var token = userinfo['token'];
        var userid = userinfo['id'];
        redisOption.getUserIdByToken(token, function(userID) {
            if(userID === undefined){
                console.log('history no this token: ' + userid + ', token: ' + token);
                io.emit('err_' + token, 'no this token, pleanse login');
                return;
            }else{
                if(userid === userID){
                    database.getUnreadMessage(userid, function(res) {
                        if(res === undefined){
                            console.error('server err');
                            return;
                        }else{
                            io.emit('history_' + userid, res);
                            if(DEBUG){
                                console.log(res);
                            }
                        }
                    })
                }
            }
        })
    });
    
    socket.on('team', function(message) {
        var token = message['token'];
		
        redisOption.getUserIdByToken(token, function(userid) {
            if(userid === undefined){
                // 没有这个token
                console.error('chat -> no this token in map');
                io.emit('err_' + token, 'no this token, please login');
                return;
            }
            
            var fromuser = message['fromuser'];
            if(fromuser == userid){
                var timeDate = moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss');
                message['time'] = timeDate;
                console.log('private: ' + message['fromuser'] + ' --> ' + message['toteam'] + ' : ' + message['message']);
                
                database.insertTeamMessage(message, function(res){
                    var toteam = message['toteam'];
                    var ackId = message['id'];
                    message['id'] = res['insertId'];
                    
                    delete(message['token']);
                    message['fromstate'] = 'read';
                    message['ackId'] = fromuser + '_' + ackId;
                    io.emit('team_' + message['toteam'], message);
                });

            }else{
                console.error('who? mapwho: ' + fromuser + ', fromuser: ' + fromuser);
                io.emit('err', 'mapuser != fromuser');
                return;
            }
        });
    });
    
    socket.on('teamUnreadMessage', function(teamUnreadMessage) {
        var token = teamUnreadMessage['token'];
        var teamid = teamUnreadMessage['teamid'];
        var messageid = teamUnreadMessage['messageid'];
        var myid = teamUnreadMessage['myid'];
        
		
        redisOption.getUserIdByToken(token, function(userid) {
            if(userid === undefined){
                // 没有这个token
                console.error('chat -> no this token in map');
                io.emit('err_' + token, 'no this token, please login');
                return;
            }
            
            if(myid == userid){
                database.getTeamUnreadMessage(teamid, messageid, function(res){
                    io.emit('teamMessage_' + myid, res);
                });

            }else{
                console.error('who? mapwho: ' + fromuser + ', fromuser: ' + fromuser);
                io.emit('err', 'mapuser != fromuser');
                return;
            }
        });
    });
    
});

exports.io = io;