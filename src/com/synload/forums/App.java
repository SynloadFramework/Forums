package com.synload.forums;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;

import com.jhalt.expiringmap.ExpiringMap;
import com.jhalt.expiringmap.ExpiringMap.ExpirationPolicy;
import com.synload.eventsystem.Addon;
import com.synload.forums.profile.ProfileModel;
import com.synload.framework.SynloadFramework;
import com.synload.framework.ws.WSRequest;

public class App extends Addon{
	public static Properties prop = new Properties();
	public static Map<String, ProfileModel> userCache = ExpiringMap.builder()
			  .expiration(10, TimeUnit.SECONDS)
			  .expirationPolicy(ExpirationPolicy.ACCESSED)
			  .build();
	public static TextProcessor processor = BBProcessorFactory.getInstance().create(new File("bbcodes.xml"));
	public void init(){
		List<String> registered = new ArrayList<String>();
		registered.add("r");
		//SynloadFramework.registerElement(new WSRequest("index","get"), Page.class, "getIndex", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("index","get"), PageHandler.class, "getIndex", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("category","get"), PageHandler.class, "getCategory", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("thread","get"), PageHandler.class, "getThread", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("post","get"), PageHandler.class, "getPost", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("thread","create"), PageHandler.class, "createThread", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("post","create"), PageHandler.class, "createPost", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("threadform","get"), PageHandler.class, "getThreadForm", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("profile","get"), PageHandler.class, "getProfile", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("profile","edit"), PageHandler.class, "editProfile", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("profile","save"), PageHandler.class, "saveProfile", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("post","edit"), PageHandler.class, "editPost", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("post","save"), PageHandler.class, "savePost", new ArrayList<String>());
		SynloadFramework.registerElement(new WSRequest("singlepost","get"), PageHandler.class, "getSinglePost", new ArrayList<String>());
		SynloadFramework.registerHTTPPage("/upload", HTTPListener.class, "sendUpload");
	}
	public void prop() throws FileNotFoundException, IOException{
		if((new File("modules/forums.ini")).exists()){
			prop.load(new FileInputStream("modules/forums.ini"));
		}else{
			prop.setProperty("imageDir", "public/images/");
			prop.setProperty("videoDir", "public/videos/");
			prop.setProperty("soundDir", "public/sounds/");
			prop.store(new FileOutputStream("modules/forums.ini"), null);
		}
	}
}
