package main.com.synload.forums.threads;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mysql.jdbc.Statement;
import com.synload.forums.App;
import com.synload.forums.categories.CategoryModel;
import main.com.synload.forums.posts.PostModel;
import main.com.synload.forums.profile.ProfileModel;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class ThreadModel {
	//public static Map<String,ThreadModel> cache = new HashMap<String,ThreadModel>();
	public String title, id, uid, pid, cid = "";
	public long createDate, postDate, views = 0;
	public PostModel post, latest = null;
	public CategoryModel category = null;
	public Map<String,String> status = new HashMap<String,String>();
	public ThreadModel(ResultSet rs){
		try {
			title = rs.getString("title");
			id = rs.getString("id");
			uid = rs.getString("uid");
			cid = rs.getString("cid");
			pid = rs.getString("pid");
			views = rs.getLong("views");
			createDate = rs.getLong("created_date");
			postDate = rs.getLong("post_date");
			if(!rs.getString("status").equals("")){
				for(String s : new ArrayList<String>( Arrays.asList(rs.getString("status").split(",")) )){
					status.put(s.split(";")[0], s.split(";")[1]);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ThreadModel(String title, String uid, String pid, String cid, String message){
		this.title = title;
		this.uid = uid;
		this.pid = pid;
		this.cid = cid;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `threads` ( `title`, `uid`, `pid`, `cid`, `created_date`, `post_date`, `status`, `views` ) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), '', 0 );",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, title);
			s.setString(2, uid);
			s.setString(3, pid);
			s.setString(4, cid);
			s.execute();
			ResultSet keys = s.getGeneratedKeys();
			if(keys.next()){
				id = keys.getString(1);
			}
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		new PostModel(title, message, id, uid, "0");	
	}
	public static List<ThreadModel> getList(List<String> ids, int page){
		List<ThreadModel> threads = new ArrayList<ThreadModel>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `title`, `uid`, `pid`, `cid`, `created_date`, `post_date`, `status`, `views` FROM `threads` WHERE `id` IN ("+ids.toString().trim().replaceAll("(?i)[\\[\\]]", "")+") ORDER BY `post_date` ASC LIMIT "+(page*25)+",25;"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				//String id = rs.getString("id");
				//ThreadModel.cache.put(id, new ThreadModel(rs));
				threads.add(new ThreadModel(rs));
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return threads;
	}
	public static ThreadModel get(String id){
		//if(!ThreadModel.cache.containsKey(id)){
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id`, `title`, `uid`, `pid`, `cid`, `created_date`, `post_date`, `status`, `views` FROM `threads` WHERE `id`=?"
				);
				s.setString(1, id);
				ResultSet rs = s.executeQuery();
				while(rs.next()){
					ThreadModel thd =  new ThreadModel(rs);
					//ThreadModel.cache.put(thd.getId(), thd);
					rs.close();
					s.close();
					return thd;
				}
				rs.close();
				s.close();
			}catch(Exception e){
			}
		//}else{
		//	return ThreadModel.cache.get(id);
		//}
		return null;
	}
	public static ThreadModel getByCategoryLatest(String id){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `title`, `uid`, `pid`, `cid`, `created_date`, `post_date`, `status`, `views` FROM `threads` WHERE `cid`=? ORDER BY `post_date` DESC"
			);
			s.setString(1, id);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				ThreadModel thd = new ThreadModel(rs); 
				//ThreadModel.cache.put(thd.getId(),thd);
				rs.close();
				s.close();
				return thd;
			}
			rs.close();
			s.close();
		}catch(Exception e){}
		return null;
	}
	public static List<ThreadModel> getList(String cid, int page){
		List<ThreadModel> threads = new ArrayList<ThreadModel>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `title`, `uid`, `pid`, `cid`, `created_date`, `post_date`, `status`, `views` FROM `threads` WHERE `cid`=? ORDER BY `post_date` DESC LIMIT "+(page*25)+", 25"
			);
			s.setString(1, cid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				threads.add(new ThreadModel(rs));
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return threads;
	}
	public static int threadCount(String cid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT COUNT(`id`) FROM `threads` WHERE `cid` = ?"
			);
			s.setString(1, cid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				int pc = rs.getInt("COUNT(`id`)");
				rs.close();
				s.close();
				return pc;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return 0;
	}
	public long getViews() {
		return views;
	}
	public void setViews(long views) {
		this.views = views;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `threads` SET `views`=? WHERE `id`=?"
			);
			s.setLong(1, views);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public String getTitle() {
		return title;
	}
	public String getId() {
		return id;
	}
	public ProfileModel getUser() {
		ProfileModel u = null;
		if(App.userCache.containsKey(uid)){
			u = App.userCache.get(uid);
		}else{
			App.userCache.put(uid, new ProfileModel(User.findUser(Long.valueOf(uid))));
			u = App.userCache.get(uid);
		}
		return u;
	}
	public PostModel getPost() {
		return post;
	}
	public void setPost(PostModel post) {
		this.post = post;
	}
	public PostModel getLatest() {
		return latest;
	}
	public void setLatest(PostModel latest) {
		this.latest = latest;
	}
	public CategoryModel getCategory() {
		return category;
	}
	public void setCategory(CategoryModel category) {
		this.category = category;
	}
	public String getUid() {
		return uid;
	}
	public String getPid() {
		return pid;
	}
	public String getCid() {
		return cid;
	}
	public void renderLatest() {
		this.setLatest(PostModel.getLatest(id));
	}
	public void renderPost() {
		this.setPost(PostModel.getMain(id));
	}
	public int getReplies() {
		return PostModel.postCount(id)-1;
	}
	public void renderCategory() {
		this.setCategory(CategoryModel.get(cid));
	}
	public long getCreateDate() {
		return createDate;
	}
	public long getPostDate() {
		return postDate;
	}
	public Map<String, String> getStatus() {
		return status;
	}
	public void setTitle(String title) {
		this.title = title;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `threads` SET `title`=? WHERE `id`=?"
			);
			s.setString(1, title);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public static void updatePostTime(String tid) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `threads` SET `post_date`=UNIX_TIMESTAMP() WHERE `id`=?"
			);
			s.setString(1, tid);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setId(String id) {
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public void setPid(String pid) {
		this.pid = pid;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `threads` SET `pid`=? WHERE `id`=?"
			);
			s.setString(1, pid);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setCid(String cid) {
		this.cid = cid;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `threads` SET `cid`=? WHERE `id`=?"
			);
			s.setString(1, cid);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public void setPostDate(long postDate) {
		this.postDate = postDate;
	}
	public void setStatus(Map<String, String> status) {
		this.status = status;
	}
	
}