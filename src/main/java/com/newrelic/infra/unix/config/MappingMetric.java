
package com.newrelic.infra.unix.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.newrelic.infra.unix.UnixAgentConstants;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "type",
    "ratio"
})
public class MappingMetric {
    	
	public MappingMetric() {}
	
	public MappingMetric(String name) {
		this(name, UnixAgentConstants.kDefaultMetricType, UnixAgentConstants.kDefaultMetricRatio);
	}
	
	public MappingMetric(String inName, String inType, Object inRatio) {
		setName(inName);
		setType(inType);
		setRatio(inRatio);
	}
	
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("ratio")
    private Object ratio;
    
    private Double actualRatio = null;
    
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
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

	public Double getActualRatio() {
		return actualRatio;
	}

	public void setActualRatio(int pageSize) {
		if(ratio instanceof Number) {
			this.actualRatio = ((Number)ratio).doubleValue();
		} else if(ratio instanceof String) {
			String ratioStr = (String)ratio;
			Expression ratioExp = new ExpressionBuilder(ratioStr)
			        .variables(UnixAgentConstants.kColumnPageSize)
			        .build()
			        .setVariable(UnixAgentConstants.kColumnPageSize, pageSize);
			try {
				this.actualRatio = ratioExp.evaluate();
			} catch (IllegalArgumentException e) {
				this.actualRatio = (Double)1.0;
			}
		} else {
			this.actualRatio = (Double)1.0;
		}
	}
}
