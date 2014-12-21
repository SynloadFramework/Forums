var tags = {
	data: new Array(),
	q: "",
	tmpkeys: 0,
	tmp: "",
	lastSelectid: -1,
	insertElement: function(tag){
		$("#query").before('<a href="javascript:tags.remove(\''+tag+'\');" class="stags '+((tag.split(':').length==2)?tag.split(':')[0].replace('-','').replace('+',''):'')+'" tag="'+tag+'" id="'+tag.replace(/\W/ig,"")+'"><span>'+((tag.split('')[0]=="+")?"+":"")+((tag.split('')[0]=="-")?"-":"")+((tag.split(':').length==2)?tag.split(':')[0].replace('-','').replace('+','')+" : "+tag.split(':')[1].replace(/_/ig,' '):tag.replace('-','').replace('+',''))+'</span></a>');
	},
	mangalist: function(){
		popup.enable=true;
		$(".mangaitem").hover(function(){
			popup.show({
				"element": "manga_"+$(this).attr("manga"),
				"r":{ 
					"data": {
						"manga": $(this).attr("manga"),
						"e": ".manga_"+$(this).attr("manga")+"_p"
					},
					"request": "get",
					"page": "popupmanga",
					"class":"Request"
				}
			});
		},function(){
			popup.hide({
				"element": "manga_"+$(this).attr("manga")
			});
		});
	},
	insert: function(tag){
		tags.q += (tags.q!="")?" "+tag:tag;
		tags.data.push(tag);
		console.log(tags.data);
		tags.insertElement(tag);
		$("#query").val("");
		$("#query").focus();
		$(".search").empty();
		$(".search").remove();
		manga.search();
	},
	selectTag: function(){
		console.log("enter");
		if(tags.lastSelectid!=-1){
			tags.insert($("a.selected").attr("tag"));
		}else{
			tags.insert($("#query").val());
		}
	},
	selectDeletePrevious: function(){
		tags.remove($($(".stags")[($(".stags").length-1)]).attr("tag"));
	},
	selectNext: function(){
		if($(".taglist").length>(tags.lastSelectid+1)){
			tags.lastSelectid+=1;
			$(".taglist").removeClass("selected");
			if($(".taglist")[tags.lastSelectid]){
				$($(".taglist")[tags.lastSelectid]).addClass("selected");
				$(".taglist").parent().parent().scrollTo( 'a.selected', 100, {offset: {top:-90} } );
			}
		}
	},
	selectPrevious: function(){
		if(tags.lastSelectid>-1){
			tags.lastSelectid -=1;
			$(".taglist").removeClass("selected");
			if($(".taglist")[tags.lastSelectid]){
				$($(".taglist")[tags.lastSelectid]).addClass("selected");
				$(".taglist").parent().parent().scrollTo( 'a.selected', 100, {offset: {top:-90} } );
			}
		}
	},
	remove: function(tag){
		var index = tags.data.indexOf(tag);
		if (index > -1) {
			tags.data.splice(index, 1);
		}
		var tmpR = tags.data;
		tags.data = new Array();
		$.each(tmpR,function(key,tagi){
			if(tagi!=tag){
				tags.data.push(tagi);
			}
		});
		$("#"+tag.replace(/\W/ig,"")).animate({"width":"0px","paddingLeft":"0px","paddingRight":"0px"},200,function(){
			$("#"+tag.replace(/\W/ig,"")).empty();
			$("#"+tag.replace(/\W/ig,"")).remove();
		});
		tags.q = "";
		$.each(tags.data,function(key,tagi){
			tags.q += (tags.q!="")?" "+tagi:tagi;
		});
		manga.search();
	},
	init: function(){
		tags.data = new Array();
		$.each(tags.q.split(" "),function(key,val){
			if(val!=""){
				tags.data.push(val);
				tags.insertElement(val);
			}
		});
		$("#query").focus();
	},
	triggerkey: function(kid, e){
		if(e.which==8 && $("#query").val()!=""){
			
		}else{
			setTimeout(function(){
				if(kid==tags.tmpkeys){
					if(e.which==13){
						tags.selectTag();
						e.preventDefault();
					}else if(e.which==38){
						tags.selectPrevious();
						e.preventDefault();
					}else if(e.which==40){
						tags.selectNext();
						e.preventDefault();
					}else if(e.which==8 && $("#query").val()==""){
						tags.selectDeletePrevious();
						e.preventDefault();
					}
				}
			},100);
			return false;
		}
	},
	trigger: function(query){
		setTimeout(function(){
			if(tags.tmp==query && tags.tmp.length>=3){
				if($(".search").length==0){
					$("body").after('<div style="display:block;position:absolute;min-height:20px;background:#EAF5F9;" class="search"></div>');
					$(".search").css({"left":$(".query").offset().left+"px","top":($(".query").offset().top+$(".query").outerHeight())+"px","width":$(".query").outerWidth()+"px"});
				}
				var data = {
					"data": {
						"q": query,
						"e": ".search"
					},
					"class": "Request",
					"request": "get",
					"page": "taglist"
				};
				ws.send(data);
			}
			if(tags.tmp.length<3){
				$(".search").empty();
				$(".search").remove();
			}
		},160);
	},
	select: function(){
		$("#query").focus();
	},
	blur: function(){
		$("#query").unbind();
		setTimeout(function(){
			$(".search").empty();
			$(".search").remove();
		},160);
		$("#query").parent().css({"boxShadow":"none"});
	},
	focus: function(){
		$("#query").on('input',function(e){
			tags.tmp = $("#query").val();
			tags.trigger(tags.tmp);
		});
		$("#query").keydown(function(e){
			if(e.which!=38 && e.which!=40 && e.which!=13 && e.which!=8){
			}else{
				tags.tmpkeys+=1;
				return tags.triggerkey(tags.tmpkeys,e);
			}
		});
		$("#query").parent().css({"boxShadow":"1px 1px 6px #69BEE5"});
	}
	
}