package com.entity;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
//利用morphia的annotation进行注解
@Entity
public class User {
	@Id
	private long id;
	private String name;
	private boolean sex;
	private int age;
	private String address;
	
	public User() {
	}
	
	public User(long id, String name, boolean sex, int age, String address) {
		super();
		this.id = id;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.address = address;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSex() {
		return sex;
	}
	public void setSex(boolean sex) {
		this.sex = sex;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", sex=" + sex + ", age=" + age + ", address=" + address + "]";
	}
	
}
