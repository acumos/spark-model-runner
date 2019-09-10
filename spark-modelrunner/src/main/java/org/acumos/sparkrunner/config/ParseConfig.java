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
	
	public String checkUrl() {
		url = (String) modelConfig.getJSONObject("sparkProperties").get("spark.master");
		return (url.substring(0, url.length() - 4)+"7077");
	}
	
	public String hitUrl() {
		url = (String) modelConfig.getJSONObject("sparkProperties").get("spark.master");
		System.out.println(url);
		url = url.substring(0, url.length() - 4)+"8080"; 
		return ("http"+url.substring(5));
	}
	
	public String getPath(JSONObject jsonConfig) {
		String path = (String) jsonConfig.getJSONObject("volume").getString("volume.Output");	
		System.out.println(path);
		return path;
	}
	
	public String getInputPath(JSONObject jsonConfig) {
		String path = (String) jsonConfig.getJSONObject("volume").getString("volume.Input");
		System.out.println(path);
		return path;
	}
	
	public void setArgs(String data) {
		args.put(data);
		modelConfig.put("appArgs", args);
	}
	
}
