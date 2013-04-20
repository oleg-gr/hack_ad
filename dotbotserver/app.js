
/**
 * Module dependencies.
 */

var express = require('express'), 
    routes = require('./routes'), 
    user = require('./routes/user'), 
    checkDB = require('./routes/checkDB'),
    siteIO = require('./routes/siteIO'),
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
app.get('/checkDB', checkDB.show);
app.get('/users', user.list);
app.get('/io', siteIO.pull);
app.get('/io/list', siteIO.listDB);
app.post('/io', siteIO.push);
app.get('/io/resetall', siteIO.resetAll);
app.get('/io/reset', siteIO.resetStream);
app.post('/io/alive', siteIO.alive);
app.get('/io/alive', siteIO.checkAlive);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
