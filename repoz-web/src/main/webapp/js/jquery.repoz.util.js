(function($) {

	baseurl = '' + location;
	baseurl = baseurl.replace(/[a-zA-Z0-9]+\.html.*/g, '');
	
	$.windfury.spec.req = function(ctx) {
		return function(modules, callback) {
			$.wf(modules, function() {
				var loads = $.makeArray(arguments);
				callback.apply(window, [ $, ctx.wf ].concat(loads));
			});
		}
	};

	$(window).ready(function() {
		ready = true
	});

	$.expr[':'].emptyval = function(obj, index, meta, stack) {
		return !$(obj).val();
	};

	$.expr[':'].val = function(obj, index, meta, stack) {
		return $(obj).val() == meta[3];
	};

	$.fn.valstr = function() {
		return $.trim($(this).val());
	}

	$.fn.valint = function() {
		var ret = $(this).valstr();
		return ret == null ? null : parseInt(ret);
	}

	$.fn.vallong = function() {
		var ret = $(this).valstr();
		return ret == null || ret === '' ? null : Number(ret);
	}

	$.fn.valfloat = function() {
		var ret = $(this).valstr();
		return ret == null ? null : parseFloat(ret);
	}

	$.opand = function() {
		for (var i = 0; i < arguments.length; i++) {
			if (!arguments[i]) {
				return false;
			}
		}
		return true;
	}

	$.opge = function(a, b) {
		return (a >= b);
	}

	$.oplt = function(a, b) {
		return (a < b);
	}

	$.fn.serializeObject = function() {
		var form = $(this);
		var arrayForm = form.serializeArray();
		var json = {};
		for (var idx = 0; idx < arrayForm.length; idx++) {
			var value = $.trim(arrayForm[idx].value);
			if (value) {
				json[arrayForm[idx].name] = value;
			}
		}
		return json;
	}

	$.hjoin = function(s) {
		var ret = '';
		for (var i = 1; i < arguments.length; i++) {
			if (arguments[i]) {
				ret += (s + arguments[i]);
			}
		}
		return (ret.length > 0 ? ret.substring(s.length) : ret);
	}

	$.fn.addClasses = function() {
		for (var i = 0; i < arguments.length; i++) {
			$(this).addClass(arguments[i]);
		}
		return $(this);
	}

	$.fn.removeClasses = function() {
		for (var i = 0; i < arguments.length; i++) {
			$(this).removeClass(arguments[i]);
		}
		return $(this);
	}

	$.phone = function(ddd, phone) {
		var ret = '';
		if (ddd) {
			ret += ('(' + ddd + ') ');
		}
		if (phone) {
			ret += phone;
		}
		return ret;
	}

	$.objsearch = function(list, attr, value) {
		return $(list).filter(function() {
			return (this && this[attr] == value);
		});
	}

	$.fn.sval = function(value) {
		if (value) {
			$(this).val(value).keyup();
			return this;
		}
		return $(this).val();
	}

	$.fn.valtext = function(value) {
		if (value) {
			$(this).find('option').each(function() {
				if ($(this).text() == value) {
					$(this).attr('selected', 'selected');
				}
			});
			return this;
		}
		return $(this).val();
	}

	$.dataMenorIgualQueHoje = function(strDate, def) {
		var date = $.formatStrToDate(strDate);
		if (!date) {
			return def;
		}
		return date.getTime() <= $.today().getTime();
	}

	$.dataMaiorIgualQueHoje = function(strDate, def) {
		var date = $.formatStrToDate(strDate);
		if (!date) {
			return def;
		}
		return date.getTime() >= $.today().getTime();
	}

	$.getYears = function() {
		var currentYear = new Date().getFullYear() + 1;
		var years = [];
		while (currentYear >= 1950) {
			years.push(currentYear--);
		}
		return years;
	}

	$.divClear = function() {
		return $('<div/>').addClass("clear blockclear");
	}

	$.fn.disable = function(value) {
		return $(this).attr('disabled', true);
	}

	$.fn.enable = function(value) {
		return $(this).attr('disabled', false);
	}

	$.parseProperties = function(text) {
		var lines = text.split('\n');
		var ret = {};
		for (var i = 0; i < lines.length; i++) {
			var line = $.trim(lines[i]);
			if (!line.match(/^#.*$/)) {
				var array = line.split('=');
				var name = array.shift();
				var value = array.join('=');
				if (name) {
					ret[name] = value;
				}
			}
		}
		return ret;
	}

	$.fn.readonly = function() {
		var list = ':text';
		var elements = $(this).find(list).andSelf(list);
		elements.disable();
		return $(this);
	}

	$.ajaxSetup({
		converters : {
			"text properties" : $.parseProperties
		}
	});

	$.listToObject = function(list, nid, nvalue) {
		var ret = {};
		for (var i = 0; i < list.length; i++) {
			var element = list[i];
			var id = element[nid];
			var value = element[nvalue];
			ret[id] = value;
		}
		return ret;
	}

	$.hiddeniframe = function(name) {
		return $('<iframe height="0" width="0" frameborder="0" scrolling="yes" />').attr('name', name);
	}

	$.generateRelativeUrl = function(path) {
		var ret = location.protocol + '//' + location.host;
		ret += location.pathname.replace(/\/[^\/]+$/, '/' + path);
		return ret;
	}

})(jQuery);