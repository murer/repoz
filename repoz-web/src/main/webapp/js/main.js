(function($) {

	$(window).hashchange(function() {
		var hash = this.location.hash || '#';
		hash = $.trim(hash);
		if (!hash || hash == '#') {
			this.location.hash = '#AC';
			return;
		}
		var p = hash.substring(1);
		p = p.split('?')[0];

		$.wf([ 't/page/Page' + p + '.html' ], function(page) {
			page.open();
		});
	});

	$(window).ready(function() {
		$(window).hashchange();
	});

})(jQuery);
