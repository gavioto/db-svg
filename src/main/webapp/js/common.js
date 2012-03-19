$.extend({
	getUrlVar : function(name, paramString) {
		paramString = typeof paramString !== 'undefined' ? paramString
				: window.location.href;

		name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
		var regexS = "[\\?&]" + name + "=([^&#]*)";
		var regex = new RegExp(regexS);
		var results = regex.exec(paramString);
		if (results == null)
			return "";
		else
			return decodeURIComponent(results[1]);
	}

});

$(document).ready(function() {
	$("#loadDialog").dialog({
		autoOpen : false,
		resizable : false,
		height : 170,
		modal : true
	});
});

// displays loading dialog
function showLoading() {
	$("#loadDialog").dialog("open");
}

// displays loading dialog
function hideLoading() {
	$("#loadDialog").dialog("close");
}

// show validation requirements
function updateReqs(t, reqs) {
	reqs.text(t).effect("highlight", {}, 1500);
}
// basic validation
function checkLength(o, n, reqs) {
	if (o.val().length < 1) {
		o.addClass('ui-state-error');
		updateReqs(n + " is required.", reqs);
		return false;
	} else {
		return true;
	}
}