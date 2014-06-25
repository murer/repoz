(function($) {

	var baseurl = '' + location;
	baseurl = baseurl.replace(/docs\.html.*/g, '');

	function summary() {
		$('.topic').each(function(i) {
			var title = $(this).find('h2').text();
			title = '' + (i+1) + '. ' + title;
			var link = $(this).find('a[name]').attr('name');
			var line = $('<li><a/></li>').appendTo('.summary ul');
			line.find('a').attr('href', '#' + link).text(title);
			$(this).find('h2').text(title)
		});
	}

	$(window).ready(function() {
		$('.code.url').text(baseurl + 'r/');

		summary();
	});

})(jQuery);
