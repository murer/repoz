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
	

	function textarea() {
		$('.topic textarea').each(function(i) {
			var rows = $(this).val().split('\n').length;
			$(this).attr('rows', rows);
			var text = $(this).val();
			text = text.replace(/\[baseurl\]/g, baseurl);
			$(this).val(text);
		});
	}

	$(window).ready(function() {
		$('.code.url').text(baseurl + 'r/');

		summary();
		textarea();
	});

})(jQuery);
