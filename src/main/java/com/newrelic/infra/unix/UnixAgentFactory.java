package com.newrelic.infra.unix;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.infra.publish.api.Agent;
import com.newrelic.infra.publish.api.AgentFactory;
import com.newrelic.infra.unix.config.AgentSettings;

public class UnixAgentFactory extends AgentFactory {
	
	private static final String kDefaultServerName = "unixserver";
	Boolean debug;
	String os;
	String hostname;
	String agentname;
	
	private static final Logger logger = LoggerFactory.getLogger(UnixAgentFactory.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	HashMap<String, Object> agentInstanceConfigs;
	
	@Override
	public void init(Map<String, Object> properties) {
		super.init(properties);
		
		if (properties.containsKey("OS") && !((String) properties.get("OS")).toLowerCase().equals("auto")) {
			os = ((String) properties.get("OS")).toLowerCase().trim();
		} else {
			os = System.getProperty("os.name").toLowerCase().trim();
		}
		
		String thisVer = System.getProperty("os.version").trim();
		
		if(os.contains("os x")) {
			os = "osx";
		} else if(os.contains("sunos")) {
			if(thisVer.equals("5.10")) {
				os = "solaris_10";
			} else if(thisVer.equals("5.11")) {
				os = "solaris_11";
			} else {
				logger.error("This version of Solaris isn't supported: " + thisVer);
				os = "unsupported";
			}
		}
	}

	@Override
	public Agent createAgent(Map<String, Object> properties) throws Exception {
		if(!properties.containsKey("host") || ((String)properties.get("host")).toLowerCase().equals("auto")) 
		{
			try {
				hostname = java.net.InetAddress.getLocalHost().getHostName(); 
			} catch (Exception e) {
				logger.error("Naming failed: " + e.toString());
				logger.error("Applying default server name (" + kDefaultServerName + ") to this server");
				hostname = kDefaultServerName;
			}
		} else {
			hostname = ((String) properties.get("host"));
		}
		
		if(properties.containsKey("name") && !((String)properties.get("name")).toLowerCase().equals("auto")) {
			agentname = ((String) properties.get("name"));
		} else {
			if(properties.containsKey("port") && !((String)properties.get("port")).toLowerCase().equals("auto")) {
				agentname = hostname + "_" + ((String) properties.get("port"));
			} else {
				agentname = hostname + "_0";
			}			
		}
		
		logger.info("Agentname: " + agentname);
		logger.info("Host OS: " + os);
		logger.info("Hostname: " + hostname);
		
		String commandsFileName = properties.containsKey("configDir") ? (String)properties.get("configDir") : "./config";
		commandsFileName += File.separator + "plugin-commands-" + os + ".json";
		
		File commandsFile = new File(commandsFileName);

		AgentSettings agentSettings = new AgentSettings();
		
		if (commandsFile.exists()) {
    			Reader reader = new FileReader(commandsFile);
    			agentSettings = objectMapper.readValue(reader, new TypeReference<AgentSettings>(){});
		} else {
    			String msg = "OS Commands File [" + commandsFileName + "] does not exist";
    			logger.error(msg);
    			throw new Exception(msg);
		}
		
		agentSettings.setOs(os);
		agentSettings.setHostname(hostname);
		agentSettings.setAgentname(agentname);
		
		return new UnixAgent(agentSettings);
	}
}