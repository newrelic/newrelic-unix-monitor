
package com.newrelic.infra.unix.config;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.newrelic.infra.unix.CommandMetricUtils;
import com.newrelic.infra.unix.UnixAgentConstants;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "eventType",
    "command",
    "checkAllRegex",
    "lineLimit",
    "interval",
    "mappings"
})
public class Command {

    @JsonProperty("eventType")
    private String eventType;
    @JsonProperty("command")
    private String command;
    @JsonProperty("checkAllRegex")
    private Boolean checkAllRegex;
    @JsonProperty("lineLimit")
    private Integer lineLimit;
    @JsonProperty("mappings")
    private List<CommandMapping> mappings = null;
    @JsonProperty("interval")
    private Integer interval;
    
    @JsonProperty("eventType")
    public String getEventType() {
        return eventType;
    }

    @JsonProperty("eventType")
    public void setEventType(String type) {
    		this.eventType = CommandMetricUtils.mungeString(UnixAgentConstants.kEventTypePrefix, type, ':');
    }

    @JsonProperty("command")
    public String getCommand() {
        return command;
    }

    @JsonProperty("command")
    public void setCommand(String command) {
        this.command = command;
    }

    @JsonProperty("checkAllRegex")
    public Boolean getCheckAllRegex() {
        return checkAllRegex;
    }

    @JsonProperty("checkAllRegex")
    public void setCheckAllRegex(Boolean checkAllRegex) {
        this.checkAllRegex = checkAllRegex;
    }

    @JsonProperty("lineLimit")
    public Integer getLineLimit() {
        return lineLimit;
    }

    @JsonProperty("lineLimit")
    public void setLineLimit(Integer lineLimit) {
        this.lineLimit = lineLimit;
    }
    
    @JsonProperty("mappings")
    public List<CommandMapping> getMappings() {
        return mappings;
    }

    @JsonProperty("mappings")
    public void setMappings(List<CommandMapping> mappings) {
        this.mappings = mappings;
    }
    
    @JsonProperty("interval")
    public Integer getInterval() {
        return interval;
    }

    @JsonProperty("interval")
    public void setInterval(Integer interval) {
        this.interval = interval;
    }
}
