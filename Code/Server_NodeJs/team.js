var database = require('./database');
var index = require('./index');
var app = index.app;
var http = index.http;
var chat = require('./chat');
var io = chat.io;
var DEBUG = require('./debug').DEBUG;
var redisOption = require('./redisOption');

app.post('/teams', function(request, response){
    console.log('/teams');
	var myId = request.body.myId;
	var token = request.body.token;
	
    if(DEBUG){
        console.log('/friends: myId: ' + myId + ', token: ' + token);
    }else{
        console.log('/friends: myId: ' + myId);
    }
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.getTeamByUserId(myId, function(res){
                response.writeHead(200);
                response.end('{"res":"success", "teams":' + JSON.stringify(res) + '}');
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

app.post('/defaultTeams', function(request, response){
    console.log('/defaultTeams');
	var token = request.body.token;
	
    database.getDefaultTeams(function(res){
        response.writeHead(200);
        response.end('{"res":"success", "defaultTeams":' + JSON.stringify(res) + '}');
        return;
    });
});

app.post('/updateTeamHeap', function(request, response){
    console.log('/updateTeamHeap');
	var heap = request.body.heap;
    var teamId = request.body.teamId;
	
    database.updateTeamHeap(teamId, heap, function(res){
        response.writeHead(200);
        response.end('{"res":"success"}');
        return;
    });
});

app.post('/updateTeamName', function(request, response){
    console.log('/updateTeamName');
	var name = request.body.name;
    var teamId = request.body.teamId;
	
    database.updateTeamName(teamId, name, function(res){
        response.writeHead(200);
        response.end('{"res":"success"}');
        return;
    });
});

app.post('/quitTeam', function(request, response){
    console.log('/quitTeam');
	var myId = request.body.myId;
    var token = request.body.token;
    var teamId = request.body.teamId;
	
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.quitTeam(teamId, myId, function(res){
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

app.post('/allTeams', function(request, response){
    console.log('/allTeams');
	var name = request.body.name;
	database.allTeams(name, function(res){
        response.writeHead(200);
        response.end('{"res":"success", "teams":' + JSON.stringify(res) + '}');
        return;
    });
});

app.post('/addTeam', function(request, response){
    console.log('/addTeam');
	var myId = request.body.myId;
    var token = request.body.token;
    var teamId = request.body.teamId;
	
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.addTeam(teamId, myId, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end();
                    return;
                }else{
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

app.post('/createTeam', function(request, response){
    console.log('/createTeam');
	var myId = request.body.myId;
    var token = request.body.token;
    var type = request.body.type;
    var name = request.body.name;
	
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.createTeam(name, type, myId, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end();
                    return;
                }else{
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

app.post('/addFriendToTeam', function(request, response){
    console.log('/addFriendToTeam');
	var myId = request.body.myId;
    var token = request.body.token;
    var teamId = request.body.teamId;
    var otherId = request.body.otherId;
	
    redisOption.getUserIdByToken(token, function(userid) {
        if(userid == myId){
            database.addFriendToTeam(teamId, otherId, function(res){
                if(res === undefined){
                    response.writeHead(401);
                    response.end();
                    return;
                }else{
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