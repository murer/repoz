(function($) {

	$.repoz = {};

	$.repoz.service = function(method, url, dataType, data, c) {
		$.ajax({
			type : method,
			url : url,
			dataType : dataType,
			data : data,
			success : c
		});
	}

	$.repoz.parseTextLine = function(row) {
		row = $.trim(row);
		if(!row || row.match(/^#/)) {
			return null;
		}
		var list = row.split(/\s+/);
		return list;
	}
	
	$.repoz.parseText = function(text) {
		var list = text.split('\n');
		var ret = [];
		for(var i = 0; i < list.length; i++) {
			var row = $.trim(list[i]);
			if(row && !row.match(/^#/)) {
				var line = $.repoz.parseTextLine(row);
				ret.push(line);
			}
		}
		return ret;
	}

	$.repoz.listRepositories = function(c) {
		$.repoz.service('GET', 'access', null, {
			path : '/'
		}, function(file) {
			var matrix = $.repoz.parseText(file);
			var list = [];
			for(var i = 0; i < matrix.length; i++) {
				list.push(matrix[i][0]);
			}
			c(list);
			
		});
	}

})(jQuery);