package com.newrelic.infra.unix.config;

import com.newrelic.infra.unix.UnixAgentConstants;

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
	
    private String input;
    private String output;
    private String type;
    private Object ratio;
    
    public String getInput() {
        return input;
    }

    public void setInput(String name) {
        this.input = name;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String name) {
        this.output = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getRatio() {
        return ratio;
    }

    public void setRatio(Object ratio) {
        this.ratio = ratio;
    }
}
