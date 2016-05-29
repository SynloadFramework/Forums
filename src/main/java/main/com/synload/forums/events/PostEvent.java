package main.com.synload.forums.events;

import com.synload.eventsystem.EventClass;
import main.com.synload.forums.posts.PostModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.ws.WSHandler;

public class PostEvent extends EventClass {
	public WSHandler session=null;
	public Request request=null;
	public PostModel post = null;
	public ThreadModel thread = null;
	public PostEvent(WSHandler session, Request request, PostModel post, ThreadModel thread){
		this.setRequest(request);
		this.setSession(session);
		this.setPost(post);
		this.setThread(thread);
	}
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	public WSHandler getSession() {
		return session;
	}
	public void setSession(WSHandler session) {
		this.session = session;
	}
	public PostModel getPost() {
		return post;
	}
	public void setPost(PostModel post) {
		this.post = post;
	}
	public ThreadModel getThread() {
		return thread;
	}
	public void setThread(ThreadModel thread) {
		this.thread = thread;
	}
}
