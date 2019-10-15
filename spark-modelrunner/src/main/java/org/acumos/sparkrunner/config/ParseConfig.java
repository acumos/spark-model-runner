package org.acumos.sparkrunner.config;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParseConfig {
	
	
	JSONObject sparkInternalProp = new JSONObject();
	JSONObject modelConfig = new JSONObject();
	JSONObject SPARK_ENV_LOADED = new JSONObject();
	JSONArray args = new JSONArray();
	String url = "";
	
	public JSONObject parseConf(JSONObject jsonConfig){
		try {
			
			modelConfig.put("action", "CreateSubmissionRequest");
			//modelConfig.put("appArgs", jsonConfig.getJSONObject("model").get("model.Args"));
			modelConfig.put("appResource", jsonConfig.getJSONObject("model").getString("model.Resource"));
			modelConfig.put("clientSparkVersion",jsonConfig.getJSONObject("spark").getString("spark.Version"));
				SPARK_ENV_LOADED.put("SPARK_ENV_LOADED", "1");
			modelConfig.put("environmentVariables", SPARK_ENV_LOADED);
			modelConfig.put("mainClass", jsonConfig.getJSONObject("model").getString("model.MainClass"));
				sparkInternalProp.put("spark.jars", jsonConfig.getJSONObject("model").getString("model.Resource"));
				sparkInternalProp.put("spark.app.name", jsonConfig.getJSONObject("model").getString("model.Name"));
				sparkInternalProp.put("spark.master", jsonConfig.getJSONObject("spark").getString("spark.master"));
			modelConfig.put("sparkProperties", sparkInternalProp);
			
			System.out.println(modelConfig.getJSONObject("sparkProperties").get("spark.master"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return modelConfig;
	}
	
	public String getUrl() {
		url = (String) modelConfig.getJSONObject("sparkProperties").get("spark.master");
		return ("http"+url.substring(5));
	}
	

	
	public void setArgs(String data) {
		JSONObject object = new JSONObject (data);
		JSONArray keys = object.names ();
		JSONArray jsonArr = new JSONArray();
		
		for (int i = 0; i < keys.length (); ++i) {

			   String key = keys.getString (i); 
			   String value = object.getString (key);
			   System.out.println(value);
			   modelConfig.put("appArgs", jsonArr.put(value));
			}
	}
	
}
