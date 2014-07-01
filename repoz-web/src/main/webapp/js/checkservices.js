(function($) {

	$(window).ready(function() {
		$('a.service').hide();
		$.ajax({
			url: 'version.properties',
			dataType: 'text',
			success: function(text) {
				text = $.trim(text);
				if(text) {
					$('a.service').show();
				}
			}
		});
	});

})(jQuery);
