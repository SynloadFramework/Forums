package com.synload.forums.threads;

import java.util.HashMap;
import java.util.List;

import com.synload.forums.posts.PostModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class ThreadPage extends Response {
	public ThreadModel thread = null;
	public List<PostModel> posts = null;
	public int page = 0;
	public int total = 0;
	public long userid = 0;
	public double lastPage = 0;
	public String toPost = "";
	public ThreadPage(WSHandler user, List<String> templateCache, String tid, int page, String toPost){
		this.page = page;
		this.setTemplateId("thread");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/thread.html"));
		}
		if(user.getUser()!=null){
			this.userid = user.getUser().getId();
		}
		thread = ThreadModel.get(tid);
		thread.setViews(thread.getViews()+1);
		thread.renderCategory();
		thread.category.renderParent();
		this.addObject("TID", this.thread.getId());
		thread.renderPost();
		this.addObject("PID", thread.getPost().getId());
		this.addObject("UID", thread.getPost().getUid());
		Request r = new Request("get","thread");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("tid", tid);
			data.put("page",String.valueOf(page));
			data.put("to", toPost);
			r.setData(data);
		this.setRequest(r);
		this.total = PostModel.postCount(tid);
		this.lastPage = Math.ceil(this.total/25);
		this.setAction("alone");
		this.setPageId("thread"+tid);
		this.posts = PostModel.getPosts(tid, page-1);
		for(int i=0;i<this.posts.size();i++){
			this.posts.get(i).renderChildren();
			this.addObject("PID", this.posts.get(i).getId());
			this.addObject("UID", this.posts.get(i).getUid());
		}
		this.posts.remove(0);
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Thread "+thread.getTitle());
		this.toPost = toPost;
	}
}
