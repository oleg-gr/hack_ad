exports.get = function(req, res){
  res.render('info_' + req.params.page + '.html.ejs');
};

exports.index = function(req, res){
  res.render('index.html.ejs');
};

exports.use = function(req, res){
  res.render('use_robo.html.ejs');
};

exports.select = function(req, res){
  res.render('use_select.html.ejs');
};
