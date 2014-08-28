package com.synload.forums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.forums.pages.CategoryPage;
import com.synload.forums.pages.ForumIndex;
import com.synload.forums.pages.ThreadForm;
import com.synload.forums.pages.ThreadPage;
import com.synload.forums.posts.PostModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Success;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class PageHandler {
	public void getIndex(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			user.ow.writeValueAsString(
				new ForumIndex(user, request.getTemplateCache())
			)
		);
	}
	public void getCategory(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			user.ow.writeValueAsString(
				new CategoryPage(user, request.getTemplateCache(), request.getData().get("cid"), Integer.valueOf(request.getData().get("page")))
			)
		);
	}
	public void getThread(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			user.ow.writeValueAsString(
				new ThreadPage(user, request.getTemplateCache(), request.getData().get("tid"), Integer.valueOf(request.getData().get("page")))
			)
		);
	}
	public void getThreadForm(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			user.ow.writeValueAsString(
				new ThreadForm(user, request.getTemplateCache(), request.getData().get("cid"))
			)
		);
	}
	public void createPost(WSHandler user, Request request) throws JsonProcessingException{
		PostModel ps = new PostModel(
			"",
			request.getData().get("message"),
			request.getData().get("tid"),
			String.valueOf(user.getUser().getId()),
			request.getData().get("pid")
		);
		
		ThreadModel.updatePostTime(request.getData().get("tid"));
		
		user.send(
			user.ow.writeValueAsString(
				new Success("post_create")
			)
		);
		
		Response r = new Response();
		r.setCallEvent("new_post");
		r.setForceParent(false);
			Map<String,String> data = new HashMap<String,String>();
			data.put("post", user.ow.writeValueAsString(PostModel.get(ps.getId())));
			data.put("tid", ps.getTid());
		r.setData(data);
		SynloadFramework.broadcast(user.ow.writeValueAsString(r)); // send out the new post event
	}
	public void createThread(WSHandler user, Request request) throws JsonProcessingException{
		ThreadModel th = new ThreadModel(
			request.getData().get("title"),
			String.valueOf(user.getUser().getId()),
			request.getData().get("pid"),
			request.getData().get("cid"),
			request.getData().get("message")
		);
		
		user.send(
			user.ow.writeValueAsString(
					new ThreadPage(user, request.getTemplateCache(), th.getId(), 1)
			)
		);
		
		Response r = new Response();
		r.setCallEvent("new_thread");
		r.setForceParent(false);
			Map<String,String> data = new HashMap<String,String>();
			data.put("thread", user.ow.writeValueAsString(th));
			data.put("cid", th.getCid());
		r.setData(data);
		SynloadFramework.broadcast(user.ow.writeValueAsString(r)); // send out the new post event
	}
}
