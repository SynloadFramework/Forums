package com.synload.forums;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;

import com.jhalt.expiringmap.ExpiringMap;
import com.jhalt.expiringmap.ExpiringMap.ExpirationPolicy;
import com.synload.forums.profile.ProfileModel;
import com.synload.framework.modules.ModuleClass;
import com.synload.framework.modules.annotations.Module;

@Module(name="Synload Forums",author="Nathaniel Davidson",version="0.0.1")
public class App extends ModuleClass{
	public static Properties prop = new Properties();
	public static Map<String, ProfileModel> userCache = ExpiringMap.builder()
			  .expiration(10, TimeUnit.SECONDS)
			  .expirationPolicy(ExpirationPolicy.ACCESSED)
			  .build();
	public static TextProcessor processor = BBProcessorFactory.getInstance().create(new File("bbcodes.xml"));
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
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
}
