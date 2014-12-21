package com.synload.forums.pages;

import java.util.HashMap;
import java.util.List;

import com.synload.forums.categories.CategoryModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class CategoryPage extends Response {
	public CategoryModel category = null;
	public List<ThreadModel> threads = null;
	public long userid = 0;
	public int page, total = 0;
	public CategoryPage(WSHandler user, List<String> templateCache, String cid, int page){
		this.page = page;
		if(user.getUser()!=null){
			this.userid = user.getUser().getId();
		}
		this.setTemplateId("cat");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/category.html"));
		}
		category = CategoryModel.get(cid);
		category.renderParent();
		total = ThreadModel.threadCount(category.getId());
		Request r = new Request("get","category");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("cid", cid);
			data.put("page",String.valueOf(page));
			r.setData(data);
		this.setRequest(r);
		this.addObject("CID", category.getId());
		this.setAction("alone");
		this.setPageId("category"+cid);
		this.threads = ThreadModel.getList(cid, page-1);
		for(int i = 0;i<threads.size();i++){
			threads.get(i).renderLatest();
			this.addObject("TID", threads.get(i).getId());
		}
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Category "+category.getName());
	}
	public CategoryModel getCategory() {
		return category;
	}
	public void setCategory(CategoryModel category) {
		this.category = category;
	}
	public List<ThreadModel> getThreads() {
		return threads;
	}
	public void setThreads(List<ThreadModel> threads) {
		this.threads = threads;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
}
