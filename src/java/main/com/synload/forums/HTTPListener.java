package com.synload.forums;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.eclipse.jetty.server.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.synload.eventsystem.events.WebEvent;
import com.synload.framework.SynloadFramework;
import com.synload.framework.modules.annotations.Event;
import com.synload.framework.modules.annotations.Event.Type;
import com.synload.framework.users.User;

public class HTTPListener {
	private static final MultipartConfigElement MULTI_PART_CONFIG = 
			new MultipartConfigElement(
				"./tmp/", 
				943718400, 
				948718400, 
				948718400
			);
	
	@Event(name="upload",description="uploading a file.",trigger={"/upload"},type=Type.HTTP)
	public void sendUpload(WebEvent e) throws IOException, ServletException{
		Request baseRequest = e.getBaseRequest();
		HttpServletRequest request = e.getRequest();
		HttpServletResponse response = e.getResponse();
		request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
		response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        //request.setStopTimeout(600000);
        baseRequest.setHandled(true);
        if(!baseRequest.getParameterMap().containsKey("user")){
        	response.getWriter().println("{\"e\":\"no user specified\"}");
        	return;
		}
        try{
        	//User account = User.findUserSession(request.getParameter("key"), baseRequest.getHeader("X-REAL-IP"));
        	User account = User.findVerifySession(request.getParameter("key"));
        	System.out.println(baseRequest.getHeader("X-REAL-IP"));
        	if(account!=null){
        		List<String> entries = new ArrayList<String>();
				for(Part part :request.getParts()){
					if(part.getSubmittedFileName()!=null){
						if(part.getSize()>0){
							System.out.println("Recieved file! "+part.getSize());
							try {
								String type = part.getSubmittedFileName().split("\\.")[part.getSubmittedFileName().split("\\.").length-1];
								System.out.println(type);
								String finalFile = "ups/";
								switch(type){
									case "jpg":
									case "jpeg":
									case "png":
									case "gif":
										finalFile+="images/";
									break;
									case "mp3":
									case "aac":
									case "ogg":
										finalFile+="music/";
									break;
									case "mp4":
									case "webm":
									case "flv":
									case "mkv":
									case "wmv":
									case "avi":
										finalFile+="video/";
									break;
									case "zip":
									case "pdf":
									case "txt":
									case "gz":
										finalFile+="compressed/";
									break;
								}
								finalFile += randomString()+"."+type;
								InputStream is = part.getInputStream();
								OutputStream out = new FileOutputStream("public/"+finalFile);
								int bytesRead;
								byte[] buffer = new byte[8 * 1024];
								while ((bytesRead = is.read(buffer)) != -1) {
									out.write(buffer, 0, bytesRead);
								}
								out.close();
								is.close();
								part.delete();
								entries.add(finalFile);
							} catch (IOException e1) {
								if(SynloadFramework.debug){
									e1.printStackTrace();
								}
							}
						}
					}
				}
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				response.getWriter().println(ow.writeValueAsString(entries));
			}else{
				response.getWriter().println("{\"e\":\"Authentication failed!\"}");
				for(Part part :request.getParts()){
					part.delete();
				}
			}
        } catch (NullPointerException e1) {
        	if(SynloadFramework.debug){
        		e1.printStackTrace();
        	}
        	response.getWriter().println("{\"e\":\"#0 Authentication failed!\"}");
			for(Part part :request.getParts()){
				part.delete();
			}
		}
	}
	public String randomString(){
		SecureRandom random = new SecureRandom();
	    return new BigInteger(130, random).toString(32);
	}
}
