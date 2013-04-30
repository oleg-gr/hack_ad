#! /app/bin/node


var request = require('request');


function clearQueue(){
  request("http://discos.herokuapp.com/io/list?from=master", function(err, resp, body){
    resp = JSON.parse(body);
    console.log(resp.inp.length);
    if (resp.inp.length > 200){
      console.log("resetting streams");
      request("http://discos.herokuapp.com/io/reset?from=master&id=1");
      request("http://discos.herokuapp.com/io/reset?from=master&id=2");
      request("http://discos.herokuapp.com/io/reset?from=master&id=3");
      request("http://discos.herokuapp.com/io/reset?from=master&id=4");
      request("http://discos.herokuapp.com/io/reset?from=master&id=5");
      request("http://discos.herokuapp.com/io/reset?from=master&id=6");
    }
  });
}

clearQueue();