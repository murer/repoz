(function($) {

	function querystring(query, l) {
		var Params = function() {

		}
		Params.prototype.get = function(name) {
			var ret = this[name];
			if (ret && ret.length) {
				return ret[0] || null;
			}
			return null;
		}
		
		Params.prototype.getint = function(name) {
			var ret = this[name];
			if (ret && ret.length) {
				return ret[0] ? parseInt(ret[0]) : null;
			}
			return null;
		}

		Params.prototype.hasParams = function() {
			var url = ('' + (l || location));
			if (url.indexOf('?') < 0) {
				return false;
			}
			return true;
		}

		var re = /([^&=]+)=?([^&]*)/g;
		function decode(str) {
			return decodeURIComponent(str.replace(/\+/g, ' '));
		}

		if (!query) {
			var url = ('' + (l || location));
			var idx = url.indexOf('?');
			if (idx < 0) {
				return new Params();
			}
			return querystring(url.substring(idx));
		}
		var params = new Params();
		var e;
		if (query) {
			if (query.substr(0, 1) == '?') {
				query = query.substr(1);
			}

			while (e = re.exec(query)) {
				var k = decode(e[1]);
				var v = decode(e[2]);
				params[k] = params[k] || [];
				params[k].push($.trim(v));
			}
		}
		return params;
	}

	$.querystring = querystring;

})(jQuery);