package com.synload.forums;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.synload.eventsystem.events.RequestEvent;
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
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.users.User;
import com.synload.framework.ws.WSHandler;

public class PageHandler {
	
	@Event(name="getIndex",description="forum index",trigger={"get","index"},type=Type.WEBSOCKET)
	public void getIndex(RequestEvent event) throws JsonProcessingException{
		event.getSession().send(
			new ForumIndex(
				event.getSession(), 
				event.getRequest().getTemplateCache()
			)
		);
	}
	
	@Event(name="getCategory",description="forum category page",trigger={"get","category"},type=Type.WEBSOCKET)
	public void getCategory(RequestEvent event) throws JsonProcessingException{
		event.getSession().send(
			new CategoryPage(
				event.getSession(), 
				event.getRequest().getTemplateCache(),
				event.getRequest().getData().get("cid"), 
				Integer.valueOf(event.getRequest().getData().get("page"))
			)
		);
	}
	
	@Event(name="getThread",description="forum thread page",trigger={"get","thread"},type=Type.WEBSOCKET)
	public void getThread(RequestEvent event) throws JsonProcessingException{
		event.getSession().send(
			new ThreadPage(
				event.getSession(), 
				event.getRequest().getTemplateCache(), 
				event.getRequest().getData().get("tid"), 
				Integer.valueOf(event.getRequest().getData().get("page")), 
				event.getRequest().getData().get("to")
			)
		);
	}
	
	@Event(name="getPost",description="forum post page",trigger={"get","post"},type=Type.WEBSOCKET)
	public void getPost(RequestEvent event) throws JsonProcessingException{
		event.getSession().send(
			new PostPage(
				event.getSession(),
				event.getRequest().getTemplateCache(), 
				event.getRequest().getData().get("pid"), 
				Integer.valueOf(event.getRequest().getData().get("page")), 
				event.getRequest().getData().get("to")
			)
		);
	}
	
	@Event(name="savePost",description="execute post save",trigger={"save","post"},type=Type.WEBSOCKET)
	public void savePost(RequestEvent event) throws JsonProcessingException{
		PostModel post = PostModel.get(event.getRequest().getData().get("pid"));
		if(event.getSession().getUser().getId()==post.getUser().getUser().getId()){
			post.setMessage(event.getRequest().getData().get("message"));
			
			Response r = new Response();
			r.setCallEvent("edit_post");
			r.setForceParent(false);
				Map<String,String> data = new HashMap<String,String>();
				data.put("post", SynloadFramework.getOw().writeValueAsString(post));
				data.put("pid", post.getId());
			r.setData(data);
			SynloadFramework.broadcast(
				OOnPage.getClients("PID", post.getTid()), 
				SynloadFramework.getOw().writeValueAsString(r)
			);
			event.getSession().send(
				new SinglePost(
					event.getSession(), 
					event.getRequest().getTemplateCache(), 
					event.getRequest().getData().get("pid"), 
					event.getRequest().getData().get("elem")
				)
			);
		}
	}
	
	@Event(name="editPost",description="forum post edit page",trigger={"edit","post"},type=Type.WEBSOCKET)
	public void editPost(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
		PostModel post = PostModel.get(request.getData().get("pid"));
		if(user.getUser().getId()==post.getUser().getUser().getId()){
			user.send(
				new PostEdit(user, request.getTemplateCache(), request.getData().get("pid"), request.getData().get("elem"))
			);
		}
	}
	
	@Event(name="getSinglePost",description="forum single post page data",trigger={"get","singlepost"},type=Type.WEBSOCKET)
	public void getSinglePost(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
		PostModel post = PostModel.get(request.getData().get("pid"));
		if(user.getUser().getId()==post.getUser().getUser().getId()){
			user.send(
				new SinglePost(user, request.getTemplateCache(), request.getData().get("pid"), request.getData().get("elem"))
			);
		}
	}
	
	@Event(name="getThreadForm",description="forum thread form data",trigger={"get","threadform"},type=Type.WEBSOCKET)
	public void getThreadForm(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
		user.send(
			new ThreadForm(user, request.getTemplateCache(), request.getData().get("cid"))
		);
	}
	
	@Event(name="getProfile",description="user profile",trigger={"get","profile"},type=Type.WEBSOCKET)
	public void getProfile(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
		user.send(
			new ProfilePage(user, request.getTemplateCache(), request.getData().get("uid"))
		);
	}
	
	@Event(name="editProfile",description="user edit profile form",trigger={"edit","profile"},type=Type.WEBSOCKET)
	public void editProfile(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
		if(user.getUser()!=null){
			user.send(
				new ProfileEdit(user, request.getTemplateCache(), String.valueOf(user.getUser().getId()))
			);
		}
	}
	
	@Event(name="saveProfile",description="execute user save profile",trigger={"save","profile"},type=Type.WEBSOCKET)
	public void saveProfile(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
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
	
	@Event(name="createPost",description="execute creation of post",trigger={"create","post"},type=Type.WEBSOCKET)
	public void createPost(RequestEvent event) throws JsonProcessingException{
		WSHandler user = event.getSession();
		Request request = event.getRequest();
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
				new Success("post_create")
			);
			
			ThreadModel th = ThreadModel.get(ps.getTid());
			th.renderLatest();
			
			Response r = new Response();
			r.setCallEvent("new_thread");
			r.setForceParent(false);
				Map<String,String> data = new HashMap<String,String>();
				data.put("thread", SynloadFramework.getOw().writeValueAsString(th));
			r.setData(data);
			SynloadFramework.broadcast(OOnPage.getClients("CID", th.getCid()),SynloadFramework.getOw().writeValueAsString(r));
			
			r = new Response();
			r.setCallEvent("new_post");
			r.setForceParent(false);
				data = new HashMap<String,String>();
				data.put("post", SynloadFramework.getOw().writeValueAsString(PostModel.get(ps.getId())));
			r.setData(data);
			SynloadFramework.broadcast(OOnPage.getClients("TID", ps.getTid()), SynloadFramework.getOw().writeValueAsString(r)); // send out the new post event
			
		}else{
			//login
		}
	}
	
	@Event(name="createThread",description="execute creation of post",trigger={"create","thread"},type=Type.WEBSOCKET)
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
			th.renderLatest();
			Response r = new Response();
			r.setCallEvent("new_thread");
			r.setForceParent(false);
				Map<String,String> data = new HashMap<String,String>();
				data.put("thread", SynloadFramework.getOw().writeValueAsString(th));
				data.put("cid", th.getCid());
			r.setData(data);
			SynloadFramework.broadcast(
				OOnPage.getClients("CID", th.getCid()), 
				SynloadFramework.getOw().writeValueAsString(r)
			); // send out the new post event.
		}else{
			//login
		}
	}
}
