package com.wegilant;

/*
 * This class is the basic product produced by the Producer and consumed by the consumer
 * 
 */

public class Product {
	
	private String groupId;
	private String artifactId;
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public Product(String groupId,String artifactId){
		this.groupId = groupId;
	}
}
