package com.synload.forums.profile;

import java.util.HashMap;
import java.util.List;

import com.synload.forums.App;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.users.User;
import com.synload.framework.ws.WSHandler;

public class ProfilePage extends Response{
	public ProfileModel profile = null;
	public long userid = 0;
	public ProfilePage(WSHandler user, List<String> templateCache, String uid){
		this.setTemplateId("prfl");
		if(user.getUser()!=null){
			this.userid = user.getUser().getId();
		}
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/profile.html"));
		}
		if(App.userCache.containsKey(uid)){
			this.profile = App.userCache.get(uid);
		}else{
			this.profile = new ProfileModel(User.findUser(Long.valueOf(uid)));
			App.userCache.put(uid,this.profile);
		}
		this.addObject("UID", String.valueOf(profile.getUser().getId()));
		Request r = new Request("get","profile");
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("uid", uid);
			r.setData(data);
		this.setRequest(r);
		this.setAction("alone");
		this.setPageId("profile"+uid);
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. "+this.profile.getUsername()+"'s Profile");
	}
}
