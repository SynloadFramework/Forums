package com.synload.forums.pages;

import java.util.HashMap;
import java.util.List;

import com.synload.forums.posts.PostModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class PostPage extends Response{
	public ThreadModel thread = null;
	public PostModel post = null;
	public List<PostModel> posts = null;
	public int page = 0;
	public int total = 0;
	public double lastPage = 0;
	public String toPost = "";
	public long userid = 0;
	public PostPage(WSHandler user, List<String> templateCache, String pid, int page, String toPost){
		this.page = page;
		this.setTemplateId("post");
		if(user.getUser()!=null){
			this.userid = user.getUser().getId();
		}
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/post.html"));
		}
		this.post = PostModel.get(pid);
		this.addObject("UID", post.getUid());
		this.addObject("PID", post.getId());
		this.thread = ThreadModel.get(post.getTid());
		//thread.setViews(thread.getViews()+1);
		thread.renderCategory();
		this.addObject("TID", this.thread.getId());
		thread.category.renderParent();
		Request r = new Request("get","post");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("pid", pid);
			data.put("page",String.valueOf(page));
			data.put("to", toPost);
			r.setData(data);
		this.setRequest(r);
		this.total = PostModel.getPostChildrenCount(pid);
		this.lastPage = Math.ceil(this.total/25);
		this.setAction("alone");
		this.setPageId("post"+pid);
		this.posts = PostModel.getPostChildren(pid, page-1);
		for(int i=0;i<this.posts.size();i++){
			this.posts.get(i).renderChildren();
			this.addObject("PID", this.posts.get(i).getId());
			this.addObject("UID", this.posts.get(i).getUid());
		}
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Post "+post.getId());
		this.toPost = toPost;
	}
}
