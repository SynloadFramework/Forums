package com.synload.forums.pages;

import java.util.List;

import com.synload.forums.App;
import com.synload.forums.profile.ProfileModel;
import com.synload.framework.handlers.Request;
import com.synload.framework.handlers.Response;
import com.synload.framework.users.User;
import com.synload.framework.ws.WSHandler;

public class ProfileEdit extends Response {
	public ProfileModel profile = null;
	public long userid = 0;
	public ProfileEdit(WSHandler user, List<String> templateCache, String uid){
		this.setTemplateId("dtprfl");
		if(user.getUser()!=null){
			this.userid = user.getUser().getId();
		}
		if(!templateCache.contains(this.getTemplateId())){
			this.setTemplate(this.getTemplate("./elements/forums/profile_edit.html"));
		}
		if(App.userCache.containsKey(uid)){
			this.profile = App.userCache.get(uid);
		}else{
			this.profile = new ProfileModel(User.findUser(Long.valueOf(uid)));
			App.userCache.put(uid,this.profile);
		}
		this.addObject("UID", String.valueOf(profile.getUser().getId()));
		Request r = new Request("edit","profile");
		this.setRequest(r);
		this.setAction("alone");
		this.setPageId("editprofile");
		this.setParent(".content[page='wrapper']");
		this.setParentTemplate("wrapper");
		this.setPageTitle(" .::. Edit Profile");
	}
}
