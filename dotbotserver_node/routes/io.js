var mongo = require('mongodb');

var mongoUri = process.env.MONGOLAB_URI || 
  'mongodb://localhost/disco'; 
console.log(mongoUri);

var Server = mongo.Server,
    Db = mongo.Db,
    BSON = mongo.BSONPure;

//var server = new Server(mongoUri, 27017, {});
var db;
mongo.Db.connect(mongoUri, {'w':1, 'safe': 'true'}, function(err, database){
  database.safe = true;
  database.w = 1;
  db = database;
  if(!err) {
    console.log("Connected to 'disco' database");
  }
});

var removeFirst = function(id, collectionName, res){
  console.log("Retrieving first obj from " + id + " in collection: " + collectionName);
  db.collection(collectionName, function(err,collection){
    collection.findOne({"id": id}, function(err, doc){
      if (err){
        console.log(err);
        res.write(JSON.stringify({status: 500, error: err}));
        res.end();
      }
      if (doc !== null){
        doc.time_sent = new Date().getTime();
        console.log(String(doc['_id']));
        collection.remove({"_id": new BSON.ObjectID(String(doc['_id']))}, {safe: true},
          function(err, result){
            console.log(result + "document deleted");
            if (err){
              console.log(err);
            } else {
              delete doc['_id'];
              doc.status = 200;
              res.write(JSON.stringify(doc));
              res.end();
            }
          });
      } else {
        res.write(JSON.stringify({status: 204, msg: "up to date"}));
        res.end();
      }
    });
  });
};

var addToDB = function(obj, collectionName, res){
  console.log("Adding Object: " + obj + " DB " + collectionName + ".");
  db.collection(collectionName, function(err, collection){
    obj.time_received = new Date().getTime();
    delete obj.time;
    collection.insert(obj, {safe: true}, function(err, result){
      if (err) {
        res.write(JSON.stringify({status: 500, error: err}));
      } else {
        console.log('Success: ' + JSON.stringify(result[0]));
        res.write(JSON.stringify({status: 200}));
      }
      res.end();
    });
  });
};

var listDB = function(res){
  console.log("Listing DB");
  resp = {};
  db.collection("in", function(err, collection){
    collection.find().toArray(function(err, items){
      resp.inp = items;
      console.log(JSON.stringify({inp: items}));
      db.collection("out", function(err, collection){
        collection.find().toArray(function(err, items){
          resp.out = items;
          console.log(JSON.stringify({out: items}));
          db.collection("alive", function(err, collection){
            collection.find().toArray(function(err, items){
              resp.alive = items;
              console.log(JSON.stringify({alive: items}));
              res.write(JSON.stringify(resp));
              res.end();
            });
          });
        });
      });
    });
  });
};


var resetStream = function(id, res){
  console.log("Resetting stream: %d", id);
  db.collection("in").remove({id: id});
  db.collection("out").remove({id: id});
  res.write(JSON.stringify({'success': true}));
  res.end();
};

var resetAll = function(res){
  console.log("Restting all streams");
  db.collection("in", function(err, collection){
    collection.drop(function(err, items){
    });
  });
  db.collection("out", function(err, collection){
    collection.drop(function(err, items){
      res.write(JSON.stringify({'success': true}));
      res.end();
    });
  });
};

var alive = function(msg, res){
  console.log("Alerting Master that slave " + msg.id + " is alive");
  delete msg.time;
  msg.time_received = new Date().getTime();
  db.collection("alive", function(err, collection){
    collection.remove({"id": msg.id}, function(err, num){
      collection.insert(msg, {safe: true}, function(err, result){
        if (err) {
          console.log(err);
          res.write(JSON.stringify({status: 500, error: err}));
        } else {
          console.log('Success: ' + JSON.stringify(result[0]));
          res.write(JSON.stringify({status: 200}));
        }
        res.end();
      });
    });
  });
};

var checkAlive = function(res){
  timeNow = new Date().getTime();
  console.log("Checking which are alive");
  db.collection("alive", function(err, collection){
    collection.find().toArray(function(err, items){
      if (err){
        res.write(JSON.stringify({status: 500, err: "Can't select db"}));
      }
      result = {status: 200, alive: {}};
      for (var i=0; i<items.length; i++){
        if (timeNow - items[i].time_received < 60000){
          // last response was within a minute
          result.alive[items[i].id] = true;
        } else {
          result.alive[items[i].id] = false;
        }
      }
      res.write(JSON.stringify(result));
      console.log(JSON.stringify(result));
      res.end();
    });
  });
};

exports.pull = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  if (req.query.from == "master"){
    collection = "in";
  } else if (req.query.from == "slave"){
    collection = "out";
  } else {
    res.write(JSON.stringify({status: 401, error: 'no from field found'}));
    res.end();
  }
  if (req.query.id){
    removeFirst(req.query.id, collection, res);
  } else {
    res.write(JSON.stringify({status: 400, error: 'no id field found'}));
    res.end();
  }
};

exports.listDB = function(req,res){
  res.writeHead(200, {"Content-Type": "application/json"});
  listDB(res);
};

exports.push = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  var msg = req.body;
  if (req.body.from == "master"){
    collection = "out";
  } else if (req.body.from == "slave"){
    collection = "in";
  } else {
    res.write(JSON.stringify({success: 401, error: 'no from field found'}));
    res.end();
  }
  if (!req.body.id){
    res.write(JSON.stringify({success: 400, 'error': 'no id field found'}));
    res.end();
  }
  else if (req.body.id && req.body.from){
    addToDB(msg, collection, res);
  }
};

exports.resetAll = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  resetAll(res);
};

exports.resetStream = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  if (req.query.id && req.query.from == "master"){
    resetStream(req.query.id, res);
  } else {
    res.write(JSON.stringify({status: 400, 
      "err": 'no id found or request not from master'}));
    res.end();
  }
};

exports.alive = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  if (req.body.id && req.body.from == "slave"){
    alive(req.body, res);
  } else {
    res.write(JSON.stringify({status: 400, 
      "err": 'no id found or request not from slave'}));
    res.end();
  }
};

exports.checkAlive = function(req, res){
  res.writeHead(200, {"Content-Type": "application/json"});
  if (req.query.from == "master"){
    checkAlive(res);
  } else {
    res.write(JSON.stringify({status: 401, 
      "err": 'request not from master'}));
  }
};
