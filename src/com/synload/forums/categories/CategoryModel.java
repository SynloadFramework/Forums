package com.synload.forums.categories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.mysql.jdbc.Statement;
import com.synload.forums.posts.PostModel;
import com.synload.forums.threads.ThreadModel;
import com.synload.framework.SynloadFramework;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="class")
public class CategoryModel {
	public static Map<String,CategoryModel> cache = new HashMap<String,CategoryModel>();
	public String name, description, order, id, parent = "";
	public CategoryModel parentCategory = null;
	public List<CategoryModel> children = null;
	public List<String> moderators = new ArrayList<String>();
	public List<String> groups = new ArrayList<String>();
	public CategoryModel(String name, String description){
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"INSERT INTO `categories` ( `name`, `description` ) VALUES ( ?, ? );",
				Statement.RETURN_GENERATED_KEYS
			);
			s.setString(1, name);
			s.setString(2, description);
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
	public CategoryModel(ResultSet rs){
		try {
			this.name = rs.getString("name");
			this.description = rs.getString("description");
			this.id = rs.getString("id");
			this.order = rs.getString("order");
			this.parent = rs.getString("parent");
			this.groups = new LinkedList<String>(Arrays.asList(rs.getString("groups").replaceAll("(?i) ", "").split(",")));
			this.moderators = new LinkedList<String>(Arrays.asList(rs.getString("moderators").replaceAll("(?i) ", "").split(",")));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static List<CategoryModel> getAll(){
		List<CategoryModel> cats = new ArrayList<CategoryModel>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `name`, `description`, `order`, `groups`, `moderators`, `parent` FROM `categories` WHERE `parent`='0' ORDER BY `order` ASC;"
			);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				CategoryModel cty = new CategoryModel(rs);
				//if(!CategoryModel.cache.containsKey(cty.getId())){
				//	CategoryModel.cache.put( cty.getId(), cty);
				//}
				cats.add(cty);
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return cats;
	}
	public static List<CategoryModel> getCategoryChildren(String id){
		List<CategoryModel> cats = new ArrayList<CategoryModel>();
		try{
			PreparedStatement s = SynloadFramework.sql.prepareStatement(
				"SELECT `id`, `name`, `description`, `order`, `groups`, `moderators`, `parent` FROM `categories` WHERE `parent`=? ORDER BY `order` ASC;"
			);
			s.setString(1, id);
			ResultSet rs = s.executeQuery();
			while(rs.next()){
				CategoryModel cty = new CategoryModel(rs);
				//if(!CategoryModel.cache.containsKey(cty.getId())){
				//	CategoryModel.cache.put( cty.getId(), cty);
				//}
				//cats.add(CategoryModel.cache.get( cty.getId() ));
				cats.add(cty);
			}
			rs.close();
			s.close();
		}catch(Exception e){
		}
		return cats;
	}
	public static CategoryModel get(String id){
		//if(!CategoryModel.cache.containsKey(id)){
			try{
				PreparedStatement s = SynloadFramework.sql.prepareStatement(
					"SELECT `id`, `name`, `description`, `order`, `groups`, `moderators`, `parent` FROM `categories` WHERE `id`=? LIMIT 1;"
				);
				s.setString(1, id);
				ResultSet rs = s.executeQuery();
				while(rs.next()){
					CategoryModel cty = new CategoryModel(rs);
					//if(!CategoryModel.cache.containsKey(cty.getId())){
					//	CategoryModel.cache.put( cty.getId(), cty);
					//}
					rs.close();
					s.close();
					return cty;
				}
				
			}catch(Exception e){
			}
		//}else{
		//	return CategoryModel.cache.get(id);
		//}
		return null;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	@JsonIgnore
	public void renderParent(){
		this.setParentCategory(CategoryModel.get(this.parent));
	}
	public CategoryModel getParentCategory() {
		return parentCategory;
	}
	public void setParentCategory(CategoryModel parentCategory) {
		this.parentCategory = parentCategory;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getOrder() {
		return order;
	}
	public ThreadModel getLatest(){
		return ThreadModel.getByCategoryLatest(id);
	}
	@JsonIgnore
	public void renderChildren(){
		children = CategoryModel.getCategoryChildren(id);
	}
	public List<CategoryModel> getChildren(){
		return children;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getModerators() {
		return moderators;
	}
	public void setModerators(List<String> moderators) {
		this.moderators = moderators;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
		
	}
	public void save(String... saveData){
		if(saveData.length%2==0){
			Map<String,String> objects = new HashMap<String,String>();
			for(int i=0;i<saveData.length;i+=2){
				objects.put(saveData[i], saveData[i+1]);
			}
			for(Entry<String, String> obj: objects.entrySet()){
				switch(obj.getKey()){
					case "groups":
						try{
							PreparedStatement s = SynloadFramework.sql.prepareStatement(
								"UPDATE `categories` SET `groups`=? WHERE `id`=?"
							);
							s.setString(1, groups.toString().replaceAll("(?i)[\\[\\]]", ""));
							s.setString(2, id);
							s.execute();
							s.close();
						}catch(Exception e){}
					break;
					case "moderators":
						try{
							PreparedStatement s = SynloadFramework.sql.prepareStatement(
								"UPDATE `categories` SET `moderators`=? WHERE `id`=?"
							);
							s.setString(1, moderators.toString().replaceAll("(?i)[\\[\\]]", ""));
							s.setString(2, id);
							s.execute();
							s.close();
						}catch(Exception e){}
					break;
					case "name":
						try{
							PreparedStatement s = SynloadFramework.sql.prepareStatement(
								"UPDATE `categories` SET `name`=? WHERE `id`=?"
							);
							s.setString(1, name);
							s.setString(2, id);
							s.execute();
							s.close();
						}catch(Exception e){}
					break;
					case "description":
						try{
							PreparedStatement s = SynloadFramework.sql.prepareStatement(
								"UPDATE `categories` SET `description`=? WHERE `id`=?"
							);
							s.setString(1, description);
							s.setString(2, id);
							s.execute();
							s.close();
						}catch(Exception e){}
					break;
					case "order":
						try{
							PreparedStatement s = SynloadFramework.sql.prepareStatement(
								"UPDATE `categories` SET `order`=? WHERE `id`=?"
							);
							s.setString(1, order);
							s.setString(2, id);
							s.execute();
							s.close();
						}catch(Exception e){}
					break;
				}
			}
		}
	}
}