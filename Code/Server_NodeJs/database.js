var mysql = require('mysql');
var databaseconfig = require('./databaseconfig');
var DEBUG = require('./debug').DEBUG;

var conn;

function handleDisconnect() {
        conn = mysql.createConnection({
            host : databaseconfig.host,
            port : databaseconfig.port,
            user : databaseconfig.user,
            password : databaseconfig.password,
            database : databaseconfig.database,
            charset : 'UTF8MB4_GENERAL_CI'
        });
        conn.on('error', function(err){
                console.log('mysql link err,reconnect');
                if(err.code === 'PROTOCOL_CONNECTION_LOST') {
                        handleDisconnect();
                } else {
                        throw err;
                }
        });

        conn.connect(function(err) {
                if(err) {
                        console.log("err when connecting mysql");
                        setTimeout(handleDisconnect, 2000);
                }else{
                    console.log('success connected mysql');
                }
        });
}

handleDisconnect();

var sql;

// 登录
function login(username, password, callback){
    sql = 'select id, username, password, nickname, type, heap ' + 
        'from chat_userinfo ' +
        'where username=\'' + username + '\' and password=\'' + password + '\' ' +
        'and (type=0 or type=1)';
    
    if(DEBUG){
        console.log('loginSQL: ' + sql);
    }
	
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.login()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//朋友列表
function getFriendListStateAll(friend_me_id, callback){
    sql = 'select a.id myFriendListId, a.friend_you yourId, a.time time, ' + 
        'a.state state, a.remark remark, b.username yourUsername, b.nickname yourNickname, ' +
        'b.type yourType, b.heap heap ' +
            'from chat_friend a, chat_userinfo b ' +
            'where a.friend_you=b.id and a.friend_me=' + friend_me_id;
    
    if(DEBUG){
        console.log('getFriendListStateAll: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getFriendListStateAll()');
		}else{
			callback(res);
		}
	});
}

//通过状态查询朋友列表
function getFriendListByState(friend_me_id, state, callback){
    sql = 'select a.id myFriendListId, a.friend_you yourId, a.time time, ' + 
        'a.state state, a.remark remark, b.username yourUsername, b.nickname yourNickname, ' +
        'b.type yourType, b.heap heap ' +
            'from chat_friend a, chat_userinfo b ' +
            'where a.friend_you=b.id and a.friend_me=' + friend_me_id + ' and a.state=\'' + state + '\'';
    
    if(DEBUG){
        console.log('getFriendListByState: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getFriendListByState()');
			throw err;
		}else{
			callback(res);
		}
	});
}

function getInviteFriends(myId, callback){
	sql = 'select a.id myFriendListId, a.friend_you yourId, a.time time, ' + 
        'a.state state, a.remark remark, b.username yourUsername, b.nickname yourNickname, ' +
        'b.type yourType, b.heap heap ' +
            'from chat_friend a, chat_userinfo b ' +
            'where a.friend_me=b.id and a.friend_you=' + myId + ' and a.state=\'invite\'';
    
    if(DEBUG){
        console.log('getFriendListByState: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getFriendListByState()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//通过状态查询朋友列表
function getFriendByFriendId(friendId, callback){
    sql = 'select a.id myFriendListId, a.friend_you yourId, a.time time, ' + 
        'a.state state, a.remark remark, b.username yourUsername, b.nickname yourNickname, ' +
        'b.type yourType, b.heap heap ' +
            'from chat_friend a, chat_userinfo b ' +
            'where a.friend_you=b.id and a.id=' + friendId + '';
    
    if(DEBUG){
        console.log('getFriendByFriendId: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getFriendByFriendId()');
			throw err;
		}else{
			callback(res);
		}
	});
}

// 新消息
function insertMessage(message, callback){
	while(message['message'].indexOf('\\') >= 0){
        message['message'] = message['message'].replace('\\', '[chat1]');
    }
	while(message['message'].indexOf('\'') >= 0){
        message['message'] = message['message'].replace('\'', '[chat2]');
    }
	
	sql = 'insert into chat_friend_message(fromuser, touser, message, time, type, path, fromstate, tostate) values(' +
		'\'' + message['fromuser'] + '\', ' +
		'\'' + message['touser'] + '\', ' +
		'\'' + message['message'] + '\', ' +
		'\'' + message['time'] + '\', ' +
		'\'' + message['type'] + '\', ' +
		'\'' + message['path'] + '\', ' +
		'\'read\', ' +
		'\'unread\'' +
		')';
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.insertMessage()');
			throw err;
		}else{
				while(message['message'].indexOf('[chat1]') >= 0){
					message['message'] = message['message'].replace('[chat1]', '\\');
				}
				while(message['message'].indexOf('[chat2]') >= 0){
					message['message'] = message['message'].replace('[chat2]', '\'');
				}
			callback(res);
		}
	});
}

// 已读消息回执
function readMessage(userID, messageId, callback){
    sql = 'update chat_friend_message set tostate=\'read\' where touser=' + userID + ' and id=' + messageId;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.readMessage()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//验证码发送
function code(phoneNumber, time, code, callback){
    sql = 'insert into chat_register(phoneNumber, time, code) values(\'' + phoneNumber + '\', \'' + time + '\', \'' + code + '\')';
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.code()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

//注册
function register(username, password, nickname, type, callback) {
    sql = 'insert into chat_userinfo(username, password, nickname, type) values(' 
    + '\'' + username + '\','
    + '\'' + password + '\','
    + '\'' + nickname + '\','
    + '' + type + ''
    + ')';
    console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.register()');
			callback(undefined);
		}else{
			callback(res['insertId']);
		}
	});
}

//验证码校验
function identifyingCode(phoneNumber, identifyingCode, callback) {
    sql = 'select * from chat_register where phoneNumber=\'' + phoneNumber + '\' and code=\'' + identifyingCode + '\'';
    console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.identifyingCode()');
			callback(undefined);
		}else{
			console.log(res);
            callback(res);
		}
	});
}

//查找用户是否存在
function validateUsername(phone, callback) {
	sql = 'select * from chat_userinfo where username=\'' + phone + '\'';    
    if(DEBUG){
        console.log('getUserInfoByID: ' + sql);
    }
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.validateUsername()' + err);
		}else{
			callback(res);
		}
	});
}

//通过id查询用户信息
function getUserInfoByID(userid, callback){
    if(userid === undefined){
        callback(undefined);
    }
    sql = 'select * from chat_userinfo where id=' + userid;
    
    if(DEBUG){
        console.log('getUserInfoByID: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getUserInfoByID()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//通过电话查询用户信息
function getUserInfoByPhone(phone, callback){
    if(phone === undefined){
        callback(undefined);
    }
    sql = 'select * from chat_userinfo where username like \'' + phone + '%\'';
    
    if(DEBUG){
        console.log('getUserInfoByPhone: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getUserInfoByPhone()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//插入朋友
function insertIntoFriend(myId, sid, time, state, remark, callback){
	
	sql = 'select * from chat_friend where friend_me=' + myId + ' and friend_you=' + sid;
	
	if(DEBUG){
        console.log('insertIntoFriend: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.insertIntoFriend()');
			throw err;
		}else{
			if(res===undefined || res[0]===undefined){
				sql = 'insert into chat_friend(friend_me, friend_you, time, state, remark) values(' + myId + ', ' + sid+ ', \'' + time + '\', \'' + state + '\', \'' + remark + '\')';
					
				if(DEBUG){
					console.log('insertIntoFriend: ' + sql);
				}
				
				conn.query(sql, function(err, res, filds){
					if(err){
						console.error('err:: database.insertIntoFriend()');
					}else{
						callback(res);
					}
				});
			}else{
				console.log('好友申请已经存在了');
				callback(undefined);
			}
		}
	});

}

//修改朋友
function updateFriend(myId, sid, state, remark, callback){
	if(state == 'submit'){
		sql = 'update chat_friend set state=\'' + state + '\' where friend_me=' + sid + ' and friend_you=' + myId + '';
		if(DEBUG){
			console.log('updateFriend: ' + sql);
		}
		
		conn.query(sql, function(err, res, filds){
			if(err){
				console.error('err:: database.updateFriend()');
				throw err;
			}else{
				sql = 'insert into chat_friend(friend_me, friend_you, time, state, remark) values(' + myId + ', ' + sid+ ', \'' + time + '\', \'' + state + '\', \'' + remark + '\')';
				conn.query(sql, function(err, res, filds){
					if(err){
						console.error('err:: database.updateFriend()');
						throw err;
					}else{
						callback(res);
					}
				});
			}
		});
	}else{
		sql = 'update chat_friend set state=\'' + state + '\' where friend_me=' + sid + ' and friend_you=' + myId + '';
		if(DEBUG){
			console.log('updateFriend: ' + sql);
		}
		
		conn.query(sql, function(err, res, filds){
			if(err){
				console.error('err:: database.updateFriend()');
				throw err;
			}else{
				callback(res);
			}
		});
	}
	
}

//获取未读消息
function getUnreadMessage(userid, callback){
    if(userid === undefined){
        callback(undefined);
    }
    sql = 'select id, fromuser, touser, message, date_format(time,\'%Y-%m-%d %H:%i:%s\') as time, type, path, fromstate, tostate from chat_friend_message where touser=' + userid + ' and tostate=\'unread\'';
    
    if(DEBUG){
        console.log('getUnreadMessage: ' + sql);
    }
    
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.getUnreadMessage()');
		}else{
			callback(res);
		}
	});
}

//获取群组
function getTeamByUserId(userId, callback) {
    if(userId === undefined){
        callback(undefined);
    }
    sql = 'select a.id roomid, a.user user, b.id teamid, b.name name, b.heap heap, b.type type from chat_room a, chat_team b where a.team=b.id and a.user=' + userId + '';
    
    if(DEBUG){
        console.log('getUnreadMessage: ' + sql);
    }
    
    conn.query(sql, function(err, res, filds) {
        if(err){
            console.error('err:: database.getTeamByUserId');
        }else{
            callback(res);
        }
    });
    
}

function getDefaultTeams(callback) {
    sql = 'select id teamid, name, heap, type from chat_team where type=0';
    
    if(DEBUG){
        console.log('getUnreadMessage: ' + sql);
    }
    
    conn.query(sql, function(err, res, filds) {
        if(err){
            console.error('err:: database.getDefaultTeams');
        }else{
            callback(res);
        }
    });
    
}

//插入群消息
function insertTeamMessage(message, callback){
	message['message'] = message['message'].replace('\\', '\\\\');
	message['message'] = message['message'].replace('\'', '\\\'');
	
	sql = 'insert into chat_team_message(fromuser, toteam, message, time, type, path) values(' +
		'\'' + message['fromuser'] + '\', ' +
		'\'' + message['toteam'] + '\', ' +
		'\'' + message['message'] + '\', ' +
		'\'' + message['time'] + '\', ' +
		'\'' + message['type'] + '\', ' +
		'\'' + message['path'] + '\'' +
		')';
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds){
		if(err){
			console.error('err:: database.insertTeamMessage()');
			throw err;
		}else{
			callback(res);
		}
	});
}

//修改用户名
function updateUsername(userid, username, callback) {
	sql = 'update chat_userinfo set nickname=\'' + username + '\' where id=' + userid;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.insertTeamMessage()');
			callback(undefined);
		}else{
			callback(res);
		}
	})
}

//修改头像
function updateHeap(userid, heap, callback) {
	sql = 'update chat_userinfo set heap=\'' + heap + '\' where id=' + userid;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.insertTeamMessage()');
			callback(undefined);
		}else{
			callback(res);
		}
	})
}

//修改备注
function updateRemark(chatFriendId, remark, callback) {
	sql = 'update chat_friend set remark=\'' + remark + '\' where id=' + chatFriendId;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.updateRemark()');
			callback(undefined);
		}else{
			getFriendByFriendId(chatFriendId, function(userinfo) {
				callback(userinfo);
			});

		}
	})
}

//插入反馈消息
function insertFeedback(myId, message, time, callback) {
	sql = 'insert into chat_feedback(userinfo_id, message, time) values(' + myId + ', \'' + message + '\', \'' + time + '\')';
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.insertFeedback()');
			callback(undefined);
		}else{
			callback(res);
		}
	})
}

//获取所有反馈消息
function getAllFeedback(maxid, callback) {
	sql = 'select a.id id, a.userinfo_id userinfo_id, a.message message, date_format(a.time,\'%Y-%m-%d %H:%i:%s\') as time, b.nickname nickname, b.username username, b.heap heap, b.type type from chat_feedback a, chat_userinfo b where a.userinfo_id=b.id and a.id>' + maxid + ' order by a.time desc';
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.getAllFeedback()');
			callback(undefined);
		}else{
			callback(res);
		}
	})
}

//删除好友
function deleteFriend(chatFriendListId, toId, myId, callback) {
	sql = 'delete from chat_friend where id=' + chatFriendListId;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.deleteFriend()');
			callback(undefined);
		}else{
			sql = 'delete from chat_friend where friend_me=' + toId + ' and friend_you=' + myId;
			console.log('sql: ' + sql);
			conn.query(sql, function(err, res, filds) {
				if(err){
					console.error('err:: database.deleteFriend()');
					callback(undefined);
				}else{
					callback(res);
				}
			});
		}
	});
}

//申请确认
function submitFriend(flistid, time, callback) {
	sql = 'select * from chat_friend where id=' + flistid;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.submitFriend()');
			callback(undefined);
		}else{
			if(res !== undefined && res[0] !== undefined){
				
				sql = 'select * from chat_friend where friend_me=' + res[0]['friend_you'] + ' and friend_you=' + res[0]['friend_me'];
				
				if(DEBUG){
					console.log('sql: ' + sql);
				}
				
				conn.query(sql, function(err, res1, filds){
					if(err){
						console.error('err:: database.insertIntoFriend()');
						//throw err;
						callback(undefined);
					}else{
						if(res1===undefined || res1[0]===undefined){
							sql = 'insert into chat_friend(friend_me, friend_you, time, state, remark) values(' + res[0]['friend_you'] + ', ' + res[0]['friend_me'] +', \'' + time + '\', \'friend\', \'\')';
							conn.query(sql, function(err, res, filds) {
								if(err){
									console.error('err:: database.submitFriend()');
									callback(undefined);
								}else{
									sql = 'update chat_friend set state=\'friend\' where id=' + flistid;
									conn.query(sql, function(err, res, filds) {
										if(err){
											console.error('err:: database.submitFriend()');
											callback(undefined);
										}else{
											callback(true);
										}
									});
								}
							});
						}else{
							sql = 'update chat_friend set state=\'friend\' where friend_me=' + res[0]['friend_you'] +' and friend_you=' + res[0]['friend_me'] + '';
							console.log(sql);
							conn.query(sql, function(err, res, filds) {
								if(err){
									console.error('err:: database.submitFriend()');
									callback(undefined);
								}else{
									sql = 'update chat_friend set state=\'friend\' where id=' + flistid;
									conn.query(sql, function(err, res, filds) {
										if(err){
											console.error('err:: database.submitFriend()');
											callback(undefined);
										}else{
											callback(true);
										}
									});
								}
							});
							//callback(undefined);
						}
					}
				});
			}
			callback(res);
		}
	});
}

function getTeamUnreadMessage(teamid, messageid, callback) {
	if(messageid == -1){
		sql = 'select id, fromuser, toteam, message, date_format(time,\'%Y-%m-%d %H:%i:%s\') as time, type, path from chat_team_message where toteam=' + teamid + ' order by id desc limit 0,10';
	}else{
		sql = 'select id, fromuser, toteam, message, date_format(time,\'%Y-%m-%d %H:%i:%s\') as time, type, path from chat_team_message where toteam=' + teamid + ' and id>' + messageid + '';
	}
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.getTeamUnreadMessage()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

function updateTeamHeap(teamId, heap, callback) {
	sql = 'update chat_team set heap=\'' + heap + '\' where id=' + teamId; 
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.updateTeamHeap()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

function updateTeamName(teamId, name, callback) {
	sql = 'update chat_team set name=\'' + name + '\' where id=' + teamId; 
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.updateTeamName()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

function quitTeam(teamId, myId, callback) {
	sql = 'delete from chat_room where team=' + teamId + ' and user=' + myId; 
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.quitTeam()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

function addTeam(teamId, myId, callback) {
	sql = 'select * from chat_room where team=' + teamId + ' and user=' + myId;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.addTeam()');
			callback(undefined);
		}else{
			if(res == undefined || res[0] == undefined){
				sql = 'insert into chat_room(team, user) values(' + teamId + ', ' + myId + ')'; 
				console.log('sql: ' + sql);
				conn.query(sql, function(err, res1, filds) {
					if(err){
						console.error('err:: database.addTeam()');
						callback(undefined);
					}else{
						callback(res1);
					}
				});
			}else{
				callback(undefined);
			}
		}
	});
}

function allTeams(name, callback) {
	sql = 'select id teamid, name, heap, type from chat_team where name like \'%' + name + '%\''; 
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.quitTeam()');
			callback(undefined);
		}else{
			callback(res);
		}
	});
}

function createTeam(name, type, myId, callback) {
	sql = 'insert into chat_team(name, type, creater) values(\''+ name + '\',' + type + ', ' + myId + ')'; 
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.createTeam()');
			callback(undefined);
		}else{
			var insertId = res['insertId'];
			sql = 'insert into chat_room(team, user) values(' + insertId + ', ' + myId + ')'; 
			console.log('sql: ' + sql);
			conn.query(sql, function(err, res, filds) {
				if(err){
					console.error('err:: database.createTeam()');
					callback(undefined);
				}else{
					callback(res);
				}
			});
		}
	});
}

function addFriendToTeam(teamId, otherId, callback) {
	
	sql = 'select * from chat_room where team=' + teamId + ' and user=' + otherId;
	console.log('sql: ' + sql);
	conn.query(sql, function(err, res, filds) {
		if(err){
			console.error('err:: database.createTeam()');
			callback(undefined);
		}else{
			if(res === undefined || res[0] === undefined){
				sql = 'insert into chat_room(team, user) values(' + teamId + ', ' + otherId + ')'; 
				console.log('sql: ' + sql);
				conn.query(sql, function(err, res, filds) {
					if(err){
						console.error('err:: database.createTeam()');
						callback(undefined);
					}else{
						callback(res);
					}
				});
			}
		}
	});
}

exports.conn = conn;
exports.login = login;
exports.getFriendListByState = getFriendListByState;
exports.getFriendListStateAll = getFriendListStateAll;
exports.insertMessage = insertMessage;
exports.readMessage = readMessage;
exports.code = code;
exports.register = register;
exports.identifyingCode = identifyingCode;
exports.validateUsername = validateUsername;
exports.getUserInfoByID = getUserInfoByID;
exports.getUserInfoByPhone = getUserInfoByPhone;
exports.insertIntoFriend = insertIntoFriend;
exports.updateFriend = updateFriend;
exports.getUnreadMessage = getUnreadMessage;
exports.getTeamByUserId = getTeamByUserId;
exports.getDefaultTeams = getDefaultTeams;
exports.insertTeamMessage = insertTeamMessage;
exports.updateUsername = updateUsername;
exports.updateHeap = updateHeap;
exports.updateRemark = updateRemark;
exports.getFriendByFriendId = getFriendByFriendId;
exports.insertFeedback = insertFeedback;
exports.getAllFeedback = getAllFeedback;
exports.getInviteFriends = getInviteFriends;
exports.submitFriend = submitFriend;
exports.deleteFriend = deleteFriend;
exports.getTeamUnreadMessage = getTeamUnreadMessage;
exports.updateTeamHeap = updateTeamHeap;
exports.updateTeamName = updateTeamName;
exports.quitTeam = quitTeam;
exports.allTeams = allTeams;
exports.addTeam = addTeam;
exports.createTeam = createTeam;
exports.addFriendToTeam = addFriendToTeam;
