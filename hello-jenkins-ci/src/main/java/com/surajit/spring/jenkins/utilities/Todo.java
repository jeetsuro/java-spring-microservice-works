package com.surajit.spring.jenkins.utilities;

public class Todo {
	private String userId;
	private Integer id;
	private String title;
	private boolean completed;
	
	public Todo() {
		
	}

	public Todo(String userId, Integer id, String title, boolean completed) {
		
		this.userId=userId;
		this.id=id;
		this.title=title;
		this.completed=completed;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public String toString() {
		
		return "[ userId : " + this.userId + " id : " + this.id + " title : " + this.title + " completed : " + this.completed + " ]";
	}
}
