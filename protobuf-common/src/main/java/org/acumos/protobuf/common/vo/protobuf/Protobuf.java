package org.acumos.protobuf.common.vo.protobuf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Protobuf implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3685522023286927954L;
	
	private String syntax; 
	private List<ProtobufOption> options;
	private ProtobufService service;
	private List<ProtobufMessage> messages;
	
	public Protobuf(){
		options = new ArrayList<ProtobufOption>();
		messages = new ArrayList<ProtobufMessage>();
	}
	
	/**
	 * @return the syntax
	 */
	public String getSyntax() {
		return syntax;
	}
	/**
	 * @param syntax the syntax to set
	 */
	public void setSyntax(String syntax) {
		this.syntax = syntax;
	}
	/**
	 * @return the options
	 */
	public List<ProtobufOption> getOptions() {
		return options;
	}
	/**
	 * @param options the options to set
	 */
	public void setOptions(List<ProtobufOption> options) {
		this.options = options;
	}
	/**
	 * @return the service
	 */
	public ProtobufService getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(ProtobufService service) {
		this.service = service;
	}
	/**
	 * @return the messages
	 */
	public List<ProtobufMessage> getMessages() {
		return messages;
	}
	/**
	 * @param messages the messages to set
	 */
	public void setMessages(List<ProtobufMessage> messages) {
		this.messages = messages;
	}
	
	
	public ProtobufMessage getMessage(String messageName){
		//check if messageName is for nested message 
		if(messageName.indexOf(".") != -1){
			messageName = messageName.substring(messageName.lastIndexOf(".")+1);
		}
		ProtobufMessage message = null;
		for(ProtobufMessage m : messages){
			if(m.getName().equals(messageName)){
				message = m; 
				break;
			}
		}
		return message;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("syntax = " + syntax + ";\n");
		sb.append("\n");
		for(ProtobufOption o : options){
			sb.append(o.toString());
		}
		sb.append("\n");
		for(ProtobufMessage m : messages){
			sb.append(m.toString());
		}
		sb.append("\n");
		sb.append(service.toString());
		sb.append("\n");
		return sb.toString();
	}
	
}
