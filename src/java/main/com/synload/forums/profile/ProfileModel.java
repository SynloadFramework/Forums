package com.synload.forums.profile;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synload.forums.App;
import com.synload.forums.posts.PostModel;
import com.synload.framework.SynloadFramework;
import com.synload.framework.users.User;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class ProfileModel {
	public String username, avatar, signature, signatureHtml = "";
	public int posts = 0;
	public JsonNode profileData = null;
	public User user = null;
	public ProfileModel(User user){
		this.posts = PostModel.postUserCount(String.valueOf(user.getId()));
		this.username = user.getUsername();
		this.user = user;
		this.profileData();
	}
	public void profileData(){
		boolean returned = false;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `avatar`, `signature`, `extra` FROM `profiles` WHERE `uid` = ? LIMIT 1"
			);
			s.setLong(1, user.getId());
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				this.avatar = rs.getString("avatar");
				this.signature = rs.getString("signature");
				this.signatureHtml = App.processor.process(this.signature);
				ObjectMapper mapper = new ObjectMapper();
				if(!rs.getString("extra").equals("")){
					this.profileData = mapper.readTree(rs.getString("extra"));
				}
				returned = true;
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		if(!returned){
			try {
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"INSERT INTO `profiles` (`id`, `uid`, `avatar`, `signature`, `extra`) VALUES (NULL, ?, '', '', '');"
				);
				s.setLong(1, this.user.getId());
				s.execute();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `profiles` SET `signature`=? WHERE `uid`=?"
			);
			s.setString(1, signature);
			s.setLong(2, this.user.getId());
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public JsonNode getProfileData() {
		return profileData;
	}
	public void saveProfileData() {
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `profiles` SET `extra`=? WHERE `uid`=?"
			);
			ObjectMapper mapper = new ObjectMapper();
			s.setString(1, mapper.writeValueAsString(profileData));
			s.setLong(2, this.user.getId());
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"UPDATE `profiles` SET `avatar`=? WHERE `uid`=?"
			);
			s.setString(1, avatar);
			s.setLong(2, this.user.getId());
			s.execute();
			s.close();
		}catch(Exception e){}
	}
	public int getPosts() {
		return posts;
	}
	public void setPosts(int posts) {
		this.posts = posts;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
