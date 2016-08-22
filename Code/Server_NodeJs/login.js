var database = require('./database');
var redisOption = require('./redisOption');
var index = require('./index');
var crypto = require('crypto');
var DEBUG = require('./debug').DEBUG;

var app = index.app;
var http = index.http;

app.post('/login', function(request, response){
    console.log('/login');
	//从客户端获取用户名密码
	var username = request.body.username;
	var password = request.body.password;
	
    if(DEBUG){
        console.log('login username: ' + username + ', password: ' + password);
    }else{
        console.log('login username: ' + username);
    }
    
    //通过用户名查询密码
	database.login(username, password, function(res){
		// 用户名不存在时返回401状态码
		if(res.toString() === ''){
			console.error(username + ' --> login fail.');
			response.writeHead(401);
			response.end('{"res":"err"}');
			return;
		}else{
            //登录成功
            console.info(username + ' --> login success.');
            
            //生成确认身份token
			var md5 = crypto.createHash('md5');
			md5.update(((Math.random() * Math.random()))
				.toString());
			var token = md5.digest('hex');
			
            //
            var userId = res[0]['id'];
            
            redisOption.setToken(token, userId, function(id){
                if(id === undefined){
                    console.log('set token err and callback undefined');
                    response.writeHead(403);
                    response.end('{"res":"fail"}');
                    return;
                }else{
                    response.writeHead(200);
                    var responseResult = '{"res":"success", "token":"' + token + '",' +
                        ' "id":'+res[0]['id']+', "nickname":"'+res[0]['nickname'] + 
                        '", "heap":"' + res[0]['heap'] + '", ' +
                        '"type":'+res[0]['type']+'}';
                    if(DEBUG){
                        console.log(username + ' --> ' + responseResult);
                    }
                    response.end(responseResult);
                    return;
                }
            });
        }
		
	});
});
