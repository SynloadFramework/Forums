var popup = {
	arr: new Array(), 
	enable: false,
	hoverOnPopup: false,
	show: function(e){
		if(!popup.enable){
			return;
		}
		if(e.element in popup.arr){
			popup.arr[e.element]++;
		}else{
			popup.arr[e.element] = 0;
			var windowWidth = $(window).width();
			var pos = "right";
			var x = $("."+e.element).parent().offset().left+$("."+e.element).parent().outerWidth()+15;
			if(x+400>windowWidth){
				x = $("."+e.element).parent().offset().left-415;
				pos = "left";
			}
			var y = $("."+e.element).offset().top-20;
			var htmlAttrs = "";
			$.each(e.r.data,function(ke,va){
				htmlAttrs += (htmlAttrs!="") ? " " : "" ;
				htmlAttrs += ke+'="'+va+'"';
			});
			$("body").append(
				'<div class="'+e.element+'_p popupshow'+((pos=="right")?" box-left-arrow":" box-right-arrow")+'" '+htmlAttrs+' style="opacity:0.99;background:#fff;min-height:80px;border:#e0e0e0 1px solid;'
				+((pos=="right")?"box-shadow: 1px 1px 5px #aaa;":"box-shadow: -1px 1px 5px #aaa;")+
				'padding:4px;top:'+y+'px;left:'+x+'px;position:absolute;width:400px;"><table width="100%" height="90"><tr><td><center><img src="http://thumb.animecap.com/loadingsmall.gif" /></center></td></tr></table></div>'
			);
			if(popup.hoverOnPopup){
				$("."+e.element+"_p").hover(function(){
					if(e.element in popup.arr){
						popup.arr[e.element]++;
					}
				},function(){
					popup.hide(e);
				});
			}
			ws.send(e.r);
		}
	},
	hide: function(e){
		var elemId = popup.arr[e.element]; 
		setTimeout(function(){
			if(elemId==popup.arr[e.element]){
				$("."+e.element+"_p").fadeOut();
				$("."+e.element+"_p").empty();
				$("."+e.element+"_p").remove();
				$("."+e.element+"_a").remove();
				delete popup.arr[e.element];
			}
		},100);
	},
	hideAll: function(e){
		$(".popupshow").fadeOut();
		$(".popupshow").empty();
		$(".popupshow").remove();
	},
	forceHide: function(e){
		$("."+e.element+"_p").fadeOut();
		$("."+e.element+"_p").empty();
		$("."+e.element+"_p").remove();
		$("."+e.element+"_a").remove();
		delete popup.arr[e.element];
	}
}