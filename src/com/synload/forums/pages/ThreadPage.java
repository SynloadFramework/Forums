package com.synload.forums.pages;

import java.util.HashMap;
import java.util.List;
import com.synload.forums.posts.PostModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class ThreadPage extends Response {
	public ThreadModel thread = null;
	public List<PostModel> posts = null;
	public int page = 0;
	public int total = 0;
	public double lastPage = 0;
	public ThreadPage(WSHandler user, List<String> templateCache, String tid, int page){
		this.page = page;
		this.setTemplateId("thread");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/thread.html"));
		}
		thread = ThreadModel.get(tid);
		thread.setViews(thread.getViews()+1);
		thread.renderCategory();
		thread.category.renderParent();
		Request r = new Request("get","thread");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("tid", tid);
			data.put("page",String.valueOf(page));
			r.setData(data);
		this.setRequest(r);
		this.total = PostModel.postCount(tid);
		this.lastPage = Math.ceil(this.total/25);
		this.setAction("alone");
		this.setPageId("thread"+tid);
		this.posts = PostModel.getPosts(tid, page-1);
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Thread "+thread.getTitle());
	}
}
