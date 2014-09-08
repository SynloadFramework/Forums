package com.synload.forums.pages;

import java.util.List;

import com.synload.forums.posts.PostModel;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class SinglePost extends Response {
	public PostModel post = null;
	public SinglePost(WSHandler user, List<String> templateCache, String pid, String elem){
		this.setTemplateId("pstsngl");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/post_single.html"));
		}
		this.setAction("alone");
		this.setForceParent(false);
		this.post = PostModel.get(pid);
		this.setParent(elem);
	}
}
