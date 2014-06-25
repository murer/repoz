(function($) {

	var baseurl = '' + location;
	baseurl = baseurl.replace(/docs\.html.*/g, '');
	console.info(baseurl);
	
	$(window).ready(function() {
		$('.code.url').text(baseurl + 'r/');
	});
	
})(jQuery);
