package org.acumos.sparkrunner.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acumos.protobuf.common.ProtoService;
import org.acumos.protobuf.common.ProtoServiceBase;
import org.acumos.protobuf.common.util.Constants;
import org.acumos.protobuf.common.util.ProtobufUtil;
import org.acumos.protobuf.common.vo.protobuf.Protobuf;
import org.acumos.protobuf.common.vo.protobuf.ProtobufMessage;
import org.acumos.protobuf.common.vo.protobuf.ProtobufMessageField;
import org.acumos.protobuf.common.vo.protobuf.ProtobufService;
import org.acumos.protobuf.common.vo.protobuf.ProtobufServiceOperation;
import org.acumos.sparkrunner.APIName;
import org.acumos.sparkrunner.config.ConfigStorage;
import org.acumos.sparkrunner.config.CopyModel;
import org.acumos.sparkrunner.config.SparkConfig;
import org.acumos.sparkrunner.util.JsonFileStorage;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.github.os72.protobuf.dynamic.DynamicSchema;
import com.github.os72.protobuf.dynamic.MessageDefinition;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.util.JsonFormat;
import com.google.protobuf.util.JsonFormat.TypeRegistry;

@Configuration
@RestController
public class SparkRunnerController {

	
	JsonFileStorage json = new JsonFileStorage();
	ConfigStorage jsonConf = new ConfigStorage();
	SparkConfig config = new SparkConfig();
	
	
	@Value("${model_copy_path}")
	private String copyPath;
	
	@Value("${model_source_path}")
	private String sourcePath;
	
	@Value("${spark_config_path}")
	private String configPath;
	
	@Value("${default_proto}")
	private String protoPath;
	
	private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());	
	

	
	@RequestMapping(value = "/getConfig", method = RequestMethod.GET)
	public Object configTemplate() {
		Object config = null;
		String file = configPath.replace("/", File.separator);
		JSONParser jsonParser = new JSONParser(); 
		
		try{
			config = (Object) jsonParser.parse(new FileReader(file));
		} catch(Exception e) {
			
			log.error("File is missing", e.getMessage());
			e.printStackTrace();
			config = e.getMessage();
		}
		return config;
	}
	
	
	@RequestMapping(value = "/setConfig", method = RequestMethod.POST)
	public ResponseEntity<Object> modelConfig(@RequestParam("files") MultipartFile files) throws ParseException {

		String content = "";
		JSONObject conf = null;
		
		try {
			
			content = new String(files.getBytes());
			conf = new JSONObject(content);
			System.out.println(conf.getClass().getName());
			jsonConf.setJsonConfig(conf);

		} catch (IOException e) {
			log.error("IOException from file upload", e.getMessage());
		}
		return new ResponseEntity<Object>("File Uploaded succesfully", HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/model/method/", method = RequestMethod.POST)
	public Object submitJob(@RequestBody String data, @RequestParam String operationName) {
		
		String configs = null;
		Object result = null;
		String url = "";
		String content = "";
				
		try {
			
			ProtobufService rpc = null;
			Protobuf protobuf = null;
			String inputClassName = null;
			String outputClassName = null;
			String defaultProtofile = new String(protoPath.replace("/", File.separator));
			String protoBuffString = null;
			InputStream is = new FileInputStream(defaultProtofile);
			protoBuffString = IOUtils.toString(is);
			
			//created protservice
			ProtoService protoService = new ProtoServiceBase(protoBuffString);
			protobuf = ProtobufUtil.parseProtobuf(protoBuffString);
			rpc = protobuf.getService();
			List<ProtobufServiceOperation> operations = rpc.getOperations();

			for (ProtobufServiceOperation opt : operations) {
				if (opt.getName().equals(operationName)) {
					inputClassName = opt.getInputMessageNames().get(0).trim();
					outputClassName = opt.getOutputMessageNames().get(0).trim();
					break;
				}
			}
			
			DynamicMessage msg = null; 
			DynamicSchema protobufSchema = setProbufSchem(protobuf);
			DynamicMessage.Builder msgBuilder = protobufSchema.newMessageBuilder(inputClassName);
			Descriptor msgDesc = msgBuilder.getDescriptorForType();
			
		    TypeRegistry registry = TypeRegistry.newBuilder().add(msgBuilder.getDescriptorForType()).build();
		    JsonFormat.Parser jFormatter = JsonFormat.parser().usingTypeRegistry(registry);
		   
		   
		   
		    //input = input.replaceAll("\"", "\\\\\"");
		    jFormatter.merge(data, msgBuilder);
		    DynamicMessage reply = msgBuilder.build();
			log.debug("ProtoBuf Validation Done successfully");
			
			jsonConf.setArgument(data);
			content = jsonConf.parse(jsonConf.getJsonConfig()).toString(4);
			config.setConfig(content);
			configs = config.getConfig();
			url = jsonConf.urlSpark();
			URL siteURL = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) siteURL.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(30000);
			connection.connect();
			int responseCode = connection.getResponseCode();
			String responseMessage=connection.getResponseMessage();
			if(responseMessage == "404")
			{
				throw new Exception();
			}
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
			HttpEntity<Object> entity = new HttpEntity<Object>(configs, headers);
			ResponseEntity<String> response = restTemplate.postForEntity(url + APIName.JOB_SUBMIT, entity, String.class);
			result = response;
	
		}
		
		catch(MalformedURLException mfue) {
			log.error("MalformedURLException from http connect : " + mfue.getMessage());
			result = mfue.getMessage();
		}
		
		catch(ProtocolException pe) {
			log.error("ProtocolException from http connect : " + pe.getMessage());
			result = pe.getMessage();
		}
		
		catch(IOException ioe) {
			log.error("IOException from http connect : " + ioe.getMessage());
			result = ioe.getMessage();
		}

		catch (Exception e) {
			log.error("Exception from spark master :", e.getMessage());
			result = e.getMessage();
		}
		
		return result;
	}
	
	private static DynamicSchema setProbufSchem(Protobuf protobuf) throws DescriptorValidationException {
		DynamicSchema protobufSchema;
		DynamicSchema.Builder schemaBuilder = DynamicSchema.newBuilder();
		schemaBuilder.setName("DatabrokerSchemaDynamic.proto");
		Map<String, MessageDefinition> msgDefinitions = new HashMap<String, MessageDefinition>();
		List<ProtobufMessage> messages = protobuf.getMessages();

		for (ProtobufMessage msg : messages) { // add MessageDefinition to msgDefinitions
			if (!msgDefinitions.containsKey(msg.getName())) {
				MessageDefinition msgDefintion = constructMessageDefinition(msg, messages, msgDefinitions);
				msgDefinitions.put(msg.getName(), msgDefintion);
			}
		}
		for (String key : msgDefinitions.keySet()) {
			schemaBuilder.addMessageDefinition(msgDefinitions.get(key));
		}
		protobufSchema = schemaBuilder.build();
		return protobufSchema;
	}
	
	private static MessageDefinition constructMessageDefinition(ProtobufMessage msg, List<ProtobufMessage> messages,
			Map<String, MessageDefinition> msgDefinitions) {
		MessageDefinition.Builder builder = null;
		List<ProtobufMessageField> fields = null;

		builder = MessageDefinition.newBuilder(msg.getName());
		fields = msg.getFields();
		for (ProtobufMessageField f : fields) {
			if (Constants.PROTOBUF_DATA_TYPE.contains(f.getType())) {
				builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
			} else if (f.getType().contains("enum")) {
				// TODO : Include Enum
			} else {
				// check if definition is available in msgDefinitions map
				boolean ispresent = msgDefinitions.containsKey(f.getType());
				MessageDefinition childMsgdefinition = null;
				if (ispresent) {
					childMsgdefinition = msgDefinitions.get(f.getType());

				} else {
					ProtobufMessage childMsg = getProtobufMessagefromList(f.getType(), messages);
					childMsgdefinition = constructMessageDefinition(childMsg, messages, msgDefinitions);
					msgDefinitions.put(f.getType(), childMsgdefinition);
				}

				builder.addMessageDefinition(childMsgdefinition);
				builder.addField(f.getRole(), f.getType(), f.getName(), f.getTag());
			}
		}
		return builder.build();
	}

	private static ProtobufMessage getProtobufMessagefromList(String name, List<ProtobufMessage> messages) {
		ProtobufMessage result = null;
		for (ProtobufMessage msg : messages) {
			if (msg.getName().equals(name)) {
				result = msg;
				break;
			}
		}
		return result;
	}
	
	@EventListener(ApplicationReadyEvent.class)
    public void CopyFile() {
		try{
			CopyModel model = new CopyModel(sourcePath.replace("/", File.separator), copyPath.replace("/", File.separator));
			log.debug("Model dumped");
			
		} catch (Exception e) {
			log.error("Dumping failed" + e.getMessage());
			
		}
    }
    
	
	
}