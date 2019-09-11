package org.acumos.sparkrunner.config;

import org.json.JSONObject;


public class ConfigStorage {

	JSONObject jsonConfig;
	ParseConfig parse = new ParseConfig();
	
	public JSONObject getJsonConfig() {
		return jsonConfig;
	}

	public void setJsonConfig(JSONObject conf) {
		this.jsonConfig = conf;
	}
	
	public JSONObject parse(JSONObject jsonConfig) {
		return parse.parseConf(jsonConfig);
	}
	
	public String urlSpark() {
		return parse.getUrl();
	}
	
	/*public String urlCheck() {
		return parse.checkUrl();
	}
	
	public String masterUrl() {
		return parse.hitUrl();
	}
	*/
	/*public String getResult(JSONObject jsonConfig) {
		return parse.getPath(jsonConfig);
	}
	*/
	/*public String setInput(JSONObject jsonConfig) {
		return parse.getInputPath(jsonConfig);
	}*/
	
	public void setArgument(String data) {
		parse.setArgs(data);
	}
}

