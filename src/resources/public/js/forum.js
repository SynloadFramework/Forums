var forum = {
	thread: 0,
	parent: "0",
	cid: 0,
	newPost: function(event,data){
			$.get("templates/newpost.html", function (tmpl) {
				var dOut = Mark.up(tmpl, $.parseJSON(data.data.post));
				$("#newpost").before(dOut);
				$(".freshpost").each(function(){
					var height=0;
					$(this).children("div").each(function(){
						if(height<$(this).outerHeight(true)){
							height = $(this).outerHeight(true);
						}
					});
					$(this).animate({"height":(height+$(this).innerHeight())+"px"},300);
					if($("body").scrollTop()>$("body")[0].scrollHeight-window.innerHeight-200){
						$("body").animate({scrollTop:$("body")[0].scrollHeight},300);
					}
					$(this).removeClass("freshpost");
				});
			},"html");
	},
	newThread: function(event, data){
		var thread = $.parseJSON(data.data.thread);
		$("#cat_"+thread.cid+"_title").attr("href","javascript:forum.loadThread('"+thread.latest.id+"',"+thread.latest.page+",'#post_"+thread.latest.id+"');");
		$("#cat_"+thread.cid+"_title").html(thread.title);
		
		$("#cat_"+thread.cid+"_user").attr("onclick","forum.loadProfile('"+thread.user.user.id+"');");
		$("#cat_"+thread.cid+"_user").html(thread.user.username);

		$("cat_"+thread.cid+"_date").html(moment(new Date(thread.latest.createDate*1000)).format("MMM D, YYYY h:mm a"));
	},
	postCreateEvent: function(){
		//$(".editor").html("");
		//$(".editor").destroy();
		$(".editor").wysibb("");
	},
	savePost: function(pid,elem_editor,elem){
		ws.send({
			"request":"save",
			"page":"post",
			"data":{
				"pid":pid,
				"message": $(elem_editor).bbcode(),
				"elem":elem
			},
			"class":"Request"
		});
	},
	reply: function(pid, page){
		if(!pid){
			$('body').animate({
				scrollTop: ($('#newReply').offset().top-100)
			}, 500);
			$('#newReply').animate({opacity:1},400).animate({opacity:0},400).animate({opacity:1},400);
		}else{
			forum.loadPost(pid,page,'#newReply');
		}
	},
	singlePost: function(pid,elem){
		ws.send({
			"request":"get",
			"page":"singlepost",
			"data":{
				"pid":pid,
				"elem":elem
			},
			"class":"Request"
		});
	},
	editPost: function(pid,elem){
		ws.send({
			"request":"edit",
			"page":"post",
			"data":{
				"pid":pid,
				"elem":elem
			},
			"class":"Request"
		});
	},
	loadProfile: function(uid){
		ws.send({
			"request":"get",
			"page":"profile",
			"data":{
				"uid":uid
			},
			"class":"Request"
		});
		template.showLoad();
	},
	editProfile: function(){
		ws.send({
			"request":"edit",
			"page":"profile",
			"class":"Request"
		});
		template.showLoad();
	},
	saveProfile: function(elem_editor,elem){
		ws.send({
			"request":"save",
			"page":"profile",
			"data":{
				"signature": $(elem_editor).bbcode(),
				"avatar":$(elem).val()
			},
			"class":"Request"
		});
		template.showLoad();
	},
	loadThread: function(tid,page,to){
		ws.send({
			"request":"get",
			"page":"thread",
			"data":{
				"tid":tid,
				"page":page,
				"to":to
			},
			"class":"Request"
		});
		template.showLoad();
	},
	loadPost: function(pid,page,to){
		ws.send({
			"request":"get",
			"page":"post",
			"data":{
				"pid":pid,
				"page":page,
				"to":to
			},
			"class":"Request"
		});
		template.showLoad();
	},
	loadCategory: function(cid,page){
		ws.send({
			"request":"get",
			"page":"category",
			"data":{
				"cid":cid,
				"page":page
			},
			"class":"Request"
		});
		template.showLoad();
	},
	threadForm: function(cid){
		ws.send({
			"request":"get",
			"page":"threadform",
			"data":{
				"cid":cid
			},
			"class":"Request"
		});
		template.showLoad();
	},
	threadCreate: function(cid,pid,elem_editor,elem_title){
		ws.send({
			"request":"create",
			"page":"thread",
			"data":{
				"cid": cid,
				"message": $(elem_editor).bbcode(),
				"title": $(elem_title).val(),
				"pid": pid
			},
			"class":"Request"
		});
		template.showLoad();
	},
	post: function(tid,pid,elem){
		ws.send({
			"request":"create",
			"page":"post",
			"data":{
				"tid":tid,
				"message":$(elem).bbcode(),
				"pid":pid
			},
			"class":"Request"
		});
	}
};
ws.addCallback(forum.postCreateEvent,"post_create");
ws.addCallback(forum.newPost,"new_post");
ws.addCallback(forum.newThread,"new_thread");