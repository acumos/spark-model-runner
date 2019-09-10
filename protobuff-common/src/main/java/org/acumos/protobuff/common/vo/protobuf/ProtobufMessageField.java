package org.acumos.protobuff.common.vo.protobuf;

import java.io.Serializable;

public class ProtobufMessageField implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6611051786680341185L;
	
	private String role;
	private String type;
	private String name;
	private int tag;
	
	/**
	 * @return the role
	 */
	public String getRole() {
		return role;
	}
	/**
	 * @param role the role to set
	 */
	public void setRole(String role) {
		this.role = role;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the tag
	 */
	public int getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(int tag) {
		this.tag = tag;
	}
	
	@Override
	public String toString() {
		return role + " " + type + " " + name + " = " + tag + ";\n";
	}
	
	
}