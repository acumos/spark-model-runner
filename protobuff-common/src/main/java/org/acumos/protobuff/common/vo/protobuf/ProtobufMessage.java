package org.acumos.protobuff.common.vo.protobuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProtobufMessage implements Serializable {

	private static final long serialVersionUID = -1481673805292740045L;

	private String name;
	private List<ProtobufMessageField> fields;
	
	
	public ProtobufMessage(){
		fields = new ArrayList<ProtobufMessageField>();
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
	 * @return the fields
	 */
	public List<ProtobufMessageField> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<ProtobufMessageField> fields) {
		this.fields = fields;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("message " + name + " {\n");
		for(ProtobufMessageField f : fields){
			sb.append(f.toString());
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	
	
	
}
