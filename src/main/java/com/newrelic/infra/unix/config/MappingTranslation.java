
package com.newrelic.infra.unix.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.newrelic.infra.unix.UnixAgentConstants;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "input",
    "output",
    "type",
    "ratio"
})
public class MappingTranslation {
    	
	public MappingTranslation() {}
	
	public MappingTranslation(String input) {
		this(input, input, UnixAgentConstants.kDefaultMetricType, UnixAgentConstants.kDefaultMetricRatio);
	}
	
	public MappingTranslation(String inName, String outName, String inType, Object inRatio) {
		setInput(inName);
		setOutput(outName);
		setType(inType);
		setRatio(inRatio);
	}
	
    @JsonProperty("input")
    private String input;
    @JsonProperty("output")
    private String output;
    @JsonProperty("type")
    private String type;
    @JsonProperty("ratio")
    private Object ratio;
    
    @JsonProperty("input")
    public String getInput() {
        return input;
    }

    @JsonProperty("input")
    public void setInput(String name) {
        this.input = name;
    }

    @JsonProperty("output")
    public String getOutput() {
        return output;
    }

    @JsonProperty("output")
    public void setOutput(String name) {
        this.output = name;
    }
    
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("ratio")
    public Object getRatio() {
        return ratio;
    }

    @JsonProperty("ratio")
    public void setRatio(Object ratio) {
        this.ratio = ratio;
    }
}
