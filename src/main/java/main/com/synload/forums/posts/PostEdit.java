package main.com.synload.forums.posts;

import java.util.List;

import com.synload.framework.handlers.Response;
import com.synload.framework.ws.WSHandler;

public class PostEdit extends Response {
	public PostModel post = null;
	public PostEdit(WSHandler user, List<String> templateCache, String pid, String elem){
		this.setTemplateId("pstedt");
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/post_edit.html"));
		}
		this.setAction("alone");
		this.setForceParent(false);
		this.post = PostModel.get(pid);
		this.setParent(elem);
	}
}