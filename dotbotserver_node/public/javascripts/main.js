activeId = "0";
activeIds = ["0"];
online = [];

var masterUpdate = function(id_list, callback){
  for (var i = 0; i < id_list.length; i++){
    getObject(id_list[i]);
  }
};

var handshake = function(){
  $.post("http://discos.herokuapp.com/io/alive", {from: "master", id: activeId}, function(resp){console.log("handshaking send"); console.log(resp);}, 'json');
  $.get("http://discos.herokuapp.com/io/alive?from=master", function(resp){
    console.log("handshaking receive"); 
    console.log(resp); 
    // check differences
    for (var key in resp.alive){
      if (resp.alive[key] === false && ($.inArray(key, online) != -1)){
        makeOffline(key);
      } else if (resp.alive[key] === true && ($.inArray(key, online) == -1)){
        makeOnline(key);
      }
    }
  }, 'json');
};

var makeOnline = function(id){
  if (id != '1' && id != '2' && id != '0'){
    console.log("Making key " + id + " online");
    online.push(id);
    $('#rover-' + id).removeClass("btn-danger disabled");
    $('#rover-' + id).addClass("btn-primary");
    $('#rover-' + id + ' span').html("Ready");
    deleteTab(id);
    printConsole("Rover " + id + " has lost connection");
  }
};

var makeOffline = function(id){
  if (id != '1' && id != '2' && id != '0'){
    console.log("Making key " + id + " offline");
    online.splice(online.indexOf(id),1);
    $('#rover-' + id).removeClass("btn-success btn-primary");
    $('#rover-' + id).addClass("btn-danger disabled");
    $('#rover-' + id + ' span').html("Offline");
    printConsole("Rover " + id + " has connected");
  }
};

var printConsole = function(text){
  $('#std-out-' + activeId).append(text + '<br />>> ');
  var stdOut = document.getElementById("std-out-" + activeId);
  stdOut.scrollTop = stdOut.scrollHeight;
};

var postObject = function(obj, callback){
  var val = $.post("http://discos.herokuapp.com/io", obj, printConsole(JSON.stringify(obj, null, 4)), 'json');
};

var getObject = function(id, callback){
  var val = $.get("http://discos.herokuapp.com/io", {from: 'master', id: id}, printConsole(JSON.stringify(resp, null, 4)), 'json');
  if (typeof callback === 'function') callback(val);
};

var parseObject = function(json, callback){
  if (typeof callback === 'function') callback();
};

var stdIn = function(str){
  obj = null;
  if (str == "start()"){
    obj = {state: "active", from: "master", id: activeId};
  } else if (str == "pause()"){
    obj = {state: "paused", from: "master", id: activeId};
  } else if (str == "stop()"){
    obj = {state: "inactive", from: "master", id: activeId};
  }
  if (obj !== null){
    postObject(obj);
  } else {
    $('#std-in').val('');
    parseJSON(str, function(json){
      json.from = 'master';
      json.id = activeId;
      console.log(json);
      postObject(json);
    });
  }
};

var parseJSON = function(string, callback){
  var match = /[^/s]+?\(.*\)/g.test(string);
  console.log(match);
  console.log("parsing");
  if (match && typeof callback === 'function'){
    parser.parseSingle(string, callback);
  } else {
    printConsole("Syntax Error on std-in");
  }
};

$(document).ready(function() {
  $("#compile").on("click", function() {
    if (syntax.check(editor.doc)) {
      parser.parse(editor.doc, function(obj){
        obj.from = 'master';
        obj.id = activeId;
        postObject(obj);});
    }
  });
  $('.std-in').on('keypress', function(e){
    if (e.keyCode == 13){
      if (e.shiftKey !== true)
      {
        e.preventDefault();
        stdIn($('#std-in').val());
      }
    }
  });

  $('#plus').on('click', function(e){
    e.preventDefault();
    avail = $('.rover-btn.btn-primary:first');
    if (avail.length > 0){
      createTab(avail.attr('id').split('-').pop());
    }
    return false;
  });
  $('#rover-row').on('click', '.rover-btn.disabled', function(e){
    e.preventDefault();
  });
  $('#rover-row').on('click', '.rover-btn.btn-success', function(e){
    e.preventDefault();
    deleteTab(e.currentTarget.id.split('-').pop()); return false;
  });
  $('#rover-row').on('click', '.rover-btn.btn-primary', function(e){
    e.preventDefault();
    createTab(e.currentTarget.id.split('-').pop()); return false;
  });
  $("#tabs").on('click', 'li', function(e){
    e.preventDefault();
    switchTab(e.currentTarget.id.split('-').pop());
  });
});



var switchTab = function(id){
  activeId = id;
  $('#tab-' + id + ' a').tab('show');
  
};

var createTab = function(id){
  $('<li id="tab-' + id + '"></li>').html($('<a href="#console-' + id + '" data-toggle="tab">Rover' + id + '</a>')).insertBefore('#plus');
  $('#tab-content').append('<div id="console-' + id + '" class="tab-pane fade container">' +
        '<div class="row-fluid">' +
          '<div class="span6">' +
            '<h3> STDOUT </h3>' +
            '<div id="std-out-' + id + '" class="std-out">>><br /></div>' +
          '</div>' +
          '<div class="span6">' +
            '<h3>Start writing your code here:</h3>' +
            '<textarea id="editor-' + id + '" name="editor" placeholder="Start programming here.."></textarea>' +
          '</div>' +
        '</div>' +
      '</div>');
  switchTab(id);
  $('#rover-' + id).toggleClass("btn-success btn-primary");
  $('#rover-' + id + ' span').html("Connected");
  editor.init("editor-" + id);
  if ($('#tabs li').length == 3){
    $('#console-0').hide();
    $('#tab-0').hide();
    window.scrollTo(0,0);
  }
  activeIds.push(id);
};

var deleteTab = function(id){
  if (id != "0"){
    $('#console-' + id).remove();
    $('#tab-' + id).remove();
    activeIds.splice(activeIds.indexOf(id),1);
    if ($('#tabs li').length == 2){
      $('#console-0').show();
      $('#tab-0').addClass('active').show();
      switchTab("0");
    } else{
      switchTab($('#tabs li').eq(1).attr('id').split('-').pop());
    }
    $('#rover-' + id).toggleClass("btn-success btn-primary");
    $('#rover-' + id + ' span').html("Ready");
  }
};

$(function () {
  $('#tabs a:first').tab('show');
  editor.init("editor-0");
});
