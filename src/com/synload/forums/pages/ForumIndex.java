package com.synload.forums.pages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.synload.forums.categories.CategoryModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class ForumIndex extends Response  {
	public List<CategoryModel> categories = new ArrayList<CategoryModel>();
	public ForumIndex(WSHandler user, List<String> templateCache){
		this.setTemplateId("indx");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/index.html"));
		}
		Request r = new Request("get","index");
		this.setRequest(r);
		this.setAction("alone");
		this.setPageId("index");
		categories = CategoryModel.getAll();
		for(int x=0;x<categories.size();x++){
			categories.get(x).renderChildren();
		}
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Forum Index");
	}
}
