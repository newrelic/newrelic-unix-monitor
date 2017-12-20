
package com.newrelic.infra.unix.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "disksCommand",
    "disksRegex",
    "interfacesCommand",
    "interfacesRegex",
    "pageSizeCommand"
})

public class OsSettings {

    @JsonProperty("disksCommand")
    private String disksCommand;
    @JsonProperty("disksRegex")
    private String disksRegex;
    @JsonProperty("interfacesCommand")
    private String interfacesCommand;
    @JsonProperty("interfacesRegex")
    private String interfacesRegex;
    private Integer pageSize;
    @JsonProperty("pageSizeCommand")
    private String pageSizeCommand;

    @JsonProperty("disksCommand")
    public String getDisksCommand() {
        return disksCommand;
    }

    @JsonProperty("disksCommand")
    public void setDisksCommand(String disksCommand) {
        this.disksCommand = disksCommand;
    }

    @JsonProperty("disksRegex")
    public String getDisksRegex() {
        return disksRegex;
    }

    @JsonProperty("disksRegex")
    public void setDisksRegex(String disksRegex) {
        this.disksRegex = disksRegex;
    }

    @JsonProperty("interfacesCommand")
    public String getInterfacesCommand() {
        return interfacesCommand;
    }

    @JsonProperty("interfacesCommand")
    public void setInterfacesCommand(String interfacesCommand) {
        this.interfacesCommand = interfacesCommand;
    }

    @JsonProperty("interfacesRegex")
    public String getInterfacesRegex() {
        return interfacesRegex;
    }

    @JsonProperty("interfacesRegex")
    public void setInterfacesRegex(String interfacesRegex) {
        this.interfacesRegex = interfacesRegex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("pageSizeCommand")
    public String getPageSizeCommand() {
        return pageSizeCommand;
    }

    @JsonProperty("pageSizeCommand")
    public void setPageSizeCommand(String pageSizeCommand) {
        this.pageSizeCommand = pageSizeCommand;
    }
}
