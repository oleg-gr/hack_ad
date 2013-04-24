
/**
 * Module dependencies.
 */

var express = require('express'), 
    routes = require('./routes'), 
    user = require('./routes/user'), 
    io = require('./routes/io'),
    http = require('http'), 
    path = require('path'),
    pages = require('./routes/pages');

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
app.use(express.favicon("public/images/favicon.ico")); 

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/', pages.index);
app.get('/users', user.list);
app.get('/io', io.pull);
app.get('/io/list', io.listDB);
app.post('/io', io.push);
app.get('/io/resetall', io.resetAll);
app.get('/io/reset', io.resetStream);
app.post('/io/alive', io.alive);
app.get('/io/alive', io.checkAlive);
app.get('/info/:page', pages.get);
app.get('/use/select', pages.select);
app.get('/use/robo', pages.use);

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
