var _p = {
	elements: [],
	func: function(data){
	},
	init: function(){
		$("body").append('<div id="bgprompt"></div>'
		+'<div id="prompt">'
			+'<h3 class="titlePopup">Testing!</h3>'
			+'<div>'
				+'Testing this here thingy!'
			+'</div>'
			+'<span class="hrSpacePopup"></span>'
			+'<span class="links">'
				+'<a href="javascript: _p.send();">Send</a>'
				+'<a href="javascript: _p.cancel();">Cancel</a>'
			+'</span>'
		+'</div>');
	},
	show: function(title,html,elements,func){
		_p.func = func;
		_p.elements = elements;
		$("#prompt div").html(html);
		$("#prompt h3").html(title);
		$("#bgprompt").fadeIn();
		$("#prompt").fadeIn();
	},
	send: function(){
		var out = []
		$.each(_p.elements,function(key,val){
			out[val]=$("#"+val).val();
		});
		$("#bgprompt").fadeOut();
		$("#prompt").fadeOut();
		_p.func(out);
	},
	cancel: function(){
		$("#bgprompt").fadeOut();
		$("#prompt").fadeOut();
	}
}
$(document).ready(function(){
	_p.init();
});