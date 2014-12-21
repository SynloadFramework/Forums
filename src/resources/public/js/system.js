$(document).ready(function(){
	ws.connect();
});
Mark.compact = true;
Mark.pipes.htmlescape = function (string) {
    return string.replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/\"/g, '&quot;')
      .replace(/\'/g, '&#39;');
};
Mark.pipes.replace = function (str, b, c) {
    return str.replace( b, c );
};
Mark.pipes.moment = function (date, format) {
    return moment(new Date(date*1000)).format(format);
};
Mark.pipes.ago = function (date, format) {
    return moment(new Date(date*1000)).fromNow();
};
Mark.pipes.nl2br = function (str, n) {
    return str.replace(/\n/g, '<br />');
};
var entityMap = {
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': '&quot;',
    "'": '&#39;',
    "/": '&#x2F;'
};
Mark.pipes.html = function (str) {
    return String(str).replace(/[&<>"'\/]/g, function (s) {
      return entityMap[s];
    });
};

ws.addCallback(template.msg,"recieve");
ws.addCallback(javascript.msg,"recieve");

ws.addCallback(template.ws_lost,"close");

var timeoutvars = new Array();
function timeoutAction(key,time,func){
	if(timeoutvars[key]==undefined){
		timeoutvars[key]=0;
	}else{
		timeoutvars[key]++;
	}
	var timout = timeoutvars[key];
	setTimeout(function(){
		if(timout == timeoutvars[key]){
			func();
		}
	},time);
}
//ws.addCallback(user.dashboard,"dashboard");
var system = {
	alert: function(text,extra){
		$.jGrowl(text,extra);
	},
	connected: function(){
		setInterval(function(){
			var data = {
				"request":"get",
				"page":"ping",
				"class":"Request"
			}
			ws.send(data);
		},30000);
	},
	loadDefault: function(){
		console.log('loading default');
		var rSent = false;
		$.each( system.defaults , function(key,val){
			if((_.contains(user.flags,val.flag) || val.flag == "") && !rSent){
				if(val.resume){
					if(template.onPage==""){
						if(window.location.hash.split("/").length==3){
							ws.send(jQuery.parseJSON(window.atob(window.location.hash.split("/")[2])));
							rSent = true;
						}
					}
				}else{
					ws.send(val.request);
					rSent = true;
				}
			}
		});
	},
	cache: true,
	defaults: [
		{
			"flag":"",
			"resume":true
		},
		{
			"flag":"r",
			"request": {
				"request": "get",
				"page": "index",
				"class": "Request"
			}
		},
		{
			"flag":"",
			"request": {
				"request": "get",
				"page": "index",
				"class": "Request"
			}
		}
	]
}
$.jGrowl.defaults.animateOpen = {
	height: 'show'
};