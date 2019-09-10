package org.acumos.sparkrunner.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.acumos.sparkrunner.config.ConfigStorage;
import org.acumos.sparkrunner.config.SparkConfig;
import org.acumos.sparkrunner.util.JsonFileStorage;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.esotericsoftware.minlog.Log;

@RestController
public class SparkRunnerController {

	JsonFileStorage json = new JsonFileStorage();
	ConfigStorage jsonConf = new ConfigStorage();
	SparkConfig config = new SparkConfig();

	private static final String URL_CONFIG = "/json/";
	
	private static final String JOB_SUBMIT = "v1/submissions/create";
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	

			
	
	@RequestMapping(value = "/getConfig", method = RequestMethod.GET)
	public Object configTemplate() {
		Object config = null;
		String file = "D:\\kr00587214\\TECHM\\sparkIO\\Request.json";
		JSONParser jsonParser = new JSONParser(); 
		
		try{
			config = (Object) jsonParser.parse(new FileReader(file));
		} catch(Exception e) {
			
			log.error("Template file is missing", e);
		}
		return config;
	}
	
	
	@RequestMapping(value = "/setConfig", method = RequestMethod.POST)
	public ResponseEntity<Object> modelConfig(@RequestParam("files") MultipartFile files) throws ParseException {

		String content = "";
		JSONObject conf = null;
		
		try {
			
			JSONParser parser = new JSONParser();			
			content = new String(files.getBytes());
			conf = new JSONObject(content);
			System.out.println(conf.getClass().getName());
			jsonConf.setJsonConfig(conf);

		} catch (IOException e) {
			log.error("File is empty", e);
		}
		return new ResponseEntity<Object>("File Uploaded succesfully", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/model/method/{input}", method = RequestMethod.POST)
	public Object submitJob(String data) {
		
		String configs = null;
		Object result = null;
		String url = "";
		String content = "";
				
		try {
			
			jsonConf.setArgument(data);
			content = jsonConf.parse(jsonConf.getJsonConfig()).toString(4);
			config.setConfig(content);
			configs = config.getConfig();
			url = jsonConf.urlSpark();
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<Object> entity = new HttpEntity<Object>(configs, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url + JOB_SUBMIT, entity, String.class);
			result = response;
	
		}
		

		catch (Exception e) {
			log.error("Input is not set", e.getMessage());
		}
		
		return result;
	}
	
	
	@RequestMapping(value = "/checkSparkURL", method = RequestMethod.GET)
	public String checkURL(String url) { 
	String result = null;
	try {
		RestTemplate restTemplate = new RestTemplate();
		result = restTemplate.getForObject((jsonConf.masterUrl())+URL_CONFIG, String.class);
		url = url.substring(0, url.length() - 4)+"7077";
		JSONObject json = new JSONObject(result);
		result = json.getString("url");
		if(url.equals(result)) {
			result = "It is available!!";
		}
		else {
			result = "Not available";
		}
	} catch (Exception e) {
		log.error("Spark master URL not provided", e);
	}
	return result;
	}
}