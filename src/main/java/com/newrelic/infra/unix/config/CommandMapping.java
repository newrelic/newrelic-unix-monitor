
package com.newrelic.infra.unix.config;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "expression",
    "metrics",
    "translations"
})
public class CommandMapping {

    @JsonProperty("expression")
    private String expression;
    @JsonProperty("metrics")
    private List<MappingMetric> metrics = null;
    @JsonProperty("translations")
    private List<MappingTranslation> translations = null;
    
    @JsonProperty("expression")
    public String getExpression() {
        return expression;
    }

    @JsonProperty("expression")
    public void setExpression(String expression) {
        this.expression = expression;
    }

    @JsonProperty("metrics")
    public List<MappingMetric> getMetrics() {
        return metrics;
    }

    @JsonProperty("metrics")
    public void setMetrics(List<MappingMetric> metrics) {
        this.metrics = metrics;
    }
    
    @JsonProperty("translations")
    public List<MappingTranslation> getTranslations() {
        return translations;
    }

    @JsonProperty("translations")
    public void setTranslations(List<MappingTranslation> translations) {
        this.translations = translations;
    }
}
