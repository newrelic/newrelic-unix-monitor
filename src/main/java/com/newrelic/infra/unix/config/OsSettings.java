package com.newrelic.infra.unix.config;

public class OsSettings {

    private String disksCommand;
    private String disksRegex;
    private String interfacesCommand;
    private String interfacesRegex;
    private Integer pageSize;
    private String pageSizeCommand;

    public String getDisksCommand() {
        return disksCommand;
    }

    public void setDisksCommand(String disksCommand) {
        this.disksCommand = disksCommand;
    }

    public String getDisksRegex() {
        return disksRegex;
    }

    public void setDisksRegex(String disksRegex) {
        this.disksRegex = disksRegex;
    }

    public String getInterfacesCommand() {
        return interfacesCommand;
    }

    public void setInterfacesCommand(String interfacesCommand) {
        this.interfacesCommand = interfacesCommand;
    }

    public String getInterfacesRegex() {
        return interfacesRegex;
    }

    public void setInterfacesRegex(String interfacesRegex) {
        this.interfacesRegex = interfacesRegex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageSizeCommand() {
        return pageSizeCommand;
    }

    public void setPageSizeCommand(String pageSizeCommand) {
        this.pageSizeCommand = pageSizeCommand;
    }
}
