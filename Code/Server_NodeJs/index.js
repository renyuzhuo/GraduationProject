var moment = require('moment');
var express = require('express');
var bodyParser = require("body-parser");
var app = express();
var http = require('http').Server(app);
app.use(bodyParser.json({inflate: true, limit: '100kb', strict: true, type: 'application/json'}));

exports.app = app;
exports.http = http;

var database = require('./database');
var redisOption = require('./redisOption');
var DEBUG = require('./debug').DEBUG;
var login = require('./login');
var chat = require('./chat');
var friends = require('./friends');
var register = require('./register');
var team = require('./team');
var userinfo = require('./userinfo');
var feedback = require('./feedback');

http.listen(process.env.PORT || 3002, function () {
    console.log('listening on *:3002(process.env.PORT)');
    console.log('start server time: ' + moment().utcOffset('+08:00').format('YYYY-MM-DD HH:mm:ss'));
});
