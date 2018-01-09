package com.newrelic.infra.unix;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.infra.publish.api.Agent;
import com.newrelic.infra.publish.api.AgentFactory;
import com.newrelic.infra.unix.config.AgentSettings;

public class UnixAgentFactory extends AgentFactory {

	private static final String body = "{\"body\": {\"integrationId\": \"%s\", \"accountId\": \"%s\", \"accountAdminApiKey\": \"%s\"}}";
	private static final String kDefaultServerName = "unixserver";
	private static final Logger logger = LoggerFactory.getLogger(UnixAgentFactory.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	HashMap<String, Object> agentInstanceConfigs;

	String agentname;
	Boolean debug;

	private Map<String, Object> globalProperties;
	String hostname;

	String os;

	@Override
	public Agent createAgent(Map<String, Object> properties) throws Exception {
		if (!properties.containsKey("host") || ((String) properties.get("host")).toLowerCase().equals("auto")) {
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

		if (properties.containsKey("name") && !((String) properties.get("name")).toLowerCase().equals("auto")) {
			agentname = ((String) properties.get("name"));
		} else {
			if (properties.containsKey("port") && !((String) properties.get("port")).toLowerCase().equals("auto")) {
				agentname = hostname + "_" + ((String) properties.get("port"));
			} else {
				agentname = hostname + "_0";
			}
		}

		logger.info("Agentname: " + agentname);
		logger.info("Host OS: " + os);
		logger.info("Hostname: " + hostname);

		String commandsFileName = properties.containsKey("configDir") ? (String) properties.get("configDir") : "./config";
		commandsFileName += File.separator + "plugin-commands-" + os + ".json";

		File commandsFile = new File(commandsFileName);

		AgentSettings agentSettings = new AgentSettings();

		if (commandsFile.exists()) {
			Reader reader = new FileReader(commandsFile);
			agentSettings = objectMapper.readValue(reader, new TypeReference<AgentSettings>() {
			});
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

	// integration.dashboard.installerUrl=https://oaq67woo45.execute-api.us-east-1.amazonaws.com/prod
	public URL getDashboardInstallerUrl() {
		String url = (String) globalProperties.get("dashboard.installerUrl");
		if (url == null)
			url = "";
		try {
			return new URL(url.trim());
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	// integration.guid=
	public String getIntegrationGuid() {
		Object value = this.globalProperties.get("integration.guid");
		if (value == null)
			return "";
		else
			return ((String) value).trim();
	}

	// rpm.account.adminApiKey= # This account's Admin API Key
	public String getRpmAccountAdminApiKey() {
		Object value = this.globalProperties.get("rpm.account.adminApiKey");
		if (value == null)
			return "";
		else
			return (String) value;
	}

	// rpm.account.id= # This account's New Relic RPM Account ID
	public String getRpmAccountId() {
		Object value = this.globalProperties.get("rpm.account.id");
		if (value == null)
			return "";
		else
			return ((String) value).trim();
	}

	@Override
	public void init(Map<String, Object> properties) {
		super.init(properties);
		this.globalProperties = properties;

		if (properties.containsKey("OS") && !((String) properties.get("OS")).toLowerCase().equals("auto")) {
			os = ((String) properties.get("OS")).toLowerCase().trim();
		} else {
			os = System.getProperty("os.name").toLowerCase().trim();
		}

		String thisVer = System.getProperty("os.version").trim();

		if (os.contains("os x")) {
			os = "osx";
		} else if (os.contains("sunos")) {
			if (thisVer.equals("5.10")) {
				os = "solaris_10";
			} else if (thisVer.equals("5.11")) {
				os = "solaris_11";
			} else {
				logger.error("This version of Solaris isn't supported: " + thisVer);
				os = "unsupported";
			}
		}
		this.installDashboards();
	}

	// Install dashboards for this account in Insights if they don't already exist
	// Update the dashboards in Insights if they exist and have been updated in S3
	private void installDashboards() {
		logger.debug("installDashboards: enter");
		if (this.getDashboardInstallerUrl() == null) {
			logger.warn("installDashboards: dashboard.installerUrl is null, unable to install or update Integration dashboards");
			return;
		}

		// curl -X POST \
		// https://oaq67woo45.execute-api.us-east-1.amazonaws.com/prod \
		// -H 'Content-Type: application/json' \
		// -d '{
		// "body": {
		// "integrationId": "RabbitMQ.Integration",
		// "accountId": "284929-4",
		// "accountAdminApiKey": "b9ace432659c5ddecb72adc09885fe67"
		// }
		// }'
		try {
			HttpsURLConnection connection = (HttpsURLConnection) this.getDashboardInstallerUrl().openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("X-Api-Key", this.getRpmAccountAdminApiKey());

			connection.setDoOutput(true);
			DataOutputStream outputStream;
			outputStream = new DataOutputStream(connection.getOutputStream());
			String msg = String.format(body, this.getIntegrationGuid(), this.getRpmAccountId(), this.getRpmAccountAdminApiKey());
			outputStream.writeBytes(msg);
			outputStream.flush();
			outputStream.close();
			int responseCode;
			responseCode = connection.getResponseCode();

			if (responseCode >= 400) {
				InputStream errorStream = connection.getErrorStream();
				logger.error("installDashboards: responseCode: {}", responseCode);
				String encoding = connection.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				Scanner scanner = new Scanner(errorStream, encoding).useDelimiter("\\A");
				String responseBody = scanner.hasNext() ? scanner.next() : "";
				scanner.close();
				errorStream.close();
				logger.error("installDashboards: responseBody: {}", responseBody);
			}
		} catch (Exception e) {
			logger.error("installDashboards: exception: {}", e.getMessage(), e);
		}
		logger.debug("installDashboards: exit");
	}

}