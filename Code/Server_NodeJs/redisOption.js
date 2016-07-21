var redis = require("redis");
var databaseconfig = require('./databaseconfig');
var client = redis.createClient(databaseconfig.redisport, databaseconfig.redisip, {});
/**
 * redis密码
 */
// client.auth(databaseconfig.redispassword, function(res){
// console.log('redis connect success');
// });  

var userid;

var tokenMap = {};//[token]
var userMap = {};//[userid]

function getUserIdByToken(token, callback){
    userid = tokenMap[token];
    if(userid){
        callback(userid);
    }else{
        try{
            client.get('tokenMap', function(errGet, map) {
                if(errGet){
                    console.log('err get tokenMap: ' + errGet);
                    tokenMap = {};
                }else{
                    if(map === undefined || map === null ||map === ''){
                        tokenMap = {};
                    }else{
                        tokenMap = JSON.parse(map);
                    }
                }
                userid = tokenMap[token];
                callback(userid);
            });
        }catch(err){
            console.log(err);
        }
    }
}

function setToken(token, id, callback){
    try{
        client.get('tokenMap', function(errGet, map) {
            if(errGet){
                console.log('err get tokenMap: ' + errGet);
                tokenMap = {};
            }else{
                if(map === undefined || map === null || map === ''){
                    tokenMap = {};
                }else{
                    tokenMap = JSON.parse(map);
                }
            }
            
            //单设备登陆控制
            if(userMap.hasOwnProperty(id)){
                delete(tokenMap[userMap[id]]);
            }
            
            tokenMap[token] = id;
            userMap[id] = token;
            
            client.set('tokenMap', JSON.stringify(tokenMap), function(err, response) {
                if(err){
                    console.error('err set tokenMap: ' + err);
                    callback(undefined);
                }else{
                    client.set('userMap', JSON.stringify(userMap), function(err, response) {
                        if(err){
                            console.error('err set userMap: ' + err);
                            callback(undefined);
                        }else{
                            callback(id);
                        }
                    })
                }
            });
        });
    }catch(err){
        console.log(err);
    }
}

client.on('errorredis',function(error){
	console.log('redis error: ' + error + ' and reconnect');
});

client.on('connect', function(){
    console.log('reconnect redis');
});

exports.getUserIdByToken = getUserIdByToken;
exports.setToken = setToken;
