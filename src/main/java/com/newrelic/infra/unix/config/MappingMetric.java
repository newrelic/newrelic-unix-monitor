package com.newrelic.infra.unix.config;

import com.newrelic.infra.unix.UnixAgentConstants;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MappingMetric {

	public MappingMetric() {
	}

	public MappingMetric(String name) {
		this(name, UnixAgentConstants.kDefaultMetricType, UnixAgentConstants.kDefaultMetricRatio);
	}

	public MappingMetric(String inName, String inType, Object inRatio) {
		setName(inName);
		setType(inType);
		setRatio(inRatio);
	}

	public MappingMetric(String inName, String inType, Object inRatio, String inFormat) {
		this(inName, inType, inRatio);
		setFormat(inFormat);
	}

	private String name;
	private String type;
	private String format = null;
	private Object ratio;
	private Double actualRatio = null;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Double getActualRatio() {
		return actualRatio;
	}

	public void setActualRatio(int pageSize) {
		if (ratio instanceof Number) {
			this.actualRatio = ((Number) ratio).doubleValue();
		} else if (ratio instanceof String) {
			String ratioStr = (String) ratio;
			Expression ratioExp = new ExpressionBuilder(ratioStr).variables(UnixAgentConstants.kColumnPageSize).build()
					.setVariable(UnixAgentConstants.kColumnPageSize, pageSize);
			try {
				this.actualRatio = ratioExp.evaluate();
			} catch (IllegalArgumentException e) {
				this.actualRatio = (Double) 1.0;
			}
		} else {
			this.actualRatio = (Double) 1.0;
		}
	}
}
