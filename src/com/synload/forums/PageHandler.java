package com.synload.forums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.forums.pages.CategoryPage;
import com.synload.forums.pages.ForumIndex;
import com.synload.forums.pages.PostEdit;
import com.synload.forums.pages.PostPage;
import com.synload.forums.pages.ProfileEdit;
import com.synload.forums.pages.ProfilePage;
import com.synload.forums.pages.SinglePost;
import com.synload.forums.pages.ThreadForm;
import com.synload.forums.pages.ThreadPage;
import com.synload.forums.posts.PostModel;
import com.synload.forums.profile.ProfileModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.OOnPage;
import com.synload.framework.SynloadFramework;
import com.synload.framework.elements.Success;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.users.User;
import com.synload.framework.ws.WSHandler;

public class PageHandler {
	public void getIndex(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new ForumIndex(user, request.getTemplateCache())
		);
	}
	public void getCategory(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new CategoryPage(user, request.getTemplateCache(), request.getData().get("cid"), Integer.valueOf(request.getData().get("page")))
		);
	}
	public void getThread(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new ThreadPage(user, request.getTemplateCache(), request.getData().get("tid"), Integer.valueOf(request.getData().get("page")), request.getData().get("to"))
		);
	}
	public void getPost(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new PostPage(user, request.getTemplateCache(), request.getData().get("pid"), Integer.valueOf(request.getData().get("page")), request.getData().get("to"))
		);
	}
	public void savePost(WSHandler user, Request request) throws JsonProcessingException{
		PostModel post = PostModel.get(request.getData().get("pid"));
		if(user.getUser().getId()==post.getUser().getUser().getId()){
			post.setMessage(request.getData().get("message"));
			
			Response r = new Response();
			r.setCallEvent("edit_post");
			r.setForceParent(false);
				Map<String,String> data = new HashMap<String,String>();
				data.put("post", user.ow.writeValueAsString(post));
				data.put("pid", post.getId());
			r.setData(data);
			SynloadFramework.broadcast(OOnPage.getClients("PID", post.getTid()), user.ow.writeValueAsString(r));
			
			user.send(
				user.ow.writeValueAsString(
					new SinglePost(user, request.getTemplateCache(), request.getData().get("pid"), request.getData().get("elem"))
				)
			);
		}
	}
	public void editPost(WSHandler user, Request request) throws JsonProcessingException{
		PostModel post = PostModel.get(request.getData().get("pid"));
		if(user.getUser().getId()==post.getUser().getUser().getId()){
			user.send(
				new PostEdit(user, request.getTemplateCache(), request.getData().get("pid"), request.getData().get("elem"))
			);
		}
	}
	public void getSinglePost(WSHandler user, Request request) throws JsonProcessingException{
		PostModel post = PostModel.get(request.getData().get("pid"));
		if(user.getUser().getId()==post.getUser().getUser().getId()){
			user.send(
				user.ow.writeValueAsString(
					new SinglePost(user, request.getTemplateCache(), request.getData().get("pid"), request.getData().get("elem"))
				)
			);
		}
	}
	public void getThreadForm(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new ThreadForm(user, request.getTemplateCache(), request.getData().get("cid"))
		);
	}
	public void getProfile(WSHandler user, Request request) throws JsonProcessingException{
		user.send(
			new ProfilePage(user, request.getTemplateCache(), request.getData().get("uid"))
		);
	}
	public void editProfile(WSHandler user, Request request) throws JsonProcessingException{
		if(user.getUser()!=null){
			user.send(
				new ProfileEdit(user, request.getTemplateCache(), String.valueOf(user.getUser().getId()))
			);
		}
	}
	public void saveProfile(WSHandler user, Request request) throws JsonProcessingException{
		if(user.getUser()!=null){
			String uid = String.valueOf(user.getUser().getId());
			ProfileModel pm;
			if(App.userCache.containsKey(uid)){
				pm = new ProfileModel(User.findUser(Long.valueOf(uid)));
			}else{
				pm = new ProfileModel(User.findUser(Long.valueOf(uid)));
				App.userCache.put(uid,pm);
			}
			if(!pm.getAvatar().equals(request.getData().get("avatar"))){
				pm.setAvatar(request.getData().get("avatar"));
			}
			if(!pm.getSignature().equals(request.getData().get("signature"))){
				pm.setSignature(request.getData().get("signature"));
			}
			App.userCache.remove(uid);
			user.send(
				new ProfilePage(user, request.getTemplateCache(), String.valueOf(user.getUser().getId()))
			);
		}
	}
	public void createPost(WSHandler user, Request request) throws JsonProcessingException{
		if(user.getUser()!=null){
			PostModel ps = new PostModel(
				"",
				request.getData().get("message"),
				request.getData().get("tid"),
				String.valueOf(user.getUser().getId()),
				request.getData().get("pid")
			);
			
			ThreadModel.updatePostTime(request.getData().get("tid"));
			PostModel.updatePostTime(request.getData().get("pid"));
			
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
				data.put("parent", request.getData().get("pid"));
				data.put("page", String.valueOf(ps.getPage()));
			r.setData(data);
			SynloadFramework.broadcast(OOnPage.getClients("TID", ps.getTid()),user.ow.writeValueAsString(r)); // send out the new post event
		}else{
			//login
		}
	}
	public void createThread(WSHandler user, Request request) throws JsonProcessingException{
		if(user.getUser()!=null){
			ThreadModel th = new ThreadModel(
				request.getData().get("title"),
				String.valueOf(user.getUser().getId()),
				request.getData().get("pid"),
				request.getData().get("cid"),
				request.getData().get("message")
			);
			
			user.send(
				new ThreadPage(user, request.getTemplateCache(), th.getId(), 1, "")
			);
			
			Response r = new Response();
			r.setCallEvent("new_thread");
			r.setForceParent(false);
				Map<String,String> data = new HashMap<String,String>();
				data.put("thread", user.ow.writeValueAsString(th));
				data.put("cid", th.getCid());
			r.setData(data);
			SynloadFramework.broadcast(OOnPage.getClients("CID", th.getCid()),user.ow.writeValueAsString(r)); // send out the new post event.
		}else{
			//login
		}
	}
}
