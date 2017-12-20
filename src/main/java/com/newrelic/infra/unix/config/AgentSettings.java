
package com.newrelic.infra.unix.config;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "os_settings",
    "commands"
})

public class AgentSettings {
    @JsonProperty("os_settings")
    private OsSettings osSettings;
    @JsonProperty("commands")
    private List<Command> commands = null;
    private String os = null;
    private String hostname = null;
    private String agentname = null;
    
    @JsonProperty("os_settings")
    public OsSettings getOsSettings() {
        return osSettings;
    }

    @JsonProperty("os_settings")
    public void setOsSettings(OsSettings osSettings) {
        this.osSettings = osSettings;
    }

    @JsonProperty("commands")
    public List<Command> getCommands() {
        return commands;
    }

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}
	
	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getAgentname() {
		return agentname;
	}

	public void setAgentname(String agentname) {
		this.agentname = agentname;
	}
}
