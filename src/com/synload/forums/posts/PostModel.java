package com.synload.forums.posts;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.kefirsf.bb.BBProcessorFactory;
import org.kefirsf.bb.TextProcessor;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mysql.jdbc.Statement;
import com.synload.forums.App;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class PostModel {
	public String message, subject, id, tid, uid, parent, html = "";
	public long createDate, postDate = 0;
	public PostModel(ResultSet rs){
		try {
			this.message = rs.getString("message");
			
			this.html = App.processor.process(rs.getString("message"));
			this.subject = rs.getString("subject");
			this.id = rs.getString("id");
			this.tid = rs.getString("tid");
			this.uid = rs.getString("uid");
			this.parent = rs.getString("parent");
			this.createDate = rs.getLong("created_date");
			this.postDate = rs.getLong("post_date");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public PostModel(String subject, String message, String tid, String uid, String parent){
		this.subject = subject;
		this.message = message;
		this.tid = tid;
		this.uid = uid;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `posts` ( `subject`, `message`, `tid`, `uid`, `post_date`, `created_date`, `parent` ) VALUES ( ?, ?, ?, ?, UNIX_TIMESTAMP(), UNIX_TIMESTAMP(), ? )",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, subject);
			s.setString(2, message);
			s.setString(3, tid);
			s.setString(4, uid);
			s.setString(5, parent);
			s.execute();
			ResultSet keys = s.getGeneratedKeys();
			if(keys.next()){
				id = keys.getString(1);
			}
			s.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static List<PostModel> getPosts(String tid, int page){
		List<PostModel> posts = new ArrayList<PostModel>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `subject`, `message`, `tid`, `uid`, `created_date`, `post_date`, `parent` FROM `posts` WHERE `tid` = ? AND `parent` = 0 ORDER BY `post_date` ASC LIMIT "+(page*25)+",25"
			);
			s.setString(1, tid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				posts.add(new PostModel(rs));
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return posts;
	}
	public static PostModel getLatest(String tid, int page){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `subject`, `message`, `tid`, `uid`, `created_date`, `post_date`, `parent` FROM `posts` WHERE `tid` = ? AND `parent` = 0 ORDER BY `created_date` DESC LIMIT "+(page*25)+",25"
			);
			s.setString(1, tid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				PostModel post = new PostModel(rs);
				rs.close();
				s.close();
				return post;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
	public static int postCount(String tid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT COUNT(`id`) FROM `posts` WHERE `tid` = ?"
			);
			s.setString(1, tid);
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
	public static int postUserCount(String uid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT COUNT(`id`) FROM `posts` WHERE `uid` = ?"
			);
			s.setString(1, uid);
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
	public static PostModel getLatest(String tid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `subject`, `message`, `tid`, `uid`, `created_date`, `post_date`, `parent` FROM `posts` WHERE `tid` = ? AND `parent` = 0 ORDER BY `created_date` DESC LIMIT 1"
			);
			s.setString(1, tid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				PostModel post = new PostModel(rs);
				rs.close();
				s.close();
				return post;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
	public static PostModel get(String pid){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `subject`, `message`, `tid`, `uid`, `created_date`, `post_date`, `parent` FROM `posts` WHERE `id` = ? "
			);
			s.setString(1, pid);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				PostModel post = new PostModel(rs);
				rs.close();
				s.close();
				return post;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return null;
	}
	public String getMessage() {
		return message;
	}
	public String getSubject() {
		return subject;
	}
	public String getId() {
		return id;
	}
	public String getTid() {
		return tid;
	}
	public String getUid() {
		return uid;
	}
	public long getCreateDate() {
		return createDate;
	}
	public long getPostDate() {
		return postDate;
	}
	public static void updatePostTime(String pid) {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `posts` SET `post_date`=UNIX_TIMESTAMP() WHERE `id`=?"
			);
			s.setString(1, pid);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setMessage(String message) {
		this.message = message;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `posts` SET `message`=? WHERE `id`=?"
			);
			s.setString(1, message);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setSubject(String subject) {
		this.subject = subject;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `posts` SET `subject`=? WHERE `id`=?"
			);
			s.setString(1, subject);
			s.setString(2, id);
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public void setId(String id) {
		this.id = id;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public User getUser() {
		User u = null;
		if(App.userCache.containsKey(uid)){
			u = App.userCache.get(uid);
		}else{
			App.userCache.put(uid,User.findUser(Long.valueOf(uid)));
			u = App.userCache.get(uid);
		}
		return u;
	}
	public void setCreateDate(long createDate) {
		this.createDate = createDate;
	}
	public void setPostDate(long postDate) {
		this.postDate = postDate;
	}
	
}
