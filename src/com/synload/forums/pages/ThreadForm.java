package com.synload.forums.pages;

import java.util.HashMap;
import java.util.List;

import com.synload.forums.categories.CategoryModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class ThreadForm extends Response {
	public CategoryModel category = null;
	public ThreadForm(WSHandler user, List<String> templateCache, String cid){
		this.setTemplateId("thdform");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/thread_form.html"));
		}
		category = CategoryModel.get(cid);
		this.addObject("CID", category.getId());
		Request r = new Request("get","threadform");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("cid", cid);
			r.setData(data);
		this.setRequest(r);
		this.setAction("alone");
		this.setPageId("thread_form");
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Create New Thread");
	}
}
