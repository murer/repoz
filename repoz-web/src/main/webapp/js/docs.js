(function($) {

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
			text = text.replace(/\[basedomain\]/g, basedomain);
			$(this).val(text);
		});
	}
	
	function codePanels() {
		$('.topic .codePanel .codeBtns .http').click(function() {
			var panel = $(this).closest('.codePanel');
			panel.find('textarea.curl').hide();
			panel.find('textarea.http').show();
		});
		$('.topic .codePanel .codeBtns .curl').click(function() {
			var panel = $(this).closest('.codePanel');
			panel.find('textarea.http').hide();
			panel.find('textarea.curl').show();
		});
		$('.topic textarea.http').show();
		$('.topic textarea.curl').hide();
	}

	$(window).ready(function() {
		$('.code.url').text(baseurl + 'r/');

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
		
		summary();
		textarea();
		codePanels();
	});

})(jQuery);
