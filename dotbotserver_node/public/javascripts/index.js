$(document).ready(function() {
  $('#navbar ul li a').bind('click', function(e) {
   e.preventDefault();
   $('html, body').animate({ scrollTop: $(this.hash).offset().top - 60 }, 300);
  });
});

