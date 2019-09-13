package org.acumos.protobuf.common.vo.protobuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProtobufService implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6149386852915447136L;
	
	private String name; 
	private List<ProtobufServiceOperation> operations;
	
	public ProtobufService(){
		operations = new ArrayList<ProtobufServiceOperation>();
	}
	/**
	 * @return the name
	 * 		This method returns name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 * 		This method accepts name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the operations
	 * 			This method returns operations	
	 */
	public List<ProtobufServiceOperation> getOperations() {
		return operations;
	}
	/**
	 * @param operations
	 * 			This method accepts operations
	 */
	public void setOperations(List<ProtobufServiceOperation> operations) {
		this.operations = operations;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("service " + name + " {\n");
		for(ProtobufServiceOperation o : operations){
			sb.append(o.toString());
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	
	
}
