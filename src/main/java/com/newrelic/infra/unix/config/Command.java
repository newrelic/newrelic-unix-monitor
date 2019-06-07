package com.newrelic.infra.unix.config;

import java.util.List;

import com.newrelic.infra.unix.CommandMetricUtils;
import com.newrelic.infra.unix.UnixAgentConstants;

public class Command {

	// "Required" fields
    private String eventType;
    private String command;
    private List<CommandMapping> mappings = null;
        
    // "Optional" fields (have a default value)
    private Boolean checkAllRegex = false;
    private Integer interval = 0;
    private Integer lineLimit = 0;
    
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String type) {
    	this.eventType = CommandMetricUtils.mungeString(UnixAgentConstants.kEventTypePrefix, type, ':');
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Boolean getCheckAllRegex() {
        return checkAllRegex;
    }

    public void setCheckAllRegex(Boolean checkAllRegex) {
        this.checkAllRegex = checkAllRegex;
    }

    public Integer getLineLimit() {
        return lineLimit;
    }

    public void setLineLimit(Integer lineLimit) {
        this.lineLimit = lineLimit;
    }
    
    public List<CommandMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<CommandMapping> mappings) {
        this.mappings = mappings;
    }
    
    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
