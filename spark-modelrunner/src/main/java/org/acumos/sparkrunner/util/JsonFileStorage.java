package org.acumos.sparkrunner.util;

import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class JsonFileStorage {
	
	JSONObject jsonContent;

	public JSONObject getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(JSONObject jsonContent) {
		this.jsonContent = jsonContent;
	}
	
}