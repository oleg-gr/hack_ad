
/**
 * Module dependencies.
 */

var express = require('express'), 
    routes = require('./routes'), 
    user = require('./routes/user'), 
    io = require('./routes/io'),
    http = require('http'), 
    path = require('path');

var app = express();

// all environments
var allowCrossDomain = function(req, res, next) {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');
    res.header('Access-Control-Allow-Headers', 'Content-Type');
    next();
};
app.set('port', process.env.PORT || 3000);
app.set('views', __dirname + '/views');
app.set('view engine', 'ejs');
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.bodyParser());
app.use(express.methodOverride());
app.use(express.cookieParser('your secret here'));
app.use(express.session());
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));
app.use(allowCrossDomain);

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', routes.index);
app.get('/users', user.list);
app.get('/io', io.pull);
app.get('/io/list', io.listDB);
app.post('/io', io.push);
app.get('/io/resetall', io.resetAll);
app.get('/io/reset', io.resetStream);
app.post('/io/alive', io.alive);
app.get('/io/alive', io.checkAlive);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});