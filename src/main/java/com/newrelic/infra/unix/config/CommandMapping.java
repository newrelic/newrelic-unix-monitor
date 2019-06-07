package com.newrelic.infra.unix.config;

import java.util.List;

public class CommandMapping {

    private String expression;
    private List<MappingMetric> metrics = null;
    private List<MappingTranslation> translations = null;
    
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<MappingMetric> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<MappingMetric> metrics) {
        this.metrics = metrics;
    }

    public List<MappingTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<MappingTranslation> translations) {
        this.translations = translations;
    }
}
